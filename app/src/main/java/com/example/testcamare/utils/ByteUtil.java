package com.example.testcamare.utils;

import java.nio.charset.StandardCharsets;

/**
 * Author by Winds on 2016/10/18.
 * Email heardown@163.com.
 */
public class ByteUtil {

    //public static void main(String[] args) {
    //    byte[] bytes = {
    //        (byte) 0xab, 0x01, 0x11
    //    };
    //    String hexStr = bytes2HexStr(bytes);
    //    System.out.println(hexStr);
    //    System.out.println(hexStr2decimal(hexStr));
    //    System.out.println(decimal2fitHex(570));
    //    String adc = "abc";
    //    System.out.println(str2HexString(adc));
    //    System.out.println(bytes2HexStr(adc.getBytes()));
    //}

    /**
     * 字节数组转换成对应的16进制表示的字符串
     *
     * @param src
     * @return
     */
    public static String bytes2HexStr(byte[] src) {
        StringBuilder builder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return "";
        }
        char[] buffer = new char[2];
        for (int i = 0; i < src.length; i++) {
            buffer[0] = Character.forDigit((src[i] >>> 4) & 0x0F, 16);
            buffer[1] = Character.forDigit(src[i] & 0x0F, 16);
            builder.append(buffer);
        }
        return builder.toString().toUpperCase();
    }

    /**
     * 十六进制字节数组转字符串
     *
     * @param src    目标数组
     * @param dec    起始位置
     * @param length 长度
     * @return
     */
    public static String bytes2HexStr(byte[] src, int dec, int length) {
        byte[] temp = new byte[length];
        System.arraycopy(src, dec, temp, 0, length);
        return bytes2HexStr(temp);
    }

    /**
     * 16进制字符串转10进制数字
     *
     * @param hex
     * @return
     */
    public static long hexStr2decimal(String hex) {
        return Long.parseLong(hex, 16);
    }

    /**
     * 把十进制数字转换成足位的十六进制字符串,并补全空位
     *
     * @param num
     * @return
     */
    public static String decimal2fitHex(long num) {
        String hex = Long.toHexString(num).toUpperCase();
        if (hex.length() % 2 != 0) {
            return "0" + hex;
        }
        return hex.toUpperCase();
    }

    /**
     * 把十进制数字转换成足位的十六进制字符串,并补全空位
     *
     * @param num
     * @param strLength 字符串的长度
     * @return
     */
    public static String decimal2fitHex(long num, int strLength) {
        String hexStr = decimal2fitHex(num);
        StringBuilder stringBuilder = new StringBuilder(hexStr);
        while (stringBuilder.length() < strLength) {
            stringBuilder.insert(0, '0');
        }
        return stringBuilder.toString();
    }

    public static String fitDecimalStr(int dicimal, int strLength) {
        StringBuilder builder = new StringBuilder(String.valueOf(dicimal));
        while (builder.length() < strLength) {
            builder.insert(0, "0");
        }
        return builder.toString();
    }

    /**
     * 字符串转十六进制字符串
     *
     * @param str
     * @return
     */
    public static String str2HexString(String str) {
        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder();
        byte[] bs = null;
        try {

            bs = str.getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int bit;
        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
        }
        return sb.toString();
    }

    /**
     * 把十六进制表示的字节数组字符串，转换成十六进制字节数组
     *
     * @param
     * @return byte[]
     */
    public static byte[] hexStr2bytes(String hexStr) {
        //去除字符串中的所有空格
        String hex = hexStr.replaceAll("\\s*", "");
        int len = (hex.length() / 2);
        byte[] result = new byte[len];
        char[] achar = hex.toUpperCase().toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (hexChar2byte(achar[pos]) << 4 | hexChar2byte(achar[pos + 1]));
        }
        return result;
    }

    /**
     * 把16进制字符[0123456789abcde]（含大小写）转成字节
     *
     * @param c
     * @return
     */
    private static int hexChar2byte(char c) {
        switch (c) {
            case '0':
                return 0;
            case '1':
                return 1;
            case '2':
                return 2;
            case '3':
                return 3;
            case '4':
                return 4;
            case '5':
                return 5;
            case '6':
                return 6;
            case '7':
                return 7;
            case '8':
                return 8;
            case '9':
                return 9;
            case 'a':
            case 'A':
                return 10;
            case 'b':
            case 'B':
                return 11;
            case 'c':
            case 'C':
                return 12;
            case 'd':
            case 'D':
                return 13;
            case 'e':
            case 'E':
                return 14;
            case 'f':
            case 'F':
                return 15;
            default:
                return -1;
        }
    }


    /**
     * 计算校验位 ，返回十六进制校验位
     */
    public static String makeCheckSum(String data) {
        data = data.replace(" ", "");// 去掉中间空格
        int dSum = 0;
        int length = data.length();
        int index = 0;
        // 遍历十六进制，并计算总和
        while (index < length) {
            String s = data.substring(index, index + 2); // 截取2位字符
            dSum += Integer.parseInt(s, 16); // 十六进制转成十进制 , 并计算十进制的总和
            index = index + 2;
        }

        int mod = dSum % 256; // 用256取余，十六进制最大是FF，FF的十进制是255
        String checkSumHex = Integer.toHexString(mod); // 余数转成十六进制
        length = checkSumHex.length();
        if (length < 2) {
            checkSumHex = "0" + checkSumHex;  // 校验位不足两位的，在前面补0
        }
        return checkSumHex;
    }

    /**
     * 将short转化成byte[] ，大端在前，虽然小慧矩阵是小端在前，但是在测试的时候
     * Android发送串口要大端在前才能发送成功。
     * java默认是大端在前，小慧的视频矩阵默认是小端在前，因此将b1放在b0前面
     *
     * @param s 要转转化的数组
     * @return
     */
    public static byte[] shortToBytes(short s) {
        return shortToBytes(s, true);
    }

    /**
     * 将一个short转化成两个长度的byte数组
     *
     * @param s     要转化的short
     * @param isBig 是否大端在前,java默认是大端在前
     * @return
     */
    public static byte[] shortToBytes(short s, boolean isBig) {
        byte[] bytes = new byte[2];
        byte b0 = (byte) (s & 0xff);
        byte b1 = (byte) (s >> 8);

        if (isBig) {
            bytes[0] = b0;
            bytes[1] = b1;
        } else {
            bytes[0] = b1;
            bytes[1] = b0;
        }
        return bytes;
    }

    /**
     * 将10进制转换成16进制，补全0
     *
     * @param s
     * @return
     */
    public static String toHexString(String s) {
        int h = Integer.parseInt(s);
        String hexString = Integer.toHexString(h);
        if (hexString.length() == 1 || hexString.length() == 3) {
            hexString = "0" + hexString;
        }
        return hexString;
    }
}
