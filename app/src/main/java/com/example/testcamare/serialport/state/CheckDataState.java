package com.example.testcamare.serialport.state;

import android.util.Log;

import com.example.testcamare.serialport.packer.ComputeData;
import com.example.testcamare.serialport.packer.PackeData;
import com.example.testcamare.serialport.packer.commandata.ContractData;


/**
 * 校验状态
 */
public class CheckDataState extends AbsSerialPortState {

    @Override
    void wiateDatas(PackeData sendData) {
//        portContext.setCurrentState(SerialPortContext.WAIT_DATA_STATE);
    }

    /**
     * 加测数据是否可用
     */
    public void datasCheck(SerialPortBean portBean) {
        Log.d(TAT, "监测的数据为:" + portBean.getCallbckCommandsStr());
        //取出返回信息进行校验
        String callbackStr = portBean.getCallbckCommandsStr();
        //主需要校验包头的数据长度和包头校验和就可以校验出数据是否完整
        //校验包头即可校验出包头链路层是否完整
        boolean headTotal = ComputeData.checkHeadTotal(callbackStr);
        if (!headTotal) {
            //重新发送
            resendCommands(portBean.sendData);
            return;
        }
        //通过校验数据长度就可以校验出传输层和协议数据层的数据是否完整
        boolean headDataLegth = ComputeData.checkHeadDataLegth(callbackStr);
        if (!headDataLegth) {
            //重新发送
            resendCommands(portBean.sendData);
            return;
        }
        if (headTotal && headDataLegth) {
            IDataCallback cataCallbck = portBean.getCataCallbck();
            if (cataCallbck != null) {
                cataCallbck.onCallbackData(portBean);
            }
            portContext.setCurrentState(SerialPortContext.SEND_STATE);
            return;
        }
    }

    /**
     * 如果获取的是失败的，那么将会重新发送三次
     * 如果超过三次，则放弃重发，直接进入可发送状态。
     */
    private int resendCount;

    private void resendCommands(PackeData packeData) {
        Log.d(TAT, "数据校验失败重新发送：" + resendCount);
        if (resendCount > 3) {
            portContext.setCurrentState(SerialPortContext.SEND_STATE);
            resendCount = 0;
            return;
        }
        ContractData contractData = packeData.getContractData();
        portContext.SEND_STATE.sendCommand(contractData.getCommandId(), contractData.getIssuer());
        resendCount++;
    }

    @Override
    boolean sendCommand(byte commandId, byte... issues) {
//        Log.d(TAT, "当前是检查状态，还不能发送数据哦！");
        return false;
    }

}