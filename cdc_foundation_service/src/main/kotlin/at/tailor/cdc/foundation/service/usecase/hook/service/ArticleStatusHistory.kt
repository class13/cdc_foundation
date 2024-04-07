package at.tailor.cdc.foundation.service.usecase.hook.service

import java.time.LocalDateTime

data class ArticleStatusHistory(
    val articleId: Long,
    val fromStatus: String,
    val toStatus: String,
    val timestamp: LocalDateTime,
)