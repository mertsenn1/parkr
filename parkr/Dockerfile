FROM openjdk:17-jdk
VOLUME /tmp
COPY ./parkr/target/parkr-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app/app.jar"]