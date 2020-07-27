package com.example.testcamare.cameracollect.statecamera.state.config;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.view.Surface;

import java.util.ArrayList;
import java.util.List;

/***********************************************************
 * 创建时间:2020/7/24
 * 作   者: [hanmingze]
 * 功能描述: 相机的信息配置类
 * 备注信息: {}
 **********************************************************/
public class CameraParams {
    private final String REAR_FACING_CAMAREID = "0";
    private List<SurfaceTexture> surfaceTextureList;
    private List<Surface> surfaceList;
    private Context context;
    private CameraDevice cameraDevice;
    private CameraCaptureSession cameraSession;
    private int width = 1920;
    private int height = 1080;

    public CameraParams() {
        this.surfaceTextureList = new ArrayList<>();
        this.surfaceList = new ArrayList<>();
    }

    public String getRearFacingCamareid() {
        return REAR_FACING_CAMAREID;
    }

    public List<SurfaceTexture> getSurfaceTextureList() {
        return surfaceTextureList;
    }

    public void setSurfaceTextureList(List<SurfaceTexture> surfaceTextureList) {
        this.surfaceTextureList = surfaceTextureList;
    }

    public List<Surface> getSurfaceList() {
        return surfaceList;
    }

    public void setSurfaceList(List<Surface> surfaceList) {
        this.surfaceList = surfaceList;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public CameraDevice getCameraDevice() {
        return cameraDevice;
    }

    public void setCameraDevice(CameraDevice cameraDevice) {
        this.cameraDevice = cameraDevice;
    }

    public CameraCaptureSession getCameraSession() {
        return cameraSession;
    }

    public void setCameraSession(CameraCaptureSession cameraSession) {
        this.cameraSession = cameraSession;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
