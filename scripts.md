Listen to aggregate topic:
```bash
docker run -it --rm --network=host edenhill/kcat:1.7.1 -b 127.0.0.1:9092 -G kafkacat dwh_article_aggregate
```