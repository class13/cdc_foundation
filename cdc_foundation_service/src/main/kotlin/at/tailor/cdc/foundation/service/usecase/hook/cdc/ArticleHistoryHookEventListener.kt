package at.tailor.cdc.foundation.service.usecase.hook.cdc

import at.tailor.cdc.foundation.service.usecase.hook.cdc.router.ArticleHistoryHookRouter
import at.tailor.cdc.foundation.service.common.cdc.model.CDCMonologEventMessage
import at.tailor.cdc.foundation.service.usecase.hook.service.ArticleStatusHistory
import at.tailor.cdc.foundation.service.usecase.hook.service.ArticleStatusHistoryService
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.treeToValue
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

/**
 Listens to the `advert_history_hook_events` topic and executes the hook, but only
 if the event is relevant. An event is relevant if the status was updated.
 */
@Component
class ArticleHistoryHookEventListener(
    private val objectMapper: ObjectMapper,
    private val articleStatusHistoryService: ArticleStatusHistoryService
) {

    data class Article (
        val id: Long,
        val status: String,
    )

    @KafkaListener(
        topics = [ArticleHistoryHookRouter.TARGET_TOPIC],
        groupId = "cdc_foundation_service_article_history_hook_listener"
    )
    fun handleArticleHistoryHookEvent(record: ConsumerRecord<String, String>) {
        val message = objectMapper.readValue<CDCMonologEventMessage>(record.value())
        if (
            message.operation == CDCMonologEventMessage.CDCMonologEventOperation.UPDATE &&
            message.after != null && message.before != null
        ) {
            val articleBefore = objectMapper.treeToValue<Article>(message.before)
            val articleAfter = objectMapper.treeToValue<Article>(message.after)

            if (articleBefore.status != articleAfter.status) {
                articleStatusHistoryService.createArticleStatusHistory(
                    ArticleStatusHistory(
                        articleBefore.id,
                        articleBefore.status,
                        articleAfter.status,
                        message.timestamp
                    )
                )
            }
        }

    }
}