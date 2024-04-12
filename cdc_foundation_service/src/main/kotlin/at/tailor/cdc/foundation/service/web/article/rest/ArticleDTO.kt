package at.tailor.cdc.foundation.service.web.article.rest

import at.tailor.cdc.foundation.service.web.article.service.Article

class ArticleDTO(
    val id: Long,
    val title: String,
    val status: String,
    val categories: List<Long>,
) {
    companion object {
        fun of(article: Article): ArticleDTO {
            return ArticleDTO(
                id = article.id,
                title = article.title,
                status = article.status,
                categories = article.categoryIds,
            )
        }
    }

}
