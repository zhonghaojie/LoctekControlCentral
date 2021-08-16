package com.lege.loctekcontrolcentral;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.kongqw.serialportlibrary.Device;
import com.kongqw.serialportlibrary.SerialPortFinder;
import com.kongqw.serialportlibrary.SerialPortManager;
import com.kongqw.serialportlibrary.desk.DeskControlCentral;
import com.kongqw.serialportlibrary.desk.callback.IDeskUIListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements IDeskUIListener {

    String[] permissions = new String[]{
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private TextView tv;
    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 100);
            return false;
        }
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == 100) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // do something
            }
            return;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        open();
        tv = findViewById(R.id.tv_show);
        DeskControlCentral.getInstance().setUiListener(this);
        findViewById(R.id.btn_info).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        DeskControlCentral.getInstance().getInfo();
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        DeskControlCentral.getInstance().stop();
                        break;
                }
                return false;
            }
        });
        findViewById(R.id.btn_up).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        DeskControlCentral.getInstance().up();
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        DeskControlCentral.getInstance().stop();
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
                        DeskControlCentral.getInstance().down();
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        DeskControlCentral.getInstance().stop();
                        break;
                }
                return false;
            }
        });
        findViewById(R.id.btn_rst).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeskControlCentral.getInstance().rst();
            }
        });
        findViewById(R.id.btn_to_height).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        DeskControlCentral.getInstance().runToSitHeight(800);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        DeskControlCentral.getInstance().stop();
                        break;
                }
                return false;
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

    @Override
    public void showHeight(final int height) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("控制逻辑", "显示高度  " + height);
                tv.setText(""+height);
            }
        });
    }

    @Override
    public void showRSTState() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv.setText("RST");
            }
        });
    }

    @Override
    public void showError(final String error) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv.setText(error);
            }
        });
    }

    @Override
    public void showDeviceInfo(final String deviceInfo) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv.setText(deviceInfo);
            }
        });
    }
}
