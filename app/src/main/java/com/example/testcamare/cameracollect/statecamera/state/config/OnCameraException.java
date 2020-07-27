package com.example.testcamare.cameracollect.statecamera.state.config;

/***********************************************************
 * 创建时间:2020/7/24
 * 作   者: [hanmingze]
 * 功能描述: 相机的异常回调函数
 * 备注信息: {客户端可以通过设置该接口来监听开启过程中相机发送错误的信息，该接口最终
 *          会在{@link DeviceOpenClose#registerOnCameraException(OnCameraException)}
 *          中被配置}
 **********************************************************/
public interface OnCameraException {

    void onException(JbCameraException cameraException);
}
