Listen to aggregate topic:
```bash
docker run -it --rm --network=host edenhill/kcat:1.7.1 -b 10.0.0.3:9092 -G kafkacat dwh_article_aggregate
```