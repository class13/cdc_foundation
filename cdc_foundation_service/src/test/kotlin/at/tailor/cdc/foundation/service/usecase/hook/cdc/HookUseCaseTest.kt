package at.tailor.cdc.foundation.service.usecase.hook.cdc

import at.tailor.cdc.foundation.service.web.article.persistence.ArticleEntity
import at.tailor.cdc.foundation.service.web.article.persistence.ArticleRepository
import at.tailor.cdc.foundation.service.usecase.hook.service.jpa.ArticleStatusHistoryEntity
import at.tailor.cdc.foundation.service.usecase.hook.service.jpa.ArticleStatusHistoryRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.support.TransactionTemplate

@SpringBootTest
class HookUseCaseTest {

    @Autowired
    lateinit var articleRepository: ArticleRepository

    @Autowired
    lateinit var articleStatusHistoryRepository: ArticleStatusHistoryRepository

    @Autowired
    lateinit var transactionTemplate: TransactionTemplate

    @Test
    fun `when an article is changing status the history is tracked`() {
        var articleId: Long = -1
        transactionTemplate.execute { action ->
            var articleEntity = ArticleEntity(
                -1,
                "title",
                "NEW"
            )
            articleEntity = articleRepository.save(articleEntity)
            articleId = articleEntity.id

            articleEntity.status = "PUBLISHED"
            articleRepository.save(articleEntity)
        }

        Thread.sleep(5000)

        transactionTemplate.execute { action ->
            val history: List<ArticleStatusHistoryEntity> = articleStatusHistoryRepository.findByArticleId(articleId)
            assertEquals(1, history.size)
        }


    }
}