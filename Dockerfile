
FROM openjdk:17 as builder


RUN chmod +x ./gradlew

RUN ./gradlew clean build

FROM openjdk:17

COPY build/libs/*.jar app.jar

EXPOSE 8085

ENTRYPOINT ["java","-jar","/app.jar"]   // jar 파일 실행