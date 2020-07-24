package com.example.testcamare.cameracollect.statecamera2.state.config;

import com.example.testcamare.app.JbBaseException;

/***********************************************************
 * 创建时间:2020/7/24
 * 作   者: [hanmingze]
 * 功能描述: 自定义相机异常类
 * 备注信息: {该类在{@link DeviceOpenClose#cameraException(String, Exception)}}中
 *          对相机异常进行类统一处理}
 **********************************************************/
public class JbCameraException extends JbBaseException {
    public JbCameraException(String message) {
        super(message);
        exc_id = 4445;
        exceptionType = AppExceptionType.EXC_APP;
    }
}
