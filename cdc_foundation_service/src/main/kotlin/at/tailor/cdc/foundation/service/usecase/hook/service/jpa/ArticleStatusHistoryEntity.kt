package at.tailor.cdc.foundation.service.usecase.hook.service.jpa

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "article_status_history")
data class ArticleStatusHistoryEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long? = null,

    @Column(name = "article_id")
    val articleId: Long,

    @Column(name = "from_status")
    val fromStatus: String,

    @Column(name = "to_status")
    val toStatus: String,

    @Column(name = "timestamp")
    val timestamp: LocalDateTime,

)