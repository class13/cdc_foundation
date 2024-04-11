package at.tailor.cdc.foundation.service.web.category.service

import at.tailor.cdc.foundation.service.web.category.persistence.CategoryEntity
import at.tailor.cdc.foundation.service.web.category.rest.CategoryDTO

data class Category(
    val id: Long,
    val name: String,
) {
    companion object {
        fun of(entity: CategoryEntity): Category =
            Category(
                entity.id,
                entity.name,
            )
    }
}
