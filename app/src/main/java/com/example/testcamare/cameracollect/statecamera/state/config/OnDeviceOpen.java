package com.example.testcamare.cameracollect.statecamera.state.config;

import android.hardware.camera2.CaptureRequest;

/***********************************************************
 * 创建时间:2020/7/24
 * 作   者: [hanmingze]
 * 功能描述: 相机打开回调接口
 * 备注信息: {客户端可以通过设置该接口来监听相机开启，该接口最终会在{@link
 *           DeviceOpenClose#registerOnDeviceOpen(OnDeviceOpen)}}中被配置
 *           会在{@link DeviceOpenClose#perviewSession()}中被调用}
 **********************************************************/
public interface OnDeviceOpen {
    /**
     * 只有建立相机回话之后，才调用该函数
     *
     * @param builder 相机配置类
     */
    void onOpened(CaptureRequest.Builder builder);
}
