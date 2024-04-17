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

    @Transactional
    fun getArticleStatusHistory(articleId: Long): List<ArticleStatusHistory> {
        return articleStatusHistoryRepository.findByArticleIdOrderByTimestampDesc(articleId).map {
            ArticleStatusHistory(
                articleId = it.articleId,
                fromStatus = it.fromStatus,
                toStatus = it.toStatus,
                timestamp = it.timestamp,
            )
        }
    }
}