package at.tailor.cdc.foundation.service.web.article.service

data class UpdateArticleModel(
    val id: Long,
    val title: String,
    val categoryIds: List<Long>,
    val status: String,
)
