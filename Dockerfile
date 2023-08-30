## ====== Build Image ====== ##
FROM maven:3.6.3-openjdk-17 AS build_image

WORKDIR /app

COPY pom.xml .
RUN mvn -e -B dependency:resolve

COPY src ./src
RUN mvn package

## ====== Unit Tests ====== ##
FROM maven:3.6.3-openjdk-17 as unit_tests

# set the working directory to /app
ENV APP_HOME /app
WORKDIR $APP_HOME

COPY --from=build_image $APP_HOME $APP_HOME

RUN mvn test -X

## ====== Deploy Image ====== ##
FROM openjdk:17.0 as release_image

# Workdir creation
WORKDIR /app

COPY --from=build_image /app/target/password_manager.jar .

# Container entrypoint
CMD [ "java", "-jar", "./password_manager.jar" ]