package at.tailor.cdc.foundation.service.web.article.status.history

import at.tailor.cdc.foundation.service.usecase.hook.service.ArticleStatusHistoryService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
@RequestMapping("/article/status/history")
class ArticleStatusHistoryController(
    private val articleStatusHistoryService: ArticleStatusHistoryService
) {

    @GetMapping("/{articleId}")
    fun getArticleStatusHistory(@PathVariable("articleId") articleId: Long): List<ArticleHistoryDTO> {
        return articleStatusHistoryService.getArticleStatusHistory(articleId).map {
            ArticleHistoryDTO(
                it.timestamp,
                it.fromStatus,
                it.toStatus,
            )
        }
    }

    data class ArticleHistoryDTO(
        val timestamp: LocalDateTime,
        val fromStatus: String,
        val toStatus: String,
    )
}