package at.tailor.cdc.foundation.service

import at.tailor.cdc.foundation.service.usecase.dwh.aggregates.persistence.ArticleRepository
import at.tailor.cdc.foundation.service.usecase.dwh.aggregates.persistence.CategoryEntity
import at.tailor.cdc.foundation.service.usecase.dwh.aggregates.persistence.CategoryRepository
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull

@SpringBootTest
class CDCFoundationServiceApplicationTests {

    @Test
    fun contextLoads() {

    }

}
