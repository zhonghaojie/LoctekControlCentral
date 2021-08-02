package com.kongqw.serialportlibrary;

import com.kongqw.serialportlibrary.listener.OnSerialPortDataListener;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Description:读写代理
 * 小秘书屏作为主设备，主动向从设备发送指令，从设备收到指令后回复主设备，从设备不会主动上报数据
 * 主板是半双工的
 * commandQueue 里面放最新的一条指令，并且只能有一条指令
 * 从设备回复之后，将commandQueue里的指令清除，保证指令一发一回
 * Created by loctek on 2021/8/2.
 */
public class ReadAndWriteProxy {
    private final static ReadAndWriteProxy instance = new ReadAndWriteProxy();

    public static ReadAndWriteProxy getInstance() {
        return instance;
    }

    private FileOutputStream fos;
    private LinkedList<String> commandQueue = new LinkedList<>();
    private ArrayList<OnSerialPortDataListener> mOnSerialPortDataListener;
    public void initFOS(FileOutputStream fos) {
        this.fos = fos;
    }

    /**
     * 添加数据通信监听
     *
     * @param listener listener
     * @return SerialPortManager
     */
    public void setOnSerialPortDataListener(OnSerialPortDataListener listener) {
        if(!mOnSerialPortDataListener.contains(listener)){
            mOnSerialPortDataListener.add(listener);
        }
    }

    public void removeSerialPortDataListener(OnSerialPortDataListener listener){
        mOnSerialPortDataListener.remove(listener);
    }
    public void sendData(byte[] sendData) {
        if (null != fos && null != sendData && 0 < sendData.length) {
            try {
                fos.write(sendData);
                for (OnSerialPortDataListener listener : mOnSerialPortDataListener) {
                    listener.onDataSent(sendData);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    }

    public void onDataReceive(byte[] receiveData) {
        for (OnSerialPortDataListener listener : mOnSerialPortDataListener) {
            listener.onDataReceived(receiveData);
        }
    }

}
