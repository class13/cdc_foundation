FROM openjdk:19-jdk-alpine
COPY gradle gradle
COPY gradlew gradlew
RUN ./gradlew -version
COPY . .
RUN ./gradlew build
CMD ./gradlew bootRun