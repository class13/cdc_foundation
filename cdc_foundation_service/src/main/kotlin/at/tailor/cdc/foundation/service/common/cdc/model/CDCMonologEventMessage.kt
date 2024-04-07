package at.tailor.cdc.foundation.service.common.cdc.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode


// keep the deserialization as flexible as possible
data class CDCMonologEventMessage(
    val source: CDCMonologEventSource,
    val before: JsonNode?,
    val after: JsonNode?,
    val op: String,
    @JsonProperty("ts_ms")
    val tsMs: String,
) {
    val operation: CDCMonologEventOperation
        get() = CDCMonologEventOperation.valueOfKey(op)
    data class CDCMonologEventSource (
        val schema: String,
        val table: String,
    )

    enum class CDCMonologEventOperation(
        val key: String,
    ) {
        DELETE("d"), CREATE("c"), UPDATE("u"), TRUNCATE("t"), SNAPSHOT("r"), MESSAGE("m");

        companion object{
            val keyMap = entries.associateBy { it.key }

            fun valueOfKey(key: String) = keyMap[key]!!
        }


    }

}
