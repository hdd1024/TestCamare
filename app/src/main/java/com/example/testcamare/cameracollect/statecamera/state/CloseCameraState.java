package com.example.testcamare.cameracollect.statecamera.state;

import android.hardware.camera2.CameraDevice;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import com.example.testcamare.cameracollect.statecamera.state.CameraContext.ContextHandler;
import com.example.testcamare.cameracollect.statecamera.state.config.OnDeviceClosed;
import com.example.testcamare.utils.LogUtilFromSDK;

/***********************************************************
 * 创建时间:2020/7/24
 * 作   者: [hanmingze]
 * 功能描述: 相机关闭状态
 * 备注信息: {该类中有两种状态 CLOSE_CAMERA_INIT 关闭初始态、CLOSE_CAMERA关闭完成状态}
 **********************************************************/
public class CloseCameraState extends CameraState implements OnDeviceClosed {
    //关闭 状态 初始化
    private final int CLOSE_CAMERA_INIT = 440;
    //关闭 状态
    private final int CLOSE_CAMERA = 444;
    //当前状态
    private int currentState = CLOSE_CAMERA_INIT;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void openCamera() {
        LogUtilFromSDK.getInstance().d("openCamera相机状态：" + currentState);
        //该延迟消息之后存在一个
        mCameraContext.getHandler().setSendDelayed(ContextHandler.CAMERA_CLOSE_DELAYED,
                500, new ContextHandler.SendDelayed() {
                    @Override
                    public void onDelayed(int what) {
                        LogUtilFromSDK.getInstance().d("openCamera---getHandler相机状态：" + currentState);
                        //递归
                        openCamera();
                    }
                });
        //如果相机未完全关闭，那么让上面的递归 循环调用openCamera()
        //如果相机完全关闭，那么就请求所有的请求打开相机、请求关闭相机的延迟消息
        //并将状态类切换到开启相机状态类中
        if (currentState != CLOSE_CAMERA) return;
        mCameraContext.getHandler().removeMessages(ContextHandler.CAMERA_CLOSE_DELAYED);
        //防止在相机打开后，存在请求关闭相机。这样会导致再次请求打开画面停留在上一帧
        mCameraContext.getHandler().removeMessages(ContextHandler.CAMERA_OPEN_DELAYED);
        mCameraContext.setCurrentState(mCameraContext.OPEN_CAMERA_STATE);
        //执行开启状态类中开启相机函数
        mCameraContext.mCurrentState.openCamera();
        currentState = CLOSE_CAMERA_INIT;
    }

    /**
     * 请求关闭相机
     * 每次请求都视为相机还行初始化中，都会通过{@link ContextHandler#setSendDelayed(int, long, ContextHandler.SendDelayed)}发生
     * 延迟消息，setSendDelayed() 函数会在发送前通过当前what删除已经存在的消息。保证了不会发送重发消息，
     * 只有{@link #onClosed(CameraDevice)}相机关闭完成后，会取消延迟的递归函数。
     */
    @Override
    public void closeCamera() {
        LogUtilFromSDK.getInstance().d("closeCamera相机状态：" + currentState);
        if (currentState != CLOSE_CAMERA_INIT) return;
        mCameraContext.getHandler().setSendDelayed(ContextHandler.CAMERA_OPEN_DELAYED, 500,
                new ContextHandler.SendDelayed() {
                    @Override
                    public void onDelayed(int what) {
                        LogUtilFromSDK.getInstance().d("closeCamera----getHandler相机状态：" + currentState);
                        //递归调用关闭函数
                        closeCamera();
                    }
                });
        //设置监听
        deviceOpenClose.registerOnDeviceClosed(this);
        //即使上面的递归会循环调用closeCamera()但是也只会请求一次close()
        if (cameraParams.getCameraSession() != null) {
            cameraParams.getCameraSession().close();
            cameraParams.setCameraSession(null);
        }
        if (cameraParams.getCameraDevice() != null) {
            cameraParams.getCameraDevice().close();
            cameraParams.setCameraDevice(null);
        }
    }

    /**
     * 相机完全关闭会回调该方法
     *
     * @param camera 相机
     */
    @Override
    public void onClosed(@NonNull CameraDevice camera) {
        currentState = CLOSE_CAMERA;
        LogUtilFromSDK.getInstance().d("onClosed--相机已经关闭喽！--相机状态：" + currentState);
        //异触请求请求关闭的延迟消息
        mCameraContext.getHandler().removeMessages(ContextHandler.CAMERA_OPEN_DELAYED);
        //清空session回话的时候创建的Surface
        if (cameraParams.getSurfaceList() != null) {
            cameraParams.getSurfaceList().clear();
        }
    }
}
