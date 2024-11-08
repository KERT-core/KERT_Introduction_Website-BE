FROM bellsoft/liberica-openjdk-alpine:17

CMD ["./gradlew", "clean", "build"]

VOLUME /tmp

ARG JAR_FILE=build/libs/*-SNAPSHOT.jar

ENV DOCKERIZE_VERSION v0.8.0
RUN wget https://github.com/jwilder/dockerize/releases/download/$DOCKERIZE_VERSION/dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz \  
    && tar -C /usr/local/bin -xzvf dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz

COPY ${JAR_FILE} app.jar
COPY docker-entrypoint.sh docker-entrypoint.sh

RUN touch .env

RUN chmod +x docker-entrypoint.sh
ENTRYPOINT ./docker-entrypoint.sh
