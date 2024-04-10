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
docker run --rm edenhill/kafkacat:1.6.0 -C -b 10.0.0.3:9092  -G kafkacat cdc_monolog article_leads advert_history_hook_events dwh_article_raw dwh_article dwh_article_aggregate
```

Listen to aggregate topic:
```bash
docker run -it --network=host edenhill/kcat:1.7.1 -b 10.0.0.3:9092 -G kafkacat4 dwh_article_aggregate
```