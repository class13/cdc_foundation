package at.tailor.cdc.foundation.service.usecase.hook.service.jpa

import at.tailor.cdc.foundation.service.usecase.hook.service.jpa.ArticleStatusHistoryEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ArticleStatusHistoryRepository: JpaRepository<ArticleStatusHistoryEntity, Long> {
    fun findByArticleIdOrderByTimestampDesc(articleId: Long): List<ArticleStatusHistoryEntity>
}