package com.example.testcamare;

import android.app.Presentation;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.display.DisplayManager;
import android.os.Bundle;
import android.view.Display;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;
import com.example.testcamare.cameracollect.Camera2Manager;
import com.example.testcamare.cameracollect.Camera3Manager;
import com.example.testcamare.cameracollect.Camera4Manager;
import com.example.testcamare.cameracollect.statecamera2.CameraHelper;
import com.example.testcamare.utils.JBDeviceUtil;
import com.example.testcamare.utils.LogUtilFromSDK;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/***********************************************************
 * 创建时间:2019-12-07
 * 作   者: [韩明泽]
 * 功能描述: <大屏投屏类>
 * 备注信息: {}
 * @see
 **********************************************************/
public class MaxScreenPresentation extends Presentation implements TextureView.SurfaceTextureListener {

    private TextureView svMaxScreenPresentation;
    private FrameLayout flPreviewMaxScreen;
    private Disposable mSubscribeClos;
    private Context mCntext;

    /**
     * 投屏初始化类
     *
     * @param context
     * @return
     */

    public static MaxScreenPresentation maxScreen(Context context) {
//        MediaRouter mediaRouter = (MediaRouter) context.getSystemService(Context.MEDIA_ROUTER_SERVICE);
//        MediaRouter.RouteInfo selectedRoute = mediaRouter.getSelectedRoute(MediaRouter.ROUTE_TYPE_LIVE_AUDIO);
//        Display display = selectedRoute != null ? selectedRoute.getPresentationDisplay() : null;
//        if (display != null) {
//            MaxScreenPresentation myPresentationOne = new MaxScreenPresentation(context.getApplicationContext(), display);
//            myPresentationOne.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
//            return myPresentationOne;
//        } else {
//            Toast.makeText(context, "不支持分屏", Toast.LENGTH_SHORT).show();
//        }
        return initPresentation(context);
//        return null;
    }


    /**
     * 初始化第二块屏幕
     **/
    public static MaxScreenPresentation initPresentation(Context context) {
        DisplayManager mDisplayManager = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
        Display[] displays = mDisplayManager.getDisplays();
        for (Display display : displays) {
            LogUtilFromSDK.getInstance().d("屏幕信息为:" + display.getName());
        }
        if (displays.length > 1) {
            // displays[1]是副屏
            MaxScreenPresentation presentation = new MaxScreenPresentation(context.getApplicationContext(), displays[1]);
            presentation.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            return presentation;
        } else {
            Toast.makeText(context, "不支持分屏", Toast.LENGTH_SHORT).show();
        }
        return null;
    }


    private MaxScreenPresentation(Context outerContext, Display display) {
        super(outerContext, display);
        this.mCntext = outerContext.getApplicationContext();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.max_screen_presentationone);
        svMaxScreenPresentation = findViewById(R.id.sv_maxScreenPresentation);
        svMaxScreenPresentation.setVisibility(View.GONE);
        flPreviewMaxScreen = findViewById(R.id.fl_preview_max_screen);
        svMaxScreenPresentation.setSurfaceTextureListener(this);
    }


    @Override
    public void show() {
        super.show();
        svMaxScreenPresentation.setVisibility(View.VISIBLE);

        //如果当前相机如信号，则根据相机的是否调用画面捕获失败函数来确定相机是否初始化完成
        //如果初始化完成，则关闭loding框
        int singa = JBDeviceUtil.getSinga(JBDeviceUtil.TEACHER_MACHINE_FLAG);
        if (singa == 0) {
            mSubscribeClos = Observable.interval(1, TimeUnit.SECONDS)
                    .subscribe(aLong -> {
                        if (Camera2Manager.capture_failed == 1) {
                            mSubscribeClos.dispose();
                            Camera2Manager.capture_failed = 0;
                        }
                    });
        }
    }


    private boolean surfaceUpdate = true;


    @Override
    public void dismiss() {
        super.dismiss();
//        Camera2Manager.init().claseSsionStop();
//        Camera3Manager.getInstance().closeCamera();
//        Camera4Manager.getInstance().closeCamera();
        CameraHelper.getInstance().closeCamera();
        svMaxScreenPresentation = null;
        if (mSubscribeClos != null) {
            mSubscribeClos.dispose();
            mSubscribeClos = null;
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture texture, int width, int height) {
        LogUtilFromSDK.getInstance().d("大屏相机Surface创建");

//        texture.setDefaultBufferSize(1920, 1080);
//        Surface surface = new Surface(texture);
//
//        Camera2Manager.init()
//                .addSurface(surface)
//                .openCamera(getContext(), "0");
//        try {
//            Camera3Manager.getInstance().openCamera(getContext(),texture);
//        } catch (TimeoutException e) {
//            e.printStackTrace();
//        }
//        Camera4Manager.getInstance().openCamera(getContext(),texture);
        CameraHelper.getInstance().openCamera(getContext(), texture);

    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        if (surfaceUpdate) {
            LogUtilFromSDK.getInstance().d("大屏相机Surface改变了");
            flPreviewMaxScreen.setVisibility(View.GONE);
            surfaceUpdate = false;
        }
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        LogUtilFromSDK.getInstance().d("大屏相机Surface销毁");
        return false;
    }


    private boolean updata = true;

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        flPreviewMaxScreen.setVisibility(View.GONE);
//        LogUtilFromSDK.instance().d("大屏相机Surface更新");
        if (updata) {
            //2.5秒关闭启动加载框，防止重复操作退出间隔太短，导致再次登陆大屏白屏的问题
            Observable.timer(800, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(aLong -> {

                    });
            updata = false;
        }
    }

}
