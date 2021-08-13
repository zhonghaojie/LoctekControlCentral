package com.kongqw.serialportlibrary.thread;

import android.util.Log;

import com.kongqw.serialportlibrary.SerialPortUtil;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Kongqw on 2017/11/14.
 * 串口消息读取线程
 */

public abstract class SerialPortReadThread extends Thread {

    public abstract void onDataReceived(byte[] bytes);

    private static final String TAG = SerialPortReadThread.class.getSimpleName();
    private InputStream mInputStream;
    private byte[] mReadBuffer;

    public SerialPortReadThread(InputStream inputStream) {
        mInputStream = inputStream;
        mReadBuffer = new byte[1024];
        Log.d(TAG, "创建串口读取线程  ");
    }


    //模拟收到数据
    public void testReceive(byte[] data){
        onDataReceived(data);
    }
    @Override
    public void run() {
        super.run();

        while (true) {
            try {
                if (null == mInputStream) {
                    return;
                }

//                try {
//                之前出现数据不规则的现象，起先以为是数据没读取完整，进行以下处理，后来发现是升降桌控制盒休眠的问题
                    //防止数据不完整，每次读取之前等200ms
//                    Thread.sleep(200);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                int size = mInputStream.read(mReadBuffer);
                if (0 >= size) {
                    continue;
                }
                int bytes = 0;
                int ch;
                byte[] readBytes = new byte[size];

                System.arraycopy(mReadBuffer, 0, readBytes, 0, size);
                onDataReceived(readBytes);

            } catch (IOException e) {
                e.printStackTrace();
                return;
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
    }

    @Override
    public synchronized void start() {
        super.start();
    }

    /**
     * 关闭线程 释放资源
     */
    public void release() {
        interrupt();

        if (null != mInputStream) {
            try {
                mInputStream.close();
                mInputStream = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
