package com.example.testcamare;

import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.testcamare.cameracollect.Camera2Manager;
import com.example.testcamare.cameracollect.Camera3Manager;
import com.example.testcamare.cameracollect.Camera4Manager;
import com.example.testcamare.cameracollect.statecamera2.CameraHelper;
import com.example.testcamare.utils.LogUtilFromSDK;

import java.util.concurrent.TimeoutException;

public class CameraFragment extends Fragment {
    FrameLayout fl_perview;
    TextureView sv_camera_capture;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View viewInflater = inflater.inflate(R.layout.fragment_camera, null, false);
        fl_perview = viewInflater.findViewById(R.id.fl_perview);
        sv_camera_capture = viewInflater.findViewById(R.id.sv_camera_capture);
        LogUtilFromSDK.getInstance().e("测试相机fragment的onCreateView");
        return viewInflater;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sv_camera_capture.setVisibility(View.VISIBLE);
        if (sv_camera_capture.isAvailable()) {
//            try {
//                Camera3Manager.getInstance().openCamera(getContext(), sv_camera_capture.getSurfaceTexture());
//            } catch (TimeoutException e) {
//                e.printStackTrace();
//            }
//            Camera4Manager.getInstance().openCamera(getContext(),sv_camera_capture.getSurfaceTexture());
            CameraHelper.getInstance().openCamera(getContext(),sv_camera_capture.getSurfaceTexture());

        } else {
            sv_camera_capture.setSurfaceTextureListener(textureListener);
        }
        LogUtilFromSDK.getInstance().e("测试相机fragment的onViewCreated");


    }

    TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture texture, int width, int height) {
//            texture.setDefaultBufferSize(1920, 1080);
//            Surface surface = new Surface(texture);
//            Camera2Manager.init()
//                    .addSurface(surface)
//                    .openCamera(getContext(), Camera2Manager.REAR_FACING_CAMAREID);
//                Camera3Manager.getInstance().openCamera(getContext(),texture);
//            Camera4Manager.getInstance().openCamera(getContext(),texture);
            CameraHelper.getInstance().openCamera(getContext(),texture);

            LogUtilFromSDK.getInstance().e("测试相机fragment的onSurfaceTextureAvailable");
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
//            svCameraCapture.setVisibility(View.GONE);
//            LogUtilFromSDK.instance().e("相机surface改变了");
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
//       LogUtilFromSDK.instance().e("相机surface销毁啊");
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
//            LogUtilFromSDK.instance().e("相机surface更新了");
        }

    };

    @Override
    public void onDestroyView() {
//        Camera2Manager.init().claseSsionStop();
//        Camera4Manager.getInstance().closeCamera();
        CameraHelper.getInstance().closeCamera();
        sv_camera_capture = null;
        LogUtilFromSDK.getInstance().e("测试相机fragment的onDestroyView");
        super.onDestroyView();
    }
}
