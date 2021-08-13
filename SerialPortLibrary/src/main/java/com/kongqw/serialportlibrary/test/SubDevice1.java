package com.kongqw.serialportlibrary.test;

import android.util.Log;

import com.kongqw.serialportlibrary.SerialPortUtil;
import com.kongqw.serialportlibrary.thread.SerialPortReadThread;

/**
 * Description:模拟从设备1
 * 固定发送
 * Created by loctek on 2021/8/2.
 */
public class SubDevice1 {
    private SerialPortReadThread mSerialPortReadThread;

    public void setSerialPortReadThread(SerialPortReadThread mSerialPortReadThread) {
        this.mSerialPortReadThread = mSerialPortReadThread;
    }


    public void receiveData(byte[] data){

        if(data[2] == (byte)0xff && data[3] == (byte)0x07){
            Log.d("控制逻辑","SubDevice1 收到数据  ");
            if(mSerialPortReadThread!=null){
                byte[] send = new byte[8];
                send[0] = (byte) 0x9b;
                send[1] = (byte) 0x9b;
                send[2] = (byte) 0xff;
                send[3] = (byte) 0x07;
                send[4] = (byte) 0x11;
                send[5] = (byte) 0x22;
                send[6] = (byte) 0x33;
                send[7] = (byte) 0x44;
                mSerialPortReadThread.onDataReceived(send);
            }
        }
    }
}
