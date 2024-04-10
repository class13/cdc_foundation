package at.tailor.cdc.foundation.service.usecase.hook.cdc.router

import at.tailor.cdc.foundation.service.common.cdc.router.CDCMonologEventRouter
import org.springframework.stereotype.Component

/**
 * Configures the `CDCMonologEventListener` to re-reoute all events of the `article` table
 * to the `article_history_hook_events` topic.
 */

@Component
class ArticleHistoryHookRouter: CDCMonologEventRouter {
    companion object {
        const val TARGET_TOPIC = "article_history_hook_events"
    }

    override fun subscribedTables(): Set<String> {
        return setOf("article")
    }

    override fun targetTopic(): String {
        return TARGET_TOPIC
    }
}