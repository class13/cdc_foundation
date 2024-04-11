package at.tailor.cdc.foundation.service.web.category.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CategoryRepository: JpaRepository<CategoryEntity, Long>