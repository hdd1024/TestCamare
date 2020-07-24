package com.example.testcamare.serialport;


import com.example.testcamare.serialport.packer.SPPakceData;
import com.example.testcamare.serialport.state.IDataCallback;
import com.example.testcamare.serialport.state.SerialPortContext;
import com.example.testcamare.utils.ByteUtil;

import java.io.IOException;

public class SerialPortHelper {
    private static final SerialPortHelper INSTANCE = new SerialPortHelper();
    private SerialPortContext portContext;

    private SerialPortHelper() {
    }

    public static SerialPortHelper instance() {
        return INSTANCE;
    }

    public void open() {
        if (portContext != null)
            return;
        try {
            portContext = new SerialPortContext();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置响应数据回调
     *
     * @param callback
     * @return
     */
    public SerialPortHelper setCallback(IDataCallback callback) {
        portContext.setCallback(callback);
        return this;
    }

    /**
     * 生成完成的byte[]串口命令
     *
     * @param commandId 命令id
     * @param issues    下发参数
     * @return 计算拼装成的完整的串口命令
     */
    public byte[] generateFullCommands(String commandId, String issues) {
        byte cId = ByteUtil.hexStr2bytes(commandId.trim())[0];
        SPPakceData.Builder builder = SPPakceData.Builder.create(cId);
        if (issues != null) {
            byte[] cIssues = ByteUtil.hexStr2bytes(issues.trim());
            builder.setIssuer(cIssues);
        }
        SPPakceData spPakceData = builder.build();
        return spPakceData.getPackeData().getFullCommands();
    }

    /**
     * 直接发送byte数组，该函数或绕过状态机，直接调用串口发送，不过目前
     * 没有为该函数设置返回响应数据的函数回调
     *
     * @param sends
     */
    public void sendBytes(byte[] sends) {
        portContext.sendByrtes(sends);
    }

    /**
     * 发送快速消息，相较于普通发送，该函数对发送命令有很好的保证
     * 发送的功能
     *
     * @param id 命令id
     */
    public void soonCommandId(String id) {
        byte cId = ByteUtil.hexStr2bytes(id.trim())[0];
        portContext.sendCommandId(true, cId);
    }

    public void soonCommandId(String id, String issuer) {
        byte cId = ByteUtil.hexStr2bytes(id.trim())[0];
        byte[] cIssues = ByteUtil.hexStr2bytes(issuer.trim());
        portContext.sendCommandId(true, cId, cIssues);
    }

    /**
     * 发送普通消息，在过线程并发情况下，该函数不一定保证消息一般会被发送出去(发送消息
     * 采用的是状态机模式)
     *
     * @param id 命令id
     */
    public synchronized void commandId(String id) {
        byte cId = ByteUtil.hexStr2bytes(id.trim())[0];
        portContext.sendCommandId(false, cId);
    }

    public synchronized void commandId(String id, String issuer) {
        byte cId = ByteUtil.hexStr2bytes(id.trim())[0];
        byte[] cIssues = ByteUtil.hexStr2bytes(issuer.trim());
        portContext.sendCommandId(false, cId, cIssues);
    }

    public void close() {
        if (portContext != null) {
            portContext.close();
        }
    }

}
