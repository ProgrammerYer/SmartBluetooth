package com.lednet.LEDBluetooth.COMM;

import android.graphics.Color;

import java.io.Serializable;


public class DeviceStateInfoBase implements Serializable {

    private boolean isOpen = false;
    private boolean isRunning = false;
    private int deviceType = LedDeviceInfo.TYPE_NONE;
    private int led_versionNum = 0;

    private int speed = 31;
    private int color = Color.WHITE;
    private int modeBuilinIndex = -1;
    private int warmWhite = 0;

    public DeviceStateInfoBase() {

    }

    public boolean getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(boolean isOpen) {
        this.isOpen = isOpen;
    }

    public boolean getIsRunning() {
        return isRunning;
    }

    public void setIsRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public int getLedVersionNum() {
        return led_versionNum;
    }

    public void setLedVersionNum(int versionNum) {
        this.led_versionNum = versionNum;
    }


    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getModeBuilinIndex() {
        return modeBuilinIndex;
    }

    public void setModeBuilinIndex(int modeBuilinIndex) {
        this.modeBuilinIndex = modeBuilinIndex;
    }

    public int getWarmWhite() {
        return warmWhite;
    }

    public void setWarmWhite(int warmWhite) {
        this.warmWhite = warmWhite;
    }
}
