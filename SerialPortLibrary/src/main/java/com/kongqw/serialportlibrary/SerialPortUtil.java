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


    //分割数据包
    public static byte[][] splitData(byte[] data) {
        String originData = byte2HexString(data);
        String replaceStr = originData.toUpperCase();
        //处理转义字符
        replaceStr = replaceStr.replace("5C9B", "@");

        if (replaceStr.contains("5C9D9B")) {
            //这种情况就是校验位最后一位是5C的情况，这种情况5C9D就不需要转义
        } else {
            replaceStr = replaceStr.replace("5C9D", "#");
        }
        String[] splits = replaceStr.split("9B");
        if (splits.length == 0) {
            return new byte[0][];
        }
        byte[][] result = new byte[splits.length][];
        for (int i = 0; i < splits.length; i++) {
            String item = "";
            if (i == 0) {
                if (originData.startsWith("9B")) {
                    item = "9B" + splits[i];
                }
            } else {
                item = "9B" + splits[i];
            }

            if (item.startsWith("9B") && item.endsWith("9D")) {
                item = item.replace("@", "9B");
                item = item.replace("#", "9D");
                result[i] = hexStringBytes(item);
            }
        }

        return result;
    }
}
