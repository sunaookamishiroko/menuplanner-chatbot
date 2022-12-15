import json
import pandas as pd
import requests
from urllib import parse

import datetime


def lambda_handler(event, context):
    # 파일 이름 파싱
    try:
        fileName = event["queryStringParameters"]["filename"]
        bookCode = event["queryStringParameters"]["bookcode"]
    except Exception as e:
        return {
            'statusCode': 404,
            'body': json.dumps({
                "message": "fileName, bookCode가 존재하지 않거나 잘못됐습니다.",
                "error": str(e)
            }, ensure_ascii=False)
        }

    # xlsx 파일 다운로드 주소 만들기
    downloadUrl = "http://contents.kpu.ac.kr/Download/engine_host=ibook.kpu.ac.kr&bookcode={0}&file_name={1}&file_no=1" \
        .format(bookCode, parse.quote(fileName))

    # 파일 다운 받아서 저장하기
    try:
        with open('/tmp/temp.xlsx', "wb") as file:
            response = requests.get(downloadUrl)
            file.write(response.content)
    except Exception as e:
        return {
            'statusCode': 500,
            'body': json.dumps({
                "message": "파일을 다운받아 저장하는데 실패했습니다.",
                "error": str(e)
            }, ensure_ascii=False)
        }

    # 파일 열기
    try:
        df = pd.read_excel('/tmp/temp.xlsx')
    except Exception as e:
        return {
            'statusCode': 500,
            'body': json.dumps({
                "message": "xlsx 파일 열기에 실패했습니다.",
                "error": str(e)
            }, ensure_ascii=False)
        }

    # Nan 값을 '0'으로 채우기
    df = df.fillna('0')

    # menuIndex : 메뉴가 존재하는 열의 index를 담음
    # menu : 메뉴를 저장
    # month : 월을 찾아 저장
    menuIndex = []
    menu = {}
    month = ''

    # 이번 주 월, 일을 가져 온다.
    week = []
    today = datetime.date.today()
    today = today - datetime.timedelta(days=today.weekday())

    for i in range(7):
        week.append(today.month.__str__() + '월 ' + today.day.__str__() + '일')
        today = today + datetime.timedelta(days=1)

    # month와 menuIndex를 찾는 과정
    for i in range(len(df.columns)):
        for j in range(len(df.index)):
            temp = str(df.iloc[j, i])
            if temp.find('일') != -1:
                if temp.find('/') != -1:
                    month = temp[:temp.find('/')] + '월'
                    temp = temp[temp.find('/') + 1:]
                menuIndex.append((j + 1, i, month + ' ' + temp))
                break
            elif temp.find('월') != -1:
                month += temp.replace(' ', '')
                break

    # menuIndex를 바탕으로 menu를 파싱하여 저장하는 과정
    for start, i, day in menuIndex:
        count = 0
        signal = False
        tempdict = {
            "lunch": [],
            "dinner": []
        }

        for j in range(start, len(df.index)):
            temp = str(df.iloc[j, i])
            if temp == '0':
                if count == 1:
                    break
                if signal:
                    signal = False
            else:
                if not signal:
                    count += 1
                    signal = True

                if temp == "미운영":
                    dash = ''
                else:
                    dash = '- '

                if count == 1:
                    tempdict["lunch"].append(dash + temp)

        tempdict["lunch"] = '\n'.join(s for s in tempdict["lunch"])
        tempdict["dinner"] = "미운영"
        menu[day] = tempdict

    # 없는 날짜 수 만큼 미운영 추가
    tempdict = {"lunch": "미운영", "dinner": "미운영"}
    for i in range(len(menu.keys()), len(week)):
        menu[week[i]] = tempdict

    return {
        'statusCode': 200,
        'body': json.dumps(menu, ensure_ascii=False)
    }
