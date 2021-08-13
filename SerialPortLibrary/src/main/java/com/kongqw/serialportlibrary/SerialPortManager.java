package com.kongqw.serialportlibrary;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import com.kongqw.serialportlibrary.desk.DeskControlCentral;
import com.kongqw.serialportlibrary.listener.OnOpenSerialPortListener;
import com.kongqw.serialportlibrary.listener.OnSerialPortDataListener;
import com.kongqw.serialportlibrary.test.SubDevice1;
import com.kongqw.serialportlibrary.test.SubDevice2;
import com.kongqw.serialportlibrary.thread.SerialPortReadThread;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 这里作为公共的收发处理类，
 * 提供：crc检验、数据发送、接收、拼接完整帧的功能
 * 具体的业务逻辑由各个子模块自己实现
 *
 * @see DeskControlCentral
 * <p>
 * SerialPortManager
 */

public class SerialPortManager extends SerialPort {
    private static final int STATE_IDLE = 0;
    private static final int STATE_BUSY = 1;


    private static final int WHAT_TIME_OUT = 2;
    private static final int WHAT_SEND_BYTES = 1;
    public static final long SEND_INTERVAL = 500L;
    private static final SerialPortManager instance = new SerialPortManager();
    private boolean isBoxResponse = true;

    public static SerialPortManager getInstance() {
        return instance;
    }

    private static final String TAG = SerialPortManager.class.getSimpleName();
    private FileInputStream mFileInputStream;
    private FileOutputStream mFileOutputStream;
    private FileDescriptor mFd;
    private ArrayList<OnOpenSerialPortListener> mOnOpenSerialPortListener = new ArrayList<OnOpenSerialPortListener>();
    private ArrayList<OnSerialPortDataListener> mOnSerialPortDataListener = new ArrayList<OnSerialPortDataListener>();

    private HandlerThread mSendingHandlerThread;
    private Handler mSendingHandler;
    private SerialPortReadThread mSerialPortReadThread;
    private static Lock lock = new ReentrantLock();
    private byte[] sendCommandList;

    /**
     * 打开串口
     *
     * @param device   串口设备
     * @param baudRate 波特率
     * @return 打开是否成功
     */
    public boolean openSerialPort(File device, int baudRate) {

        Log.i(TAG, "openSerialPort: " + String.format("打开串口 %s  波特率 %s", device.getPath(), baudRate));

        // 校验串口权限
        if (!device.canRead() || !device.canWrite()) {
            boolean chmod777 = chmod777(device);
            if (!chmod777) {
                Log.i(TAG, "openSerialPort: 没有读写权限");
                if (null != mOnOpenSerialPortListener) {
                    for (OnOpenSerialPortListener listener : mOnOpenSerialPortListener) {
                        listener.onFail(device, OnOpenSerialPortListener.Status.NO_READ_WRITE_PERMISSION);
                    }
                }
                return false;
            }
        }

        try {
            mFd = open(device.getAbsolutePath(), baudRate, 0);
            mFileInputStream = new FileInputStream(mFd);
            mFileOutputStream = new FileOutputStream(mFd);
            Log.i(TAG, Thread.currentThread().getName() + "  openSerialPort: 串口已经打开 " + mFd + "   " + mFileInputStream + "  " + mFileOutputStream);
            if (null != mOnOpenSerialPortListener) {
                for (OnOpenSerialPortListener listener : mOnOpenSerialPortListener) {
                    listener.onSuccess(device);
                }
            }
            // 开启发送消息的线程
            startSendThread();
            // 开启接收消息的线程
            startReadThread();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            if (null != mOnOpenSerialPortListener) {
                for (OnOpenSerialPortListener listener : mOnOpenSerialPortListener) {
                    listener.onFail(device, OnOpenSerialPortListener.Status.NO_READ_WRITE_PERMISSION);
                }
            }
        }
        return false;
    }

    /**
     * 关闭串口
     */
    public void closeSerialPort() {

        if (null != mFd) {
            close();
            mFd = null;
        }
        // 停止发送消息的线程
        stopSendThread();
        // 停止接收消息的线程
        stopReadThread();

        if (null != mFileInputStream) {
            try {
                mFileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mFileInputStream = null;
        }

        if (null != mFileOutputStream) {
            try {
                mFileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mFileOutputStream = null;
        }

        mOnOpenSerialPortListener = null;

        mOnSerialPortDataListener = null;
    }


    private int retryTime = 0;

    /**
     * 开启发送消息的线程
     */
    private void startSendThread() {
        // 开启发送消息的线程
        mSendingHandlerThread = new HandlerThread("mSendingHandlerThread");
        mSendingHandlerThread.start();
        // Handler
        mSendingHandler = new Handler(mSendingHandlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                try {
                    if (WHAT_SEND_BYTES == msg.what) {//发送数据
                        byte[] sendBytes = (byte[]) msg.obj;
                        if(msg.arg1 == 1){
                            Log.d("控制逻辑", "发送指令  超时重发" + SerialPortUtil.byte2HexString(sendBytes));
                        }else{
                            Log.d("控制逻辑", "发送指令  " + SerialPortUtil.byte2HexString(sendBytes));
                        }
                        sendCommandList = sendBytes;
                        mFileOutputStream.write(sendBytes);
                        //将发送指令加入队列
                        if (null != mOnSerialPortDataListener) {
                            for (OnSerialPortDataListener listener : mOnSerialPortDataListener) {
                                listener.onDataSent(sendBytes);
                            }
                        }
                        sendEmptyMessageDelayed(WHAT_TIME_OUT, 500);
                    } else if (WHAT_TIME_OUT == msg.what) {
                        clear();
                        if (retryTime < 5) {
                            Message m = new Message();
                            m.what = WHAT_SEND_BYTES;
                            m.obj = sendCommandList;
                            m.arg1 = 1;
                            sendMessage(m);
                            retryTime+=1;
                            Log.d("控制逻辑","重试");
                        }else {
                            isBoxResponse = true;
                            retryTime = 0;
                            Log.d("控制逻辑","超出重试次数  isBoxResponse = "+isBoxResponse+"  state = "+state);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }


    /**
     * 开启接收消息的线程
     */
    private void startReadThread() {
        mSerialPortReadThread = new SerialPortReadThread(mFileInputStream) {
            @Override
            public void onDataReceived(byte[] bytes) {
                checkData(bytes);
            }
        };
        mSerialPortReadThread.start();
    }


    private int state = STATE_IDLE;
    private int bufferCount = 0;
    private byte[] buffer = new byte[40];

    private void checkData(byte[] receivedData) {

        byte[] item = new byte[1];
        for (byte datum : receivedData) {
            item[0] = datum;

            if (state == STATE_IDLE && datum == SerialPortUtil.HEAD) {
                state = STATE_BUSY;
                Log.d("缓存", "原始数据  " + SerialPortUtil.byte2HexString(item));
            } else if (state == STATE_BUSY) {
                //转义字符判定
                if ((datum == SerialPortUtil.HEAD || datum == SerialPortUtil.FOOT) && (bufferCount < buffer[0])//计数器是否小于协议中规定的帧长
                        && buffer[bufferCount - 1] == (byte) 0x5c) {
                    buffer[bufferCount - 1] = datum;
                } else if (datum != SerialPortUtil.FOOT || bufferCount != buffer[0]) {
                    buffer[bufferCount] = datum;
                    bufferCount++;
                    if (bufferCount > 31) {
//                        onFrameReceived(createFrame());
                        clear();
                    }
                    Log.i("缓存", "原始数据  " + SerialPortUtil.byte2HexString(item) + "  bufferCount = " + bufferCount + "  buffer[0] = " + buffer[0]);
                } else if (datum == SerialPortUtil.FOOT) {
                    Log.e("缓存", "原始数据  " + SerialPortUtil.byte2HexString(item));
                    onFrameReceived(createFrame());
                    clear();
                }
            }
        }
    }

    private byte[] createFrame() {

        byte[] result = new byte[bufferCount + 2];
        result[0] = (byte) 0x9b;
        result[bufferCount + 1] = (byte) 0x9d;
        for (int i = 0; i < bufferCount; i++) {
            result[i + 1] = buffer[i];
        }
        return result;
    }

    private void clear() {
        state = STATE_IDLE;
        bufferCount = 0;
        Arrays.fill(buffer, (byte) 0x00);
    }

    /**
     * 接收到完整的一帧数据了
     *
     * @param frameData
     */
    private void onFrameReceived(byte[] frameData) {
        //如果发送和收到的属于同一设备，则是合法的一次发、收
        if (frameData[2] == sendCommandList[2] && frameData[3] == sendCommandList[3]) {
            Log.e("控制逻辑", "收到完整一帧数据 " + SerialPortUtil.byte2HexString(frameData) +"  isBoxResponse = "+isBoxResponse +"  state = "+state);
            clear();
            mSendingHandler.removeMessages(WHAT_TIME_OUT);
            mSendingHandler.removeMessages(WHAT_SEND_BYTES);
            retryTime = 0;
            isBoxResponse = true;
            byte[] body = new byte[frameData.length - 2];//去掉头尾

            for (int i = 0; i < body.length; i++) {
                body[i] = frameData[i + 1];
            }

            byte[] bodyWithoutCRC = new byte[body.length - 2];
            for (int i = 0; i < bodyWithoutCRC.length; i++) {
                bodyWithoutCRC[i] = body[i];
            }
            String str1 = CRC16M.getBufHexStr(body);
            String str2 = CRC16M.getBufHexStr(CRC16M.getSendBuf(CRC16M.getBufHexStr(bodyWithoutCRC)));
            if (str1.equalsIgnoreCase(str2)) {
                if (null != mOnSerialPortDataListener) {
                    for (OnSerialPortDataListener listener : mOnSerialPortDataListener) {
                        listener.onDataReceived(frameData);
                    }
                }
            }
        }
    }

    /**
     * 发送数据
     *
     * @param sendBytes 发送数据
     * @return 发送是否成功
     */
    public void sendBytes(byte[] sendBytes) {
        if (state == STATE_BUSY || !isBoxResponse) {
            Log.e("控制逻辑", "发送线程正忙，请稍候 state = "+state +"  isBoxResponse = "+isBoxResponse);
            return;
        }
        isBoxResponse = false;
        lock.lock();
        try {
            if (null != mFd && null != mFileInputStream && null != mFileOutputStream) {
                if (0 < sendBytes.length) {
                    if (null != mSendingHandler) {
                        Message message = Message.obtain();
                        message.obj = sendBytes;
                        message.what = WHAT_SEND_BYTES;
                        mSendingHandler.sendMessage(message);
                    }

                }

            }

        } finally {
            lock.unlock();
        }

        return;
    }

    /**
     * 停止接收消息的线程
     */
    private void stopReadThread() {
        if (null != mSerialPortReadThread) {
            mSerialPortReadThread.release();
        }
    }


    /**
     * 停止发送消息线程
     */
    private void stopSendThread() {
        mSendingHandler = null;
        if (null != mSendingHandlerThread) {
            mSendingHandlerThread.interrupt();
            mSendingHandlerThread.quit();
            mSendingHandlerThread = null;
        }
    }

    /**
     * 添加打开串口监听
     *
     * @param listener listener
     * @return SerialPortManager
     */
    public SerialPortManager setOnOpenSerialPortListener(OnOpenSerialPortListener listener) {
        if (!mOnOpenSerialPortListener.contains(listener)) {
            mOnOpenSerialPortListener.add(listener);
        }
        return this;
    }


    public void removeSerialPortOpenListener(OnOpenSerialPortListener listener) {
        mOnOpenSerialPortListener.remove(listener);
    }

    /**
     * 添加数据通信监听
     *
     * @param listener listener
     * @return SerialPortManager
     */
    public void addOnSerialPortDataListener(OnSerialPortDataListener listener) {
        if (!mOnSerialPortDataListener.contains(listener)) {
            mOnSerialPortDataListener.add(listener);
        }
    }

    public void removeSerialPortDataListener(OnSerialPortDataListener listener) {
        mOnSerialPortDataListener.remove(listener);
    }
}
