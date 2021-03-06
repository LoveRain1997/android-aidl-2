package com.pythoncat.ipcorservice2.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.apkfuns.logutils.LogUtils;
import com.pythoncat.aidl.RemoteBinder;
import com.pythoncat.aidl.bean.Duck;
import com.pythoncat.ipcorservice2.R;
import com.pythoncat.service.RemoteService;

public class AidlActivity extends AppCompatActivity {

    private ServiceConnection mConn;
    private RemoteBinder mRemoteBinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aidl);
        TextView tvShowResult = (TextView) findViewById(R.id.aidl_show_tv);
        TextView tvParcelable = (TextView) findViewById(R.id.aidl_show_parcelable_tv);
        tvParcelable.setText(getString(R.string.show_about_parcelable_service, ""));
        tvShowResult.setText(getString(R.string.show_about_normal_service, ""));
        Button btnExecute = (Button) findViewById(R.id.aidl_ui_btn);
        btnExecute.setOnClickListener(v -> {
            if (mRemoteBinder != null) {
                try {
                    mRemoteBinder.setResult(1024);
                    int result = mRemoteBinder.getResult();
                    tvShowResult.setText(getString(R.string.show_about_normal_service, result));
                    LogUtils.e("aidl result===" + result);
                    Duck duck = new Duck();
                    duck.name = "pythonCat";
                    duck.id = 12306;
                    mRemoteBinder.setDuck(duck);
                    Duck d = mRemoteBinder.getDuck();
                    tvParcelable.setText(getString(R.string.show_about_parcelable_service, d.toString()));
                    LogUtils.e("aidl parcelable===" + d.toString());
                } catch (RemoteException e) {
                    LogUtils.e("bind remote service fail...");
                    LogUtils.e(e);
                }
            }
        });

        mConn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mRemoteBinder = RemoteBinder.Stub.asInterface(service);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mRemoteBinder = null;
            }
        };
        bindService(new Intent(get(), RemoteService.class), mConn, Context.BIND_AUTO_CREATE);
    }

    public AppCompatActivity get() {
        return this;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mConn != null) {
            unbindService(mConn);
            mConn = null;
        }
    }
}
