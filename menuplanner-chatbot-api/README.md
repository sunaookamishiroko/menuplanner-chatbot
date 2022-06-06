# menuplanner-chatbot-api

## Description

spring boot로 만든 rest api 서버입니다.

## Project structure

<img height=700 src="/images/menuplanner-chatbot-api-structure.png">

## How it works?

1. 서버는 맨 처음 실행되면 file_name 테이블에 데이터가 존재하는지, 가지고 있는 데이터가 최신인지 판단합니다.
2. 두 요소가 전부 참이면 아무것도 하지 않고, 둘 중에 하나라도 참이 아니라면 데이터 파싱이 시작됩니다.
3. 파싱은 ParsingMenuData.java의 getDataAndSaveToDatabase() 메서드에서 실행합니다.
4. 파싱할 때는 시그널을 사용해 DB에 접근하지 못하게 하고, 카카오 챗봇 메시지로 정보 수집중이란 메시지를 보냅니다.
5. 자세한 파싱 과정은 코드 주석과 [이 곳](https://github.com/somewheregreeny/menuplanner-chatbot/tree/main/aws-rambda-python)을 참고해주세요.
6. 이후 요청에 따라 카카오 챗봇 메시지 형식 json에 맞춰 응답합니다.
7. 매주 월요일 0시 0분이 되면, 시그널을 이용해 식단이 업로드되지 않았으니 기다려달라는 메시지를 보냅니다.
8. 월요일 7시 30분이 되면, DB에 저장되어 있는 파일 이름과 파싱한 파일 이름을 10분마다 비교하기 시작합니다.
9. 달라졌다면 식단이 업로드 된 것으로, 3번 과정부터 다시 시작합니다. (tip와 e동 각각 진행됩니다)


## Run server

1. 프로젝트를 받아서 IDE로 엽니다. (인텔리제이 추천)
2. 라이브러리를 전부 다운 받습니다.
3. ParsingMenuData.java의 getFileName, getEblockMenu, getTipMenu 메서드에서 해당하는 API 주소를 넣습니다.
4. application.yml에서 DB주소를 설정합니다.
5. 실행합니다.
