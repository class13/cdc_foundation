package at.tailor.cdc.foundation.service.usecase.outbox.persistence

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "kafka_outbox")
data class KafkaOutboxEntity(
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = -1,
    @Column(name = "timestamp")
    val timestamp: LocalDateTime,
    @Column(name = "topic")
    val topic: String,
    @Column(name = "value")
    val value: String?,
    @Column(name = "key")
    val key: String
)
