package com.example.testcamare.serialport.state;


import androidx.annotation.Keep;

import com.example.testcamare.serialport.packer.PackeData;

/**
 * 该类主要用于存放发送和响应数据
 */
@Keep
public class SerialPortBean {
    //处理完毕后会调用该接口将数据发送给客户端
    public IDataCallback cataCallbck;
    //发送数据的储存类
    public PackeData sendData;
    //将响应数据处理成字符串
    public String callbckCommandsStr;
    //响应数据的数据
    public byte[] callbcakCommands;
    //响应了对少为，也就是响应了多少个byte
    private int dataCount;

    public IDataCallback getCataCallbck() {
        return cataCallbck;
    }

    public void setCataCallbck(IDataCallback cataCallbck) {
        this.cataCallbck = cataCallbck;
    }

    public PackeData getSendData() {
        return sendData;
    }

    public void setSendData(PackeData sendData) {
        this.sendData = sendData;
    }

    public String getCallbckCommandsStr() {
        return callbckCommandsStr;
    }

    public void setCallbckCommandsStr(String callbckCommandsStr) {
        this.callbckCommandsStr = callbckCommandsStr;
    }

    public byte[] getCallbcakCommands() {
        return callbcakCommands;
    }

    public void setCallbcakCommands(byte[] callbcakCommands) {
        this.callbcakCommands = callbcakCommands;
    }

    public int getDataCount() {
        return dataCount;
    }

    public void setDatasCount(int dataCount) {
        this.dataCount = dataCount;
    }
}