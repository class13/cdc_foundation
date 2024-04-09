package at.tailor.cdc.foundation.service.usecase.outbox.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface KafkaOutboxRepository: JpaRepository<KafkaOutboxEntity, Long>