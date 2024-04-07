Get all topics:
```bash
kafkacat -b 10.0.0.3:9092 -L
```

Show topic:
```bash
kafkacat -C -b 10.0.0.3:9092 -t cdc_monolog -o beginning
```
