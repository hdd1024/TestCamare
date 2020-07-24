package com.example.testcamare.cameracollect.statecamera2;

import android.content.Context;
import android.graphics.SurfaceTexture;
import com.example.testcamare.cameracollect.statecamera2.state.CameraContext2;

public class CameraHelper {

    private static CameraHelper mInstance;
    private CameraContext2 cameraContext2;

    private CameraHelper() {
        cameraContext2 = new CameraContext2();
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
}
