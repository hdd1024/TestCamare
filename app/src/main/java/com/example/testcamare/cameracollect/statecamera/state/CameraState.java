package com.example.testcamare.cameracollect.statecamera.state;

import com.example.testcamare.cameracollect.statecamera.CameraContext;

public abstract class CameraState {

    protected CameraContext cameraContext;

    public void setCameraContext(CameraContext cameraContext) {
        this.cameraContext = cameraContext;
    }

    abstract void open();

    abstract void capture();

    abstract void stop();

    abstract void closeCamera();
}
