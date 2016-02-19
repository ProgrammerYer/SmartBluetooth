package com.lednet.LEDBluetooth.COMM;

import java.util.ArrayList;
import java.util.Calendar;

public class LEDDeviceCMDMgr {

    public static byte[] getCommandDataForQuery() {

        byte[] data = new byte[3];
        data[0] = (byte) 0xEF;
        data[1] = (byte) 0X01;
        data[2] = (byte) 0X77;
        return data;
    }

    public static DeviceStateInfoBase getDeviceStateInfoBaseByData(byte[] bytes) {

        if (bytes.length < 12) {
            return null;
        }
        DeviceStateInfoBase dev = null;
        if (bytes[0] == (byte) 0x66 && bytes[11] == (byte) 0x99) {
            dev = new DeviceStateInfoBase();
            dev.setDeviceType(bytes[1] & 0xFF);
            if (bytes[2] == (byte) 0x23) {
                dev.setIsOpen(true);
            } else {
                dev.setIsOpen(false);
            }

            if (bytes[4] == (byte) 0x21) {
                dev.setIsRunning(true);
            } else {
                dev.setIsRunning(false);
            }

            dev.setLedVersionNum(bytes[10] & 0xFF);
        }
        return dev;

    }

    public static byte[] getCommandDataForRGBW(byte red, byte green, byte blue, byte warm, byte mode) {
        byte[] arrayOfByte = new byte[7];
        arrayOfByte[0] = (byte) 0x56;
        arrayOfByte[1] = red;
        arrayOfByte[2] = green;
        arrayOfByte[3] = blue;
        arrayOfByte[4] = warm;
        arrayOfByte[5] = mode;
        arrayOfByte[6] = (byte) 0xAA;
        return arrayOfByte;
    }

    public static byte[] getCommandDataForRGBColor(byte r, byte g, byte b) {
        byte[] arrayOfByte = new byte[6];
        arrayOfByte[0] = (byte) 0x56;
        arrayOfByte[1] = r;
        arrayOfByte[2] = g;
        arrayOfByte[3] = b;
        arrayOfByte[4] = (byte) 0x00;
        arrayOfByte[5] = (byte) 0xAA;
        return arrayOfByte;
    }

    public static byte[] getCommandDataForBuiltInModeValue(int builtIn, int speed) {
        speed = 31 - speed;
        byte[] data = new byte[4];
        data[0] = (byte) 0xBB;
        data[1] = (byte) builtIn;
        data[2] = (byte) speed;
        data[3] = (byte) 0X44;
        return data;
    }

    public static byte[] getCommandDataForSetTime( Calendar cal) {

        int yearFrom2000 = cal.get(Calendar.YEAR)-2000;
        int month = cal.get(Calendar.MONTH)+1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hour  = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int sec = cal.get(Calendar.SECOND);
        int week = -1;
        if(cal.get(Calendar.DAY_OF_WEEK) == 1){
            week = 7;
        }

        byte[] data = new byte[11];
        data[0] = (byte) 0x10;
        data[1] = (byte) 0x14;
        data[2] = (byte) yearFrom2000;
        data[3] = (byte) month;
        data[4] = (byte) day;
        data[5] = (byte) hour;
        data[6] = (byte) minute;
        data[7] = (byte) sec;
        data[8] = (byte) week;
        data[9] = (byte) 0x00;
        data[10] = (byte) 0x01;
        return data;
    }

    public static byte[] getCommandDataForTimerItems(ArrayList<TimerDetailItem> items) {

        byte[] data = new byte[87];
        data[0] = (byte) 0x23;
        int index=1;
        for (TimerDetailItem itm : items) {
            data[index]=itm.mEnableTimer? (byte)0xF0: (byte)0x0F; index++;
            data[index]=(byte) itm.mYearFrom2000;index++;
            data[index]=(byte) itm.mMonth;index++;
            data[index]=(byte) itm.mDay;index++;
            data[index]=(byte) itm.mHour;index++;
            data[index]=(byte) itm.mMinute;index++;
            data[index]=(byte) itm.mSec;index++;
            data[index]=(byte) itm.mWeek;index++;
            data[index]=(byte) itm.mModeValue;index++;
            data[index]=(byte) itm.mValue1;index++;
            data[index]=(byte) itm.mValue2;index++;
            data[index]=(byte) itm.mValue3;index++;
            data[index]=(byte) itm.mValue4;index++;
            data[index]=itm.mPower? (byte)0xF0: (byte)0x0F; index++;
        }
        for (int i = items.size()+1; i <= 6; i++)
        {
            //, (byte)0x0F,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,0x00,0x00,(byte)0x06, (byte)0x00, (byte)0x00,  (byte)0x00, 0x00, (byte)0x00, (byte)0x00
            data[index]=(byte)0x0F;index++;
            data[index]=(byte)0x00;index++;
            data[index]=(byte)0x00;index++;
            data[index]=(byte)0x00;index++;
            data[index]=(byte)0x00;index++;
            data[index]=(byte)0x00;index++;
            data[index]=(byte)0x00;index++;
            data[index]=(byte)0x00;index++;
            data[index]=(byte)0x00;index++;
            data[index]=(byte)0x00;index++;
            data[index]=(byte)0x00;index++;
            data[index]=(byte)0x00;index++;
            data[index]=(byte)0x00;index++;
            data[index]=(byte)0x00;index++;
        }
        data[index] = (byte)0x00;index++;
        data[index] = (byte)0x32;
        return data;

    }

}
