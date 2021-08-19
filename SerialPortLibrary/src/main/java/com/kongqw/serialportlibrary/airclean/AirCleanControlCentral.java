package com.kongqw.serialportlibrary.airclean;

import com.kongqw.serialportlibrary.DataProcessingCenter;
import com.kongqw.serialportlibrary.DeviceInfo;
import com.kongqw.serialportlibrary.SerialPortManager;
import com.kongqw.serialportlibrary.SerialPortUtil;
import com.kongqw.serialportlibrary.airclean.listener.IAirCleanUIListener;
import com.kongqw.serialportlibrary.listener.OnSerialPortDataListener;

import java.util.ArrayList;

/**
 * Description:
 * Created by loctek on 2021/8/17.
 */
public class AirCleanControlCentral implements OnSerialPortDataListener {
    private static AirCleanControlCentral instance = new AirCleanControlCentral();

    private AirCleanControlCentral() {
        SerialPortManager.getInstance().addOnSerialPortDataListener(this);
    }

    public static AirCleanControlCentral getInstance() {
        return instance;
    }
//    private ArrayList<IAirCleanUIListener> uiListener = new ArrayList<>();
    private  IAirCleanUIListener uiListener ;

    private int gear ;
    private int pm25;
    private int jiaquan;
    private String temperature;
    private String shidu ;
    private String filterScreenLeft;
    private int uvState ;
    private int fulizi;
    private int version;
    private byte[] error;

    public IAirCleanUIListener getUiListener() {
        return uiListener;
    }

    public void setUiListener(IAirCleanUIListener uiListener) {
        this.uiListener = uiListener;
    }

    public void getState() {
        DataProcessingCenter.getInstance().setCurrentCommand(Constants.COMMAND_STATE_SEARCH);
    }

    public void stop() {
        DataProcessingCenter.getInstance().setCurrentCommand(Constants.COMMAND_NO_BUTTON);
    }

    /**
     * 调整档位
     *
     * @param gear
     */
    public void changeGear(int gear) {
        String command = "";
        switch (gear) {
            case Constants.GEAR_CLOSE:
                command = Constants.COMMAND_GEAR_CLOSE;
                break;
            case Constants.GEAR_SMART:
                command = Constants.COMMAND_GEAR_SMART;
                break;
            case Constants.GEAR_1:
                command = Constants.COMMAND_GEAR_1;
                break;
            case Constants.GEAR_2:
                command = Constants.COMMAND_GEAR_2;
                break;
            case Constants.GEAR_3:
                command = Constants.COMMAND_GEAR_3;
                break;
            case Constants.GEAR_4:
                command = Constants.COMMAND_GEAR_4;
                break;
            case Constants.GEAR_5:
                command = Constants.COMMAND_GEAR_5;
                break;
        }
        DataProcessingCenter.getInstance().setCurrentCommand(command);
    }

    /**
     * 打开/关闭紫外灯
     *
     * @param isOpen
     */
    public void changeUVLight(boolean isOpen) {
        if (isOpen) {
            DataProcessingCenter.getInstance().setCurrentCommand(Constants.COMMAND_UV_OPEN);
        } else {
            DataProcessingCenter.getInstance().setCurrentCommand(Constants.COMMAND_UV_CLOSE);
        }
    }

    /**
     * 重置滤网寿命
     */
    public void resetFilterScreenLife() {
        DataProcessingCenter.getInstance().setCurrentCommand(Constants.COMMAND_RESET_FILTER_SCREEN_LIFE);
    }

    /**
     * 休眠、唤醒
     *
     * @param state
     */
    public void sleepOrWakeup(int state) {
        if (state == 0) {
            DataProcessingCenter.getInstance().setCurrentCommand(Constants.COMMAND_SLEEP);
        } else {
            DataProcessingCenter.getInstance().setCurrentCommand(Constants.COMMAND_WAKEUP);
        }
    }

//            5+0:风机状态，0关闭，0x10智能档位，其他为风机档位；
//            5+1：PM2.5高；
//            5+2：PM2.5低；
//            5+3：甲醛高；
//            5+4：甲醛低；
//            5+5：温度（有符号）；
//            5+6：湿度；
//            5+7：滤网剩余百分比；
//            5+8：uv灯状态，0关闭，1打开；
//            5+9：负离子状态,0关闭，1打开（预留）；
//            5+(10-15)：前3字节为软件版本，后3字节为软件编码；
//            5+(16-20)：错误状态，依次为滤网寿命错误、电机错误、温湿度错误、PM2.5错误、甲醛错误，0表示正常，1表示错误
    @Override
    public void onDataReceived(byte[] bytes) {
        if (bytes[4] == (byte) 0x01) {
            //回复设备状态
            //例：00 01 02 03 04 05 06 07 08 09 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27
            //例：9B 1B FF 01 01 00 00 00 00 04 1D 38 64 00 00 42 30 31 30 30 31 00 00 00 00 00 2F FC 9D
            gear = bytes[5];
            String pm25Str = SerialPortUtil.byte2HexString(new byte[]{bytes[6], bytes[7]});
            pm25 = Integer.parseInt(pm25Str,16);
            String jiaquanStr = SerialPortUtil.byte2HexString(new byte[]{bytes[8], bytes[9]});
            jiaquan = Integer.parseInt(jiaquanStr,16);
            temperature = bytes[10]+"";
            shidu = bytes[11]+"";
            filterScreenLeft = bytes[12]+"";
            uvState = bytes[13];
            fulizi = bytes[14];
            version = Integer.parseInt(SerialPortUtil.byte2HexString(new byte[]{bytes[15],bytes[16],bytes[17]}),16);
            error = new byte[]{bytes[21],bytes[22],bytes[23],bytes[24],bytes[25]};
            if(uiListener!=null){
                DeviceInfo deviceInfo = new DeviceInfo();
                deviceInfo.setGear(gear);
                deviceInfo.setPm25(pm25);
                deviceInfo.setError(error);
                deviceInfo.setFilterScreen(filterScreenLeft);
                deviceInfo.setFulizi(fulizi);
                deviceInfo.setJiaquan(jiaquan);
                deviceInfo.setShidu(shidu);
                deviceInfo.setTemperature(temperature);
                deviceInfo.setUv(uvState);
                deviceInfo.setVersion(version);
                uiListener.onDeviceInfoResponse(deviceInfo);
            }


        }
    }

    @Override
    public void onDataSent(byte[] bytes) {
    }
}
