# menuplanner-chatbot-api

## Description

spring boot로 만든 REST api 서버입니다.

## Project structure

```
madeby.seoyun.menuplannerchatbotapi
    |
    +- MenuplannerChatbotApplication.java
    |
    +- component
    |   +- CommandApplicationRunner.java
    |   +- GetDateTime.java
    |   +- LogData.java
    |   +- ParsingMenu.java
    |   +- SettingProperty.java
    |
    +- config
    |   +- ClockConfig.java
    |   +- WebClientConfig.java
    |
    +- controller
    |   +- EblockMenuController.java
    |   +- TipMenuController.java
    |
    +- exceptions
    |   +- CustomizedResponseEntityExceptionHandler.java
    |   +- DatabaseConnectFailedException.java
    |   +- ParsingDataFailedException.java
    |   +- WrongCommandException.java
    |
    +- model
    |   +- RestaurantFileName.java
    |   +- RestaurantMenu.java
    |   +- RestaurantProperty.java
    |   +- ServerInfo.java
    |
    +- repository
    |   +- RestaurantFileNameRepository.java
    |   +- RestaurantMenuRepository.java
    |   +- RestaurantPropertyRepository.java
    |   +- ServerInfoRepository.java
    |
    +- service
        +- DefaultMessageService.java
        +- EblockMenuService.java
        +- TipMenuService.java
```

## How it works?

1. 서버는 옵션에 따라 파싱 여부/방학 여부를 따집니다.
2. `--p=true`라면 파싱 시작, `--p=false`라면 파싱하지 않습니다. 적지 않으면 true입니다.
3. `--v=true`라면 방학 모드, `--v=true`라면 학기 모드입니다. 적지 않으면 false입니다.
4. 파싱은 `ParsingMenu.java`의 `getDataAndSaveToDatabase()`메서드에서 실행합니다.
5. 파싱할 때는 시그널을 사용해 DB에 접근하지 못하게 하고, 카카오 챗봇 메시지로 정보 수집중이란 메시지를 보냅니다.
6. 자세한 파싱 과정은 코드 주석과 [이 곳](https://github.com/somewheregreeny/menuplanner-chatbot/tree/main/aws-rambda-python)을 참고해주세요.
7. 이후 요청에 따라 카카오 챗봇 메시지 형식 json에 맞춰 응답합니다.
8. 매주 월요일 0시 0분이 되면, 시그널을 이용해 식단이 업로드되지 않았으니 기다려달라는 메시지를 보냅니다.
9. 월요일 7시 0분이 되면, DB에 저장되어 있는 파일 이름과 파싱한 파일 이름을 10분마다 비교하기 시작합니다.
10. 달라졌다면 식단이 업로드 된 것으로, 파싱을 시작합니다. (tip와 e동 각각 진행됩니다)


## Run server

Docker 사용 X
1. `application.yml`에서 DB, 크롤링 endpoint를 설정합니다.
2. `./gradlew build`로 빌드합니다.
3. `java -jar menuplanner-chatbot-api-x.x.x-RELEASE.jar`로 실행합니다. x.x.x는 버전에 맞게 넣어주면 됩니다.
4. `java -jar menuplanner-chatbot-api-x.x.x-RELEASE.jar --p=true --v=true` 같이 옵션을 설정할 수 있습니다.

Docker 사용 O
1. `application.yml`에서 DB, 크롤링 endpoint를 설정합니다.
2. `./gradlew build`로 빌드합니다.
3. `docker build --platform linux/amd64 -t username/imagename:x.x.x .`로 빌드합니다.
4. `docker run -d --name server -p 8080:8080 username/imagename:x.x.x --p=false --v=true`로 실행합니다.
