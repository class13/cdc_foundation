package at.tailor.cdc.foundation.service.web.category.rest

import at.tailor.cdc.foundation.service.web.category.persistence.CreateCategoryDTO
import at.tailor.cdc.foundation.service.web.category.service.CategoryService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/category")
class CategoryController(
    val categoryService: CategoryService,
) {

    @GetMapping
    fun getCategories(): List<CategoryDTO> {
        return categoryService.getCategories().map { CategoryDTO.of(it) }
    }
    @PostMapping
    fun postCategory(createCategoryDTO: CreateCategoryDTO): CategoryDTO {
        return categoryService.createCategory(createCategoryDTO.toServiceModel()).let { CategoryDTO.of(it) }
    }

    @PutMapping("/{:id}")
    fun putCategory(@PathVariable("id") id: Long, updateCategoryDTO: UpdateCategoryDTO): CategoryDTO {
        return categoryService.updateCategory(updateCategoryDTO.toServiceModel(id)).let { CategoryDTO.of(it) }
    }
}