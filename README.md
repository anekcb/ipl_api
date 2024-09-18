**IPL Match Backend API**
This project provides a Spring Boot API for IPL match data management.

docker hub repo pull for IPL: docker pull anekcb/ipl:0.0.1

docker hub repo pull for SQL(port = 1000) : docker pull anekcb/ipl_database:0.0.1

**Completed**
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
 
 ▶ Unit and integration tests. (done without auth)
 
 ✔ Create dev, QA.
 
 ✔ Cache setup (redis) to reduce DB hits, with cache invalidation on new uploads.

 ✔ Log API calls to Kafka topic match-logs-topic.

 ▶ Add an API Gateway (in local).
 
 ✔ Containerize and set up Kubernetes.

 ✔ dockerised SQL

 ✔ hosted in docker hub (IPL and SQL)
 
 ▶ JWT(in clone file) 
 
 ✔ Actuator.

**to-do**
- JWT in the main project
  
- Unit and integration test with JWT
  
- API Gateway (30005)
