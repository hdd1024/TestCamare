package com.example.testcamare.serialport.packer.commandata;


import androidx.annotation.Keep;

import com.example.testcamare.utils.ByteUtil;

/**
 * 串口数据的包头类
 */
@Keep
public class LinkHead {

    /**
     * 同步码 F0 AA 55
     * p[0]-p[2]
     */
    private byte synchroCode1 = -16;
    private byte synchroCode2 = -86;
    private byte synchroCode3 = 85;
    /**
     * 包类型   51
     * p[3] 单播
     */
    private byte packeTyoe = 81;

    /**
     * 目标地址 01
     * p[4] 默认
     */
    private byte targetAddress = 1;
    /**
     * 原地址  00
     * p[5] 默认
     */
    private byte sourceAddress = 0;
    /**
     * 数据长度 0A 00
     * p[6]-p[7]
     * 该数据长度为P[9]–P[N]的个数
     */
    private short dataLegth = 10;
//
//    private byte dataLegth1 = 10;
//    private byte dataLegth2 = 0;
    /**
     * 包头校验和 4B
     * p[8]
     * P[8]=P[0]+…P[7]
     */
    private byte verifySum = 75;

    public byte[] getSynchroCode() {
        return new byte[]{synchroCode1, synchroCode2, synchroCode3};
    }

    public void setSynchroCode(byte[] synchroCode) {
        this.synchroCode1 = synchroCode[0];
        this.synchroCode2 = synchroCode[1];
        this.synchroCode3 = synchroCode[2];
    }

    public byte getPackeTyoe() {
        return packeTyoe;
    }

    public void setPackeTyoe(byte packeTyoe) {
        this.packeTyoe = packeTyoe;
    }

    public byte getTargetAddress() {
        return targetAddress;
    }

    public void setTargetAddress(byte targetAddress) {
        this.targetAddress = targetAddress;
    }

    public byte getSourceAddress() {
        return sourceAddress;
    }

    public void setSourceAddress(byte sourceAddress) {
        this.sourceAddress = sourceAddress;
    }

    public short getDataLegth() {
        return dataLegth;
    }

    public void setDataLegth(short dataLegth) {
        this.dataLegth = dataLegth;
    }

    //    public byte[] getDataLegth() {
//        return new byte[]{dataLegth1, dataLegth2};
//    }
//
//    public void setDataLegth(byte[] dataLegth) {
//        this.dataLegth1 = dataLegth[0];
//        this.dataLegth1 = dataLegth[1];
//    }


    public byte getVerifySum() {
        return verifySum;
    }

    public void setVerifySum(byte verifySum) {
        this.verifySum = verifySum;
    }


    /**
     * 该方法将会返回一个byte数组，该数字的内容为p0到p7的值
     *
     * @return
     */
    public byte[] getP0_P7() {
        byte[] p0_p7 = new byte[8];
        p0_p7[0] = synchroCode1;
        p0_p7[1] = synchroCode2;
        p0_p7[2] = synchroCode3;
        p0_p7[3] = packeTyoe;
        p0_p7[4] = targetAddress;
        p0_p7[5] = sourceAddress;
        byte[] shortToBytes = ByteUtil.shortToBytes(dataLegth);
        p0_p7[6] = shortToBytes[0];
        p0_p7[7] = shortToBytes[1];
        return p0_p7;
    }

    public byte[] getP0_P8() {
        byte[] p0_p8 = new byte[9];
        p0_p8[0] = synchroCode1;
        p0_p8[1] = synchroCode2;
        p0_p8[2] = synchroCode3;
        p0_p8[3] = packeTyoe;
        p0_p8[4] = targetAddress;
        p0_p8[5] = sourceAddress;
        byte[] shortToBytes = ByteUtil.shortToBytes(dataLegth);
        p0_p8[6] = shortToBytes[0];
        p0_p8[7] = shortToBytes[1];
        p0_p8[8] = verifySum;
        return p0_p8;
    }

}