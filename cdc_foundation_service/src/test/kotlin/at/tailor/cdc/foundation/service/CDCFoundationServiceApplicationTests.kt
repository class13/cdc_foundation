package at.tailor.cdc.foundation.service

import at.tailor.cdc.foundation.service.usecase.outbox.KafkaService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.UUID

@SpringBootTest
class CDCFoundationServiceApplicationTests {

    @Autowired
    lateinit var kafkaService: KafkaService

    data class ArticleRead (
        val articleId: Long,
        val userUuid: UUID?,
    )

    data class ArticleKey (
        val articleId: Long
    )

    @Test
    fun contextLoads() {
        kafkaService.send(
            "article_leads",
            ArticleKey(1L),
            ArticleRead(1L, UUID.randomUUID())
        )
    }

}
