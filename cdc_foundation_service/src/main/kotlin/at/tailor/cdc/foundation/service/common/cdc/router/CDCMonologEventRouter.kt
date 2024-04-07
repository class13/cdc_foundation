package at.tailor.cdc.foundation.service.common.cdc.router

interface CDCMonologEventRouter {
    fun subscribedTables(): Set<String>
    fun targetTopic(): String
}
