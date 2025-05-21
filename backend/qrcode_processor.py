#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
二维码识别与代码生成工具

这个脚本可以：
1. 识别图像中的二维码
2. 解析二维码内容
3. 根据二维码内容自动生成文件和代码
"""

import os
import sys
import argparse
import json
import cv2
import webbrowser
import re
from pyzbar.pyzbar import decode
from datetime import datetime

def parse_arguments():
    """解析命令行参数"""
    parser = argparse.ArgumentParser(description='二维码识别与代码生成工具')
    parser.add_argument('-i', '--image', required=True, help='二维码图像路径')
    parser.add_argument('-o', '--output', default='output', help='输出目录路径')
    parser.add_argument('-t', '--type', default='auto', choices=['auto', 'java', 'python', 'js'],
                        help='生成代码类型，默认为自动识别')
    parser.add_argument('-v', '--verbose', action='store_true', help='显示详细信息')
    parser.add_argument('--open-url', action='store_true', 
                       help='如果是URL则自动在浏览器中打开')
    parser.add_argument('--no-open-url', action='store_true', 
                       help='禁止自动打开URL（即使检测到URL）')
    return parser.parse_args()

def read_qrcode(image_path):
    """读取图像中的二维码"""
    try:
        # 读取图像
        image = cv2.imread(image_path)
        if image is None:
            print(f"错误: 无法读取图像 {image_path}")
            return None

        # 解码二维码
        decoded_objects = decode(image)
        if not decoded_objects:
            print("错误: 未在图像中检测到二维码")
            return None

        # 返回第一个二维码的内容
        qr_data = decoded_objects[0].data.decode('utf-8')
        return qr_data

    except Exception as e:
        print(f"二维码读取错误: {str(e)}")
        return None

def parse_qrcode_content(content):
    """解析二维码内容"""
    try:
        # 尝试解析为JSON
        data = json.loads(content)
        return data, 'json'
    except json.JSONDecodeError:
        pass

    # 检查是否为URL
    # 使用更强大的URL检测正则表达式
    url_pattern = re.compile(
        r'^(?:http|https|ftp)s?://'  # http://, https://, ftp://
        r'(?:(?:[A-Z0-9](?:[A-Z0-9-]{0,61}[A-Z0-9])?\.)+(?:[A-Z]{2,6}\.?|[A-Z0-9-]{2,}\.?)|'  # 域名
        r'localhost|'  # localhost
        r'\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3})'  # IP地址
        r'(?::\d+)?'  # 可选端口
        r'(?:/?|[/?]\S+)$', re.IGNORECASE)
    
    if url_pattern.match(content) or content.startswith(('http://', 'https://', 'ftp://')):
        return content, 'url'

    # 检查是否为特定格式
    if content.startswith('part://'):
        return content[7:], 'part'

    # 默认作为纯文本处理
    return content, 'text'

def open_url_in_browser(url):
    """在默认浏览器中打开URL"""
    try:
        print(f"正在打开URL: {url}")
        webbrowser.open(url)
        return True
    except Exception as e:
        print(f"打开URL失败: {str(e)}")
        return False

def determine_code_type(content_type, user_type):
    """确定要生成的代码类型"""
    if user_type != 'auto':
        return user_type

    # 根据内容类型自动确定代码类型
    if content_type == 'json':
        return 'js'
    elif content_type == 'part':
        return 'java'
    else:
        return 'python'  # 默认生成Python代码

def generate_code(data, content_type, code_type):
    """根据数据和类型生成代码"""
    if code_type == 'java':
        return generate_java_code(data, content_type)
    elif code_type == 'python':
        return generate_python_code(data, content_type)
    elif code_type == 'js':
        return generate_js_code(data, content_type)
    else:
        return f"// 未支持的代码类型: {code_type}"

def generate_java_code(data, content_type):
    """生成Java代码"""
    if content_type == 'part':
        # 生成配件相关的Java代码
        java_code = f"""package com.example.inspectioncarparts.service.impl;

import com.example.inspectioncarparts.domain.entity.Part;
import com.example.inspectioncarparts.mapper.PartMapper;
import org.springframework.stereotype.Service;

@Service
public class QrCodeServiceImpl implements QrCodeService {{

    private final PartMapper partMapper;

    public QrCodeServiceImpl(PartMapper partMapper) {{
        this.partMapper = partMapper;
    }}

    /**
     * 处理二维码识别到的配件编码
     * @param partCode 配件编码
     * @return 配件信息
     */
    public Part processPartCode(String partCode) {{
        // 根据配件编码 {data} 查询配件信息
        return partMapper.selectByPartCode("{data}");
    }}
}}
"""
        return java_code

    elif content_type == 'json':
        try:
            # 尝试将JSON转换为Java类
            java_code = "package com.example.model;\n\n"
            java_code += "import lombok.Data;\n\n"
            java_code += "@Data\n"
            java_code += "public class QrCodeData {\n"

            if isinstance(data, dict):
                for key, value in data.items():
                    java_type = "String"
                    if isinstance(value, int):
                        java_type = "Integer"
                    elif isinstance(value, float):
                        java_type = "Double"
                    elif isinstance(value, bool):
                        java_type = "Boolean"

                    java_code += f"    private {java_type} {key};\n"

            java_code += "}\n"
            return java_code
        except:
            return "// 无法从JSON生成Java类"

    else:
        # 默认生成简单的Java处理代码
        return f"""package com.example.qrcode;

public class QrCodeProcessor {{

    public static void main(String[] args) {{
        String qrContent = "{data}";
        System.out.println("QR Code Content: " + qrContent);
        // 在这里添加处理逻辑
    }}
}}
"""

def generate_python_code(data, content_type):
    """生成Python代码"""
    if content_type == 'url':
        return f"""#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import requests
from bs4 import BeautifulSoup

def process_url():
    \"\"\"处理二维码中的URL\"\"\"
    url = "{data}"
    try:
        response = requests.get(url)
        response.raise_for_status()

        # 解析HTML内容
        soup = BeautifulSoup(response.text, 'html.parser')
        title = soup.title.string if soup.title else "No title"

        print(f"URL: {{url}}")
        print(f"Title: {{title}}")
        print(f"Status: {{response.status_code}}")

        # 返回页面内容
        return response.text
    except Exception as e:
        print(f"Error processing URL: {{str(e)}}")
        return None

if __name__ == "__main__":
    process_url()
"""

    elif content_type == 'json':
        return f"""#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import json

def process_json_data():
    \"\"\"处理二维码中的JSON数据\"\"\"
    data_str = '''{json.dumps(data, indent=4)}'''

    try:
        # 解析JSON数据
        data = json.loads(data_str)

        print("JSON数据内容:")
        for key, value in data.items():
            print(f"{{key}}: {{value}}")

        # 返回解析后的数据
        return data
    except json.JSONDecodeError as e:
        print(f"JSON解析错误: {{str(e)}}")
        return None

if __name__ == "__main__":
    process_json_data()
"""

    else:
        return f"""#!/usr/bin/env python3
# -*- coding: utf-8 -*-

def process_qrcode_content():
    \"\"\"处理二维码内容\"\"\"
    content = "{data}"
    print(f"二维码内容: {{content}}")

    # 在这里添加处理逻辑

    return content

if __name__ == "__main__":
    process_qrcode_content()
"""

def generate_js_code(data, content_type):
    """生成JavaScript代码"""
    if content_type == 'json':
        return f"""// QR Code Data Processor

/**
 * 处理二维码中的JSON数据
 * @returns {{Object}} 解析后的数据对象
 */
function processQrCodeData() {{
    const data = {json.dumps(data, indent=4)};

    console.log('QR Code JSON Data:');
    for (const [key, value] of Object.entries(data)) {{
        console.log(`${{key}}: ${{value}}`);
    }}

    return data;
}}

// 调用处理函数
processQrCodeData();
"""

    elif content_type == 'url':
        return f"""// QR Code URL Processor

/**
 * 处理二维码中的URL
 * @returns {{Promise<Object>}} 请求结果
 */
async function processUrl() {{
    const url = "{data}";

    try {{
        console.log(`Fetching URL: ${{url}}`);
        const response = await fetch(url);

        if (!response.ok) {{
            throw new Error(`HTTP error! Status: ${{response.status}}`);
        }}

        const data = await response.text();
        console.log(`Response received, length: ${{data.length}}`);

        return {{
            url,
            status: response.status,
            data
        }};
    }} catch (error) {{
        console.error(`Error processing URL: ${{error.message}}`);
        return null;
    }}
}}

// 使用示例
// processUrl().then(result => console.log(result));
"""

    else:
        return f"""// QR Code Content Processor

/**
 * 处理二维码内容
 * @returns {{string}} 处理结果
 */
function processQrContent() {{
    const content = "{data}";
    console.log(`QR Code Content: ${{content}}`);

    // 在这里添加处理逻辑

    return content;
}}

// 调用处理函数
processQrContent();
"""

def save_to_file(content, output_dir, code_type):
    """保存内容到文件"""
    # 确保输出目录存在
    os.makedirs(output_dir, exist_ok=True)

    # 根据代码类型确定文件扩展名
    extensions = {
        'java': '.java',
        'python': '.py',
        'js': '.js'
    }
    ext = extensions.get(code_type, '.txt')

    # 生成文件名
    timestamp = datetime.now().strftime("%Y%m%d%H%M%S")
    filename = f"qrcode_output_{timestamp}{ext}"
    filepath = os.path.join(output_dir, filename)

    # 写入文件
    with open(filepath, 'w', encoding='utf-8') as f:
        f.write(content)

    return filepath

def main():
    """主函数"""
    # 解析命令行参数
    args = parse_arguments()

    # 读取二维码
    if args.verbose:
        print(f"正在读取二维码图像: {args.image}")

    qr_content = read_qrcode(args.image)
    if not qr_content:
        return 1

    if args.verbose:
        print(f"二维码内容: {qr_content}")

    # 解析二维码内容
    data, content_type = parse_qrcode_content(qr_content)
    if args.verbose:
        print(f"内容类型: {content_type}")

    # 如果是URL，并且用户要求自动打开
    if content_type == 'url':
        if args.open_url and not args.no_open_url:
            if not open_url_in_browser(data):
                return 1
        elif args.verbose:
            print("检测到URL，但未启用自动打开功能")

    # 确定代码类型
    code_type = determine_code_type(content_type, args.type)
    if args.verbose:
        print(f"代码类型: {code_type}")

    # 生成代码
    generated_code = generate_code(data, content_type, code_type)

    # 保存到文件
    output_file = save_to_file(generated_code, args.output, code_type)
    print(f"代码已生成并保存到: {output_file}")

    return 0

if __name__ == "__main__":
    sys.exit(main())
