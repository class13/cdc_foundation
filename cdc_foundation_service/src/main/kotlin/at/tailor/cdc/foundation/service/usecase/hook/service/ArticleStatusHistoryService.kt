package at.tailor.cdc.foundation.service.usecase.hook.service

import at.tailor.cdc.foundation.service.usecase.hook.service.jpa.ArticleStatusHistoryEntity
import at.tailor.cdc.foundation.service.usecase.hook.service.jpa.ArticleStatusHistoryRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class ArticleStatusHistoryService(
    val articleStatusHistoryRepository: ArticleStatusHistoryRepository
) {


    @Transactional
    fun createArticleStatusHistory(articleStatusHistory: ArticleStatusHistory) {
        val articleStatusHistoryEntity = ArticleStatusHistoryEntity(
            articleId = articleStatusHistory.articleId,
            fromStatus = articleStatusHistory.fromStatus,
            toStatus = articleStatusHistory.toStatus,
            timestamp = articleStatusHistory.timestamp,
        )
        articleStatusHistoryRepository.save(articleStatusHistoryEntity)
    }
}