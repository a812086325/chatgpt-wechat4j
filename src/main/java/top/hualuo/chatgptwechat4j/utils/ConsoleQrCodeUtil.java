package top.hualuo.chatgptwechat4j.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.HashMap;
import java.util.Map;

/**
 * @author quentin
 * @date 2024/7/1 上午11:11
 */
public class ConsoleQrCodeUtil {

    /**
     * 解析二维码流并打印到控制台
     * @param content
     */
    public static void printQrCode(String content) {
        // 二维码图片的宽度
        int width = 3;
        // 二维码图片的高度
        int height = 3;

        // 创建二维码配置信息
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);

        try {
            // 生成二维码矩阵
            BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);

            // 将二维码矩阵转换为ASCII艺术
            String asciiArt = bitMatrixToAsciiArt(bitMatrix);

            // 输出ASCII艺术到控制台
            System.out.println(asciiArt);

        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    private static String bitMatrixToAsciiArt(BitMatrix bitMatrix) {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < bitMatrix.getHeight(); y++) {
            for (int x = 0; x < bitMatrix.getWidth(); x++) {
                sb.append(bitMatrix.get(x, y) ? "██" : "  ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}

