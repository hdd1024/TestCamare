package com.example.testcamare.cameracollect;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.view.Surface;
import android.view.TextureView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class Camera1Manager {
    private static final String TAG = "Camera1Manager";

    /**
     * 前置摄像头
     */
    public static final String FONT_FACING_CAMAREID = "1";
    /**
     * 后置摄像头id
     */
    public static final String REAR_FACING_CAMAREID = "0";

    private final int PREVIEW_WIDTH = 1920;
    private final int PREVIEW_HEIGHT = 1080;
    private static final int OPEN_CAMERA = 1;
    private final int FIRST_START = 2;
    private final int VIRTUAL_START = 3;
    private Handler mHandler;

    private Camera mCamera;
    private Context mContext;
    private List<Surface> mSufaceList = new ArrayList<>();
    /**
     * 是否调用了打开相机，如果是则不需要在此打开
     */
    private boolean openCamera = false;
    private CaptureRequest.Builder captureRequest;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private Camera1Manager() {
        mHandler = new CameraHandler(this);
    }

    /**
     * 通过无所的方式实现线程安全的单例模式
     */
    public static class SingleHodler {
        private static final Camera1Manager instance = new Camera1Manager();
    }

    public static Camera1Manager getInstance() {
        return SingleHodler.instance;
    }


    public TextureView.SurfaceTextureListener getTextureListener(Context context) {
        this.mContext = context;
        return textureListener;
    }

    public void openCamera(Surface surface) {
        if (!openCamera) {
            synchronized (this) {
                mSufaceList.add(surface);
                Message message = mHandler.obtainMessage(OPEN_CAMERA);
                message.sendToTarget();
            }
        }
    }

    TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture texture, int width, int height) {
            texture.setDefaultBufferSize(PREVIEW_WIDTH, PREVIEW_HEIGHT);
            Surface surface = new Surface(texture);
            openCamera(surface);

        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

    private CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            try {
                captureRequest = camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
            for (Surface surface : mSufaceList) {

                captureRequest.addTarget(surface);
            }
            try {
                camera.createCaptureSession(mSufaceList, sessionCallback, null);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {

        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {

        }
    };

    CameraCaptureSession.StateCallback sessionCallback = new CameraCaptureSession.StateCallback() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onConfigured(@NonNull CameraCaptureSession session) {
            try {
                session.setRepeatingRequest(captureRequest.build(), null, null);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession session) {

        }
    };

    public CameraDevice.StateCallback getStateCallback() {
        return mStateCallback;
    }

    public Context getContext() {
        return mContext;
    }

    public Looper getLooper() {
        HandlerThread handlerThread = new HandlerThread(getClass().getSimpleName());
        handlerThread.start();
        return handlerThread.getLooper();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    static class CameraHandler<T extends Camera1Manager> extends Handler {

        private WeakReference<T> mCarmare1Manager;


        public CameraHandler(T manager) {
            super(manager.getLooper(), null);
            this.mCarmare1Manager = new WeakReference<>(manager);
        }

        @SuppressLint("MissingPermission")
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == OPEN_CAMERA) {
                if (mCarmare1Manager.get().getContext() == null) {
                }
                CameraManager cameraManager = (CameraManager) mCarmare1Manager.get()
                        .getContext().getSystemService(Context.CAMERA_SERVICE);
                try {
                    cameraManager.openCamera(REAR_FACING_CAMAREID, mCarmare1Manager.get()
                            .getStateCallback(), null);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }

            }
        }
    }

}

