package com.example.testcamare.cameracollect.statecamera2.state;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.SparseArray;
import com.example.testcamare.cameracollect.statecamera2.state.config.CameraParams;
import com.example.testcamare.cameracollect.statecamera2.state.config.DeviceOpenClose;

/***********************************************************
 * 创建时间:2020/7/24
 * 作   者: [hanmingze]
 * 功能描述: 控制状态类的上下文类
 * 备注信息: {该类中用于对客户端提供了操作相机的开启和关闭功能，也初始化了
 *           开启相机的很多配置参数类}
 **********************************************************/
public class CameraContext2 {
    private static final String TAG = CameraContext2.class.getSimpleName();
    final CloseCameraState CLOSE_CAMERA_STATE = new CloseCameraState();
    final OpenCameraState OPEN_CAMERA_STATE = new OpenCameraState();
    public CameraState2 mCurrentState;
    private ContextHandler handler;
    private HandlerThread handlerThread;
    private CameraParams cameraParams;
    private DeviceOpenClose deviceOpenClose;

    public CameraContext2() {
        handlerThread = new HandlerThread(TAG + "_HandlerThread");
        handlerThread.start();
        handler = new ContextHandler(handlerThread.getLooper());
        cameraParams = new CameraParams();
        deviceOpenClose = new DeviceOpenClose(cameraParams);
        //第一次启动默认是开启状态
        setCurrentState(OPEN_CAMERA_STATE);
    }

    /**
     * 设置当前状态
     *
     * @param currentState
     */
    void setCurrentState(CameraState2 currentState) {
        mCurrentState = currentState;
        mCurrentState.setCameraContext(this);
        mCurrentState.setCameraParams(cameraParams);
        mCurrentState.setDeviceOpenClose(deviceOpenClose);
        handler.setCurrentState(mCurrentState);
    }

    /**
     * 开启相机
     * 该方法会在UI线程或者其他线程中调用
     *
     * @param context        上下文
     * @param surfaceTexture 渲染
     */
    public void openCamera(Context context, SurfaceTexture surfaceTexture) {
        //如果除了UI线程还有替他线程设置 渲染Surface 那么要考虑并发问题
        if (!cameraParams.getSurfaceTextureList().contains(surfaceTexture)) {
            cameraParams.getSurfaceTextureList().add(surfaceTexture);
        }
        cameraParams.setContext(context);
        handler.obtainMessage(handler.CAMERA_OPEN).sendToTarget();
    }

    /**
     * 关闭线程
     * 该方法会在UI线程或者其他线程中调用
     */
    public void closeCamera() {
        handler.obtainMessage(handler.CAMERA_CLOSE).sendToTarget();
        //List<SurfaceTexture> 条件和创建都交给CameraContext管理
        cameraParams.getSurfaceTextureList().clear();
    }

    ContextHandler getHandler() {
        return handler;
    }

    public static class ContextHandler extends Handler {
        final int CAMERA_OPEN = 0x1;
        final int CAMERA_CLOSE = 0x2;
        //发送开启的循环消息标记
        public static final int CAMERA_OPEN_DELAYED = 0x11;
        //发送关闭循环消息标记
        public static final int CAMERA_CLOSE_DELAYED = 0x22;
        private SparseArray<SendDelayed> delayedSparseArray;
        private CameraState2 mCurrentState;

        /**
         * 设置当前状态
         * 为了防止多线程并发问题，该方法一定要在和handler同一个线程中调用，建议在
         * {@link CameraContext2#setCurrentState(CameraState2)} 方法中调用 }
         *
         * @param currentState 当前状态
         */
        public void setCurrentState(CameraState2 currentState) {
            this.mCurrentState = currentState;
        }

        public ContextHandler(Looper looper) {
            super(looper);
        }

        /**
         * 发送延迟循环消息
         * 该函数可以使用{@link Handler#removeMessages(int) 方法 取消消息}
         * 如果使用的是{@link Handler#postDelayed(Runnable, long) 则无法取消消息}
         * 因为调用{@link Handler#removeCallbacksAndMessages(Object) 取消消息的时候回报一个异常}
         *
         * @param what        消息标记
         * @param delayMillis 延迟多少秒
         * @param sendDelayed 回调函数
         */
        public void setSendDelayed(int what, long delayMillis, SendDelayed sendDelayed) {
            if (this.delayedSparseArray == null) {
                this.delayedSparseArray = new SparseArray<>();
            }
            this.delayedSparseArray.put(what, sendDelayed);
            removeMessages(what);
            sendEmptyMessageDelayed(what, delayMillis);
        }


        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CAMERA_OPEN:
                    mCurrentState.openCamera();
                    break;
                case CAMERA_CLOSE:
                    mCurrentState.closeCamera();
                    break;
                case CAMERA_OPEN_DELAYED:
                case CAMERA_CLOSE_DELAYED:
                    //处理循环消息
                    if (this.delayedSparseArray != null) {
                        SendDelayed sendDelayed = delayedSparseArray.get(msg.what);
                        if (sendDelayed != null)
                            sendDelayed.onDelayed(msg.what);
                    }
                    break;
            }
        }

        /**
         * 该函数用于处理发送的{@link CloseCameraState#openCamera() 开启请求}和
         * {@link CloseCameraState#closeCamera() 关闭请求}延迟消息
         */
        public interface SendDelayed {
            void onDelayed(int what);
        }
    }
}
