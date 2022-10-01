FROM maven:3.8.2-openjdk-17 as build
WORKDIR /opt
COPY . .
RUN mvn -B clean package
RUN mv target/java-docker-executor.jar app.jar

FROM docker:dind
RUN apk add openjdk17
COPY --from=build /opt/app.jar /opt/
COPY --from=build /opt/workerRun.sh /opt/
RUN mkdir -p /opt/javas
ENTRYPOINT ((sleep 5 && java -jar /opt/app.jar) &) && dockerd
