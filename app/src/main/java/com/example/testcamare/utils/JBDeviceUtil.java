package com.example.testcamare.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import androidx.annotation.RequiresPermission;
import com.firefly.api.FireflyApi;
import com.firefly.api.shell.Command;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import static android.Manifest.permission.INTERNET;
import static android.content.Context.ACTIVITY_SERVICE;

/**
 * 设备工具类
 */
public class JBDeviceUtil {
    /**
     * 笔记本标记
     */
    public static final int LAPTOP_FLAG = 0x01;
    /**
     * 无线投屏标记
     */
    public static final int WIRELESS_SCREEN_FLAG = 0x02;
    /**
     * 教师机标记
     */
    public static final int TEACHER_MACHINE_FLAG = 0x04;
    /**
     * 远程教室标记
     */
    public static final int REMOTE_ROOM_FLAG = 0x03;
    private static volatile int laptop_singal = 0;
    private static volatile int wireless_screen = 0;
    private static volatile int teacher_machine = 0;

    public static final String DAZHI = "dazhi";
    public static final String XIAOHUI = "xiaohui";

    private JBDeviceUtil() {
    }

    /**
     * 获取屏幕切换的信号状态
     *
     * @param device 当前选择的显示设备标记
     * @return 0代表为无信号、1代表有信号
     */
    public static int getSinga(int device) {
        switch (device) {
            case TEACHER_MACHINE_FLAG:
                //教师机
                return teacher_machine;
            case LAPTOP_FLAG:
                //笔记本
                return laptop_singal;
            case WIRELESS_SCREEN_FLAG:
                //无线投屏
                return wireless_screen;
            case REMOTE_ROOM_FLAG:
                return 1;
            default:
                return 1;
        }
    }

    public static void setSinga(int device, int singal) {
        switch (device) {
            case TEACHER_MACHINE_FLAG:
                //教师机
                teacher_machine = singal;
                break;
            case LAPTOP_FLAG:
                //笔记本
                laptop_singal = singal;
                break;
            case WIRELESS_SCREEN_FLAG:
                //无线投屏
                wireless_screen = singal;
                break;
            default:
        }
    }

    /**
     * 是否有网了
     * 该方法是通过ping ip的方式来判断是否有网
     *
     * @return true 为网络可用
     */
    public static boolean hasNetword() {
        boolean hasInternet = false;
        //该命令 如果output中有64 bytes from输出说明地址ping通了
        Command command = FireflyApi.getInstance().execCmd("ping -w 2 -c 2 114.114.114.114 ");
        for (String s : command.output) {
            if (s.contains("64 bytes from")) {
                hasInternet = true;
                return hasInternet;
            }
            LogUtilFromSDK.getInstance().d("ping的ip地址返回信息为：" + s);
        }
        return hasInternet;
    }

    /***
     *
     获取设备的IP地址
     */
    @RequiresPermission(INTERNET)
    public static String getDeviceIP(boolean useIPv4) {
        try {
            Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
            LinkedList<InetAddress> adds = new LinkedList<>();
            while (nis.hasMoreElements()) {
                NetworkInterface ni = nis.nextElement();
                // To prevent phone of xiaomi return "10.0.2.15"
                if (!ni.isUp() || ni.isLoopback()) continue;
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    adds.addFirst(addresses.nextElement());
                }
            }
            for (InetAddress add : adds) {
                if (!add.isLoopbackAddress()) {
                    String hostAddress = add.getHostAddress();
                    boolean isIPv4 = hostAddress.indexOf(':') < 0;
                    if (useIPv4) {
                        if (isIPv4) return hostAddress;
                    } else {
                        if (!isIPv4) {
                            int index = hostAddress.indexOf('%');
                            return index < 0
                                    ? hostAddress.toUpperCase()
                                    : hostAddress.substring(0, index).toUpperCase();
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 判断某个界面是否在前台
     *
     * @param context   Context
     * @param className 界面的类名
     * @return 是否在前台显示
     */
    public static boolean isForeground(Context context, String className) {
        if (context == null || TextUtils.isEmpty(className))
            return false;
        ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        for (ActivityManager.RunningTaskInfo taskInfo : list) {
            if (taskInfo.topActivity.getShortClassName().contains(className)) { // 说明它已经启动了
                return true;
            }
        }
        return false;
    }


    static Context context;

    public static Context getContext() {

        try {
            Class<?> ActivityThread = Class.forName("android.app.ActivityThread");
            Method method = ActivityThread.getMethod("currentActivityThread");
            Object currentActivityThread = method.invoke(ActivityThread);//获取currentActivityThread 对象

            Method method2 = currentActivityThread.getClass().getMethod("getApplication");
            context = (Context) method2.invoke(currentActivityThread);//获取 Context对象

        } catch (Exception e) {
            e.printStackTrace();
        }
        return context;
    }


    /**
     * 获取产品类型
     *
     * @param context 上下文
     * @return xiaohui 、dazhi
     */
    public static String getProductType(Context context) {
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo
                    (context.getPackageName(), PackageManager.GET_META_DATA);
            String pruduct_type = info.metaData.getString("PRUDUCT_TYPE");
            if (pruduct_type != null)
                return pruduct_type;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "null";
    }


}
