package com.example.testcamare.cameracollect;

import android.content.Context;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.view.Surface;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.testcamare.utils.JBDeviceUtil;
import com.example.testcamare.utils.LogUtilFromSDK;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class Camera2Manager {
    private final static Camera2Manager instance = new Camera2Manager();
    /**
     * 前置摄像头
     */
    public static final String FONT_FACING_CAMAREID = "1";
    /**
     * 后置摄像头id
     */
    public static final String REAR_FACING_CAMAREID = "0";
    /**
     * 开启相机
     */
    private final int OPEN = 1;
    /**
     * 关闭相机
     */
    private final int CLOSE_DEIVCE = 2;
    private final int CLOSE_SSION = 3;
    private final int CAPTUER_INIT = 4;
    private final int CLOSE_DEIVCE_AND_SURFACE = 5;
    /**
     * 是否有画面，1代表有画面数据
     */
    public static int device_now = 0;

    /**
     *
     */
    private int close_stop = 0;
    /**
     * 是否重启相机 默认不重启
     */
    private boolean reboot_camera = true;

    private String mCameraId = "0";
    private Handler mHandler;
    private HandlerThread mHandlerThread;
    private List<Surface> mSurfaces;

    private CameraManager mCameraManager;
    private CameraDevice mCameraDevice;
    private CameraCaptureSession mSession;
    /**
     * 相机状态 0代表已经关闭
     */
    private int mCameraColse = 0;
    private Context mContext;

    private Handler.Callback mHandlerCallback = new Handler.Callback() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == OPEN) {
                LogUtilFromSDK.getInstance().e("相机马上开启");
                if (mCameraManager == null) {
                    mCameraManager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
                }

                try {
                    mCameraManager.openCamera(mCameraId, mStateCallback, null);
                    LogUtilFromSDK.getInstance().e("相机马上开启22222");
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtilFromSDK.getInstance().e("相机请求开始失败" + e.getLocalizedMessage());
                    Toast.makeText(mContext, "相机启动失败", Toast.LENGTH_SHORT).show();

                }
            } else if (msg.what == CAPTUER_INIT) {
//                claseSsion2();
                captureInit();
            } else if (msg.what == CLOSE_DEIVCE) {
                LogUtilFromSDK.getInstance().e("相机真的要关闭哦" + System.currentTimeMillis());
                closeCamera2();
            } else if (msg.what == CLOSE_SSION) {
                if (mSession != null) {
                    mSession.close();
                    mSession = null;
                }
                if (mSurfaces != null && mSurfaces.size() != 0) {
                    for (Surface mSurface : mSurfaces) {
                        if (captureRequest != null) {
                            captureRequest.removeTarget(mSurface);
                        }
                    }
                    captureRequest = null;
                    mSurfaces.clear();
                }
                if (cESubscribe != null && !cESubscribe.isDisposed()) {
                    cESubscribe.dispose();
                    tta.set(0);
                    t1.set(0);
                }
            } else if (msg.what == CLOSE_DEIVCE_AND_SURFACE) {
                if (mSession != null) {
                    mSession.close();
                    mSession = null;
                }
                closeCamera2();
                if (mSurfaces != null && mSurfaces.size() != 0) {
                    mSurfaces.clear();
                }
            }
            return false;
        }
    };
    private CaptureRequest.Builder captureRequest;

    private Camera2Manager() {
        mSurfaces = new ArrayList<>();
        mHandlerThread = new HandlerThread(Camera2Manager.class.getName());
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper(), mHandlerCallback);
        chaceCError();
    }

    public static Camera2Manager init() {
        return instance;
    }


    public synchronized Camera2Manager addSurface(Surface surface) {
        //获取Surface显示预览数据
        mSurfaces.add(surface);
        return instance;
    }

    private boolean isOpen;

    public synchronized void openCamera(Context context, String camareId) {
        close_stop = 0;
        if (mContext == null) {
            this.mContext = context;
        }
        if (camareId != null) {
            mCameraId = camareId;
        }
        if (mCameraDevice != null) {
            if (mCameraColse == 1) {
                t1.set(0);
                mHandler.sendEmptyMessage(CAPTUER_INIT);
                LogUtilFromSDK.getInstance().e("相机init");
            }
        } else {
            if (!isOpen) {
                mHandler.sendEmptyMessage(OPEN);
                isOpen = true;
                LogUtilFromSDK.getInstance().e("请求开启相机==" + mCameraColse);

            }
            LogUtilFromSDK.getInstance().e("相机open==" + mCameraColse);
        }

    }


    CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            mCameraDevice = camera;
            captureInit();
            mCameraColse = 1;
            LogUtilFromSDK.getInstance().e("相机打开了");
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            LogUtilFromSDK.getInstance().e("相机断开连接");
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            LogUtilFromSDK.getInstance().e("相机打开异常");
            capture_failed = 1;
        }

        @Override
        public void onClosed(@NonNull CameraDevice camera) {
            super.onClosed(camera);
            mCameraColse = 0;
            isOpen = false;
            LogUtilFromSDK.getInstance().e("相机关闭");
            if (reboot_camera) {
                openCamera(mContext, mCameraId);
                reboot_camera = false;
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void captureInit() {
        if (cESubscribe != null && cESubscribe.isDisposed()) {
            chaceCError();
        }
        try {
            captureRequest = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            for (Surface surface : mSurfaces) {
                captureRequest.addTarget(surface);
            }
            mCameraDevice.createCaptureSession(mSurfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    mSession = session;
                    try {
                        session.setRepeatingRequest(captureRequest.build(), mCaptureCallback, null);
                    } catch (Exception e) {
                        e.printStackTrace();
                        LogUtilFromSDK.getInstance().e("Ssion相机setRepeatingRequest>>>>>" + e.getMessage());

                        t1.set(0);

                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                    LogUtilFromSDK.getInstance().e("Ssion相机onConfigureFailed");

                }
            }, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private AtomicLong t1 = new AtomicLong(0);
    private Disposable cESubscribe;
    private AtomicLong tta = new AtomicLong(0);
//    private AtomicLong t2 = new AtomicLong(0);

    private void chaceCError() {
        LogUtilFromSDK.getInstance().e("相机循环为马上开始");
        cESubscribe = Observable.interval(50, TimeUnit.MILLISECONDS, Schedulers.computation())
                .subscribe(aLong -> {
                    if (t1.get() == 0) {
                        return;
                    }
                    if (close_stop == 1) {
                        close_stop = 0;
                        return;
                    }
                    long c = System.currentTimeMillis() - t1.get();
                    if (c > 350) {
                        LogUtilFromSDK.getInstance().e(tta + "相机循环中马上重启。。。" + c);
                        t1.set(0);
//                        t2.set(0);
                        tta.set(0);
                        if (JBDeviceUtil.getSinga(device_now) == 1) {
//                            cESubscribe.dispose();
                            reboot_camera = true;
                            closeCamera2();
                            LogUtilFromSDK.getInstance().e("相机中重启");
                        } else {
                            reboot_camera = false;
                        }
                    }
                });
    }

    public static int capture_failed = 0;


    CameraCaptureSession.CaptureCallback mCaptureCallback = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
            long ti = System.currentTimeMillis();
//            Log.d("相机——————>", "相机预览画面成功");
            if (t1.get() != 0) {
                tta.set(ti - t1.get());
            }
            t1.set(ti);
        }

        @Override
        public void onCaptureFailed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureFailure failure) {
            super.onCaptureFailed(session, request, failure);
            capture_failed = 1;
            LogUtilFromSDK.getInstance().e("相机预览画面数据失败:" + System.currentTimeMillis());
        }
    };


    public void closeCamera() {
        mHandler.sendEmptyMessage(CLOSE_DEIVCE);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void closeCamera2() {

        if (mCameraDevice != null) {
            mCameraDevice.close();
            mCameraDevice = null;
        }

        mCameraManager = null;
    }

    /**
     * 关闭相机ssion
     */
    public synchronized void claseSsionStop() {
        if (close_stop == 1) return;
        Message message = mHandler.obtainMessage(CLOSE_SSION);
        message.sendToTarget();
        close_stop = 1;
    }

    private void claseSsion2() {
        if (mSurfaces != null && mSurfaces.size() != 0) {
            for (Surface mSurface : mSurfaces) {
                if (captureRequest != null) {
                    captureRequest.removeTarget(mSurface);
                }
            }
            captureRequest = null;
        }

        if (mSession != null) {
            try {
                mSession.stopRepeating();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mSession.close();
            mSession = null;
        }

        if (cESubscribe != null && !cESubscribe.isDisposed()) {
            cESubscribe.dispose();
            tta.set(0);
            t1.set(0);
        }
    }

    /**
     * 释放handler、surface数据
     */
    public void closeDeiveAndSurfacs() {
        Message message = mHandler.obtainMessage(CLOSE_DEIVCE_AND_SURFACE);
        message.sendToTarget();
        reboot_camera = false;
        mContext = null;
        if (cESubscribe != null && !cESubscribe.isDisposed()) {
            cESubscribe.dispose();
            tta.set(0);
            t1.set(0);
        }
    }

    private void releaseData() {
        if (mHandlerThread != null) {
            mHandlerThread.quitSafely();
            try {
                mHandlerThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (mHandler != null) {
                mHandler.removeCallbacks(mHandlerThread);
            }
            mHandler = null;
            mHandlerThread = null;
        }
        mCameraManager = null;
        mContext = null;
    }
}
