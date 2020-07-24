package com.example.testcamare.app;

public class JbBaseException extends Exception {
    protected int exc_id = 4440;
    protected AppExceptionType exceptionType = AppExceptionType.EXC_BASE;
    private int code;

    public JbBaseException(String message) {
        super(message);
    }

    public JbBaseException(String message, int code) {
        super(message);
        this.code = code;
    }

    public void setCode(int code) {
        this.code = code;
    }


    public int getCode() {
        return code;
    }

    public AppExceptionType getExceptionType() {
        return exceptionType;
    }

    public int getExcUuid() {
        return exc_id;
    }

    public enum AppExceptionType {
        EXC_BASE,
        EXC_APP,
        EXC_DEVICE
    }

}
