package com.lednet.LEDBluetooth.COMM;

import java.io.Serializable;

public class LedDeviceInfo implements Serializable {

    public final static int TYPE_NONE = 0;
    public final static int TYPE_RBGW_Bulb_New = (int) (0x15 & 0xFF);
    public final static int TYPE_RBG = (int) (0x03 & 0xFF);
    public final static int TYPE_RBGW_UFO = (int) (0x44 & 0xff);


    private int led_versionNum = 0;
    private int deviceTpye = TYPE_NONE;
    private String deviceName;
    private String macAddress;
    private int rssi = 0;
    private String moduleID = "";
    public boolean isSelected =false;

    public int getRSSI() {
        return rssi;
    }

    public void setRSSI(int rssi) {
        this.rssi = rssi;
    }

    public int getDeviceTpye() {
        return deviceTpye;
    }

    public void setDeviceTpye(int deviceTpye) {
        this.deviceTpye = deviceTpye;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getModuleID() {
        return moduleID;
    }

    public void setModuleID(String moduleID) {
        this.moduleID = moduleID;
    }


    public int getLEDVersionNum() {
        return led_versionNum;
    }

    public void setLEDVersionNum(int versionNum) {
        this.led_versionNum = versionNum;
    }

    @Override
    public String toString() {
        return "DeviceName=" + deviceName + ",MAC=" + macAddress + ",RSSI=" + rssi;
    }

}
