package at.tailor.cdc.foundation.service.usecase.dwh.aggregate

import at.tailor.cdc.foundation.service.usecase.dwh.aggregates.persistence.ArticleEntity
import at.tailor.cdc.foundation.service.usecase.dwh.aggregates.persistence.ArticleRepository
import at.tailor.cdc.foundation.service.usecase.dwh.aggregates.persistence.CategoryEntity
import at.tailor.cdc.foundation.service.usecase.dwh.aggregates.persistence.CategoryRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.transaction.support.TransactionTemplate

@SpringBootTest
class DWHAggregateUseCaseTest {

    @Autowired
    lateinit var articleRepository: ArticleRepository
    @Autowired
    lateinit var transactionTemplate: TransactionTemplate
    @Autowired
    lateinit var categoryRepository: CategoryRepository

    @Test
    fun `on create update or delete on article events are sent`() {
        transactionTemplate.propagationBehavior = TransactionTemplate.PROPAGATION_REQUIRES_NEW
        val categoryId = transactionTemplate.execute { action ->
            CategoryEntity(name = "category").let { categoryRepository.save(it) }.id
        }!!
        val articleId = transactionTemplate.execute {
            ArticleEntity(
                -1,
                "title",
                "NEW"
            ).let { articleRepository.save(it) }
                .let { it.id }
        }!!
        Thread.sleep(2000)

        transactionTemplate.execute {
            val articleEntity = articleRepository.findByIdOrNull(articleId)!!
            val categoryEntity = categoryRepository.findByIdOrNull(categoryId)!!
            articleEntity.status = "PUBLISHED"
            articleEntity.categories.add(categoryEntity)
            articleEntity.let { articleRepository.save(it) }
        }
        Thread.sleep(2000)
        transactionTemplate.execute {
            val categoryEntity = categoryRepository.findByIdOrNull(categoryId)!!
            categoryEntity.name = "renamed"
            categoryRepository.save(categoryEntity)
        }
        Thread.sleep(2000)

        transactionTemplate.execute {
            val articleEntity = articleRepository.findByIdOrNull(articleId)!!
            articleEntity.categories.clear()
            articleEntity.let { articleRepository.save(it) }
        }
        Thread.sleep(2000)

        transactionTemplate.execute {
            val articleEntity = articleRepository.findByIdOrNull(articleId)!!
            articleEntity.status = "PUBLISHED"
            articleEntity.let { articleRepository.save(it) }
        }
        Thread.sleep(2000)


        transactionTemplate.execute {
            articleRepository.deleteById(articleId)
        }
    }

}