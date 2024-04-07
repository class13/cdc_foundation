package at.tailor.cdc.foundation.service

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CDCFoundationServiceApplication

fun main(args: Array<String>) {
    runApplication<CDCFoundationServiceApplication>(*args)
    // todo: needs an event listener
    // todo: needs something that creates events
}
