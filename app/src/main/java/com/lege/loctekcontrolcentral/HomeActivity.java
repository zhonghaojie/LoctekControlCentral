package com.lege.loctekcontrolcentral;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.kongqw.serialportlibrary.Device;
import com.kongqw.serialportlibrary.SerialPortFinder;
import com.kongqw.serialportlibrary.SerialPortManager;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {
    private void open() {
        SerialPortFinder serialPortFinder = new SerialPortFinder();
        ArrayList<Device> devices = serialPortFinder.getDevices();
        for (Device obj : devices) {
            Log.d("device===", obj.getName() + "..." + obj.getRoot());
            if (TextUtils.equals(obj.getName(), "ttyS5")) {
                SerialPortManager.getInstance().openSerialPort(obj.getFile(), 38400);
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        open();
        findViewById(R.id.btn_desk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this,DeskActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.btn_airclean).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this,AirCleanActivity.class);
                startActivity(intent);
            }
        });
    }
}
