# CDC Foundation - Proof of Concept
## Description
This repository includes a working proof of concept for the **CDC Foundation**. This includes setting up all necessary 
services, and an application implementing examples for the CDC use-cases.

## Architecture
[compose.yaml](compose.yaml) contains all necessary services for the **CDC Foundation**. This includes:
- Kafka (including ZooKeeper)
- Kafka Connect
- Postgres
- The `cdc_foundation_service`

The `cdc_foundation_service` module contains a Kotlin application running a REST api 
and several Kafka listeners and producers. All example uses cases are implemented in this module.

## Testing Use-Cases
### Hooks
The example of the hook is to when an article status changes the transition is logged in a seperate table.

##### 1. Create an article:
```bash
docker run --network=host --rm appropriate/curl -X POST http://localhost:8080/article \
-H "Content-Type: application/json" \
-d '{
  "title": "title",
  "status": "NEW",
  "categories": []
}'
```

##### 2. Change that articles status:
```bash
docker run --network=host --rm appropriate/curl -X PUT http://localhost:8080/article/12 \
-H "Content-Type: application/json" \
-d '{
  "id": 1,
  "title": "title",
  "status": "PUBLISHED",
  "categories": []
}'
```
##### 3. Check out the log for that article:
```bash
docker run --network=host --rm appropriate/curl -X GET http://localhost:8080/article/status/history/1
```


### Syncing to DWH
#### Row Level
##### 1. Observe the `dwh_article_row` topic:
```bash
docker run -it --rm --network=host edenhill/kcat:1.7.1 -b 127.0.0.1:9092 -G kafkacat dwh_article_row
```
##### 2. Create an article:
```bash
docker run --network=host --rm appropriate/curl -X POST http://localhost:8080/article \
-H "Content-Type: application/json" \
-d '{
  "title": "title",
  "status": "NEW",
  "categories": []
}'
```
This results in a message in the `dwh_article_row` topic.

##### 3. Update the article:
```bash
docker run --network=host --rm appropriate/curl -X PUT http://localhost:8080/article/2 \
-H "Content-Type: application/json" \
-d '{
  "id": 2,
  "title": "title",
  "status": "PUBLISHED",
  "categories": []
}'
```
This results in a message in the `dwh_article_row` topic.

**Note:**: Something like adding or removing a category to an article will not show up in this topic, because it doesn't
affect the `article` table directly. This can be handled by routing row events of the other tables as well, or by 
creating aggregates.

#### Aggregates
##### 1. Observe the `dwh_article_row` topic:
```bash
docker run -it --rm --network=host edenhill/kcat:1.7.1 -b 127.0.0.1:9092 -G kafkacat dwh_article_aggregate
```

##### 2. Create an article:
```bash
docker run --network=host --rm appropriate/curl -X POST http://localhost:8080/article \
-H "Content-Type: application/json" \
-d '{
  "title": "title",
  "status": "NEW",
  "categories": []
}'
```

##### 3. Create a category:
```bash
docker run --network=host --rm appropriate/curl -X POST http://localhost:8080/category \
-H "Content-Type: application/json" \
-d '{
  "name": "Sports"
}'
```

##### 4. Assign the article to the category:
```bash
docker run --network=host --rm appropriate/curl -X PUT http://localhost:8080/article/3 \
-H "Content-Type: application/json" \
-d '{
  "title": "title",
  "status": "NEW",
  "categories": [1]
}'
```

**Note:** If you, for example, update the title and the categories within one call or add multiple categories during a 
single call there will be multiple identical update events in the target topic. This happens because there are multiple
rows being updated/created on the database and each of these row changes trigger a CDC event. One for updating the title
and one for creating the reference from article to category.
But when the multiple CDC events are being processed the transaction is already committed and only the final state can
be fetched from the database. That is why the events are identical.
Most consumers should be able to handle duplicated events, but if one doesn't, consider building an additional 
state store that is in sync with the CDC events. Kafka Streams is perfect for such a use case.

### Kafka Outbox
#### Observe target topoic
```bash
docker run -it --rm --network=host edenhill/kcat:1.7.1 -b 127.0.0.1:9092 -G kafkacat article_leads
```

#### Send a kafka message per outbox
```bash
docker run --network=host --rm appropriate/curl -X POST http://localhost:8080/kafka/message \
-H "Content-Type: application/json" \
-d '{
  "topic": "article_leads",
  "value": {
    "type": "VIEW",
    "article": 22,
    "user": 32
  },
  "key": {"id": 1} 
}'
```

## Commands
#### Run all services (including kafka, kafka connect, postgres and the example rest service)
```bash
docker compose up
```
Currently when starting all service, it can happen kafka does not start up correctly, because zookeeper is not available yet.

### Observe Kafka Topics
####
```bash
docker run -it --rm --network=host edenhill/kcat:1.7.1 -b 127.0.0.1:9092 -G kafkacat dwh_article_aggregate
```

### REST Calls
#### GET ARTICLES
```bash
docker run --network=host --rm appropriate/curl -X GET http://localhost:8080/article
```

#### POST ARTICLE
```bash
docker run --network=host --rm appropriate/curl -X POST http://localhost:8080/article \
-H "Content-Type: application/json" \
-d '{
  "title": "title",
  "status": "PUBLISHED",
  "categories": []
}'
```

#### PUT ARTICLE
```bash
docker run --network=host --rm appropriate/curl -X PUT http://localhost:8080/article/3 \
-H "Content-Type: application/json" \
-d '{
  "id": 3,
  "title": "title",
  "status": "PUBLISHED",
  "categories": [3,2,1]
}'
```

#### GET ARTICLE
```bash
docker run --network=host --rm appropriate/curl -X GET http://localhost:8080/article/3
```

#### GET CATEGORY
```bash
docker run --network=host --rm appropriate/curl -X GET http://localhost:8080/category
```

#### POST CATEGORY
```bash
docker run --network=host --rm appropriate/curl -X POST http://localhost:8080/category \
-H "Content-Type: application/json" \
-d '{
  "name": "Sports"
}'
```

#### PUT CATEGORY
```bash
docker run --network=host --rm appropriate/curl -X PUT http://localhost:8080/category/3 \
-H "Content-Type: application/json" \
-d '{
  "id": 4,
  "name": "Sports Resort"
}'
```

## Additional References
- https://github.com/conduktor/kafka-stack-docker-compose
- https://docs.confluent.io/platform/current/connect/userguide.html
- https://access.redhat.com/documentation/en-us/red_hat_amq_streams/2.3/html/kafka_configuration_properties/kafka-connect-configuration-properties-str
- https://docs.confluent.io/platform/current/connect/references/restapi.html
- [Install Debezium](https://debezium.io/documentation/reference/stable/install.html)
- [Running Kafka Connect in Docker](https://developer.confluent.io/courses/kafka-connect/docker-containers/)

