package at.tailor.cdc_foundation_service

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CDCFoundationServiceApplication

fun main(args: Array<String>) {
    runApplication<CDCFoundationServiceApplication>(*args)
}
