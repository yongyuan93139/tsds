#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
测试二维码生成工具

这个脚本用于生成测试用的二维码图像，包括不同类型的内容：
1. JSON数据
2. URL
3. 特定格式（part://）
4. 纯文本
"""

import os
import json
import qrcode
import argparse
from datetime import datetime

def parse_arguments():
    """解析命令行参数"""
    parser = argparse.ArgumentParser(description='测试二维码生成工具')
    parser.add_argument('-o', '--output', default='test_qrcodes', help='输出目录路径')
    return parser.parse_args()

def generate_qrcode(data, filename, output_dir):
    """生成二维码图像"""
    # 创建QR码实例
    qr = qrcode.QRCode(
        version=1,
        error_correction=qrcode.constants.ERROR_CORRECT_L,
        box_size=10,
        border=4,
    )

    # 添加数据
    qr.add_data(data)
    qr.make(fit=True)

    # 创建图像
    img = qr.make_image(fill_color="black", back_color="white")

    # 确保输出目录存在
    os.makedirs(output_dir, exist_ok=True)

    # 保存图像
    filepath = os.path.join(output_dir, filename)
    img.save(filepath)

    return filepath

def main():
    """主函数"""
    args = parse_arguments()
    output_dir = args.output

    # 生成JSON数据二维码
    json_data = {
        "name": "测试产品",
        "id": 12345,
        "price": 99.99,
        "features": ["防水", "耐高温", "抗腐蚀"],
        "manufacturer": {
            "name": "示例制造商",
            "country": "中国"
        }
    }
    json_qrcode = generate_qrcode(
        json.dumps(json_data, ensure_ascii=False),
        "json_qrcode.png",
        output_dir
    )
    print(f"JSON数据二维码已生成: {json_qrcode}")

    # 生成URL二维码
    url = "https://www.example.com/product?id=12345"
    url_qrcode = generate_qrcode(url, "url_qrcode.png", output_dir)
    print(f"URL二维码已生成: {url_qrcode}")

    # 生成特定格式二维码（part://）
    part_code = "part://ABC123XYZ"
    part_qrcode = generate_qrcode(part_code, "part_qrcode.png", output_dir)
    print(f"配件编码二维码已生成: {part_qrcode}")

    # 生成纯文本二维码
    text = "这是一个测试二维码，包含纯文本内容。"
    text_qrcode = generate_qrcode(text, "text_qrcode.png", output_dir)
    print(f"纯文本二维码已生成: {text_qrcode}")

    print("\n所有测试二维码已生成完毕。可以使用qrcode_processor.py脚本进行测试。")
    print("\n示例命令:")
    print(f"python qrcode_processor.py -i {json_qrcode} -v")
    print(f"python qrcode_processor.py -i {part_qrcode} -t java -v")

if __name__ == "__main__":
    main()
