Get all topics:
```bash
kafkacat -b 10.0.0.3:9092 -L
```

Show topic:
```bash
kafkacat -C -b 10.0.0.3:9092 -t cdc_monolog -o beginning
```

Explore all topics:
```bash
 kafkacat -C -b 10.0.0.3:9092  -G kafkacat cdc_monolog article_leads advert_history_hook_events dwh_article_raw dwh_article dwh_article_aggregate
```
