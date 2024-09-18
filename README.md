**IPL Match Backend API**
This project provides a Spring Boot API for IPL match data management.

✔ Design DB schema in PostgreSQL based on the provided JSON files.
✔ Entity classes with relationships.
 **Endpoints**:
✔ Upload a JSON file.
✔ Get all matches by player name.
✔ Get cumulative score by player name.
✔ Get scores for matches on a given date.
✔ Paginated list of top batsmen.
✔ Swagger documentation.

 ✔ Create a "dev" branch and push code.
 ▶ Unit and integration tests.(done without auth)
 ✔ Create dev, QA.
 
 ✔ Cache setup (redis) to reduce DB hits, with cache invalidation on new uploads.

 ✔ Log API calls to Kafka topic match-logs-topic.

 ▶ Add an API Gateway (in local).
Part VI
 ✔ Containerize and set up Kubernetes (2 pods).
 ▶ JWT(in clone file) 
 ✔ Actuator.
