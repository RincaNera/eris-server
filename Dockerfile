FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
COPY ./data/app.db data/app.db
ENV SERVER_SSL_KEYSTORE_PASSWORD=""
ENV SERVER_SSL_TRUSTSTORE_PASSWORD=""
ENV CONFIG_SERVER_HOST=""
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=dev"]