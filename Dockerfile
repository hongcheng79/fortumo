FROM openjdk:12-jdk-alpine

ARG APP_VERSION

# we need the bash for the script
RUN apk add bash

RUN mkdir /app
WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring
RUN chown spring:spring /app

USER spring:spring

RUN echo "App Version:${APP_VERSION}"
ADD build/libs/fortumo-${APP_VERSION}.jar /app/.
ADD scripts/start.sh /app/.
ENTRYPOINT ["bash","start.sh"]
