package at.tailor.cdc.foundation.service.usecase.dwh.aggregates

import at.tailor.cdc.foundation.service.common.cdc.model.CDCMonologEventMessage
import at.tailor.cdc.foundation.service.usecase.dwh.aggregates.model.DWHAggregateEvent
import at.tailor.cdc.foundation.service.usecase.dwh.aggregates.model.DWHArticleAggregate
import at.tailor.cdc.foundation.service.web.article.persistence.ArticleEntity
import at.tailor.cdc.foundation.service.web.article.persistence.ArticleRepository
import at.tailor.cdc.foundation.service.web.category.persistence.CategoryEntity
import at.tailor.cdc.foundation.service.web.category.persistence.CategoryRepository
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.treeToValue
import jakarta.transaction.Transactional
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.data.repository.findByIdOrNull
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import kotlin.jvm.optionals.getOrNull

/**
 Listens to the `article_history_hook_events` topic and executes the hook, but only
 if the event is relevant. An event is relevant if the status was updated.
 */
@Component
class DWHAggregateEventListener(
    private val objectMapper: ObjectMapper,
    private val articleRepository: ArticleRepository,
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val categoryRepository: CategoryRepository
) {
    @KafkaListener(
        topics = [DWHAggregateEventRouter.TARGET_TOPIC],
        groupId = "dwh_aggregate_listener"
    )
    @Transactional
    fun handleDWHAggregateEvent(record: ConsumerRecord<String, String>) {
        val message = objectMapper.readValue<CDCMonologEventMessage>(record.value())

        when (message.source.table) {
            ArticleEntity.TABLE_NAME -> handleArticleEvent(message)
            ArticleEntity.CATEGORIES_JOIN_TABLE_NAME -> handleArticleCategoriesEvent(message)
            CategoryEntity.TABLE_NAME -> handleCategoryEvent(message)
        }

    }

    private fun sendAggregateEvent(articleId: Long, event: DWHAggregateEvent<*>) {
        kafkaTemplate.send(
            "dwh_article_aggregate",
            articleId.toString(),
            objectMapper.writeValueAsString(event),
        )
        kafkaTemplate.flush()
    }

    private fun convertOperation(message: CDCMonologEventMessage): DWHAggregateEvent.Operation? {
        return when(message.operation) {
            CDCMonologEventMessage.CDCMonologEventOperation.CREATE -> DWHAggregateEvent.Operation.CREATE
            CDCMonologEventMessage.CDCMonologEventOperation.UPDATE -> DWHAggregateEvent.Operation.UPDATE
            CDCMonologEventMessage.CDCMonologEventOperation.SNAPSHOT -> DWHAggregateEvent.Operation.SNAPSHOT
            CDCMonologEventMessage.CDCMonologEventOperation.DELETE -> DWHAggregateEvent.Operation.DELETE
            else -> return null
        }
    }

    // each create, update, delete or snapshot article event triggers an aggregate event
    private fun handleArticleEvent(message: CDCMonologEventMessage) {
        data class Article(
            val id: Long,
        )

        val operation = convertOperation(message) ?: return

        val article = (if (operation == DWHAggregateEvent.Operation.DELETE) message.before else message.after)
            ?.let { objectMapper.treeToValue<Article>(it) } ?: return

        val entity: Any = if (operation == DWHAggregateEvent.Operation.DELETE)
            DWHArticleAggregate.Tombstone(article.id)
        else
            createArticleAggregate(article.id) ?: return

        val event = DWHAggregateEvent(
            operation,
            entity,
            message.timestamp,
        )
        sendAggregateEvent(article.id, event)
    }

    // an important note
    // fetching from the regular database triggered by a cdc event has a limitation
    // the data fetched from the database might be newer than the data change captured
    // this might cause intermediate states getting lost in between very fast changes
    // example: an article update event comes in but on the database it has already been deleted
    // to solve this issue, a secondary state store is needed that is always synchronous to the cdc events
    // Kafka Streams and its state stores are explicitly made for such use cases
    private fun createArticleAggregate(articleId: Long): DWHArticleAggregate? {
        val articleEntity = articleRepository.findById(articleId).getOrNull() ?: return null
        return DWHArticleAggregate(
            id = articleEntity.id,
            title = articleEntity.title,
            status = articleEntity.status,
            categories = articleEntity.categories.map { it.name }
        )
    }

    // each create, update or delete article categories event should trigger an aggregate for the referenced articles

    private fun handleArticleCategoriesEvent(message: CDCMonologEventMessage) {
        val operation = convertOperation(message) ?: return
        val allowedOperations = setOf(
            DWHAggregateEvent.Operation.UPDATE,
            DWHAggregateEvent.Operation.DELETE,
            DWHAggregateEvent.Operation.CREATE
        )
        if (!allowedOperations.contains(operation)) return

        data class ArticleCategories(
            @JsonProperty("article_id")
            val articleId: Long,
        )
        val articleCategories = (if (operation == DWHAggregateEvent.Operation.DELETE) message.before else message.after)
            ?.let { objectMapper.treeToValue<ArticleCategories>(it) } ?: return
        val articleAggregate = createArticleAggregate(articleCategories.articleId) ?: return
        sendAggregateEvent(articleAggregate.id, DWHAggregateEvent(DWHAggregateEvent.Operation.UPDATE, articleAggregate, message.timestamp))
    }

    // each update category event triggers an aggregate event for all referencing articles
    private fun handleCategoryEvent(message: CDCMonologEventMessage) {
        val operation = convertOperation(message) ?: return
        if (operation != DWHAggregateEvent.Operation.UPDATE) return
        data class Category(
            val id: Long,
        )
        val category = message.after?.let { objectMapper.treeToValue<Category>(it) } ?: return
        val categoryEntity = categoryRepository.findByIdOrNull(category.id) ?: return
        val aggregates = categoryEntity.articles
            .mapNotNull { createArticleAggregate(it.id) }
        aggregates.forEach{
            sendAggregateEvent(it.id, DWHAggregateEvent(operation, it, message.timestamp))
        }
    }
}