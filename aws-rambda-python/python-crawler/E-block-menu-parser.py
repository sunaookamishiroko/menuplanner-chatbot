import json
import pandas as pd
import requests
from urllib import parse

def lambda_handler(event, context):
    fileName = event["queryStringParameters"]["fileName"]
    downloadUrl = 'http://contents.kpu.ac.kr/Download/engine_host=ibook.kpu.ac.kr&bookcode=HLDE4UJWP2VS&file_name={0}&file_no=1'.format(parse.quote(fileName))

    with open('/tmp/temp.xlsx', "wb") as file:
        response = requests.get(downloadUrl)
        file.write(response.content)

    df = pd.read_excel('/tmp/temp.xlsx')
    df = df.fillna('0')

    menuIndex = []
    menu = {}
    month = ''

    for i in range(len(df.columns)):
        for j in range(len(df.index)):
            temp = str(df.iloc[j, i])
            if temp.find('일') != -1:
                menuIndex.append((j + 1, i, month + ' ' + temp))
                break
            elif temp.find('월') != -1:
                month += temp.replace(' ', '')
                break

    for start, i, day in menuIndex:
        count = 0
        signal = False
        lunch = []
        dinner = []
        for j in range(start, len(df.index)):
            temp = str(df.iloc[j, i])
            if temp == '0':
                if count == 2:
                    break
                if signal:
                    signal = False
            else:
                if not signal:
                    count += 1
                    signal = True
                if count == 1:
                    lunch.append(temp)
                else:
                    dinner.append(temp)

        menu[day + " 중식"] = lunch
        menu[day + " 석식"] = dinner

    return {
        'statusCode': 200,
        'body': json.dumps(menu, ensure_ascii=False)
    }
