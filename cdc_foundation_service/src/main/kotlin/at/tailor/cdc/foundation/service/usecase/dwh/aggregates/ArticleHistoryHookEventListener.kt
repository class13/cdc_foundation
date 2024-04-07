package at.tailor.cdc.foundation.service.usecase.dwh.aggregates

import at.tailor.cdc.foundation.service.usecase.hook.cdc.router.ArticleHistoryHookRouter
import at.tailor.cdc.foundation.service.common.cdc.model.CDCMonologEventMessage
import at.tailor.cdc.foundation.service.usecase.dwh.row.DWHRowEventRouter
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
        topics = [DWHAggregateEventRouter.TARGET_TOPIC],
        groupId = "dwh_aggregate_listener"
    )
    fun handleDWHAggregateEvent(record: ConsumerRecord<String, String>) {
        val message = objectMapper.readValue<CDCMonologEventMessage>(record.value())
        if (
            message.operation == CDCMonologEventMessage.CDCMonologEventOperation.UPDATE &&
            message.after != null && message.before != null
        ) {
            // todo: fetch and build aggregated event
            // todo: should include  categories
            // todo: if a cateegory gets updated all aggregated events of referencing adverts must be also published
            // todo: showcase a generic kafka outbox sending article viewed events out when an article is viewed
        }

    }
}