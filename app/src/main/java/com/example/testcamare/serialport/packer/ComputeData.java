package com.example.testcamare.serialport.packer;


import android.util.Log;

import androidx.annotation.Keep;

import com.example.testcamare.utils.ByteUtil;

/***********************************************************
 * 创建时间:2020-06-30
 * 作   者: [hanmingze]
 * 功能描述: <处理串口的数据长度、校验和的工具类>
 * 备注信息: {}
 * @see
 **********************************************************/
@Keep
public class ComputeData {
    private static final String TAG = "ComputeData";

    /**
     * 包头数据长度
     * P[9]--Pn
     *
     * @param fullcommands 完整的字符串
     */
    public static short computeHeadDataLegth(String fullcommands) {
        String p9_pnStr = fullcommands.substring(9 * 2);
        int length = p9_pnStr.length();
        String hexStr = Integer.toHexString(length);
        return Short.parseShort(hexStr);
    }

    /**
     * 校验包头的数据长度是否正确
     * 最多计算255个数据长度，也就是一个byte
     *
     * @param fullcommands 完整的命令字符串
     * @return true代表正确
     */
    public static boolean checkHeadDataLegth(String fullcommands) {
        try {
            String p9_pnStr = fullcommands.substring(9 * 2);
            int length = p9_pnStr.length() / 2;
            String headDataLegthStr = fullcommands.substring(6 * 2, 7 * 2);
            long headDataLegth = ByteUtil.hexStr2decimal(headDataLegthStr);
            return length == headDataLegth;
        } catch (Exception e) {
            Log.e(TAG, "校验包头的数据长度异常信息为：" + e.getMessage());
            return false;
        }
    }

    /**
     * 校验包头校验和是否正确
     * 最多计算255个数据长度，也就是一个byte
     *
     * @param fullcommands 完整的命令字符串
     * @return true代表正确
     */
    public static boolean checkHeadTotal(String fullcommands) {
        try {
            String p0_p7Str = fullcommands.substring(0, 8 * 2);
            String sum = ByteUtil.makeCheckSum(p0_p7Str);
            long sumL = ByteUtil.hexStr2decimal(sum);
            String headTotalStr = fullcommands.substring(8 * 2, 9 * 2);
            long headTotal = ByteUtil.hexStr2decimal(headTotalStr);
            return sumL == headTotal;
        } catch (Exception e) {
            Log.e(TAG, "包头校验和异常信息为：" + e.getMessage());
            return false;
        }
    }

    /**
     * 校验协议数据层的下发参数长度是否正确
     * 最多计算255个数据长度，也就是一个byte
     *
     * @param fullcommands 完整的命令字符串
     * @return true代表正确
     */
    public static boolean checkCommandParamsLegth(String fullcommands) {
        try {
            String commandParamsLegthStr = fullcommands.substring(17 * 2, fullcommands.length() - 2);
            int length = commandParamsLegthStr.length() / 2;
            String commandParamsStr = fullcommands.substring(16 * 2, 17 * 2);
            long commandParams = ByteUtil.hexStr2decimal(commandParamsStr);
            return length == commandParams;
        } catch (Exception e) {
            Log.e(TAG, "校验协议数据层的下发参数长度异常信息为：" + e.getMessage());
            return false;
        }

    }

    /**
     * 校验协议数据层的数据校验和是否正确
     * 最多计算255个数据长度，也就是一个byte
     *
     * @param fullcommands 完整的命令字符串
     * @return true代表正确
     */
    public static boolean checkContractDataTotal(String fullcommands) {
        int length = fullcommands.length() / 2;
        try {
            return checkDataTotal(fullcommands, length, 9, length - 1);
        } catch (Exception e) {
            Log.e(TAG, "校验协议数据层的数据校验和异常信息为：" + e.getMessage());
            e.printStackTrace();
            return false;
        }

    }


    /**
     * 校验数据合的计算
     * 参数说明：例如校验<code>F0AA5551000110005102020100100051070001010201030176</code>
     * 串口响应字符串的包头校验和P[8]是该值的目标位置，改值是通过P[8] = P[0] + … P[7]得。那么P[0]就
     * 是计算的开始位置，P[7]就是计算的结束位置
     *
     * @param fullcommands 完整的字符串数据
     * @param checkIndx    校验目标值的位置
     * @param start        从第位开始计算
     * @param end          到第几位计算结束
     * @return true 代表正确
     */
    public static boolean checkDataTotal(String fullcommands, int checkIndx, int start, int end)
            throws Exception {

        String startEndStr = fullcommands.substring(start * 2, end * 2);
        String sum = ByteUtil.makeCheckSum(startEndStr);
        long sumL = ByteUtil.hexStr2decimal(sum);
        String headTotalStr = fullcommands.substring((checkIndx - 1) * 2, checkIndx * 2);
        long headTotal = ByteUtil.hexStr2decimal(headTotalStr);
        return sumL == headTotal;


    }

}
