package com.lednet.LEDBluetooth;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.support.v7.app.AppCompatActivity;

public class ActivitySMB extends AppCompatActivity {

    public ActivitySMB thisActivity() {
        return this;
    }

    private ProgressDialog mProgressDialog;

    public void showDefProgressDialog(String message) {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage(message);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }


    public synchronized void hideDefProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    public interface OnConfirmListener {
        void onConfirm(boolean confirm);
    }

    public void showAlertDialog(String title, String message,
                                final OnConfirmListener listener) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setNegativeButton("OK", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onConfirm(true);
                    }
                }).show();
    }


}
