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

public class Camera4Manager {
    private static final int STATE_OPEN = 111;
    private static final int STATE_OPENING = 112;
    private static final int STATE_CAPTURE = 222;
    private static final int STATE_STOP = 333;
    private static final int STATE_CLOSE = 444;
    private static final int STATE_CLOSEING = 442;
    //默认状态下相机是关闭的
    private volatile int currentState = STATE_CLOSE;

    private static final String TAG = Camera4Manager.class.getSimpleName();
    //后置像头
    private static final String REAR_FACING_CAMAREID = "0";
    private static final int width = 1920;
    private static final int height = 1080;
    private Handler handler;
    private HandlerThread handlerThread;
    private Semaphore cameraOpenCloseLock = new Semaphore(1);
    private CameraDevice cameraDevice;
    private static Camera4Manager mInstance;
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
                    close();
                    break;
            }
            return false;
        }
    };
    private Context context;

    private Camera4Manager() {
        currentState = STATE_CLOSE;
        surfaceTextureList = new ArrayList<>();
        surfaceList = new ArrayList<>();
        handlerThread = new HandlerThread(TAG + "_HandlerThread");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper(), callback);
    }

    public static Camera4Manager getInstance() {
        if (mInstance == null) {
            synchronized (Camera4Manager.class) {
                if (mInstance == null) {
                    mInstance = new Camera4Manager();
                }
            }
        }
        return mInstance;
    }

    public Camera4Manager openCamera(Context context, SurfaceTexture surfaceTexture) {
        if (!surfaceTextureList.contains(surfaceTexture))
            surfaceTextureList.add(surfaceTexture);
        LogUtilFromSDK.getInstance().e("openCamera当前状态>>>>>>" + currentState);
        if (currentState == STATE_CLOSE) {
            currentState = STATE_OPENING;
            this.context = context;
            handler.obtainMessage(CAMERA_OPEN).sendToTarget();
        } else if (currentState == STATE_CAPTURE) {
            stopCapture();
            createCameraPreviewSession();
        } else if (currentState == STATE_OPEN || currentState == STATE_STOP) {
            createCameraPreviewSession();
        } else if (currentState == STATE_CLOSEING) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    openCamera(context, surfaceTexture);

                }
            }, 100);
        }
        return this;
    }

    public Camera4Manager stopCapture() {
        if (currentState != STATE_CAPTURE) return this;
        cameraCaptureSession.close();
        cameraCaptureSession = null;
        if (surfaceList != null) {
            surfaceList.clear();
        }
        currentState = STATE_STOP;
        return this;
    }

    @SuppressLint("MissingPermission")
    private void cameraConfig(Context context) throws TimeoutException {
        try {
            CameraManager cameraManager = (CameraManager) context.getSystemService(Service.CAMERA_SERVICE);
            if (!cameraOpenCloseLock.tryAcquire(15, TimeUnit.SECONDS)) {
                throw new TimeoutException("请求开启相机超时");
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
            cameraOpenCloseLock.release();
            LogUtilFromSDK.getInstance().e("onOpened当前状态>>>>>>" + currentState);
            if (currentState == STATE_CLOSEING) {
                closeCamera();
                return;
            }
            currentState = STATE_OPEN;
            createCameraPreviewSession();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            cameraOpenCloseLock.release();
            cameraDevice.close();
            cameraDevice = null;
            currentState = STATE_CLOSE;
            LogUtilFromSDK.getInstance().e("onDisconnected当前状态>>>>>>" + currentState);
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            cameraOpenCloseLock.release();
            cameraDevice.close();
            cameraDevice = null;
            currentState = STATE_CLOSE;
            LogUtilFromSDK.getInstance().e("onError当前状态>>>>>>" + currentState);
        }

        @Override
        public void onClosed(@NonNull CameraDevice camera) {
            super.onClosed(camera);
            LogUtilFromSDK.getInstance().e("onClosed当前状态>>>>>>" + currentState);
            currentState = STATE_CLOSE;
        }
    };

    private void createCameraPreviewSession() {
        try {
            previewRequestBuilder
                    = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            for (SurfaceTexture surfaceTexture : surfaceTextureList) {
                surfaceTexture.setDefaultBufferSize(width, height);
                Surface surface = new Surface(surfaceTexture);
                previewRequestBuilder.addTarget(surface);
                surfaceList.add(surface);
            }
            cameraDevice.createCaptureSession(surfaceList, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    Camera4Manager.this.cameraCaptureSession = cameraCaptureSession;
                    LogUtilFromSDK.getInstance().e("onConfigured当前状态>>>>>>" + currentState);
                    try {
                        if (currentState == STATE_CLOSEING || currentState == STATE_CLOSE)
                            return;
                        cameraCaptureSession.setRepeatingRequest(previewRequestBuilder.build(), new CameraCaptureSession.CaptureCallback() {
                            @Override
                            public void onCaptureStarted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, long timestamp, long frameNumber) {
                                super.onCaptureStarted(session, request, timestamp, frameNumber);
                            }

                            @Override
                            public void onCaptureFailed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureFailure failure) {
                                super.onCaptureFailed(session, request, failure);

                            }
                        }, null);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    } finally {
                        LogUtilFromSDK.getInstance().e("onConfigured--finally当前状态>>>>>>" + currentState);
                        currentState = STATE_CAPTURE;
                        cameraOpenCloseLock.release();
                    }
                }

                @Override
                public void onConfigureFailed(
                        @NonNull CameraCaptureSession cameraCaptureSession) {
                    cameraOpenCloseLock.release();
                }
            }, null);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public void closeCamera() {
        LogUtilFromSDK.getInstance().e("closeCamera当前状态>>>>>>" + currentState);
        if (currentState == STATE_CLOSE) {
            if (surfaceTextureList != null) {
                surfaceTextureList.clear();
            }
            return;
        }
        if (currentState == STATE_OPENING) {
            currentState = STATE_CLOSEING;
            if (surfaceTextureList != null) {
                surfaceTextureList.clear();
            }
            return;
        }
        currentState = STATE_CLOSEING;
        handler.obtainMessage(CAMERA_CLOSE).sendToTarget();
    }

    private void close() {
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
            }
            if (surfaceTextureList != null) {
                surfaceTextureList.clear();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            cameraOpenCloseLock.release();
        }
    }

    private void stopHandlerThread() {
        if (handlerThread != null) {
            handlerThread.quitSafely();
            try {
                handlerThread.join();
                handlerThread = null;
                handler.removeCallbacksAndMessages(handler);
                handler = null;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
