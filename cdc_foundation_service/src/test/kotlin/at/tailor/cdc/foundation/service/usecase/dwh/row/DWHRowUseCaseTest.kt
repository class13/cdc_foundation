package at.tailor.cdc.foundation.service.usecase.dwh.row

import at.tailor.cdc.foundation.service.usecase.dwh.aggregates.persistence.ArticleEntity
import at.tailor.cdc.foundation.service.usecase.dwh.aggregates.persistence.ArticleRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.transaction.support.TransactionTemplate

@SpringBootTest
class DWHRowUseCaseTest {

    @Autowired
    lateinit var articleRepository: ArticleRepository
    @Autowired
    lateinit var transactionTemplate: TransactionTemplate

    @Test
    fun `on create update or delete on article events are sent`() {
        val articleId = transactionTemplate.execute {
            ArticleEntity(
                -1,
                "title",
                "NEW"
            ).let { articleRepository.save(it) }
                .let { it.id }
        }!!
        transactionTemplate.execute {
            val articleEntity = articleRepository.findByIdOrNull(articleId)!!
            articleEntity.status = "PUBLISHED"
            articleEntity.let { articleRepository.save(it) }
        }

        transactionTemplate.execute {
            articleRepository.deleteById(articleId)
        }
    }

}