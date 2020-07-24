package com.example.testcamare;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.example.testcamare.cameracollect.Camera2Manager;
import com.example.testcamare.serialport.SerialPortHelper;
import com.example.testcamare.utils.JBDeviceUtil;

public class CamarexActivity extends AppCompatActivity {

    private MaxCameraXScreenPresentation mMaxScreenPre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camarex);

        mMaxScreenPre = MaxCameraXScreenPresentation.maxScreen(this);
        getLifecycle().addObserver(mMaxScreenPre);
        if (mMaxScreenPre != null) {
            mMaxScreenPre.show();
        }

        byte[] screenBytes = MediaSerialPortApi.teacherMachineCode();
        SerialPortHelper.instance().sendBytes(screenBytes);
        Camera2Manager.device_now = JBDeviceUtil.TEACHER_MACHINE_FLAG;
    }

    public void cameraXuPing(View view) {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fl_camerax, new CamareXFramgent())
                .commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mMaxScreenPre != null) {
            mMaxScreenPre.dismiss();
            mMaxScreenPre = null;
        }
    }
}
