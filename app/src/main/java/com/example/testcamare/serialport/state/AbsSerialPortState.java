package com.example.testcamare.serialport.state;


import com.example.testcamare.serialport.packer.PackeData;

/**
 * 串口的状态类
 */
abstract class AbsSerialPortState {

    protected final String TAT = this.getClass().getName();

    /**
     * 上下文，该类是主要处理串口逻辑的类
     */
    protected SerialPortContext portContext;


    void setPortContext(SerialPortContext portContext) {
        this.portContext = portContext;
    }

    /**
     * 进入等待状态
     */
    abstract void wiateDatas(PackeData sendData);

    /**
     * 校验数据
     */
    abstract void datasCheck(SerialPortBean portBean);

    /**
     * 发送数据
     *
     */
    abstract boolean sendCommand(byte commandId, byte... issues);
}