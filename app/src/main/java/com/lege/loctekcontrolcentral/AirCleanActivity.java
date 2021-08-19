package com.lege.loctekcontrolcentral;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.kongqw.serialportlibrary.DeviceInfo;
import com.kongqw.serialportlibrary.SerialPortUtil;
import com.kongqw.serialportlibrary.airclean.AirCleanControlCentral;
import com.kongqw.serialportlibrary.airclean.Constants;
import com.kongqw.serialportlibrary.airclean.listener.IAirCleanUIListener;


/**
 * Description:
 * Created by loctek on 2021/8/17.
 */
public class AirCleanActivity extends Activity implements IAirCleanUIListener {
    Switch uv;
    Switch sleepOrWakeup;
    TextView tv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_air_clean);
        AirCleanControlCentral.getInstance().setUiListener(this);
        tv = findViewById(R.id.tv_result);
        //查询状态
        findViewById(R.id.btn_get_state).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        AirCleanControlCentral.getInstance().getState();
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        AirCleanControlCentral.getInstance().stop();
                        break;
                }
                return false;
            }
        });
        //关
        findViewById(R.id.btn_gear_close).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        AirCleanControlCentral.getInstance().changeGear(Constants.GEAR_CLOSE);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        getState();
                        break;
                }
                return false;
            }
        });
        //智能档位
        findViewById(R.id.btn_gear_smart).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        AirCleanControlCentral.getInstance().changeGear(Constants.GEAR_SMART);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        getState();
                        break;
                }
                return false;
            }
        });
        //1挡
        findViewById(R.id.btn_gear_1).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        AirCleanControlCentral.getInstance().changeGear(Constants.GEAR_1);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        getState();
                        break;
                }
                return false;
            }
        });
        //2档
        findViewById(R.id.btn_gear_2).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        AirCleanControlCentral.getInstance().changeGear(Constants.GEAR_2);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        getState();
                        break;
                }
                return false;
            }
        });
        //3档
        findViewById(R.id.btn_gear_3).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        AirCleanControlCentral.getInstance().changeGear(Constants.GEAR_3);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        getState();
                        break;
                }
                return false;
            }
        });
        //4档
        findViewById(R.id.btn_gear_4).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        AirCleanControlCentral.getInstance().changeGear(Constants.GEAR_4);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        getState();
                        break;
                }
                return false;
            }
        });
        //5档
        findViewById(R.id.btn_gear_5).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        AirCleanControlCentral.getInstance().changeGear(Constants.GEAR_5);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        getState();
                        break;
                }
                return false;
            }
        });
        //紫外线灯
        uv = (Switch) findViewById(R.id.btn_uv_change);
        uv.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                uv.setEnabled(false);
                AirCleanControlCentral.getInstance().changeUVLight(isChecked);
                uv.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getState();
                        if (!isFinishing() && !isDestroyed()) {
                            uv.setEnabled(true);
                        }
                    }
                }, 100);
            }
        });
        //滤网寿命
        findViewById(R.id.btn_reset_filter_screen_life).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        AirCleanControlCentral.getInstance().resetFilterScreenLife();
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        getState();
                        break;
                }
                return false;
            }
        });

        //休眠唤醒
        sleepOrWakeup = (Switch) findViewById(R.id.btn_sleep_wakeup);
        sleepOrWakeup.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sleepOrWakeup.setEnabled(false);
                int state = 0;
                if(isChecked){
                    state =1;
                }
                AirCleanControlCentral.getInstance().sleepOrWakeup(state);
                sleepOrWakeup.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getState();
                        if (!isFinishing() && !isDestroyed()) {
                            sleepOrWakeup.setEnabled(true);
                        }
                    }
                }, 100);
            }
        });
    }
    private Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(1 == msg.what){
                tv.setText((String)msg.obj);
            }else if(2 == msg.what){
                AirCleanControlCentral.getInstance().stop();
            }
        }
    };
    private void getState(){
        AirCleanControlCentral.getInstance().getState();
        handler.sendEmptyMessageDelayed(2,200);

    }

    @Override
    public void onDeviceInfoResponse(DeviceInfo deviceInfo) {
        StringBuilder sb = new StringBuilder();
        sb.append("档位："+deviceInfo.getGear()+"\n");
        sb.append("PM2.5:"+deviceInfo.getPm25()+"\n");
        sb.append("甲醛:"+deviceInfo.getJiaquan()+"\n");
        sb.append("温度:"+deviceInfo.getTemperature()+"\n");
        sb.append("湿度:"+deviceInfo.getShidu()+"\n");
        sb.append("滤网剩余:"+deviceInfo.getFilterScreen()+"\n");
        sb.append("uv灯:"+deviceInfo.getUv()+"\n");
        sb.append("负离子:"+deviceInfo.getFulizi()+"（预留字段）"+"\n");
        sb.append("错误:"+ SerialPortUtil.byte2HexString(deviceInfo.getError())+"\n");
        Message msg = Message.obtain();
        msg.what = 1;
        msg.obj = sb.toString();
        handler.sendMessage(msg);

    }
}
