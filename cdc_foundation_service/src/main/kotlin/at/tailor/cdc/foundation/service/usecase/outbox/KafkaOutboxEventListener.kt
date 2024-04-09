package at.tailor.cdc.foundation.service.usecase.outbox

import at.tailor.cdc.foundation.service.common.cdc.model.CDCMonologEventMessage
import at.tailor.cdc.foundation.service.common.cdc.router.CDCMonologEventRouter
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.treeToValue
import jakarta.transaction.Transactional
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class KafkaOutboxEventListener(
    val kafkaTemplate: KafkaTemplate<String, String>,
    val objectMapper: ObjectMapper
) {

    companion object {
        const val TOPIC = "kafka_outbox"
    }

    @Component
    class Router: CDCMonologEventRouter {
        override fun subscribedTables(): Set<String> {
            return setOf(TOPIC)
        }

        override fun targetTopic(): String {
            return "kafka_outbox"
        }
    }

    data class KafkaOutboxEventPayload(
        val key: String,
        val value: String,
        val topic: String,
    )

    @KafkaListener(
        topics = [TOPIC],
        groupId = TOPIC
    )
    @Transactional
    fun handleKafkaOutboxEvent(record: ConsumerRecord<String, String>) {
        val cdcMonologEventMessage = record.value().let { objectMapper.readValue<CDCMonologEventMessage>(it) }
        val payload = cdcMonologEventMessage.after?.let { objectMapper.treeToValue<KafkaOutboxEventPayload>(it) } ?: return

        kafkaTemplate.send(payload.topic, payload.key, payload.value)
    }
}