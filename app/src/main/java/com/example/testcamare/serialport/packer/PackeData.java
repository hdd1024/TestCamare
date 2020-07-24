package com.example.testcamare.serialport.packer;

import androidx.annotation.Keep;

import com.example.testcamare.serialport.packer.commandata.ContractData;
import com.example.testcamare.serialport.packer.commandata.LinkHead;
import com.example.testcamare.serialport.packer.commandata.TransportLayer;
/**
 * 串口发送或者向右数据报数据类
 */
@Keep
public class PackeData {

    /**
     * 拼装完成后的完整byte数据
     */
    public byte[] fullCommands;

    /**
     * 完整的字符串形式的串口数据
     */
    public String fullCommandsStr;

    /**
     * 完成初始化的链路包头数据类
     */
    public LinkHead linkHead;

    /**
     * 完成初始化的传输层数据类
     */
    public TransportLayer transportLayer;

    /**
     * 完成初始化的数据层类
     */
    public ContractData contractData;


    public byte[] getFullCommands() {
        return fullCommands;
    }

    public void setFullCommands(byte[] fullCommands) {
        this.fullCommands = fullCommands;
    }

    public String getFullCommandsStr() {
        return fullCommandsStr;
    }

    public void setFullCommandsStr(String fullCommandsStr) {
        this.fullCommandsStr = fullCommandsStr;
    }

    public LinkHead getLinkHead() {
        return linkHead;
    }

    public void setLinkHead(LinkHead linkHead) {
        this.linkHead = linkHead;
    }

    public TransportLayer getTransportLayer() {
        return transportLayer;
    }

    public void setTransportLayer(TransportLayer transportLayer) {
        this.transportLayer = transportLayer;
    }

    public ContractData getContractData() {
        return contractData;
    }

    public void setContractData(ContractData contractData) {
        this.contractData = contractData;
    }
}