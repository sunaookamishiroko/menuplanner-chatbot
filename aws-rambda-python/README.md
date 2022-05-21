# aws-rambda-python

## Description

파일 이름과 메뉴 엑셀 파일을 다운 받아 파싱하기 위한 파이썬 함수입니다.

## Architecture

<img src="/images/aws-rambda-python_architecture.png">

## How it works?

- 파이썬 함수들은 전부 AWS lambda에 업로드되어 있으며, API gateway를 설정하여 API 역할을 수행중입니다.
현재 spring 서버와 lambda 함수는 다음과 같은 순서로 동작합니다.
1. spring 서버가 파일 이름 파싱 API에 요청을 보냅니다. 그러면 파이썬 코드에서 selenium으로 tip와 e동의 파일 이름을 각각 파싱해서 json으로 응답합니다. (각각 따로 한번씩 총 두번 요청)
2. spring 서버가 파일 이름을 받습니다. 그 후에 파일 이름을 포함해 TIP 식당 메뉴 파싱 API에 요청을 보냅니다. 그러면 파이썬 코드에서 해당 엑셀 파일을 다운 받아 파싱하여 json으로 응답합니다.
3. spring 서버가 마지막으로 파일 이름을 포함해 E동 식당 메뉴 파싱 API에 요청을 보냅니다. 그러면 파이썬 코드에서 해당 엑셀 파일을 다운 받아 파싱하여 json으로 응답합니다.
## Run code

- 코드들을 lambda에서 실행하기 위해서는 라이브러리를 lambda 레이어에 설정해야합니다.
- 라이브러리 정보들은 python-library 디렉토리를 참고해주세요.
- 추후 블로그에 글을 작성할 예정입니다..
