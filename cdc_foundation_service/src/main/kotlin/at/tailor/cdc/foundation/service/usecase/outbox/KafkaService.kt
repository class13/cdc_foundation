package at.tailor.cdc.foundation.service.usecase.outbox

import at.tailor.cdc.foundation.service.usecase.outbox.persistence.KafkaOutboxEntity
import at.tailor.cdc.foundation.service.usecase.outbox.persistence.KafkaOutboxRepository
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.time.LocalDateTime


@Service
class KafkaService(
    val kafkaOutboxRepository: KafkaOutboxRepository,
    val objectMapper: ObjectMapper
){

    @Transactional
    fun send(topic: String, key: Any, value: Any?) {
        KafkaOutboxEntity(
            timestamp = LocalDateTime.now(),
            key = key.let { objectMapper.writeValueAsString(it) },
            value = value?.let { objectMapper.writeValueAsString(it) },
            topic = topic
        ).let { kafkaOutboxRepository.save(it) }
    }






}