package com.example.testcamare.cameracollect.statecamera.state.config;

import android.hardware.camera2.CameraDevice;
import androidx.annotation.NonNull;
/***********************************************************
 * 创建时间:2020/7/24
 * 作   者: [hanmingze]
 * 功能描述: 相机关闭回调接口
 * 备注信息: {客户端可以通过设置该接口来监听相机关闭，该接口最终会在{@link
 *          DeviceOpenClose#registerOnCameraException(OnCameraException)}中
 *          被配置，会在{@link DeviceOpenClose#onClosed(CameraDevice)}中
 *          被调用}
 **********************************************************/
public interface OnDeviceClosed {
    void onClosed(@NonNull CameraDevice camera);
}
