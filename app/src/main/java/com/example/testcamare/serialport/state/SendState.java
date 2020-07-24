package com.example.testcamare.serialport.state;


import android.util.Log;

import com.example.testcamare.serialport.packer.PackeData;
import com.example.testcamare.serialport.packer.SPPakceData;
import com.example.testcamare.utils.ByteUtil;


/**
 * 可以发送数据状态
 */
public class SendState extends AbsSerialPortState {
    @Override
    public void wiateDatas(PackeData portBean) {
    }

    /**
     * 校验数据
     */
    @Override
    public void datasCheck(SerialPortBean portBean) {
//        Log.d(TAT, "当前为发送状态不能datasCheck！");
    }

    @Override
    boolean sendCommand(byte commandId, byte... issues) {
//        Log.d(TAT, "发送状态线程：" + Thread.currentThread().getName());
        //拼装发送信息
        SPPakceData.Builder builder = SPPakceData.Builder.create(commandId);
        if (issues != null) {
            builder.setIssuer(issues);
        }
        SPPakceData spPakceData = builder.build();
        PackeData packeData = spPakceData.getPackeData();
        //切换成扥等待状态
        portContext.setCurrentState(SerialPortContext.WAIT_DATA_STATE);
        //将发送数据传给等待状态
        SerialPortContext.WAIT_DATA_STATE.wiateDatas(packeData);
        //解析要发送的数据，先切换状态在发送，防止响应过快，初始化接受响应信息的类
        //还没完成从而引起该条信息的响应数据没有交给数据监测状态类来处理
        sendByrtes(packeData.fullCommands);
        return true;

    }

    void sendByrtes(byte[] bytes) {
        if (portContext == null || portContext.getSerialPort() == null) {
            return;
        }
        Log.d(TAT, "----->发送状态下发送的数据:" + ByteUtil.bytes2HexStr(bytes));
        synchronized (portContext.block) {
            portContext.getSerialPort().sendHexMsg(bytes);
        }
    }

}