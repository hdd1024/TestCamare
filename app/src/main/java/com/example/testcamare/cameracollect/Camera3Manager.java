package com.example.testcamare.cameracollect;


import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.view.Surface;

import androidx.annotation.NonNull;

import com.example.testcamare.utils.LogUtilFromSDK;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Camera3Manager {
    private static final String TAG = Camera3Manager.class.getSimpleName();
    //后置像头
    private static final String REAR_FACING_CAMAREID = "0";
    private static final int width = 1920;
    private static final int height = 1080;
    private Handler handler;
    private HandlerThread handlerThread;
    private Semaphore cameraOpenCloseLock = new Semaphore(1);
    private CameraDevice cameraDevice;
    private static Camera3Manager mInstance;
    private List<SurfaceTexture> surfaceTextureList;
    private CaptureRequest.Builder previewRequestBuilder;
    private List<Surface> surfaceList;
    private CameraCaptureSession cameraCaptureSession;
    private final int CAMERA_OPEN = 1;

    private final int CAMERA_CLOSE = 2;

    private Handler.Callback callback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case CAMERA_OPEN:
                    try {
                        cameraConfig(context);
                    } catch (TimeoutException e) {
                        e.printStackTrace();
                    }
                    break;

                case CAMERA_CLOSE:

                    break;
            }
            return false;
        }
    };
    private Context context;

    private Camera3Manager() {
        init();
    }

    public static Camera3Manager getInstance() {
        if (mInstance == null) {
            synchronized (Camera3Manager.class) {
                if (mInstance == null) {
                    mInstance = new Camera3Manager();
                }
            }
        }
        return mInstance;
    }


    private void init() {
        surfaceTextureList = new ArrayList<>();
        surfaceList = new ArrayList<>();
        handlerThread = new HandlerThread(TAG + "_HandlerThread");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper(), callback);
    }

    public void openCamera(Context context, SurfaceTexture surfaceTexture) throws TimeoutException {

        try {
            cameraOpenCloseLock.acquire();
            surfaceTextureList.add(surfaceTexture);
            if (cameraDevice != null && cameraCaptureSession != null) {
                surfaceList.clear();
                cameraCaptureSession.close();
                createCameraPreviewSession();
                cameraOpenCloseLock.release();
                return;
            }
            cameraOpenCloseLock.release();
        } catch (InterruptedException | CameraAccessException e) {
            e.printStackTrace();
            cameraOpenCloseLock.release();
        }

        this.context = context;
        handler.obtainMessage(CAMERA_OPEN).sendToTarget();
    }

    @SuppressLint("MissingPermission")
    private void cameraConfig(Context context) throws TimeoutException {
        try {
            CameraManager cameraManager = (CameraManager) context.getSystemService(Service.CAMERA_SERVICE);

            if (!cameraOpenCloseLock.tryAcquire(9, TimeUnit.SECONDS)) {
                throw new TimeoutException("请求开启相机超时");
            }
            if (cameraDevice != null && cameraCaptureSession != null) {
                surfaceList.clear();
                cameraCaptureSession.close();
                createCameraPreviewSession();
                cameraOpenCloseLock.release();
                return;
            }
            cameraManager.openCamera(REAR_FACING_CAMAREID, mStateCallback, handler);
        } catch (InterruptedException | CameraAccessException e) {
            e.printStackTrace();
            LogUtilFromSDK.getInstance().e("相机开启出现问题：" + e.getMessage());
        }
    }

    private final CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            cameraDevice = camera;
            try {
                createCameraPreviewSession();
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
//            cameraOpenCloseLock.release();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            cameraOpenCloseLock.release();
            cameraDevice.close();
            cameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            cameraOpenCloseLock.release();
            cameraDevice.close();
            cameraDevice = null;
        }
    };

    private void createCameraPreviewSession() throws CameraAccessException {
        for (SurfaceTexture surfaceTexture : surfaceTextureList) {
            surfaceTexture.setDefaultBufferSize(width, height);

            Surface surface = new Surface(surfaceTexture);
            previewRequestBuilder
                    = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            previewRequestBuilder.addTarget(surface);
            surfaceList.add(surface);
        }
        // Here, we create a CameraCaptureSession for camera preview.
        cameraDevice.createCaptureSession(surfaceList,
                new CameraCaptureSession.StateCallback() {
                    @Override
                    public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                        Camera3Manager.this.cameraCaptureSession = cameraCaptureSession;
                        try {
                            cameraCaptureSession.setRepeatingRequest(previewRequestBuilder.build(), new CameraCaptureSession.CaptureCallback() {
                                @Override
                                public void onCaptureStarted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, long timestamp, long frameNumber) {
                                    super.onCaptureStarted(session, request, timestamp, frameNumber);
                                    cameraOpenCloseLock.release();

                                }

                                @Override
                                public void onCaptureFailed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureFailure failure) {
                                    super.onCaptureFailed(session, request, failure);
                                    cameraOpenCloseLock.release();

                                }
                            }, null);
                        } catch (CameraAccessException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onConfigureFailed(
                            @NonNull CameraCaptureSession cameraCaptureSession) {
                        cameraOpenCloseLock.release();

                    }
                }, null);
    }

    public void closeCamera() {
        try {
            cameraOpenCloseLock.acquire();
            if (cameraCaptureSession != null) {
                cameraCaptureSession.close();
                cameraCaptureSession = null;
            }

            if (cameraDevice != null) {
                cameraDevice.close();
                cameraDevice = null;
            }
            if (surfaceList != null) {
                surfaceList.clear();
                surfaceList = null;
            }
            if (surfaceTextureList != null) {
                surfaceTextureList.clear();
                surfaceTextureList = null;
            }
            stopHandlerThread();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            cameraOpenCloseLock.release();
        }

    }


    private void stopHandlerThread() {
        if (handlerThread != null) {
            handlerThread.quit();
            try {
                handlerThread.join();
                handlerThread = null;
                handler.removeCallbacksAndMessages(handler);
                handler = null;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (mInstance != null) {
            mInstance =null;
        }
    }


}
