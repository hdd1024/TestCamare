package com.example.testcamare.app;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;


import com.example.testcamare.MediaSerialPortApi;
import com.example.testcamare.serialport.state.IDataCallback;
import com.example.testcamare.serialport.state.SerialPortBean;
import com.example.testcamare.utils.ByteUtil;
import com.example.testcamare.utils.JBDeviceUtil;
import com.example.testcamare.utils.LogUtilFromSDK;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;


public class HdmiDeviceSignal implements IDataCallback {
    private static final String TAG = "HdmiDeviceSignal";
    private Context context;
    private final int CHECK_DATA = 666;
    private Handler mHandler;
    private Handler.Callback mHandCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case CHECK_DATA:
                    SerialPortBean datas = (SerialPortBean) msg.obj;
                    cheakData(datas);
                    break;

                default:
            }
            return false;
        }
    };

    public HdmiDeviceSignal(Context context) {


        this.context = context;
        HandlerThread handlerThread = new HandlerThread(TAG + "_HandlerThread");
        handlerThread.start();
        mHandler = new Handler(handlerThread.getLooper(), mHandCallback);

        Disposable subscribe = Observable.interval(1000, TimeUnit.MILLISECONDS)
                .subscribe(aLong -> {
                    LogUtilFromSDK.getInstance().d("查询串口");

                    MediaSerialPortApi.contractStatusCode("ff", this);
                });
    }

    public void cheakData(SerialPortBean dataBean) {
        LogUtilFromSDK.getInstance().d("串口查询返回数据的线程" + Thread.currentThread().getName());
        byte[] datas = dataBean.getCallbcakCommands();
        if (datas==null) return;
        byte verify = ByteUtil.hexStr2bytes("51")[0];
        byte dataLong = ByteUtil.hexStr2bytes("07")[0];
        byte errorCode = ByteUtil.hexStr2bytes("00")[0];
        if (verify == datas[15] && dataLong == datas[16] && errorCode == datas[17]) {
            JBDeviceUtil.setSinga(JBDeviceUtil.LAPTOP_FLAG, datas[19]);
            JBDeviceUtil.setSinga(JBDeviceUtil.WIRELESS_SCREEN_FLAG, datas[21]);
            String productType = JBDeviceUtil.getProductType(context);
            LogUtilFromSDK.getInstance().e("串口255服务的笔记本信号位：------->" + productType);

            if (JBDeviceUtil.DAZHI.equals(productType)) {
                JBDeviceUtil.setSinga(JBDeviceUtil.TEACHER_MACHINE_FLAG, 1);
            } else {
                JBDeviceUtil.setSinga(JBDeviceUtil.TEACHER_MACHINE_FLAG, datas[23]);
            }

            LogUtilFromSDK.getInstance().e("串口255服务的笔记本信号位：" + JBDeviceUtil.getSinga(JBDeviceUtil.LAPTOP_FLAG));
            LogUtilFromSDK.getInstance().e("串口255服务的无线投屏信号位：" + JBDeviceUtil.getSinga(JBDeviceUtil.WIRELESS_SCREEN_FLAG));
            LogUtilFromSDK.getInstance().e("串口255服务的教室机信号位：" + JBDeviceUtil.getSinga(JBDeviceUtil.TEACHER_MACHINE_FLAG));
        }
    }

    @Override
    public void onCallbackData(SerialPortBean dataBean) {
        if (dataBean != null) {
            if (mHandler != null) {
                Message message = mHandler.obtainMessage(CHECK_DATA, dataBean);
                message.sendToTarget();
            }
        }
    }
}
