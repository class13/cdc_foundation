package at.tailor.cdc.foundation.service.web.article.persistence

import at.tailor.cdc.foundation.service.web.category.persistence.CategoryEntity
import at.tailor.cdc.foundation.service.web.article.persistence.ArticleEntity.Companion.TABLE_NAME
import jakarta.persistence.*

@Entity
@Table(name = TABLE_NAME)
data class ArticleEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long,

    @Column(name = "title")
    val title: String,

    @Column(name = "status")
    var status: String,

    @ManyToMany
    @JoinTable(
        name = CATEGORIES_JOIN_TABLE_NAME,
        joinColumns = [JoinColumn(name = "article_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "category_id", referencedColumnName = "id")]
    )
    val categories: MutableList<CategoryEntity> = mutableListOf()
) {
    companion object {
        const val TABLE_NAME = "article"
        const val CATEGORIES_JOIN_TABLE_NAME = "article_category"
    }
}
