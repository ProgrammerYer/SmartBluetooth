package com.lednet.LEDBluetooth;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import com.blebulb.core.BLEPeripheralClient;
import com.blebulb.core.BLEPeripheralClientTimer;
import com.lednet.LEDBluetooth.COMM.ConnectionManager;
import com.lednet.LEDBluetooth.COMM.DeviceStateInfoBase;
import com.lednet.LEDBluetooth.COMM.LEDDeviceCMDMgr;
import com.lednet.LEDBluetooth.COMM.LedDeviceInfo;

import java.util.Calendar;


public class ActivityCommand extends ActivitySMB {

    private LedDeviceInfo mLedDeviceInfo;
    private DeviceStateInfoBase mDeviceStateInfoBase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_command);

        mLedDeviceInfo = (LedDeviceInfo) getIntent().getSerializableExtra("LEDDeviceInfo");
        mDeviceStateInfoBase = (DeviceStateInfoBase) getIntent().getSerializableExtra("DeviceState");
        if (mLedDeviceInfo == null || mDeviceStateInfoBase == null) {
            finish();
        }
        //Test Change2
        //calibration time
        BLEPeripheralClient client = ConnectionManager.GetCurrent().getBLEPeripheralClientUniID(mLedDeviceInfo.getMacAddress());
        if (client.getClass() != BLEPeripheralClientTimer.class) {
            byte[] data = LEDDeviceCMDMgr.getCommandDataForSetTime(Calendar.getInstance());
            ConnectionManager.GetCurrent().SendDataByDeviceUniD(mLedDeviceInfo.getMacAddress(), data);
        }

        if (mLedDeviceInfo.getDeviceTpye() == LedDeviceInfo.TYPE_RBGW_Bulb_New ||
                mLedDeviceInfo.getDeviceTpye() == LedDeviceInfo.TYPE_RBG ||
                mLedDeviceInfo.getDeviceTpye() == LedDeviceInfo.TYPE_RBGW_UFO) {
            initRGBWFragment();
        }

    }

    private void initRGBWFragment() {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        FragmentRGBW rgbwFragment = new FragmentRGBW();
        Bundle bundle = new Bundle();
        bundle.putSerializable("LEDDeviceInfo", mLedDeviceInfo);
        bundle.putSerializable("DeviceState", mDeviceStateInfoBase);
        rgbwFragment.setArguments(bundle);
        transaction.replace(R.id.root_layout, rgbwFragment);
        transaction.commit();
    }

}
