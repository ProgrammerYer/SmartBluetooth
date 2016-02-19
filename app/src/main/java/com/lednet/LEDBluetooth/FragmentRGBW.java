package com.lednet.LEDBluetooth;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import com.lednet.LEDBluetooth.COMM.ConnectionManager;
import com.lednet.LEDBluetooth.COMM.DeviceStateInfoBase;
import com.lednet.LEDBluetooth.COMM.LEDDeviceCMDMgr;
import com.lednet.LEDBluetooth.COMM.LedDeviceInfo;
import com.lednet.LEDBluetooth.COMM.TimerDetailItem;

import java.util.ArrayList;

public class FragmentRGBW extends Fragment implements View.OnClickListener {

    private ActivitySMB mActivity;
    private LedDeviceInfo mLedDeviceInfo;
    private DeviceStateInfoBase mDeviceStateInfoBase;
    private SeekBar mRedBrightSlider = null;
    private SeekBar mGreenBrightSlider = null;
    private SeekBar mBlueBrightSlider = null;
    private SeekBar mWarmBrightSlider = null;
    private Button mButton_timer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mActivity = (ActivitySMB) getActivity();

        View rootView = inflater.inflate(R.layout.fragment_rgbw2, container, false);

        Bundle bundle = getArguments();
        mLedDeviceInfo = (LedDeviceInfo) bundle.getSerializable("LEDDeviceInfo");
        mDeviceStateInfoBase = (DeviceStateInfoBase) bundle.getSerializable("DeviceState");

        init(rootView);
        return rootView;
    }

    private void init(View view) {
        view.findViewById(R.id.fragment_rgbw_button_gradual).setOnClickListener(this);
        view.findViewById(R.id.fragment_rgbw_button_flash).setOnClickListener(this);
        view.findViewById(R.id.fragment_rgbw_button_saltus).setOnClickListener(this);

        mButton_timer = (Button) view.findViewById(R.id.fragment_rgbw_button_timer1);
        mButton_timer.setOnClickListener(this);

        mRedBrightSlider = (SeekBar) view.findViewById(R.id.fragment_rgbw_seekBar_RedBright);
        mGreenBrightSlider = (SeekBar) view.findViewById(R.id.fragment_rgbw_seekBar_GreenBright);
        mBlueBrightSlider = (SeekBar) view.findViewById(R.id.fragment_rgbw_seekBar_BlueBright);
        mWarmBrightSlider = (SeekBar) view.findViewById(R.id.fragment_rgbw_seekBar_WarmBright);
        mRedBrightSlider.setOnSeekBarChangeListener(onSeekBarChangeListener);
        mGreenBrightSlider.setOnSeekBarChangeListener(onSeekBarChangeListener);
        mBlueBrightSlider.setOnSeekBarChangeListener(onSeekBarChangeListener);

        mWarmBrightSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mRedBrightSlider.setProgress(0);
                    mGreenBrightSlider.setProgress(0);
                    mBlueBrightSlider.setProgress(0);
                    sendDataForWarmColor((byte) progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {            }

        });
    }

    SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                int r = 255, g = 255, b = 255;
                switch (seekBar.getId()) {
                    case R.id.fragment_rgbw_seekBar_RedBright:
                        r = progress;
                        g = mGreenBrightSlider.getProgress();
                        b = mBlueBrightSlider.getProgress();
                        break;
                    case R.id.fragment_rgbw_seekBar_GreenBright:
                        r = mRedBrightSlider.getProgress();
                        g = progress;
                        b = mBlueBrightSlider.getProgress();
                        break;
                    case R.id.fragment_rgbw_seekBar_BlueBright:
                        r = mRedBrightSlider.getProgress();
                        g = mGreenBrightSlider.getProgress();
                        b = progress;
                        break;
                    default:
                        break;
                }
                mWarmBrightSlider.setProgress(0);
                sendDataForRGBColor((byte) r, (byte) g, (byte) b);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {        }

    };


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_rgbw_button_gradual:
                byte[] data1 = LEDDeviceCMDMgr.getCommandDataForBuiltInModeValue((byte) 0x25, 28);
                sendDate(data1);
                break;
            case R.id.fragment_rgbw_button_flash:
                byte[] data2 = LEDDeviceCMDMgr.getCommandDataForBuiltInModeValue((byte) 0x30, 25);
                sendDate(data2);
                break;
            case R.id.fragment_rgbw_button_saltus:
                byte[] data3 = LEDDeviceCMDMgr.getCommandDataForBuiltInModeValue((byte) 0x38, 28);
                sendDate(data3);
                break;
            case R.id.fragment_rgbw_button_timer1:
                setTimer();
                break;
            default:
                break;
        }
    }

    private void setTimer() {
        TimerDetailItem timer1 = TimerDetailItem.createTimerItemForAfterMinute(1);
        TimerDetailItem timer2 = TimerDetailItem.createTimerItemForAfterMinute(2);
        TimerDetailItem timer3 = TimerDetailItem.createTimerItemForAfterMinute(3);
        timer2.mPower = true;
        timer3.mPower = true;
        timer3.mModeValue = (byte) 0x41;
        timer3.mValue1 = (byte) 255;
        timer3.mValue2 = (byte) 0;
        timer3.mValue3 = (byte) 0;
        ArrayList<TimerDetailItem> items = new ArrayList<TimerDetailItem>();
        items.add(timer1);
        items.add(timer2);
        items.add(timer3);

        mActivity.showDefProgressDialog(getString(R.string.loading));

        AsyncTask<ArrayList<TimerDetailItem>, Void, Boolean> asyncTask = new AsyncTask<ArrayList<TimerDetailItem>, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(ArrayList<TimerDetailItem>... params) {

                byte[] data = LEDDeviceCMDMgr.getCommandDataForTimerItems(params[0]);
                boolean response = ConnectionManager.GetCurrent().GetCurrent().SendDataByDeviceUniDsForOver20Char(mLedDeviceInfo.getMacAddress(), data);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return response;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                mActivity.hideDefProgressDialog();
                if (aBoolean) {
                    Snackbar.make(mButton_timer, mActivity.getText(R.string.timer_successful), Snackbar.LENGTH_LONG).show();
                } else {
                    Toast.makeText(mActivity, mActivity.getText(R.string.timer_failed), Toast.LENGTH_LONG).show();
                }
            }
        };
        asyncTask.execute(items);
    }

    private void sendDataForRGBColor(byte red, byte green, byte blue) {
        byte[] data = null;
        switch (mLedDeviceInfo.getDeviceTpye()) {
            case LedDeviceInfo.TYPE_RBGW_Bulb_New:
                data = LEDDeviceCMDMgr.getCommandDataForRGBW(red, green, blue, (byte) 0, (byte) 0xf0);
                break;
            case LedDeviceInfo.TYPE_RBG:
                data = LEDDeviceCMDMgr.getCommandDataForRGBColor(red, green, blue);
                break;
            case LedDeviceInfo.TYPE_RBGW_UFO:
                data = LEDDeviceCMDMgr.getCommandDataForRGBW(red, green, blue, (byte) 0, (byte) 0xf0);
                break;
            default:
                break;
        }
        sendDate(data);
    }

    private void sendDataForWarmColor(int warm) {
        byte[] data = null;
        switch (mLedDeviceInfo.getDeviceTpye()) {
            case LedDeviceInfo.TYPE_RBGW_Bulb_New:
                data = LEDDeviceCMDMgr.getCommandDataForRGBW((byte) 0, (byte) 0, (byte) 0, (byte) warm, (byte) 0x0f);
                break;
            case LedDeviceInfo.TYPE_RBG:
                break;
            case LedDeviceInfo.TYPE_RBGW_UFO:
                data = LEDDeviceCMDMgr.getCommandDataForRGBW((byte) 0, (byte) 0, (byte) 0, (byte) warm, (byte) 0x0f);
                break;
            default:
                break;
        }
        sendDate(data);
    }

    private void sendDate(byte[] data) {
        if (data != null) {
            ConnectionManager.GetCurrent().SendDataByDeviceUniD(mLedDeviceInfo.getMacAddress(), data);
        }
    }

}
