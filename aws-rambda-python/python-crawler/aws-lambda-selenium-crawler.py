from selenium import webdriver
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.common.by import By
from selenium.webdriver.chrome.options import Options
import json
from bs4 import BeautifulSoup

def lambda_handler(event, context):
    # chromium 세팅... 건들면 안됨
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

    # 크롬 드라이버 로드
    driver = webdriver.Chrome(chrome_options=chrome_options, executable_path='/opt/python/bin/chromedriver')

    # E동 / TIP 구분
    classify = event["classify"]
    url = ''

    # 0-> E동
    # 1-> TIP
    if classify == 0:
        url = 'https://ibook.kpu.ac.kr/Viewer/menu01'
    elif classify == 1:
        url = 'https://ibook.kpu.ac.kr/Viewer/menu02'

    driver.get(url)
    WebDriverWait(driver, 20).until(EC.presence_of_all_elements_located((By.CSS_SELECTOR, '#raw-download > div.content > div > div:nth-child(1)')))
    html = driver.page_source
    driver.quit()

    # 식단표 파일 이름 파싱
    fileName = ''
    soup = BeautifulSoup(html, 'html.parser')
    for li in soup.select('#raw-download > div.content > div > div:nth-child(1)'):
        fileName = li.text

    # 파일 이름 리턴
    return {
        'statusCode': 200,
        'body': json.dumps({
            "fileName" : fileName
        }, ensure_ascii=False)
    }