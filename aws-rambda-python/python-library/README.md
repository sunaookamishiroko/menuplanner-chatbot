# python-library

사용된 라이브러리 목록들입니다.

- selenium 3.13.0
- requests 2.27.1
- beautifulsoup4 4.11.1
- pandas 1.4.2, pytz 2022.1
- numpy, openpyxl -> aws 레이어 사용

## 상세 정보

### aws-lambda-selenium-crawler.py
- python 3.6
- selenium, beautifulsoup4 사용
- selenium은 https://github.com/ManivannanMurugavel/selenium-python-aws-lambda 레포지토리에서 참조함

### TIP-menu-parser.py & E-block-menu-parser.py
- python 3.8
- pandas, requests, numpy, openpyxl 사용
- pandas는 https://medium.com/swlh/how-to-add-python-pandas-layer-to-aws-lambda-bab5ea7ced4f 참조하여 사용
- numpy는 aws 계층 사용 ( AWSLambda-Python38-SciPy1x )
- openpyxl는 https://github.com/keithrozario/Klayers/blob/master/deployments/python3.8/arns/eu-central-1.csv 에서 찾을 수 있음
- ARN지정 : arn:aws:lambda:ap-northeast-2:770693421928:layer:Klayers-python38-openpyxl:9
