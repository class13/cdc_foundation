package at.tailor.cdc.foundation.service.usecase.dwh.row

import at.tailor.cdc.foundation.service.common.cdc.router.CDCMonologEventRouter
import org.springframework.stereotype.Component

/**
 * As any further aggregation is done in the DWH itself,
 * this just re-routes all article cdc events to the `dwh_article` topic.
 */
@Component
class DWHRowEventRouter: CDCMonologEventRouter {
    override fun subscribedTables(): Set<String> {
        return setOf("article")
    }

    override fun targetTopic(): String {
        return "dwh_article"
    }
}