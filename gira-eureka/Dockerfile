# 빌드 스테이지
# OpenJDK 17 slim 버전을 사용하여 Gradle 빌드 작업을 수행합니다.
FROM openjdk:17-jdk-slim AS build

# 작업 디렉토리를 /app으로 설정합니다.
WORKDIR /app

# Gradle Wrapper 및 필요한 빌드 파일만 먼저 복사합니다.
# 종속성 캐싱을 활용하기 위해 나머지 소스 코드 복사보다 우선 수행합니다.
COPY gradlew /app/
COPY gradle /app/gradle
COPY build.gradle /app/
COPY settings.gradle /app/

# Gradle Wrapper 파일의 소유자를 루트로 설정하고, 모든 사용자에게 실행 권한을 부여합니다.
# 실행 권한이 없으면 "Permission Denied" 오류가 발생합니다.
RUN chown root:root ./gradlew
RUN chmod +x ./gradlew

# Gradle의 종속성 다운로드를 먼저 수행하여 캐싱을 활용합니다.
RUN ./gradlew dependencies --no-daemon

# 전체 소스 코드를 복사합니다.
COPY . /app

# Gradle 빌드를 수행합니다.
# 테스트는 실행하지 않도록 설정(-x test).
RUN ./gradlew clean build -x test

# 실행 스테이지
# 빌드 결과물을 실행할 환경을 설정합니다.
# OpenJDK 17 slim 버전을 사용하여 실행 환경을 최소화합니다.
FROM openjdk:17-jdk-slim

# 작업 디렉토리를 /app으로 설정합니다.
WORKDIR /app

# 빌드 스테이지에서 생성된 JAR 파일을 실행 스테이지로 복사합니다.
COPY --from=build /app/build/libs/*.jar app.jar

# 컨테이너가 실행되면 Spring Boot 애플리케이션을 실행합니다.
ENTRYPOINT ["java", "-jar", "app.jar"]
