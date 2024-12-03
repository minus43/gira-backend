# 빌드 스테이지
FROM openjdk:17-jdk-slim AS build
WORKDIR /app

# 소스 코드 복사 및 빌드
COPY . .
RUN chmod +x ./gradlew
RUN ./gradlew clean build -x test

# 실행 스테이지
FROM openjdk:17-jdk-slim
WORKDIR /app

# curl openssh-client설치
RUN apt-get update && apt-get install -y curl && apt-get clean


# 빌드된 JAR 파일 복사
COPY --from=build /app/build/libs/*.jar app.jar

# Spring Boot 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]