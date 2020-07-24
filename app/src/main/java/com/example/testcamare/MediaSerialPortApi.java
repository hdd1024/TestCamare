package com.example.testcamare;

import com.example.testcamare.serialport.SerialPortHelper;
import com.example.testcamare.serialport.state.IDataCallback;
import com.example.testcamare.utils.ByteUtil;

public final class MediaSerialPortApi {
    //视频输入交叉ID
    public static final String HDMI_IN_1 = " 01";
    public static final String HDMI_IN_2 = " 02";
    public static final String HDMI_IN_3 = " 03";
    public static final String _3531 = " 04";//3531原生输出 导播输出
    public static final String _3531_9022 = " 08";//3531 输出（9022）SPI输出
    public static final String _OPS = " 05";//OPS输出
    public static final String HDMI_3399 = " 06";//3399 HDMI 输出
    public static final String MIPI_3399 = " 07";//3399 MIPI 输出
    //视频输出交叉ID
    public static final String MIPI_3399_IN_1 = " 01";//3399 MIPI 输入1
    public static final String MIPI_3399_IN_2 = " 02";//3399 MIPI 输入2
    public static final String _3531_IN_7508 = " 03";//3531 输入（7508）
    public static final String HDMI_RESERVED = " 04";//预留
    public static final String HDMI_OUT_1 = " 05";
    public static final String HDMI_OUT_2 = " 06";
    public static final String HDMI_OUT_3 = " 07";
    public static final String HDMI_OUT_4 = " 08";
    public static final String NOT_SHOW = " 00";//无数据、关闭屏幕使用
    /**
     * 同步相机mipi二进制码，此码可用于观察室使用
     */
    public static byte[] sSyncMipi = null;
    /**
     * USB切换码
     */

    // 鼠标跟键盘切换到对应通道
    public static final String USB_CH1 = "56 30 43 53 0E";//USB1通道
    public static final String USB_CH2 = "56 31 38 53 0E";//USB2通道
    public static final String USB_CH3 = "56 35 45 53 0E";//3399通道
    public static final String USB_CH4 = "56 30 38 53 0E";//3531通道
    //四路同步控制
    public static final String USB_CH5 = "0x56 0x32 0x34 0x53 0x0E";


    /**
     * 3399 HDMI 输出带hdmi_out1
     * <p>
     * 3399输入口为06，输出口为05即HDMI_OUT_1
     */
    public static byte[] hdmi3399Out1() {
        return contractCode(MIPI_3399, HDMI_OUT_1);
    }

    /**
     * 将3399的HDMI输出到屏幕2
     *
     * @return
     */
    public static byte[] toMaxHDMI() {
        sSyncMipi = contractCode(HDMI_3399, MIPI_3399_IN_1);
        return contractCode(HDMI_3399, HDMI_OUT_2);
    }

    /**
     * 教师机协议码
     * <p>
     * ops输入口为05，输出口为06即HDMI_OUT_2
     */
    public static byte[] teacherMachineCode() {
        sSyncMipi = contractCode(HDMI_IN_3, MIPI_3399_IN_1);
        return contractCode(HDMI_IN_3, MIPI_3399_IN_2);
    }

    public static String getTeacherMachineCode_in() {
        return HDMI_IN_3;
    }

    /**
     * 无线投屏协议码
     * <p>
     * HDMI_3输入口为03，输出口为06即HDMI_OUT_2
     */
    public static byte[] touPingCode() {
        sSyncMipi = contractCode(HDMI_IN_2, MIPI_3399_IN_1);
        return contractCode(HDMI_IN_2, MIPI_3399_IN_2);
    }

    /**
     * 笔记本协议码
     * 笔记本输入口为01即HDMI_IN_1，输出口为06即HDMI_OUT_2
     */
    public static byte[] notbookeCode() {
        sSyncMipi = contractCode(HDMI_IN_1, MIPI_3399_IN_1);
        return contractCode(HDMI_IN_1, MIPI_3399_IN_2);
    }

    /**
     * 导播/sip给后屏
     *
     * @return
     */
    public static byte[] backScreen() {
        return contractCode(_3531, HDMI_OUT_3);
    }

    /**
     * 导播/sip给相机2
     *
     * @return
     */
    public static byte[] daoBo() {
        sSyncMipi = contractCode(_3531, MIPI_3399_IN_1);
        return contractCode(_3531, MIPI_3399_IN_2);
    }

    /**
     * 远端只看大屏
     *
     * @param inputCode 输入当前大屏的串口码
     * @return
     */
    public static byte[] onlyShowMaxScreen(String inputCode) {
        return contractCode(inputCode, _3531_IN_7508);
    }

    /**
     * 关闭后屏 随便切换一个不可使用的流
     *
     * @return
     */
    public static byte[] not_show_HDMI_IN_3() {
        return contractCode(NOT_SHOW, HDMI_OUT_3);
    }

    /**
     * 关闭大屏 随便切换一个不可使用的流
     *
     * @return
     */
    public static byte[] not_show_HDMI_IN_2() {
        sSyncMipi = contractCode(NOT_SHOW, MIPI_3399_IN_1);
        return contractCode(NOT_SHOW, HDMI_OUT_2);
    }


    /*************************USB切换***********************************/

    /**
     * 鼠标键盘切换到USB1路
     */
    public static byte[] usb1() {
        return usbCode(USB_CH1);
    }

    /**
     * 鼠标键盘切换到USB2路
     */
    public static byte[] usb2() {
        return usbCode(USB_CH2);
    }

    /**
     * 鼠标键盘切换到3399
     */
    public static byte[] usbTo3399() {
        return usbCode(USB_CH3);
    }

    /**
     * 鼠标键盘切换到3531
     */
    public static byte[] usbTo3531() {
        return usbCode(USB_CH4);
    }

    /**
     * 四路同步控制
     */
    public static byte[] usbTo_CH4() {
        return usbCode(USB_CH4);
    }


    public static byte[] usbCode(String code) {
        code = code.replace(" ", "");
        return ByteUtil.hexStr2bytes(code);
    }

    /**
     * 获取协议码
     * 当上面定义的方法不能满足切换的时候，可以调用此方法自定义切开方式
     *
     * @param inputCode  视频输入
     * @param outputCode 视频输出
     * @return 返回计算好的通讯码
     */
    public static byte[] contractCode(String inputCode, String outputCode) {

        String commandId = "42";
        String issues = inputCode + " 01 " + outputCode;
        return SerialPortHelper.instance().generateFullCommands(commandId, issues);
    }

    /**
     * HDMI输入的数据状态查询
     *
     * @param link 01 笔记本、02 无线投屏、03、教师机、ff代表全查
     * @return 截取返回数据的第18位字节，01代表有信号、00代表无信号
     * 如果 link是255 那么第18位值是按照hdmi_in的比特位返回 也就是
     * 返回 01 笔记本、02 无线投屏、03 教师机、如果不包含那个位那么就说明哪一个hdmi_in无信号
     */
    public static void contractStatusCode(String link, IDataCallback dataCallback) {
        SerialPortHelper.instance().setCallback(dataCallback).commandId("51", link);
    }


    /**
     * 获取大智主板rtc的时间
     */
    public static void rtcTime() {
        SerialPortHelper.instance().soonCommandId("54");
    }

    /**
     * 设置大智主板rtc的时间
     */
    public static void rtcSetTime(String year, String month, String day,
                                  String hour, String minute, String second) {
        String commandId = "53";
        String issues = toHexString(year)
                + " " + toHexString(month) + " " + toHexString(day) + " " + toHexString(hour)
                + " " + toHexString(minute) + " " + toHexString(second);
        SerialPortHelper.instance().soonCommandId(commandId, issues);
    }


    /**
     * ops设置
     *
     * @param code 01 开机、02、休眠、ff 关机
     * @return 截取返回数据的第18位字节，0代表成功
     */
    public static void opsControl(String code) {
        String commandId = "52";
        SerialPortHelper.instance().soonCommandId(commandId, code);

    }

    public static String toHexString(String s) {
        int h = Integer.parseInt(s);
        String hexString = Integer.toHexString(h);
        if (hexString.length() == 1 || hexString.length() == 3) {
            hexString = "0" + hexString;
        }
        return hexString;
    }
}
