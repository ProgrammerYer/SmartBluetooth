package com.lednet.LEDBluetooth.COMM;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.blebulb.core.BLEPeripheralClient;
import com.blebulb.core.BLEPeripheralClientTimer;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Observable;

public class ConnectionManager extends Observable {

    Context mComtext = null;
    private static ConnectionManager _current;
    private static Object lockObject = new Object();
    private static Object lockObject_list = new Object();
    private LinkedHashMap<String, BLEPeripheralClient> moduleTCLClients = new LinkedHashMap<String, BLEPeripheralClient>();
    private HashMap<String, LedDeviceInfo> deviceInfoList = new HashMap<String, LedDeviceInfo>();
    private BluetoothAdapter _bluetoothAdapter;


    public static ConnectionManager CreatConnectionManager(BluetoothAdapter bluetoothAdapter, Context context) {
        synchronized (lockObject) {
            if (_current != null) {
                _current.Close();
            }

            _current = new ConnectionManager();
            _current.init(bluetoothAdapter, context);
        }
        return _current;
    }

    public static ConnectionManager GetCurrent() {
        return _current;
    }

    private void init(BluetoothAdapter bluetoothAdapter, final Context context) {
        _bluetoothAdapter = bluetoothAdapter;
        mComtext = context;
    }


    public void startScanDevice() {
        _bluetoothAdapter.startLeScan(_LeScanCallback);

        //stop scanning after 10 seconds.
        new Handler().postDelayed(new Runnable() {
            public void run() {
                stopScan();
            }
        }, 5 * 1000);

    }

    public void stopScan() {
        try {
            if (_bluetoothAdapter != null) {
                _bluetoothAdapter.stopLeScan(_LeScanCallback);
                _bluetoothAdapter = null;
            }
        } catch (Exception e) {
        }
    }

    LeScanCallback _LeScanCallback = new LeScanCallback() {


        // The Handler that gets information back from the BluetoothChatService
        Handler mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0) {
                    setChanged();
                    notifyObservers();
                } else if (msg.what == 1) {

                    BluetoothDevice bleDevice = (BluetoothDevice) msg.obj;

                    if (bleDevice != null) {

                        String uniID = bleDevice.getAddress();
                        String localName = bleDevice.getName();
                        if (localName == null) {

                        }

                        if (!moduleTCLClients.containsKey(uniID)) {
                            if (localName == null) {
                                BLEPeripheralClientTimer client = new BLEPeripheralClientTimer(bleDevice);
                                moduleTCLClients.put(uniID, client);
                            } else if (AppConfig.getCheckIsRFStarDevice(localName)) {
                                BLEPeripheralClientTimer client = new BLEPeripheralClientTimer(bleDevice);
                                moduleTCLClients.put(uniID, client);
                            } else {
                                BLEPeripheralClient client = new BLEPeripheralClient(bleDevice);
                                moduleTCLClients.put(uniID, client);
                            }
                        }
                    }
                }
            }
        };

        @Override
        public void onLeScan(final BluetoothDevice bleDevice, int rssi, byte[] scanRecord) {
            {

                String localName = bleDevice.getName();
                if (localName == null) {
                    return;
                }

                if (!localName.startsWith("LEDBlue") && !localName.startsWith("LEDBLE")
                        && !localName.startsWith("LEDnet")) {
                    return;
                }


                String uniID = bleDevice.getAddress();
                LedDeviceInfo dev = new LedDeviceInfo();
                dev.setMacAddress(uniID);
                dev.setDeviceName(bleDevice.getName());
                dev.setRSSI(rssi);

                if (!deviceInfoList.containsKey(uniID)) {
                    deviceInfoList.put(uniID, dev);


                    mHandler.removeMessages(0);
                    Message msg = mHandler.obtainMessage(0);
                    mHandler.sendMessageDelayed(msg, 500);

                }

                Message msg = mHandler.obtainMessage(1);
                msg.obj = bleDevice;
                mHandler.sendMessage(msg);

            }
            ;

        }
    };

    public boolean ConnectDeviceByUniIDSyna(String uniID) {
        //stopScan();
        BLEPeripheralClient client = moduleTCLClients.get(uniID);
        if (client != null) {

            if (!client.isConnectedDataService()) {

                try {
                    for (int i = 0; i < 3; i++) {
                        boolean connected = client.ConnectSynch(mComtext, 8 * 1000);
                        if (connected) {
                            return true;
                        } else if (client.getIsConnectedGatt()) {


                        } else {
                            if (i == 1) {
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            } else {
                return true;
            }
        }
        return false;
    }

    public BLEPeripheralClient getBLEPeripheralClientUniID(String uniID) {
        return moduleTCLClients.get(uniID);
    }


    public boolean SendDataByDeviceUniDsForOver20Char(String deviceUniID, byte[] data) {
        boolean r_success = true;
        synchronized (lockObject_list) {

            BLEPeripheralClient client = moduleTCLClients.get(deviceUniID);
            if (client != null && client.isConnectedDataService()) {
                LedDeviceInfo dev = deviceInfoList.get(deviceUniID);
                if (dev != null && dev.getDeviceName().startsWith("LEDBLE") && dev.getLEDVersionNum() >= 7) {
                    client.sendDataWithSpliteData(data, BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
                } else {
                    client.sendData(data);
                }

            } else {
                r_success = false;
            }

        }
        return r_success;
    }

    public boolean SendDataByDeviceUniD(String deviceUniID, byte[] data) {
        boolean r_success = true;
        synchronized (lockObject_list) {
            BLEPeripheralClient client = moduleTCLClients.get(deviceUniID);
            if (client != null && client.isConnectedDataService()) {
                client.sendData(data);
            } else {
                r_success = false;
            }
        }
        return r_success;
    }

    public Collection<LedDeviceInfo> getAllDeviceModules() {
        return deviceInfoList.values();
    }


    public synchronized void Close() {

        synchronized (lockObject_list) {
            stopScan();

            if (moduleTCLClients == null) {
                return;
            }

            for (BLEPeripheralClient client : moduleTCLClients.values()) {
                client.close();
            }
            moduleTCLClients = new LinkedHashMap<String, BLEPeripheralClient>();
            deviceInfoList = new HashMap<String, LedDeviceInfo>();
        }
    }

}


