package at.tailor.cdc.foundation.service.usecase.dwh.aggregates.persistence

import at.tailor.cdc.foundation.service.usecase.dwh.aggregates.persistence.CategoryEntity.Companion.TABLE_NAME
import jakarta.persistence.*

@Entity
@Table(name = TABLE_NAME)
data class CategoryEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long = -1,

    @Column(name = "name")
    var name: String,

    @ManyToMany(mappedBy = "categories")
    val articles: Set<ArticleEntity> = setOf()
) {
    companion object {
        const val TABLE_NAME = "category"
    }
}