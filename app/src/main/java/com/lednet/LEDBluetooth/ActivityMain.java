package com.lednet.LEDBluetooth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.blebulb.core.BLEPeripheralClient;
import com.lednet.LEDBluetooth.Adapter.AdapterActivityMain;
import com.lednet.LEDBluetooth.COMM.BLECommandService;
import com.lednet.LEDBluetooth.COMM.ConnectionManager;
import com.lednet.LEDBluetooth.COMM.DeviceStateInfoBase;
import com.lednet.LEDBluetooth.COMM.LEDResponse;
import com.lednet.LEDBluetooth.COMM.LedDeviceInfo;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class ActivityMain extends ActivitySMB implements Observer {

    public static final int REQUEST_FINE_LOCATION = 11;
    public static final int REQUEST_LOCATION_STATE = 12;
    public final static int REQUEST_ENABLE_BT = 2;

    private ActivityMain me = this;
    private ListView mListView;
    private ArrayList<LedDeviceInfo> mDeviceLists = new ArrayList();
    private AdapterActivityMain mAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothManager mBluetoothManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        initUserView();
        checkLocation();
    }

    private boolean checkLocationState() {
        if (Build.VERSION.SDK_INT < 23) {
            return true;
        }
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) || locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return true;
        } else {
            return false;
        }
    }

    private void checkPermission() {

        if (Build.VERSION.SDK_INT >= 23) {
            int checkPermission = ContextCompat.checkSelfPermission(me,
                    Manifest.permission.ACCESS_COARSE_LOCATION);
            if (checkPermission != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    Snackbar.make(mListView, getString(R.string.need_permission),
                            Snackbar.LENGTH_INDEFINITE)
                            .setAction("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    ActivityCompat.requestPermissions(me,
                                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_FINE_LOCATION);
                                }
                            })
                            .show();
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_FINE_LOCATION);
                }

                return;
            }

        }
        initializeBLE();
    }

    private void checkLocation() {
        if (checkLocationState()) {
            checkPermission();
        } else {
            showAlertDialog("", getString(R.string.open_location), new OnConfirmListener() {

                @Override
                public void onConfirm(boolean confirm) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(intent, REQUEST_LOCATION_STATE);
                }
            });

        }
    }

    private void initUserView() {
        mListView = (ListView) findViewById(R.id.mian_listview);
        mAdapter = new AdapterActivityMain(mDeviceLists, me);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ConnectionManager.GetCurrent().stopScan();
                LedDeviceInfo dev = mDeviceLists.get(position);

                didAddNewDevice(dev);
            }
        });
    }

    private void initializeBLE() {

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            showAlertDialog(getString(R.string.bluetooth_miss_title), getString(R.string.bluetooth_miss), new OnConfirmListener() {

                @Override
                public void onConfirm(boolean confirm) {
                    finish();
                }
            });
            return;
        }
        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.

        mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();

        if (mBluetoothAdapter == null) {
            showAlertDialog(getString(R.string.bluetooth_miss_title), getString(R.string.bluetooth_miss), new OnConfirmListener() {

                @Override
                public void onConfirm(boolean confirm) {
                    finish();
                }
            });
            return;
        }

        ConnectionManager mgr = ConnectionManager.CreatConnectionManager(mBluetoothAdapter, thisActivity());
        if (mgr != null) {
            mgr.addObserver(this);
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, REQUEST_ENABLE_BT);
        } else {
            startScanDevice(mBluetoothAdapter);
        }

    }

    //Scan Bluetooth Device
    private void startScanDevice(BluetoothAdapter adapter) {

        ConnectionManager.CreatConnectionManager(adapter, thisActivity());
        ConnectionManager.GetCurrent().startScanDevice();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if (me.isFinishing()) {
                    return;
                }
                ConnectionManager mgr = ConnectionManager.GetCurrent();
                if (mgr != null) {
                    mgr.addObserver(me);
                }
            }
        }, 0);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (mBluetoothAdapter.isEnabled()) {
                startScanDevice(mBluetoothAdapter);
            } else {
                me.finish();
            }
        } else if (requestCode == REQUEST_LOCATION_STATE) {
            if (checkLocationState()) {
                checkPermission();
            } else {
                finish();
            }
        }

    }

    @Override
    public void update(Observable observable, Object data) {
        mDeviceLists.clear();
        mDeviceLists.addAll(ConnectionManager.GetCurrent().getAllDeviceModules());
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        if (ConnectionManager.GetCurrent() != null) {
            ConnectionManager.GetCurrent().deleteObserver(this);
        }
        super.onDestroy();
    }


    private void didAddNewDevice(final LedDeviceInfo dev) {
        showDefProgressDialog(getString(R.string.loading));
        AsyncTask<Void, Void, LEDResponse<DeviceStateInfoBase>> asyncTask = new AsyncTask<Void, Void, LEDResponse<DeviceStateInfoBase>>() {

            public final static int ErrorCode_CannotConnect = 99;

            @Override
            protected LEDResponse<DeviceStateInfoBase> doInBackground(Void... params) {

                boolean success = ConnectionManager.GetCurrent().ConnectDeviceByUniIDSyna(dev.getMacAddress());

                BLEPeripheralClient client = ConnectionManager.GetCurrent().getBLEPeripheralClientUniID(dev.getMacAddress());
                if (!success) {
                    return new LEDResponse<DeviceStateInfoBase>(ErrorCode_CannotConnect, "Unable to create Bluetooth connection");
                }

                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                LEDResponse<DeviceStateInfoBase> r_response = BLECommandService.getDeviceStateInfoBaseByUniID(client, 5 * 1000);
                return r_response;
            }

            @Override
            protected void onPostExecute(LEDResponse<DeviceStateInfoBase> result) {
                thisActivity().hideDefProgressDialog();
                if (result.getErrorCode() == LEDResponse.LED_ErrorCode_Successful) {

                    DeviceStateInfoBase deviceStateInfoBase = result.getResult();
                    if (deviceStateInfoBase.getDeviceType() == LedDeviceInfo.TYPE_RBGW_Bulb_New ||
                            deviceStateInfoBase.getDeviceType() == LedDeviceInfo.TYPE_RBG ||
                            deviceStateInfoBase.getDeviceType() == LedDeviceInfo.TYPE_RBGW_UFO) {
                        Intent intent = new Intent(me, ActivityCommand.class);
                        LedDeviceInfo devInfo = dev;
                        devInfo.setDeviceTpye(deviceStateInfoBase.getDeviceType());
                        devInfo.setLEDVersionNum(deviceStateInfoBase.getLedVersionNum());
                        intent.putExtra("LEDDeviceInfo", devInfo);
                        intent.putExtra("DeviceState", deviceStateInfoBase);
                        me.startActivity(intent);
                        return;
                    } else {
                        Toast.makeText(me, me.getText(R.string.unsupported), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(me, me.getText(R.string.connection_failed), Toast.LENGTH_SHORT).show();
                }
            }

        };
        asyncTask.execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {

            startScanDevice(mBluetoothAdapter);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == REQUEST_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeBLE();
            } else {
                finish();
            }
        }
    }
}