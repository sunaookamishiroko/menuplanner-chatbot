import json
import requests
from urllib import parse
from bs4 import BeautifulSoup
from selenium import webdriver
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.common.by import By
from selenium.webdriver.chrome.options import Options


def lambda_handler(event, context):
    chrome_options = Options()
    chrome_options.add_argument('--headless')
    chrome_options.add_argument('--no-sandbox')
    chrome_options.add_argument('--disable-gpu')
    chrome_options.add_argument('--window-size=1280x1696')
    chrome_options.add_argument('--user-data-dir=/tmp/user-data')
    chrome_options.add_argument('--hide-scrollbars')
    chrome_options.add_argument('--enable-logging')
    chrome_options.add_argument('--log-level=0')
    chrome_options.add_argument('--v=99')
    chrome_options.add_argument('--single-process')
    chrome_options.add_argument('--data-path=/tmp/data-path')
    chrome_options.add_argument('--ignore-certificate-errors')
    chrome_options.add_argument('--homedir=/tmp')
    chrome_options.add_argument('--disk-cache-dir=/tmp/cache-dir')
    chrome_options.add_argument('user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/101.0.4951.54 Safari/537.36')
    chrome_options.binary_location = "/opt/python/bin/headless-chromium"

    driver = webdriver.Chrome(chrome_options=chrome_options, executable_path='/opt/python/bin/chromedriver')
    url = 'https://ibook.kpu.ac.kr/Viewer/menu01'

    driver.get(url)
    WebDriverWait(driver, 20).until(EC.presence_of_all_elements_located((By.CSS_SELECTOR, '#raw-download > div.content > div > div:nth-child(1)')))
    html = driver.page_source
    driver.quit()

    fileName = ''

    soup = BeautifulSoup(html, 'html.parser')
    for li in soup.select('#raw-download > div.content > div > div:nth-child(1)'):
        fileName = li.text

    downloadUrl = 'http://contents.kpu.ac.kr/Download/engine_host=ibook.kpu.ac.kr&bookcode=HLDE4UJWP2VS&file_name={0}&file_no=1'.format(parse.quote(fileName))

    pd.set_option('display.max_rows', None)
    pd.set_option('display.max_columns', None)
    with open('./temp.xlsx', "wb") as file:
        response = requests.get(downloadUrl)
        file.write(response.content)
    print(pd.read_excel('./temp.xlsx'))



    return {
        'statusCode': 200,
        'body': json.dumps({
            "message" : fileName
        })
    }