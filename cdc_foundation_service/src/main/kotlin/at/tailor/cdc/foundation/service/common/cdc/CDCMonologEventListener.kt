package at.tailor.cdc.foundation.service.common.cdc

import at.tailor.cdc.foundation.service.common.cdc.model.CDCMonologEventMessage
import at.tailor.cdc.foundation.service.common.cdc.router.CDCMonologEventRouter
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class CDCMonologEventListener (
    eventHandlers: List<CDCMonologEventRouter>,
    private val objectMapper: ObjectMapper,
    private val kafkaTemplate: KafkaTemplate<String, String>
) {
    companion object {
        val LOG: Logger = LoggerFactory.getLogger(CDCMonologEventListener::class.java)
    }

    // we create a map showing which event handler has subscribed to which tables
    private final val tableSubscribers = eventHandlers.flatMap { eventHandler ->
        eventHandler.subscribedTables().map { tableName ->
            Pair(tableName, eventHandler.targetTopic())
        }
    }.groupBy(
        { it.first },
        { it.second }
    )
    // we create a set of all subscribed tables,
    // that will be used to stop processing irrelevant messages as fast as possible
    val allSubscribedTables = tableSubscribers.keys

    @KafkaListener(topics = ["cdc_monolog"], groupId = "cdc_foundation_service_monolog_listener")
    fun handleCDCMonologEvent(record: ConsumerRecord<String, String>) {
        try {
            // deserialize the cdc message (by debezium)
            val message = objectMapper.readValue<CDCMonologEventMessage>(record.value())

            // as performance is key here, we stop processing as fast as possible
            if (!allSubscribedTables.contains(message.source.table)) return

            // depending on the table, re-route decided by the event routers
            tableSubscribers[message.source.table]?.forEach { topic ->
                try {
                    kafkaTemplate.send(topic, record.key(), record.value())
                } catch (e: Exception) {
                    // if a handler fails, other handlers should still attempt to process that event
                    LOG.error("${topic::class.qualifiedName} failed to process cdc monolog event.", e)
                }
            }


        } catch (e: Exception) {
            // the cdc foundation should never stop processing if in error cases
            // this can only happen when debezium produced messages that can't be deserialized
            // we keep the deserialization as flexible as possible
            LOG.error("Failed to process cdc monolog event", e)
        }
    }

}