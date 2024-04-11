package at.tailor.cdc.foundation.service.web.article.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ArticleRepository: JpaRepository<ArticleEntity, Long>