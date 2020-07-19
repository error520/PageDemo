package com.kinco.kmlink.EventBusUtils;

/**
 * 从蓝牙返回的数据信息
 */
public class BleDataEvent {
    private byte[] bleData;

    public BleDataEvent(byte[] bleData){
        this.bleData = bleData;
    }

    public byte[] getBleData() {
        return bleData;
    }

    public void setBleData(byte[] bleData) {
        this.bleData = bleData;
    }
}
