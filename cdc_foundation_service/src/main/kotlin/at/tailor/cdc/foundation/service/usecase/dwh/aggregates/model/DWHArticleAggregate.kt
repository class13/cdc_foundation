package at.tailor.cdc.foundation.service.usecase.dwh.aggregates.model

data class DWHArticleAggregate(
    val id: Long,
    val title: String,
    val status: String,
    val categories: List<String>
) {
    data class Tombstone (
        val id: Long
    )
}

