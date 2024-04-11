package at.tailor.cdc.foundation.service.web.category.persistence

import at.tailor.cdc.foundation.service.web.category.persistence.CategoryEntity.Companion.TABLE_NAME
import at.tailor.cdc.foundation.service.web.article.persistence.ArticleEntity
import jakarta.persistence.*

@Entity
@Table(name = TABLE_NAME)
data class CategoryEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long = -1,

    @Column(name = "name")
    var name: String,

    @ManyToMany(mappedBy = "categories")
    val articles: Set<ArticleEntity> = setOf()
) {
    companion object {
        const val TABLE_NAME = "category"
    }
}