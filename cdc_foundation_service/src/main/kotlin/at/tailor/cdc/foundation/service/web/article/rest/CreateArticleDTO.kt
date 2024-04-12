package at.tailor.cdc.foundation.service.web.article.rest

import at.tailor.cdc.foundation.service.web.article.service.CreateArticleModel

data class CreateArticleDTO(
    val title: String,
    val status: String,
    val categories: List<Long>,
) {
    fun toServiceModel(): CreateArticleModel {
        return CreateArticleModel(
            title,
            status,
            categories
        )
    }

}
