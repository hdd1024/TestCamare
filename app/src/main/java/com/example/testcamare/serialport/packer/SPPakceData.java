package com.example.testcamare.serialport.packer;


import androidx.annotation.Keep;

import com.example.testcamare.serialport.packer.commandata.ContractData;
import com.example.testcamare.serialport.packer.commandata.LinkHead;
import com.example.testcamare.serialport.packer.commandata.TransportLayer;
import com.example.testcamare.utils.ByteUtil;
/**
 * 处理串口命令的类
 */
@Keep
public class SPPakceData {
    //数据处理完成终止会将完整的数据放入该类中，用于储存
    private PackeData packeData;
    //链路层
    private LinkHead linkHead;
    private TransportLayer transportLayer;
    //协议数据类
    private ContractData contractData;
    //完整的链路串字符串
    private String fullLinkHeadStr;
    //完整的传输层字符串
    private String fullTransportStr;
    //完整的协议数据层的字符串
    private String fullContractDataStr;

    public SPPakceData(ContractData contractData) {
        this.contractData = contractData;
        transportLayer = new TransportLayer();
        linkHead = new LinkHead();
    }

    public PackeData getPackeData() {
        //先处理协议层
        computeContractDataCommanLegth();
        computeContractDataTotal();
        //在处理包头的数据长度
        computeHeadDataLegth();
        //最后处理包头的校验和
        computeHeadTotal();
        packeData = new PackeData();
        //设置完整的字符串命令

        fullTransportStr = ByteUtil.bytes2HexStr(transportLayer.getP9_P12());
        String fullCommandStr = fullLinkHeadStr + fullTransportStr
                + fullContractDataStr;
        packeData.setFullCommandsStr(fullCommandStr);
        packeData.setFullCommands(ByteUtil.hexStr2bytes(fullCommandStr));
        packeData.setLinkHead(linkHead);
        packeData.setTransportLayer(transportLayer);
        packeData.setContractData(contractData);
        return packeData;
    }

    /**
     * 计算包头长度
     */
    private void computeHeadDataLegth() {
        int transportLegth = transportLayer.getP9_P12().length;
        int contractLegth = contractData.getP13_Pn().length;
        int sum = transportLegth + contractLegth + 1;
        String sumStr = ByteUtil.decimal2fitHex(sum);
        byte sumByte = ByteUtil.hexStr2bytes(sumStr)[0];
        linkHead.setDataLegth(Short.valueOf(sumByte));
    }

    /**
     * 计算包头的校验和
     */
    private void computeHeadTotal() {
        byte[] p0_p7 = linkHead.getP0_P7();
        String p0_p7Str = ByteUtil.bytes2HexStr(p0_p7);
        String checkSum = ByteUtil.makeCheckSum(p0_p7Str);
        fullLinkHeadStr = p0_p7Str + checkSum;
        linkHead.setVerifySum(ByteUtil.hexStr2bytes(checkSum)[0]);
    }

    /**
     * 计算协议数据层的命令长度
     */
    private void computeContractDataCommanLegth() {
        byte[] Pn_1 = contractData.getIssuer();
        int commandLegth = Pn_1.length;
        String hex = ByteUtil.decimal2fitHex(commandLegth);
        byte byteCommandLegth = ByteUtil.hexStr2bytes(hex)[0];
        contractData.setParamLegth(byteCommandLegth);
    }

    /**
     * 计算协议数据层的校验数据和
     */
    private void computeContractDataTotal() {

        String transportStr = ByteUtil.bytes2HexStr(transportLayer.getP9_P12());
        String contractP13_Pn_1 = ByteUtil.bytes2HexStr(contractData.getP13_Pn_1());
        String computeStr = transportStr + contractP13_Pn_1;
        String checkSum = ByteUtil.makeCheckSum(computeStr);
        fullContractDataStr = contractP13_Pn_1 + checkSum;
        contractData.setDataVirfySum(ByteUtil.hexStr2bytes(checkSum)[0]);

    }

    public static class Builder {
        //协议数据类
        private ContractData contractData;

        public static Builder create(byte commandId) {

            return new Builder(commandId);
        }

        public Builder(byte commandId) {
            contractData = new ContractData();
            contractData.setCommandId(commandId);
        }

        /**
         * 设置协议数据层的数据长度
         *
         * @param leght
         */
        public Builder setContractParamLegth(byte leght) {
            contractData.setParamLegth(leght);
            return this;
        }

        /**
         * 设置下发命令
         */
        public Builder setIssuer(byte... issuer) {
            contractData.setIssuer(issuer);

            return this;
        }

        /**
         * 设置协议数据层的校验和
         */
        public Builder setContralTotal(byte total) {
            contractData.setDataVirfySum(total);
            return this;
        }

        public SPPakceData build() {
            SPPakceData pakceData = new SPPakceData(this.contractData);
            return pakceData;
        }

    }
}