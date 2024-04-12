package at.tailor.cdc.foundation.service.web.article.service

import at.tailor.cdc.foundation.service.web.article.persistence.ArticleEntity
import at.tailor.cdc.foundation.service.web.article.persistence.ArticleRepository
import at.tailor.cdc.foundation.service.web.common.exception.ServiceIllegalArgumentException
import at.tailor.cdc.foundation.service.web.category.persistence.CategoryRepository
import at.tailor.cdc.foundation.service.web.category.service.CategoryService
import at.tailor.cdc.foundation.service.web.category.service.ServiceModelNotFoundException
import jakarta.transaction.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class ArticleService(
    val categoryService: CategoryService,
    val categoryRepository: CategoryRepository,
    val articleRepository: ArticleRepository,
) {
    @Transactional
    fun createArticle(article: CreateArticleModel): Article {
        val entity = ArticleEntity(
            title = article.title,
            status = article.status,
            categories = article.categoryIds.map { categoryId ->
                try {
                    categoryService.getCategory(categoryId).let { categoryRepository.findByIdOrNull(it.id)!! }
                } catch (e: ServiceModelNotFoundException) {
                    throw ServiceIllegalArgumentException()
                }

            }.toMutableList()
        )
        return articleRepository.save(entity).let { Article.ofEntity(it) }
    }

    @Transactional
    fun getArticles(): List<Article> {
        return articleRepository.findAll().map { Article.ofEntity(it) }
    }

    @Transactional
    fun updateArticle(article: UpdateArticleModel): Article {
        val entity = articleRepository.findByIdOrNull(article.id) ?: throw ServiceModelNotFoundException()
        entity.title = article.title
        entity.status = article.status
        entity.categories = article.categoryIds.map { categoryId ->
            try {
                categoryService.getCategory(categoryId).let { categoryRepository.findByIdOrNull(it.id)!! }
            } catch (e: ServiceModelNotFoundException) {
                throw ServiceIllegalArgumentException()
            }

        }.toMutableList()
        articleRepository.save(entity)
        return entity.let { Article.ofEntity(it) }
    }

    @Transactional
    fun getArticle(id: Long): Article {
        val entity = articleRepository.findByIdOrNull(id) ?: throw ServiceModelNotFoundException()
        return entity.let { Article.ofEntity(it) }
    }

}
