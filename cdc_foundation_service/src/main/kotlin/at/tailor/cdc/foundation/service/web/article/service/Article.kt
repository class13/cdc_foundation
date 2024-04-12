package at.tailor.cdc.foundation.service.web.article.service

import at.tailor.cdc.foundation.service.web.article.persistence.ArticleEntity

class Article(
    val id: Long,
    val title: String,
    val status: String,
    val categoryIds: List<Long>
) {
    companion object {
        fun ofEntity(entity: ArticleEntity): Article {
            return Article(
                id = entity.id,
                title = entity.title,
                status = entity.status,
                categoryIds = entity.categories.map { it.id },
            )
        }
    }

}
