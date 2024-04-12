package at.tailor.cdc.foundation.service.web.category.rest

import at.tailor.cdc.foundation.service.web.category.persistence.CreateCategoryDTO
import at.tailor.cdc.foundation.service.web.category.service.CategoryService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/category")
class CategoryController(
    val categoryService: CategoryService,
) {

    @GetMapping
    fun getCategories(): List<CategoryDTO> {
        return categoryService.getCategories().map { CategoryDTO.of(it) }
    }
    @PostMapping
    fun postCategory(@RequestBody category: CreateCategoryDTO): CategoryDTO {
        return categoryService.createCategory(category.toServiceModel()).let { CategoryDTO.of(it) }
    }

    @PutMapping("/{id}")
    fun putCategory(@PathVariable("id") id: Long, @RequestBody category: UpdateCategoryDTO): CategoryDTO {
        return categoryService.updateCategory(category.toServiceModel(id)).let { CategoryDTO.of(it) }
    }
}