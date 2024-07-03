package top.hualuo.chatgptwechat4j.utils;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.*;

import javax.imageio.ImageIO;

/**
 * @author quentin
 * @date 2024/7/1 上午10:27
 */
public class ImageToAscii {
    private BufferedImage img;
    private double pixval;
    private PrintWriter prntwrt;
    private FileWriter filewrt;


    public void convertToAscii(InputStream inputStream) {
        try {
            img = ImageIO.read(inputStream);
        } catch (IOException e) {
        }

        for (int i = 0; i < img.getHeight(); i++) {
            for (int j = 0; j < img.getWidth(); j++) {
                Color pixcol = new Color(img.getRGB(j, i));
                pixval = (((pixcol.getRed() * 0.30) + (pixcol.getBlue() * 0.59) + (pixcol.getGreen() * 0.11)));
                print(strChar(pixval));
            }
        }
    }

    public String strChar(double g) {
        String str = " ";
        if (g >= 240) {
            str = " ";
        } else if (g >= 210) {
            str = ".";
        } else if (g >= 190) {
            str = "*";
        } else if (g >= 170) {
            str = "+";
        } else if (g >= 120) {
            str = "^";
        } else if (g >= 110) {
            str = "&";
        } else if (g >= 80) {
            str = "8";
        } else if (g >= 60) {
            str = "#";
        } else {
            str = "@";
        }
        return str;
    }

    public void print(String str) {
        try {
            prntwrt.print(str);
            prntwrt.flush();
            filewrt.flush();
        } catch (Exception ex) {
        }
    }

}
