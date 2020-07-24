package com.example.testcamare.app;


public class AppException extends JbBaseException {

    public AppException(String message) {
        super(message);
        exceptionType = AppExceptionType.EXC_APP;
    }
}
