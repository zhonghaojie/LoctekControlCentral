package com.kongqw.serialportlibrary.desk;

import android.os.CountDownTimer;
import android.util.Log;

import com.kongqw.serialportlibrary.DataProcessingCenter;
import com.kongqw.serialportlibrary.SerialPortManager;
import com.kongqw.serialportlibrary.SerialPortUtil;
import com.kongqw.serialportlibrary.desk.callback.IDeskCallback;
import com.kongqw.serialportlibrary.desk.callback.IDeskUIListener;
import com.kongqw.serialportlibrary.listener.OnSerialPortDataListener;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Description:升降模块控制中心，提供发送接收能力、最基础的协议封装
 * Created by loctek on 2021/8/4.
 */
public class DeskControlCentral implements OnSerialPortDataListener {
    public static String STATE_RST = "RST";
    public static String STATE_NORMAL = "NORMAL";
    public static String STATE_ERROR = "ERROR";
    private IDeskUIListener uiListener;
    //升降桌状态
    private String state = "";
    private boolean isStop = true;
    private boolean isRST = false;
    private byte[] currentCommand;
    //当前高度
    private int currentHeight = 0;
    //单位
    private int mUnit = 0;
    private int maxHeight = 0;
    private int minHeight = 0;
    private int sensitivity = 0;
    private String errorCode = "";
    //是否支持童锁
    private boolean supportChildLock = false;
    //是否支持急停
    private boolean supportEmergencyStop = false;
    //是否支持显示新编码
    private boolean supportShowNewEncode = false;
    //是否支持IAP功能
    private boolean supportIAP = false;
    //是否支持遇阻回退
    private boolean supportBack = false;

    public String getErrorCode() {
        return errorCode;
    }


    private static final DeskControlCentral instance = new DeskControlCentral();

    public static DeskControlCentral getInstance() {
        return instance;
    }

    private DeskControlCentral() {
        loadDeskControl();
    }

    /**
     * 添加监听
     */
    public void loadDeskControl() {
        SerialPortManager.getInstance().addOnSerialPortDataListener(this);
    }

    public void uninstallDeskControl() {
        SerialPortManager.getInstance().removeSerialPortDataListener(this);
    }


    private IDeskCallback callback;

    /**
     * 向上
     */
    public void up() {
        DataProcessingCenter.getInstance().setCurrentCommand(Constants.COMMAND_UP);
    }

    /**
     * 向下
     */
    public void down() {
        DataProcessingCenter.getInstance().setCurrentCommand(Constants.COMMAND_DOWN);
    }

    /**
     * 停止
     */
    public void stop() {
        isStop = true;

        DataProcessingCenter.getInstance().setCurrentCommand(Constants.COMMAND_NO_BUTTON);
    }

    /**
     * 紧急停止
     */
    public void stopImmediately() {
        isStop = true;
        DataProcessingCenter.getInstance().setCurrentCommand(Constants.COMMAND_STOP_IMMEDIATELY);
    }

    /**
     * 运行过程中回退
     */
    public void backWhenRunning() {
        DataProcessingCenter.getInstance().setCurrentCommand(Constants.COMMAND_BACK_WHEN_RUNNING);
    }

    /**
     * 运行到指定高度
     *
     * @param height 单位mm
     */
    public void runToSitHeight(int height) {
        byte[] heightBytes = new byte[2];
        heightBytes[0] = (byte) (0xff & height);
        heightBytes[1] = (byte) ((0xff00 & height) >> 8);
        DataProcessingCenter.getInstance().setCurrentCommand(Constants.COMMAND_RUN_TO_SPECIFIC_HEIGHT + SerialPortUtil.byte2HexString(heightBytes));
    }

    /**
     * 获取设备信息
     */
    public void getInfo() {
        DataProcessingCenter.getInstance().setCurrentCommand(Constants.COMMAND_GET_DEVICE_INFO);
    }

    /**
     * 获取控制盒状态
     */
    public void getDeviceState() {
        DataProcessingCenter.getInstance().setCurrentCommand(Constants.COMMAND_GET_DEVICE_STATE);
    }

    /**
     * 停止复位
     */
    public void stopRST() {
        isStop = true;
    }

    /**
     * 复位
     */
    public void rst() {
        isStop = false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                //只要控制盒没有回复rst或者没有手动停止，就一直发rst
                DataProcessingCenter.getInstance().setCurrentCommand(Constants.COMMAND_NO_BUTTON);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                DataProcessingCenter.getInstance().setCurrentCommand(Constants.COMMAND_KEY_RST);
            }
        }).start();
    }


    /**
     * 设置遇阻回退灵敏度
     *
     * @param sensitivity
     */
    public void setSensitivity(int sensitivity) {
        switch (sensitivity) {
            case 0:
                DataProcessingCenter.getInstance().setCurrentCommand(Constants.COMMAND_CHANGE_SENSITIVITY_CLOSE);
                break;
            case 1:
                DataProcessingCenter.getInstance().setCurrentCommand(Constants.COMMAND_CHANGE_SENSITIVITY_LOW);
                break;
            case 2:
                DataProcessingCenter.getInstance().setCurrentCommand(Constants.COMMAND_CHANGE_SENSITIVITY_MID);
                break;
            case 3:
                DataProcessingCenter.getInstance().setCurrentCommand(Constants.COMMAND_CHANGE_SENSITIVITY_HIGH);
                break;
        }
    }

    /**
     * 数据解析
     *
     * @param bytes 接收到的数据
     */
    @Override
    public void onDataReceived(byte[] bytes) {
        if (currentCommand != null) {
            if (bytes[2] == (byte) 0x00 && bytes[3] == (byte) 0x07) {
                byte s1 = bytes[5];
                byte s2 = bytes[6];
                byte s3 = bytes[7];
                String result = "";
                if (ButtonValue.isRST(s1, s2, s3)) {
                    result = "RST";
                    if (!isRST) {
                        stop();
                    }
                    Log.i("控制逻辑", "复位");
                    isRST = true;
                    if (uiListener != null) {
                        uiListener.showRSTState();
                    }
                    state = STATE_RST;
                    errorCode = "";
                } else if (ButtonValue.isBottom(s1, s2, s3)) {
                    state = STATE_NORMAL;
                    result = "Bottom";
                    errorCode = "";
                } else if (ButtonValue.isError(s1)) {
                    state = STATE_ERROR;
                    result = ButtonValue.getError(s1, s2, s3);
                    errorCode = result;
                    if (uiListener != null) {
                        uiListener.showError(errorCode);
                    }
                } else if (ButtonValue.isOFF(s1, s2, s3)) {
                    result = "电源关闭";
                } else if (ButtonValue.isOL(s1, s2, s3)) {
                    result = "结构下滑";
                } else if (ButtonValue.isOn(s1, s2, s3)) {
                    result = "电源打开";
                    errorCode = "";
                } else if (ButtonValue.isTop(s1, s2, s3)) {
                    state = STATE_NORMAL;
                    result = "Top";
                    errorCode = "";
                } else if (bytes[4] == (byte) 0x82) {
                    if (uiListener != null) {
                        //例 00 01 02 03 04 05 06 07 08 09 10 11 12 13 14 15 16 17 18 19
                        //例 9B 12 00 07 82 00 42 04 12 02 03 10 00 6E 00 64 00 74 81 9D
                        int unit = bytes[5];//单位
                        String max = SerialPortUtil.byte2HexString(new byte[]{bytes[7], bytes[6]});
                        String min = SerialPortUtil.byte2HexString(new byte[]{bytes[9], bytes[8]});
                        maxHeight = Integer.parseInt(max, 16);
                        minHeight = Integer.parseInt(min, 16);
                        sensitivity = bytes[10];//遇阻回退灵敏度
                        //设备功能标志位 设备功能标志位说明，1表示支持，0表示不支持。
                        //7：保留；6：保留；5：保留；4：是否支持IAP功能；3：新显示编码；2：遇阻回退命令；1：急停命令；0：童锁功能
                        String functionStr = SerialPortUtil.byteToBinary(bytes[11]);//要转成二进制
                        supportIAP = (functionStr.charAt(3) == '1');
                        supportShowNewEncode = (functionStr.charAt(4) == '1');
                        supportBack = (functionStr.charAt(5) == '1');
                        supportEmergencyStop = (functionStr.charAt(6) == '1');
                        supportChildLock = (functionStr.charAt(7) == '1');
                        String encode = SerialPortUtil.byte2HexString(new byte[]{bytes[13], bytes[12]});//软件编码
                        String version = SerialPortUtil.byte2HexString(new byte[]{bytes[15], bytes[14]});
                        int encodeInt = Integer.parseInt(encode, 16);
                        int v = Integer.parseInt(version, 16);
                        result = "单位：" + unit + "  maxHeight = " + maxHeight +
                                "  minHeight = " + minHeight + " sensitivity = " + sensitivity +
                                " function { " + "支持IAP = " + supportIAP + "，支持新编码 = " + supportShowNewEncode + "，遇阻回退 = " + supportBack + "，急停 = " + supportEmergencyStop + "，童锁 = " + supportChildLock + "}  " +
                                "编码 = " + encodeInt + " 版本 = " + v;
                        Log.d("控制逻辑", result);
                        uiListener.showDeviceInfo(result);
                    }
                } else {
                    state = STATE_NORMAL;
                    isRST = false;
                    result = ButtonValue.getNumber(s1, s2, s3);

                    try {
                        float number = Float.parseFloat(result);
                        currentHeight = (int) (number * 10);
                        if (uiListener != null) {
                            uiListener.showHeight(currentHeight);
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }

                    errorCode = "";
                }
            }
        }
    }

    @Override
    public void onDataSent(byte[] bytes) {
        currentCommand = bytes;
    }


    public void setUiListener(IDeskUIListener uiListener) {
        this.uiListener = uiListener;
    }
}
