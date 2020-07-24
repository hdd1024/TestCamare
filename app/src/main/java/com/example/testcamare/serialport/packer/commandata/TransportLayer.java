package com.example.testcamare.serialport.packer.commandata;


import androidx.annotation.Keep;

/**
 * 串口数据的传输层数据类
 */
@Keep
public class TransportLayer {
    //目的端口号 p[9]、目的端口号 p[9]、分包个数p[11]、当前分包ID P[12]
    private byte[] transportYers = {0x02, 0x02, 0x01, 0x00};


    /**
     * 返回该类的数组，该数组中值为p9到p12
     *
     * @return
     */
    public byte[] getP9_P12() {
        return this.transportYers;
    }

}