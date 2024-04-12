package at.tailor.cdc.foundation.service.web.category.service

import at.tailor.cdc.foundation.service.web.category.persistence.CategoryEntity
import at.tailor.cdc.foundation.service.web.category.persistence.CategoryRepository
import jakarta.transaction.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class CategoryService(
    val categoryRepository: CategoryRepository
){
    @Transactional
    fun getCategories(): List<Category> {
        return categoryRepository.findAll().map { Category.of(it) }
    }

    @Transactional
    fun createCategory(category: CreateCategoryModel): Category {
        var entity = CategoryEntity(
            name = category.name
        )
        entity = categoryRepository.save(entity)
        return Category.of(entity)
    }

    @Transactional
    fun updateCategory(category: UpdateCategoryModel): Category {
        val entity = categoryRepository.findByIdOrNull(category.id)!!
        entity.name = category.name
        categoryRepository.save(entity)
        return Category.of(entity)
    }

    fun getCategory(id: Long): Category {
        val entity = categoryRepository.findByIdOrNull(id) ?: throw ServiceModelNotFoundException()
        return entity.let { Category.of(it) }
    }


}