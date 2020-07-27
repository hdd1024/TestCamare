package com.example.testcamare.cameracollect.statecamera.state;

import com.example.testcamare.cameracollect.statecamera.state.config.CameraParams;
import com.example.testcamare.cameracollect.statecamera.state.config.DeviceOpenClose;
/***********************************************************
 * 创建时间:2020/7/24
 * 作   者: [hanmingze]
 * 功能描述: 相机消息的父类
 **********************************************************/
public abstract class CameraState {
    protected CameraContext mCameraContext;
    protected CameraParams cameraParams;
    protected DeviceOpenClose deviceOpenClose;

    public void setDeviceOpenClose(DeviceOpenClose deviceOpenClose) {
        this.deviceOpenClose = deviceOpenClose;
    }

    public void setCameraContext(CameraContext cameraContext) {
        mCameraContext = cameraContext;
    }

    public void setCameraParams(CameraParams cameraParams) {
        this.cameraParams = cameraParams;
    }
    public abstract void openCamera();

    public abstract void closeCamera();
}
