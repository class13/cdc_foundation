package at.tailor.cdc.foundation.service.web.category.persistence

import at.tailor.cdc.foundation.service.web.category.service.Category
import at.tailor.cdc.foundation.service.web.category.service.CreateCategoryModel

data class CreateCategoryDTO(
    val name: String,
) {
    fun toServiceModel(): CreateCategoryModel {
        return CreateCategoryModel(name)
    }
}