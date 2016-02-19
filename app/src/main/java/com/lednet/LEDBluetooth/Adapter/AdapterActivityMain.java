package com.lednet.LEDBluetooth.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lednet.LEDBluetooth.COMM.LedDeviceInfo;
import com.lednet.LEDBluetooth.R;

import java.util.ArrayList;


public class AdapterActivityMain extends BaseAdapter {

    private ArrayList<LedDeviceInfo> mArrayList;
    private Context mContext;
    private LayoutInflater mInflater;
    public AdapterActivityMain(ArrayList<LedDeviceInfo> arrayList, Context context){
        mArrayList = arrayList;
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return mArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView = mInflater.inflate(R.layout.adapter_item_main,null);
        }
        TextView tvDeviceName = (TextView) convertView.findViewById(R.id.textView_DeviceName);
        TextView tvDetailed = (TextView) convertView.findViewById(R.id.textView_MacAddress);
        ImageView imgIcon = (ImageView)convertView.findViewById(R.id.imageView_Icon);
        LedDeviceInfo dev =  mArrayList.get(position);
        tvDeviceName.setText( dev.getDeviceName());
        tvDetailed.setText( dev.getMacAddress()+", RSSI:"+dev.getRSSI());
        return convertView;
    }
}
