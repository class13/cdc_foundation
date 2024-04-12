package at.tailor.cdc.foundation.service.web.article.rest

import at.tailor.cdc.foundation.service.web.article.service.UpdateArticleModel

data class UpdateArticleDTO(
    val title: String,
    val categories: List<Long>,
    val status: String,
) {
    fun toServiceModel(id: Long): UpdateArticleModel {
        return UpdateArticleModel(
            id,
            title,
            categories,
            status
        )
    }

}
