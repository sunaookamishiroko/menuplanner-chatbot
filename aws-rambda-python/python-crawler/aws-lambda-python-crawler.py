import json
from bs4 import BeautifulSoup
from selenium import webdriver
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.common.by import By
from selenium.webdriver.chrome.options import Options
import os

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

    new = ''
    soup = BeautifulSoup(html, 'html.parser')
    for li in soup.select('#raw-download > div.content > div > div:nth-child(1)'):
        new = li.text

    return {
        'statusCode': 200,
        'body': json.dumps({
            "message" : new
        })
    }