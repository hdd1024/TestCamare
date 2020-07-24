package com.example.testcamare.serialport.packer.commandata;

import androidx.annotation.Keep;

/**
 * 串口数据的协议数据类
 */
@Keep
public class ContractData {
    //回话 P[13]-P[14]
    private byte talk1 = 0x10;
    private byte talk2 = 0x00;
    //命令Id P[15]
    private byte commandId;
    //命令长度 至少一个命令 P[16]
    //领了长度可以通过下面公式计算：命令参数长度=下发参数长度
    private byte commandParamLegth = 0x01;
    //下发参数
    private byte[] issue = {0x00};
    //数据包检验和
    private byte dataVirfySum;


    public void setCommandId(byte commandId) {
        this.commandId = commandId;
    }

    public byte getCommandId() {
        return this.commandId;
    }

    public void setParamLegth(byte legth) {
        this.commandParamLegth = legth;
    }

    public byte getParamLegth() {
        return this.commandParamLegth;
    }

    public void setIssuer(byte[] issue) {
        this.issue = issue;
    }

    public byte[] getIssuer() {
        return this.issue;
    }

    public byte getDataVirfySum() {
        return dataVirfySum;
    }

    public void setDataVirfySum(byte dataVirfySum) {
        this.dataVirfySum = dataVirfySum;
    }

    /**
     * 返回p13到pn-1的数据，该数据配合传输层长度可用于计算数据包的校验和
     */
    public byte[] getP13_Pn_1() {
        byte[] p13_pn_1 = new byte[4 + issue.length];
        p13_pn_1[0] = talk1;
        p13_pn_1[1] = talk2;
        p13_pn_1[2] = commandId;
        p13_pn_1[3] = commandParamLegth;
        for (int i = 0; i < issue.length; i++) {
            p13_pn_1[4 + i] = issue[i];
        }
        return p13_pn_1;
    }


    /**
     * 返回的是13到pn的数据，可以用配合校验数据
     */
    public byte[] getP13_Pn() {
        byte[] p13_pn = new byte[4 + issue.length];
        p13_pn[0] = talk1;
        p13_pn[1] = talk2;
        p13_pn[2] = commandId;
        p13_pn[3] = commandParamLegth;
        for (int i = 0; i < issue.length; i++) {
            p13_pn[4 + i] = issue[i];
        }
        p13_pn[p13_pn.length - 1] = dataVirfySum;
        return p13_pn;
    }


}