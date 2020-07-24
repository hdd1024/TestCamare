package com.example.testcamare;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.example.testcamare.cameracollect.Camera2Manager;
import com.example.testcamare.serialport.SerialPortHelper;
import com.example.testcamare.utils.JBDeviceUtil;

public class CamareActivity extends AppCompatActivity {
    private MaxScreenPresentation mMaxScreenPre;
    private CameraFragment cameraFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camare);
        mMaxScreenPre = MaxScreenPresentation.maxScreen(this);
        if (mMaxScreenPre != null) {
            mMaxScreenPre.show();
        }

        byte[] screenBytes = MediaSerialPortApi.teacherMachineCode();
        SerialPortHelper.instance().sendBytes(screenBytes);
        Camera2Manager.device_now = JBDeviceUtil.TEACHER_MACHINE_FLAG;

        cameraFragment=new CameraFragment();
    }

    public void openXuPing(View view) {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fl_camare_fragment,cameraFragment)
                .commit();
    }

    public void teacherMachine(View view) {
        byte[] screenBytes = MediaSerialPortApi.teacherMachineCode();
        SerialPortHelper.instance().sendBytes(screenBytes);
        Camera2Manager.device_now = JBDeviceUtil.TEACHER_MACHINE_FLAG;
    }

    public void laptop(View view) {
        byte[] screenBytes = MediaSerialPortApi.notbookeCode();
        SerialPortHelper.instance().sendBytes(screenBytes);
        Camera2Manager.device_now = JBDeviceUtil.TEACHER_MACHINE_FLAG;
    }

    public void wireless(View view) {
        byte[] screenBytes = MediaSerialPortApi.touPingCode();
        SerialPortHelper.instance().sendBytes(screenBytes);
        Camera2Manager.device_now = JBDeviceUtil.TEACHER_MACHINE_FLAG;
    }

    public void remotedesktop(View view) {
        byte[] screenBytes = MediaSerialPortApi.daoBo();
        SerialPortHelper.instance().sendBytes(screenBytes);
        Camera2Manager.device_now = JBDeviceUtil.TEACHER_MACHINE_FLAG;
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
