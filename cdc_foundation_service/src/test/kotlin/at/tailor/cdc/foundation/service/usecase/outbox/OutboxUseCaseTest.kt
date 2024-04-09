package at.tailor.cdc.foundation.service.usecase.outbox

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.*

@SpringBootTest
class OutboxUseCaseTest {

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
    fun `send article read event through kafka outbox`() {
        kafkaService.send(
            "article_leads",
            ArticleKey(1L),
            ArticleRead(1L, UUID.randomUUID())
        )
    }

}