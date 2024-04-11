package at.tailor.cdc.foundation.service.web.category.rest

import at.tailor.cdc.foundation.service.web.category.service.Category

data class CategoryDTO(
    val id: Long,
    val name: String,
) {
    fun toServiceModel(): Category {
        return Category(
            id,
            name
        )
    }

    companion object {
        fun of(category: Category): CategoryDTO {
            return CategoryDTO(
                category.id,
                category.name,
            )
        }
    }
}