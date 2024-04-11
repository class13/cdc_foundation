package at.tailor.cdc.foundation.service.web.category.rest

import at.tailor.cdc.foundation.service.web.category.service.UpdateCategoryModel

data class UpdateCategoryDTO(
    val name: String,
) {
    fun toServiceModel(id: Long): UpdateCategoryModel {
        return UpdateCategoryModel(id, name)
    }
}
