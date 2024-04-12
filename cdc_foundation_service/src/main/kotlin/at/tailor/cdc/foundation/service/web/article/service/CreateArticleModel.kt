package at.tailor.cdc.foundation.service.web.article.service

data class CreateArticleModel(
    val title: String,
    val status: String,
    val categoryIds: List<Long>
)
