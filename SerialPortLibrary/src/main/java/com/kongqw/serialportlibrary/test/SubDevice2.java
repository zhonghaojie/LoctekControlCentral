package com.kongqw.serialportlibrary.test;

import android.util.Log;

import com.kongqw.serialportlibrary.thread.SerialPortReadThread;

/**
 * Description:模拟从设备2
 * Created by loctek on 2021/8/2.
 */
public class SubDevice2 {
    private SerialPortReadThread mSerialPortReadThread;

    public void setSerialPortReadThread(SerialPortReadThread mSerialPortReadThread) {
        this.mSerialPortReadThread = mSerialPortReadThread;
    }

    public void receiveData(byte[] data){
        if(data[2] == (byte)0xff && data[3] == (byte)0x06){
            Log.d("控制逻辑","SubDevice2 收到数据  ");
            if(mSerialPortReadThread!=null){
                byte[] send = new byte[8];
                send[0] = (byte) 0x9b;
                send[1] = (byte) 0x9b;
                send[2] = (byte) 0xff;
                send[3] = (byte) 0x06;
                send[4] = (byte) 0x11;
                send[5] = (byte) 0x22;
                send[6] = (byte) 0x33;
                send[7] = (byte) 0x44;
                mSerialPortReadThread.onDataReceived(send);
            }
        }
    }
}
