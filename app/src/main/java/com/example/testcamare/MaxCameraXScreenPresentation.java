package com.example.testcamare;

import android.annotation.SuppressLint;
import android.app.Presentation;
import android.content.Context;
import android.hardware.display.DisplayManager;
import android.os.Bundle;
import android.util.Rational;
import android.util.Size;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

import com.example.testcamare.utils.LogUtilFromSDK;
import com.google.common.util.concurrent.ListenableFuture;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutionException;


/***********************************************************
 * 创建时间:2019-12-07
 * 作   者: [韩明泽]
 * 功能描述: <大屏投屏类>
 * 备注信息: {}
 * @see
 **********************************************************/
public class MaxCameraXScreenPresentation<T extends AppCompatActivity> extends Presentation implements LifecycleObserver {

    private PreviewView pv_maxScreenPresentation;
    private FrameLayout fl_max_screen_camerax;

    ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

    private WeakReference<T> mContext;

    /**
     * 投屏初始化类
     *
     * @param context
     * @return
     */

    public static MaxCameraXScreenPresentation maxScreen(Context context) {
        return initPresentation(context);
    }


    /**
     * 初始化第二块屏幕
     **/
    public static MaxCameraXScreenPresentation initPresentation(Context context) {
        DisplayManager mDisplayManager = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
        Display[] displays = mDisplayManager.getDisplays();
        for (Display display : displays) {
            LogUtilFromSDK.getInstance().d("屏幕信息为:" + display.getName());
        }
        if (displays.length > 1) {
            // displays[1]是副屏
            MaxCameraXScreenPresentation presentation = new MaxCameraXScreenPresentation(context, displays[1]);
            presentation.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            return presentation;
        } else {
            Toast.makeText(context, "不支持分屏", Toast.LENGTH_SHORT).show();
        }
        return null;
    }


    private MaxCameraXScreenPresentation(Context outerContext, Display display) {
        super(outerContext, display);
        this.mContext = new WeakReference<T>((T) outerContext);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.max_camerax_screen_presentationone);
        pv_maxScreenPresentation = findViewById(R.id.pv_maxScreenPresentation);
        fl_max_screen_camerax = findViewById(R.id.fl_max_screen_camerax);
    }

    private void bindPreview(ProcessCameraProvider cameraProvider) {

        @SuppressLint("RestrictedApi")
        Preview preview = new Preview.Builder()
                .setTargetResolution(new Size(1920, 1080))
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner) mContext.get(), cameraSelector, preview);

        preview.setSurfaceProvider(pv_maxScreenPresentation.createSurfaceProvider(camera.getCameraInfo()));

    }


    @Override
    public void show() {
        super.show();
        pv_maxScreenPresentation.setVisibility(View.VISIBLE);
        cameraProviderFuture = ProcessCameraProvider.getInstance(getContext());
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(getContext()));

        fl_max_screen_camerax.setVisibility(View.GONE);
    }


    @Override
    public void dismiss() {
        super.dismiss();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {

        dismiss();

    }
}
