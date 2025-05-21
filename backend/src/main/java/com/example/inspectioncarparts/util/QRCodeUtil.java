package com.example.inspectioncarparts.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * 二维码生成工具类
 */
public class QRCodeUtil {

    /**
     * 生成带有配件编号的二维码
     * @param content 二维码内容
     * @param partCode 配件编号
     * @param width 二维码宽度
     * @param height 二维码高度
     * @return Base64编码的二维码图像
     */
    public static String generateQRCode(String content, String partCode, int width, int height) {
        try {
            // 创建二维码生成器
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            
            // 设置二维码参数
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H); // 高纠错级别，为了在中间放置文字
            hints.put(EncodeHintType.MARGIN, 1);
            
            // 生成二维码位图
            BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);
            
            // 转换为BufferedImage
            BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
            
            // 在二维码中间添加配件编号
            BufferedImage finalImage = addTextToCenter(qrImage, partCode, width, height);
            
            // 转换为Base64
            return toBase64(finalImage);
            
        } catch (WriterException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 在二维码中间添加文本
     * @param qrImage 原始二维码图像
     * @param text 要添加的文本
     * @param width 图像宽度
     * @param height 图像高度
     * @return 添加文本后的图像
     */
    private static BufferedImage addTextToCenter(BufferedImage qrImage, String text, int width, int height) {
        // 创建一个带有透明度的BufferedImage
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        
        // 获取Graphics2D对象
        Graphics2D g2d = image.createGraphics();
        
        // 绘制原始二维码
        g2d.drawImage(qrImage, 0, 0, null);
        
        // 计算中心区域
        int centerSize = Math.min(width, height) / 4;
        int centerX = (width - centerSize) / 2;
        int centerY = (height - centerSize) / 2;
        
        // 绘制白色背景
        g2d.setColor(Color.WHITE);
        g2d.fillRect(centerX, centerY, centerSize, centerSize);
        
        // 设置文本属性
        g2d.setColor(Color.BLACK);
        // 动态调整字体大小以适应中心区域
        int fontSize = centerSize / (text.length() > 4 ? text.length() / 2 : 2);
        Font font = new Font("Arial", Font.BOLD, fontSize);
        g2d.setFont(font);
        
        // 计算文本位置
        FontMetrics metrics = g2d.getFontMetrics(font);
        int textWidth = metrics.stringWidth(text);
        int textHeight = metrics.getHeight();
        int textX = centerX + (centerSize - textWidth) / 2;
        int textY = centerY + ((centerSize - textHeight) / 2) + metrics.getAscent();
        
        // 绘制文本
        g2d.drawString(text, textX, textY);
        
        // 释放资源
        g2d.dispose();
        
        return image;
    }
    
    /**
     * 将BufferedImage转换为Base64编码
     * @param image 图像
     * @return Base64编码的图像
     * @throws IOException 如果转换过程中发生错误
     */
    private static String toBase64(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", baos);
        byte[] imageBytes = baos.toByteArray();
        return "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes);
    }
    
    /**
     * 生成带有配件编号的二维码（使用默认尺寸）
     * @param content 二维码内容
     * @param partCode 配件编号
     * @return Base64编码的二维码图像
     */
    public static String generateQRCode(String content, String partCode) {
        return generateQRCode(content, partCode, 300, 300);
    }
}
