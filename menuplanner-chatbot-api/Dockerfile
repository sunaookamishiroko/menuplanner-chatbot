FROM openjdk:11
ARG JAR_FILE_PATH=./build/libs/menuplanner-chatbot-api-1.5.0-RELEASE.jar
COPY ${JAR_FILE_PATH} menuplanner-chatbot-api-1.5.0-RELEASE.jar
ENTRYPOINT ["java", "-jar", "-Duser.timezone=Asia/Seoul", "./menuplanner-chatbot-api-1.5.0-RELEASE.jar"]