package com.example.testcamare.serialport.state;


import android.util.Log;

import com.example.testcamare.serialport.packer.PackeData;
import com.example.testcamare.utils.ByteUtil;
import com.firefly.api.serialport.SerialPort;
import java.util.ArrayList;
import java.util.List;


/**
 * 待定后台响应数据
 */
public class WaitDataState extends AbsSerialPortState implements SerialPort.Callback {
    private SerialPortBean serialPortBean;
    //主动查询命令反馈机制中需要对反馈查询进行ACK反馈，通知MCU接收数据成功。
    //ACK包跟协议功能无依赖固定包格式:
    private String sendTo = "F0 AA 55 65 01 00 00 00 55";
    private IDataCallback dataCallback;
    //本集合中存放的是直接通过sendBytes()绕过状态机模式发送的消息的函数回调
    private List<IDataCallback> notStateCallback = new ArrayList<>();
    //超时
    private long timeout = 5000;
    //开始等待时间
    private long startWiateTime;

    void setDataCallback(boolean isStateCallback, IDataCallback dataCallback) {
        if (isStateCallback) {
            this.dataCallback = dataCallback;
        } else {
            notStateCallback.add(dataCallback);
        }
    }

    @Override
    void wiateDatas(PackeData sendData) {
        startWiateTime = System.currentTimeMillis();
//        LogUtilFromSDK.getInstance().d("等待状态当前线程" + Thread.currentThread().getName());
        serialPortBean = new SerialPortBean();
        serialPortBean.setSendData(sendData);
        if (dataCallback != null)
            serialPortBean.setCataCallbck(dataCallback);
    }

    @Override
    public void datasCheck(SerialPortBean portBean) {
//        Log.d(TAT, "当前为等待状态不能进行校验数据！");
    }

    @Override
    boolean sendCommand(byte commandId, byte... issues) {
//        Log.d(TAT, "当前为等待状态不能发送数据！");
        //超时直接进入进入发送状态
        long currentTime = System.currentTimeMillis();
        if (currentTime - startWiateTime > timeout) {
            Log.d(TAT, "等待串口响应超时！");
            portContext.setCurrentState(portContext.SEND_STATE);
        }
        return false;
    }

    /**
     * 矩阵返回串口数据
     *
     * @param bytes 响应的数据
     * @param i     数据个数
     */
    @Override
    public void onDataReceived(byte[] bytes, int i) {
        startWiateTime = System.currentTimeMillis();
//        LogUtilFromSDK.getInstance().d("串口响应数据长度" + i);
        if (serialPortBean != null) {
            // 设置响应数据
            serialPortBean.setCallbcakCommands(bytes);
            String hexStr = ByteUtil.bytes2HexStr(bytes);
            String fullCallbackStr = hexStr.substring(0, i * 2);
            serialPortBean.setCallbckCommandsStr(fullCallbackStr);
            serialPortBean.setDatasCount(i);
            //收到响应要马上给矩阵回复一个固定的数据
            portContext.sendByrtes(ByteUtil.hexStr2bytes(sendTo));
            //将当前状态设置为校验状态
            portContext.setCurrentState(SerialPortContext.CHACK_DATA_STATE);
            //进入数据校验状态
            portContext.srialPortState.datasCheck(serialPortBean);
        }
    }
}