package com.example.testcamare.cameracollect.statecamera2.state;

import android.annotation.SuppressLint;
import android.hardware.camera2.*;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import com.example.testcamare.cameracollect.statecamera2.state.config.JbCameraException;
import com.example.testcamare.cameracollect.statecamera2.state.config.OnCameraException;
import com.example.testcamare.cameracollect.statecamera2.state.config.OnDeviceOpen;
import com.example.testcamare.utils.LogUtilFromSDK;

/***********************************************************
 * 创建时间:2020/7/24
 * 作   者: [hanmingze]
 * 功能描述: 相机开启状态类
 * 备注信息: {该类中定义了3中状态，OPEN_CAMERA_INIT 出事状态、OPEN_CAMERA_ING 开启中、
 *          OPEN_CAMERA 开启状态}
 **********************************************************/
public class OpenCameraState extends CameraState2 implements OnDeviceOpen, OnCameraException {
    private final int OPEN_CAMERA_INIT = 220;
    private final int OPEN_CAMERA_ING = 221;
    private final int OPEN_CAMERA = 222;
    private int currentState = OPEN_CAMERA_INIT;

    @SuppressLint("MissingPermission")
    @Override
    public void openCamera() {
        LogUtilFromSDK.getInstance().d("openCamera相机状态：" + currentState);
        //如果是开启中的状态那么就什么也不处理
        if (currentState == OPEN_CAMERA_ING) {
            return;
        }
        //如果相机是开启状态，那么它不需要重新在打开相机
        //只有调用相机预览就可以
        if (currentState == OPEN_CAMERA) {
            cameraParams.getSurfaceList().clear();
            if (cameraParams.getCameraSession() != null)
                cameraParams.getCameraSession().close();
            deviceOpenClose.perviewSession();
            return;
        }
        //开启相机配置
        currentState = OPEN_CAMERA_ING;
        deviceOpenClose.setOnDeviceOpen(this);
        deviceOpenClose.setOnCameraException(this);
        //开启相机
        deviceOpenClose.openCamera(mCameraContext.getHandler());
    }

    @Override
    public void onOpened(CaptureRequest.Builder builder) {
        LogUtilFromSDK.getInstance().d("onOpened--开启相机喽！--相机状态：" + currentState);
        deviceOpenClose.setRepeateRequest(builder, new CameraCaptureSession.CaptureCallback() {
            @Override
            public void onCaptureStarted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request,
                                         long timestamp, long frameNumber) {
                super.onCaptureStarted(session, request, timestamp, frameNumber);
                if (currentState != OPEN_CAMERA_INIT)
                    currentState = OPEN_CAMERA;
            }
            @Override
            public void onCaptureFailed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request,
                                        @NonNull CaptureFailure failure) {
                super.onCaptureFailed(session, request, failure);
                if (currentState != OPEN_CAMERA_INIT)
                    currentState = OPEN_CAMERA;
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void closeCamera() {
        LogUtilFromSDK.getInstance().d("---closeCamera相机状态：" + currentState);
        mCameraContext.setCurrentState(mCameraContext.CLOSE_CAMERA_STATE);
        //执行关闭状态类中的关闭函数
        mCameraContext.mCurrentState.closeCamera();
        currentState = OPEN_CAMERA_INIT;
    }

    @Override
    public void onException(JbCameraException cameraException) {
        LogUtilFromSDK.getInstance().e(cameraException.getExcUuid() + "----perviewSession相机状态异常信息：" + cameraException.getMessage());
        currentState = OPEN_CAMERA_INIT;
    }
}
