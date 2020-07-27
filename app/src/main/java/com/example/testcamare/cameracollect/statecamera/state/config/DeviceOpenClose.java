package com.example.testcamare.cameracollect.statecamera.state.config;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.*;
import android.view.Surface;
import androidx.annotation.NonNull;
import com.example.testcamare.cameracollect.statecamera.state.CameraContext;
import com.example.testcamare.utils.LogUtilFromSDK;

import java.util.ArrayList;
import java.util.List;

/***********************************************************
 * 创建时间:2020/7/24
 * 作   者: [hanmingze]
 * 功能描述: 该类主要用于处理相机的开启、关闭、session回话异常等功能
 * 备注信息: {}
 **********************************************************/

public class DeviceOpenClose extends CameraDevice.StateCallback {
    private CameraParams cameraParams;
    //设备开启回调监听
    private List<OnDeviceOpen> onDeviceOpenList;
    //设备关闭回调监听
    private List<OnDeviceClosed> onDeviceClosedList;
    //设备异常回调接口
    private List<OnCameraException> onCameraExceptionList;

    public DeviceOpenClose(CameraParams cameraParams) {
        this.cameraParams = cameraParams;
        this.onDeviceOpenList = new ArrayList<>();
        this.onDeviceClosedList = new ArrayList<>();
        this.onCameraExceptionList = new ArrayList<>();
    }

    /**
     * 设置相机设备关闭监听
     *
     * @param onDeviceClosed 相机关闭监听回调
     */
    public void registerOnDeviceClosed(OnDeviceClosed onDeviceClosed) {
        if (!onDeviceClosedList.contains(onDeviceClosed))
            this.onDeviceClosedList.add(onDeviceClosed);
    }

    /**
     * 设置相机开启监听回调
     * 该函数会在session回话连接成功后调用执行
     *
     * @param onDeviceOpen 设置相机开启 监听回调
     */
    public void registerOnDeviceOpen(OnDeviceOpen onDeviceOpen) {
        if (!onDeviceOpenList.contains(onDeviceOpen))
            this.onDeviceOpenList.add(onDeviceOpen);
    }

    /**
     * 相机异常
     *
     * @param onCameraException 自定义的相机异常
     */
    public void registerOnCameraException(OnCameraException onCameraException) {
        if (!onCameraExceptionList.contains(onCameraException))
            this.onCameraExceptionList.add(onCameraException);
    }

    /**
     * 开启相机
     *
     * @param handler 数据处理的handler 该handler是在子线程中处理的
     * @throws CameraAccessException    相机异常处理
     * @throws IllegalArgumentException 相机其他异常
     */
    @SuppressLint("MissingPermission")
    public void openCamera(CameraContext.ContextHandler handler) {
        try {
            CameraManager cameraManager = (CameraManager) cameraParams.getContext().getSystemService(Context.CAMERA_SERVICE);
            cameraManager.openCamera(cameraParams.getRearFacingCamareid(), this, handler);
        } catch (CameraAccessException | IllegalArgumentException e) {
            LogUtilFromSDK.getInstance().e("相机状态————打开相机异常：" + e.getMessage());
            cameraException("相机打开异常", e);
        }
    }

    @Override
    public void onOpened(@NonNull CameraDevice camera) {
        if (onDeviceOpenList != null) {
            cameraParams.setCameraDevice(camera);
            perviewSession();
        }
    }

    @Override
    public void onDisconnected(@NonNull CameraDevice camera) {
        camera.close();
        cameraParams.setCameraDevice(null);
    }

    @Override
    public void onError(@NonNull CameraDevice camera, int error) {
        camera.close();
        cameraParams.setCameraDevice(null);
    }

    @Override
    public void onClosed(@NonNull CameraDevice camera) {
        super.onClosed(camera);
        for (OnDeviceClosed onDeviceClosed : onDeviceClosedList) {
            //关闭回调执行前
            onDeviceClosed.onClosed(camera);
        }
        //关闭回调执行后
        this.onDeviceOpenList.clear();
        this.onDeviceClosedList.clear();
        this.onCameraExceptionList.clear();
    }

    /**
     * 相机session回话函数
     */
    public void perviewSession() {
        try {
            if (cameraParams.getCameraDevice() == null) return;
            //配置相机
            CaptureRequest.Builder captureRequest = cameraParams.getCameraDevice().createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            //配置surface
            for (SurfaceTexture surfaceTexture : cameraParams.getSurfaceTextureList()) {
                surfaceTexture.setDefaultBufferSize(cameraParams.getWidth(), cameraParams.getHeight());
                Surface surface = new Surface(surfaceTexture);
                captureRequest.addTarget(surface);
                cameraParams.getSurfaceList().add(surface);
            }
            //建立session回话
            cameraParams.getCameraDevice().createCaptureSession(cameraParams.getSurfaceList(), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    cameraParams.setCameraSession(session);
                    if (onDeviceOpenList != null)
                        for (OnDeviceOpen onDeviceOpen : onDeviceOpenList) {
                            onDeviceOpen.onOpened(captureRequest);
                        }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                    LogUtilFromSDK.getInstance().e("相机状态----session建立失败");
                }
            }, null);
        } catch (CameraAccessException | IllegalStateException | IllegalArgumentException e) {
            cameraException("相机回话异常", e);
        }
    }

    /**
     * 请求捕获相机预览画面
     *
     * @param builder         相机配置
     * @param captureCallback 回调
     */
    public void setRepeateRequest(CaptureRequest.Builder builder,
                                  CameraCaptureSession.CaptureCallback captureCallback) {
        try {
            cameraParams.getCameraSession().setRepeatingRequest(builder.build(), captureCallback, null);
        } catch (CameraAccessException | IllegalStateException e) {
            cameraException("相机画面捕获异常", e);
        }
    }

    /**
     * 包装相机异常函数
     *
     * @param msg 异常消息
     * @param exc 异常类型
     */
    private void cameraException(String msg, Exception exc) {
        JbCameraException jbCameraException = new JbCameraException(msg);
        jbCameraException.addSuppressed(exc);
        try {
            throw jbCameraException;
        } catch (JbCameraException cameraException) {
            if (onCameraExceptionList != null)
                for (OnCameraException onCameraException : onCameraExceptionList) {
                    onCameraException.onException(jbCameraException);
                }
        }
    }

}
