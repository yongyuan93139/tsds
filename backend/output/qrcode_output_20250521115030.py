#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import requests
from bs4 import BeautifulSoup

def process_url():
    """处理二维码中的URL"""
    url = "http://localhost:3001/parts/detail/TL01"
    try:
        response = requests.get(url)
        response.raise_for_status()

        # 解析HTML内容
        soup = BeautifulSoup(response.text, 'html.parser')
        title = soup.title.string if soup.title else "No title"

        print(f"URL: {url}")
        print(f"Title: {title}")
        print(f"Status: {response.status_code}")

        # 返回页面内容
        return response.text
    except Exception as e:
        print(f"Error processing URL: {str(e)}")
        return None

if __name__ == "__main__":
    process_url()
