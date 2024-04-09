package at.tailor.cdc.foundation.service.usecase.dwh.aggregates.persistence

import at.tailor.cdc.foundation.service.usecase.dwh.aggregates.persistence.ArticleEntity.Companion.TABLE_NAME
import jakarta.persistence.*

@Entity
@Table(name = TABLE_NAME)
data class ArticleEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long,

    @Column(name = "title")
    val title: String,

    @Column(name = "status")
    val status: String,

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
