FROM openjdk:11
ARG JAR_FILE_NAME=menuplanner-chatbot-api-2.0.0-RELEASE.jar
ARG JAR_FILE_PATH=./build/libs/${JAR_FILE_NAME}
COPY ${JAR_FILE_PATH} ${JAR_FILE_NAME}
ENTRYPOINT ["java", "-jar", "-Duser.timezone=Asia/Seoul", "./menuplanner-chatbot-api-2.0.0-RELEASE.jar"]