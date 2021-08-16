package com.kongqw.serialportlibrary;

import com.kongqw.serialportlibrary.desk.Constants;

import java.util.Formatter;

/**
 * Description:
 * Created by loctek on 2021/8/2.
 */
public class SerialPortUtil {
    public static byte HEAD = (byte) 0x9B;
    public static byte FOOT = (byte) 0x9D;
    /**
     * 在body字节数组前后分别添加头字节后尾字节
     *
     * @param data
     * @return
     */
    public static byte[] packageBody(String data) {
        byte[] body = CRC16M.getSendBuf(data);
        byte[] result = new byte[body.length + 2];

        result[0] = HEAD;
        result[result.length - 1] = FOOT;

        for (int i = 0; i < body.length; i++) {
            result[i + 1] = body[i];
        }

        return result;
    }

    // 字节数组按照一定格式转换拼装成字符串用于打印显示
    public static String byte2HexString(byte[] b) {
        int len = b.length;
        StringBuilder sb = new StringBuilder(b.length * (2 + 1));
        Formatter formatter = new Formatter(sb);

        for (int i = 0; i < len; i++) {
            if (i < len - 1)
                formatter.format("%02X", b[i]);
            else
                formatter.format("%02X", b[i]);

        }
        formatter.close();

        return sb.toString();
    }
    /**
     * 16进制字符串转字节数组
     *
     * @param hex
     * @return
     */
    public static byte[] hexStringBytes(String hex) {

        if ((hex == null) || (hex.equals(""))) {
            return null;
        } else if (hex.length() % 2 != 0) {
            return null;
        } else {
            hex = hex.toUpperCase();
            int len = hex.length() / 2;
            byte[] b = new byte[len];
            char[] hc = hex.toCharArray();
            for (int i = 0; i < len; i++) {
                int p = 2 * i;
                b[i] = (byte) (charToByte(hc[p]) << 4 | charToByte(hc[p + 1]));
            }
            return b;
        }

    }
    /**
     * 字符转换为字节
     *
     * @param c
     * @return
     */
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }
    //字节转二进制字符串
    public static String byteToBinary(byte aByte) {
        String text = Integer.toBinaryString(aByte);
        int sub = 8 - text.length();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < sub; i++) {
            sb.append("0");
        }
        sb.append(text);
        return sb.toString();
    }

}
