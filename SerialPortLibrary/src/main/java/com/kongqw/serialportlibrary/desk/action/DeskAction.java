package com.kongqw.serialportlibrary.desk.action;

import android.util.Log;

import com.kongqw.serialportlibrary.SerialPortManager;
import com.kongqw.serialportlibrary.desk.DeskControlCentral;

/**
 * Description:升降桌的一系列动作，根据DeskControlCentral提供的能力，去组合功能
 * Created by loctek on 2021/8/9.
 */
public class DeskAction {
    private Runnable runnable;
    private boolean isStop = false;


    public void getInfo() {
        DeskControlCentral.getInstance().getInfo();
    }

    public void startUp() {
        startAction();
        Log.i("控制逻辑", "按下上");
        runnable = new Runnable() {
            @Override
            public void run() {
                while (!isStop) {
                    try {
                        DeskControlCentral.getInstance().up();
                        Thread.sleep(SerialPortManager.SEND_INTERVAL);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        new Thread(runnable).start();
    }

    public void startDown() {
        startAction();
        runnable = new Runnable() {
            @Override
            public void run() {
                while (!isStop) {
                    try {
                        DeskControlCentral.getInstance().down();
                        Thread.sleep(SerialPortManager.SEND_INTERVAL);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        new Thread(runnable).start();
    }

    private synchronized void startAction() {
        isStop = false;
    }

    public synchronized void stop() {
        DeskControlCentral.getInstance().stop();
        isStop = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                DeskControlCentral.getInstance().stop();
                DeskControlCentral.getInstance().stop();
                DeskControlCentral.getInstance().stop();
                DeskControlCentral.getInstance().stop();
                DeskControlCentral.getInstance().stop();
            }
        }).start();

    }

    public void rst(){
        DeskControlCentral.getInstance().rst();
    }

    public void toHeight(int height){
        DeskControlCentral.getInstance().runToSitHeight(height);
    }
}
