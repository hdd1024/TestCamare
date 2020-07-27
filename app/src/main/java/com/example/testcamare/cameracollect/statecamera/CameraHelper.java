package com.example.testcamare.cameracollect.statecamera;

import android.content.Context;
import android.graphics.SurfaceTexture;
import com.example.testcamare.cameracollect.statecamera.state.CameraContext;
import com.example.testcamare.cameracollect.statecamera.state.config.OnCameraException;
import com.example.testcamare.cameracollect.statecamera.state.config.OnDeviceClosed;
import com.example.testcamare.cameracollect.statecamera.state.config.OnDeviceOpen;

public class CameraHelper {

    private static CameraHelper mInstance;
    private CameraContext cameraContext2;

    private CameraHelper() {
        cameraContext2 = new CameraContext();
    }

    public static CameraHelper getInstance() {
        if (mInstance == null) {
            synchronized (CameraHelper.class) {
                if (mInstance == null) {
                    mInstance = new CameraHelper();
                }
            }
        }
        return mInstance;
    }

    public void openCamera(Context context, SurfaceTexture surfaceTexture) {
        cameraContext2.openCamera(context, surfaceTexture);
    }

    public void closeCamera() {
        cameraContext2.closeCamera();
    }

    public CameraHelper registerOnCameraOpen(OnDeviceOpen onDeviceOpen) {
        cameraContext2.registerOnDeviceOpen(onDeviceOpen);
        return this;
    }

    public CameraHelper registerOnCameraClose(OnDeviceClosed onDeviceClosed) {
        cameraContext2.registerOnDeviceClosed(onDeviceClosed);
        return this;
    }

    public CameraHelper registerOnCameraException(OnCameraException cameraException) {
        cameraContext2.registerOnCameraException(cameraException);
        return this;
    }
}
