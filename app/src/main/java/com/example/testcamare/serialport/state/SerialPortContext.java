package com.example.testcamare.serialport.state;

import com.firefly.api.serialport.SerialPort;

import java.io.File;
import java.io.IOException;

/**
 * 串口的上下文类
 */
public class SerialPortContext {
    private static final String TAG = "SerialPortHelper";
    private static final String DEVICE_PATH = "/dev/ttyS0";
    private static final int BAUDRATE = 9600;
    public static final Object block = new Object();
    //串口
    private final SerialPort mSerialPort;
    //状态基类
    protected AbsSerialPortState srialPortState;
    //发送数据状态类
    static final SendState SEND_STATE = new SendState();
    static final WaitDataState WAIT_DATA_STATE = new WaitDataState();
    static final CheckDataState CHACK_DATA_STATE = new CheckDataState();
    //快速发送数据包装
    private boolean hasSoon = false;
    private byte soonCommandId = 0;
    private byte[] soonIssues = null;

    public SerialPortContext() throws IOException {
        this.mSerialPort = new SerialPort(new File(DEVICE_PATH), BAUDRATE, 0);
        mSerialPort.setCallback(WAIT_DATA_STATE);
        //其实状态为可发送状态
        setCurrentState(SEND_STATE);
    }

    /**
     * 该方法交给状态类切换当前状态
     */
    void setCurrentState(AbsSerialPortState currentState) {
//        synchronized (block) {
        this.srialPortState = currentState;
        this.srialPortState.setPortContext(this);
        //如果是发送状态，并且快速发送消息数据包装类不为空，那么将会立刻发送该包装类数据
        if (currentState instanceof SendState && hasSoon) {
            synchronized (block) {
                boolean sendCommand = this.srialPortState.sendCommand(soonCommandId, soonIssues);
                if (sendCommand) {
                    hasSoon = false;
                    soonIssues = null;
                    soonCommandId = 0;
                }
            }

        }
//            block.notifyAll();
//        }
    }

    /**
     * 添加返回响应数据监听
     */
    public void setCallback(IDataCallback dataCallback) {
        WAIT_DATA_STATE.setDataCallback(true, dataCallback);
    }

    /**
     * 该函数可以直接发送串口命令，不收状态模式约束
     *
     * @param bytes 串口命令
     */
    public void sendByrtes(byte[] bytes) {
        synchronized (block) {
            SEND_STATE.sendByrtes(bytes);
        }
    }

    public void sendCommandId(boolean isSoon, byte commandId) {
        sendCommandId(isSoon, commandId, null);
    }

    /**
     * 发送命令消息函数
     *
     * @param isSoon    是否马上发送，true 则在消息发送失败后，会在下次可发送消息状态
     *                  优先发送这次消息
     * @param commandId 命令id
     * @param issues    下发参数
     */
    public void sendCommandId(boolean isSoon, byte commandId, byte... issues) {

        synchronized (block) {
            boolean send = srialPortState.sendCommand(commandId, issues);
            //如果是快速发送类，则会创建一个新的数据包装类暂存发送信息
            if (isSoon && !send) {
                hasSoon = true;
                soonCommandId = commandId;
                soonIssues = issues;
            }
            block.notifyAll();
        }
    }

    SerialPort getSerialPort() {
        return mSerialPort;
    }

    public void close() {
        mSerialPort.closeSerialPort();
    }
}