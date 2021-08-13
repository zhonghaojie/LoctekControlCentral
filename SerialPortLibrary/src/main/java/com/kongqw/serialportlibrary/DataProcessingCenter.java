package com.kongqw.serialportlibrary;

import android.util.Log;

import com.kongqw.serialportlibrary.desk.Constants;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Description:线程锁，为了保证一收一发
 * 所有发送逻辑走这里，即 {@link DataProcessingCenter#setCurrentCommand(String)}
 * Created by loctek on 2021/8/13.
 */
public class DataProcessingCenter {
    private static DataProcessingCenter instance = new DataProcessingCenter();
    private static Lock lock = new ReentrantLock();
    private static final int NO_BUTTON_SEND_MAX_COUNT = 10;

    public static DataProcessingCenter getInstance() {
        return instance;
    }

    private int count = 0;
    private String currentCommand = "";
    private Runnable processRunnable = new Runnable() {
        @Override
        public void run() {
            while (true) {
                synchronized (lock) {
                    try {
                        if (!currentCommand.equals("")) {
                            Log.d("控制逻辑", "发送指令  " + currentCommand);
                            SerialPortManager.getInstance().sendBytes(SerialPortUtil.packageBody(currentCommand));
                            if (isNoButtonCommand()) {
                                if (count < NO_BUTTON_SEND_MAX_COUNT) {
                                    count++;
                                } else {
                                    currentCommand = "";
                                }
                            }
                            //发送一次数据后就把当前线程wait掉，知道SerialPortManager收到完整的一帧数据或超时了调用notify才能发下一个数据
                            lock.wait();
                        }
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }


            }
        }
    };
    private Thread thread = new Thread(processRunnable);

    private DataProcessingCenter() {
        thread.start();
    }

    public String getCurrentCommand() {
        return currentCommand;
    }

    public void setCurrentCommand(String currentCommand) {
        this.currentCommand = currentCommand;
    }

    public Lock getLock() {
        return lock;
    }

    private boolean isNoButtonCommand() {
        return currentCommand.equals(Constants.COMMAND_NO_BUTTON);
    }
}
