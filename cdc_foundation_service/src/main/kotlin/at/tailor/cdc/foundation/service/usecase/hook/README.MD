# Use Case: Hook
As an example this hook will keep a history record for every status change is made for an article.

The `ArticleHistoryHookRouter` configures the `CDCMonologEventListener` to re-reoute all events of the `article` table
to the `advert_history_hook_events` topic.

The `ArticleHistoryHookEventListener` listens to the `advert_history_hook_events` topic and executes the hook, but only
if the event is relevant. An event is relevant if the status was updated.
