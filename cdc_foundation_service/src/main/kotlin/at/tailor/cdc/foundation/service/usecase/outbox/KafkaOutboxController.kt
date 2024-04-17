package at.tailor.cdc.foundation.service.usecase.outbox

import com.fasterxml.jackson.databind.JsonNode
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/kafka")
class KafkaOutboxController(
    val kafkaService: KafkaService
) {

    data class MessageDTO(
        val topic: String,
        val key: JsonNode,
        val value: JsonNode,
    )

    @PostMapping("/message")
    fun postMessage(@RequestBody message: MessageDTO) {
        kafkaService.send(message.topic, message.key, message.value)
    }
}