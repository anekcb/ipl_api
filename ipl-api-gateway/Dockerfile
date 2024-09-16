FROM eclipse-temurin:22-alpine

COPY target/ipl-api-gateway-0.0.1.jar app.jar
EXPOSE 7000
ENV MATCH_API_URI=host.docker.internal:9000
ENTRYPOINT ["java", "-jar", "/app.jar"]


