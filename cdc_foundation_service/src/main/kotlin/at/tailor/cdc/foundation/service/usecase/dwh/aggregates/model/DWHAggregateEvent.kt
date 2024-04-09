package at.tailor.cdc.foundation.service.usecase.dwh.aggregates.model

import java.time.LocalDateTime

data class DWHAggregateEvent<T>(
    val operation: Operation,
    val entity: T,
    val timestamp: LocalDateTime,
) {
    enum class Operation{
        CREATE, UPDATE, DELETE, SNAPSHOT
    }
}