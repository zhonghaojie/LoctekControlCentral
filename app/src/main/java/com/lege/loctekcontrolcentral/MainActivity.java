package com.lege.loctekcontrolcentral;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.kongqw.serialportlibrary.Device;
import com.kongqw.serialportlibrary.SerialPortFinder;
import com.kongqw.serialportlibrary.SerialPortManager;
import com.kongqw.serialportlibrary.desk.action.DeskAction;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private SerialPortManager manager = new SerialPortManager();

    private DeskAction deskAction = new DeskAction();
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        open();
        findViewById(R.id.btn_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deskAction.getInfo();
            }
        });
        findViewById(R.id.btn_up).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        deskAction.startUp();
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        deskAction.stop();
                        break;
                }
                return false;
            }
        });
        findViewById(R.id.btn_down).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        deskAction.startDown();
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        deskAction.stop();
                        break;
                }
                return false;
            }
        });
        findViewById(R.id.btn_rst).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deskAction.rst();
            }
        });
        findViewById(R.id.btn_to_height).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deskAction.toHeight(800);
            }
        });
    }

    private void open() {
        SerialPortFinder serialPortFinder = new SerialPortFinder();
        ArrayList<Device> devices = serialPortFinder.getDevices();
        for (Device obj : devices) {
            Log.d("device===", obj.getName() + "..." + obj.getRoot());
            if (TextUtils.equals(obj.getName(), "ttyS5")) {
                SerialPortManager.getInstance().openSerialPort(obj.getFile(), 9600);
            }
        }
    }
}
