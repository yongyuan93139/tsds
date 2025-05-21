# 二维码识别与代码生成工具

这个Python脚本可以识别图像中的二维码，并根据二维码内容自动生成相应的文件和代码。

## 功能特点

- 识别图像中的二维码
- 自动解析二维码内容（JSON、URL、特定格式等）
- 根据内容类型自动生成不同语言的代码（Java、Python、JavaScript）
- 支持自定义输出目录和代码类型

## 安装依赖

在使用此脚本前，请确保安装以下依赖：

```bash
pip install opencv-python pyzbar argparse beautifulsoup4 requests
```

注意：在Windows系统上，安装pyzbar可能需要额外步骤：

1. 下载并安装[Visual C++ Redistributable](https://support.microsoft.com/en-us/help/2977003/the-latest-supported-visual-c-downloads)
2. 下载[zbar共享库](https://sourceforge.net/projects/zbar/files/zbar/0.10/)并将其添加到系统PATH

## 使用方法

```bash
python qrcode_processor.py -i <二维码图像路径> [选项]
```

### 命令行参数

- `-i, --image`: 二维码图像路径（必需）
- `-o, --output`: 输出目录路径（默认为"output"）
- `-t, --type`: 生成代码类型，可选值：auto, java, python, js（默认为"auto"）
- `-v, --verbose`: 显示详细信息

## 使用示例

### 基本用法

```bash
python qrcode_processor.py -i qrcode.png

python qrcode_processor.py -i .\TL01.png --open-url -v
```

### 指定输出目录和代码类型

```bash
python qrcode_processor.py -i qrcode.png -o generated_code -t java -v
```

## 支持的二维码内容类型

1. **JSON数据**
   - 自动解析为对象
   - 默认生成JavaScript代码
   - 如果指定Java，会生成Java类

2. **URL**
   - 识别http://, https://, ftp://开头的URL
   - 默认生成Python代码，包含请求处理

3. **特定格式**
   - 支持"part://"格式（用于配件识别）
   - 默认生成Java代码，包含配件处理逻辑

4. **纯文本**
   - 处理其他所有格式
   - 默认生成Python代码

## 输出示例

### JSON数据生成的JavaScript代码

```javascript
// QR Code Data Processor

/**
 * 处理二维码中的JSON数据
 * @returns {Object} 解析后的数据对象
 */
function processQrCodeData() {
    const data = {
        "name": "测试产品",
        "id": 12345,
        "price": 99.99
    };
    
    console.log('QR Code JSON Data:');
    for (const [key, value] of Object.entries(data)) {
        console.log(`${key}: ${value}`);
    }
    
    return data;
}

// 调用处理函数
processQrCodeData();
```

### 配件编码生成的Java代码

```java
package com.example.inspectioncarparts.service.impl;

import com.example.inspectioncarparts.domain.entity.Part;
import com.example.inspectioncarparts.mapper.PartMapper;
import org.springframework.stereotype.Service;

@Service
public class QrCodeServiceImpl implements QrCodeService {
    
    private final PartMapper partMapper;
    
    public QrCodeServiceImpl(PartMapper partMapper) {
        this.partMapper = partMapper;
    }
    
    /**
     * 处理二维码识别到的配件编码
     * @param partCode 配件编码
     * @return 配件信息
     */
    public Part processPartCode(String partCode) {
        // 根据配件编码 ABC123 查询配件信息
        return partMapper.selectByPartCode("ABC123");
    }
}
```

## 注意事项

1. 确保图像清晰，二维码完整可见
2. 对于复杂的二维码内容，可能需要手动调整生成的代码
3. 生成的代码仅作为起点，可能需要根据实际需求进行修改
