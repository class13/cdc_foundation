### GET ARTICLES
```bash
docker run --network=host --rm appropriate/curl -X GET http://localhost:8080/article
```

### POST ARTICLE
```bash
docker run --network=host --rm appropriate/curl -X POST http://localhost:8080/article \
-H "Content-Type: application/json" \
-d '{
  "title": "title",
  "status": "PUBLISHED",
  "categories": []
}'
```

### PUT ARTICLE
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

### GET ARTICLE
```bash
docker run --network=host --rm appropriate/curl -X GET http://localhost:8080/article/3
```

### GET CATEGORY
```bash
docker run --network=host --rm appropriate/curl -X GET http://localhost:8080/category
```

### POST CATEGORY
```bash
docker run --network=host --rm appropriate/curl -X POST http://localhost:8080/category \
-H "Content-Type: application/json" \
-d '{
  "name": "Sports"
}'
```

### PUT CATEGORY
```bash
docker run --network=host --rm appropriate/curl -X PUT http://localhost:8080/category/3 \
-H "Content-Type: application/json" \
-d '{
  "id": 4,
  "name": "Sports Resort"
}'
```
