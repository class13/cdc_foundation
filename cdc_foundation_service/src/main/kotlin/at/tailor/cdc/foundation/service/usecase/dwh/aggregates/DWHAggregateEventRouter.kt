package at.tailor.cdc.foundation.service.usecase.dwh.aggregates

import at.tailor.cdc.foundation.service.common.cdc.router.CDCMonologEventRouter
import org.springframework.stereotype.Component

/**
 * this just re-routes all article cdc events to the `dwh_article_raw` topic, so it can be used to built aggregates
 */
@Component
class DWHAggregateEventRouter: CDCMonologEventRouter {

    companion object {
        const val TARGET_TOPIC = "dwh_article_raw"
    }
    override fun subscribedTables(): Set<String> {
        return setOf("article")
    }

    override fun targetTopic(): String {
        return TARGET_TOPIC
    }
}