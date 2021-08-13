package com.kongqw.serialportlibrary.desk;

/**
 * Description:
 * Created by loctek on 2021/8/4.
 */
public class Constants {
    public static int SENS_CLOSE = 0;//遇阻回退 关闭
    public static int SENS_LOW = 1;//遇阻回退 低
    public static int SENS_MID = 2;//遇阻回退 中
    public static int SENS_HIGH = 3;//遇阻回退 高

    public static String STATE_RST = "RST";
    public static String STATE_NORMAL = "NORMAL";
    public static String STATE_ERROR = "ERROR";


    public static byte COMMAND_GET_BUTTON = (byte) 0x11;//控制板向按键板获取键值命令 0x11
    public static byte COMMAND_SHOW_STR = (byte) 0x12;//控制板控制按键板显示指定字符命令0x12
    public static byte RESPONSE_DEVICE_INFO = (byte) 0x82;//控制盒回复设备信息


    public static String COMMAND_FINISH_RESPONSE = "060007ff";//控制盒回复动作完成
    public static String COMMAND_RUN_TO_SPECIFIC_HEIGHT = "08000780";//运行到指定高度
    public static String COMMAND_GET_DEVICE_STATE = "06000781";//获取控制盒状态
    public static String COMMAND_GET_DEVICE_STATE_RESPONSE = "09000781";//控制盒回复设备状态

//    public static String COMMAND_GET_DEVICE_INFO = "050382";//获取控制盒信息
    public static String COMMAND_GET_DEVICE_INFO = "06000782";//获取控制盒信息
    public static String COMMAND_GET_DEVICE_INFO_RESPONSE = "11000782";//控制盒回复设备信息11:6+11(单位、最高高度低、高字节；最低高度低、高字节；遇阻回退灵敏度；设备功能标志位；软件编码低、高字节；软件版本低、高字节)

    public static String COMMAND_NO_BUTTON = "080007830000";//无按键
    public static String COMMAND_UP = "080007830100";//上
//    public static String COMMAND_UP = "06020100";//上
    public static String COMMAND_DOWN = "080007830200";//下
//    public static String COMMAND_DOWN = "06020200";//下
    public static String COMMAND_KEY_1 = "080007830400";//1键
    public static String COMMAND_KEY_2 = "080007830800";//2键
    public static String COMMAND_KEY_3 = "080007831000";//3键
    public static String COMMAND_KEY_M =   "080007832000";//M键
    public static String COMMAND_KEY_RST = "080007833000";//复位 M+3
    public static String COMMAND_KEY_A = "080007834000";//A键
    public static String COMMAND_KEY_CHILD_LOCK_OPEN = "080007830001";//童锁开
    public static String COMMAND_KEY_CHILD_LOCK_CLOSE = "080007830002";//童锁关

    public static String COMMAND_BACK_WHEN_RUNNING = "06000784";//运行过程中回退，无应答的话延时后继续发
    public static String COMMAND_STOP_IMMEDIATELY = "06000785";//紧急停止

    public static String COMMAND_CHANGE_SENSITIVITY_CLOSE = "0800078900";//遇阻回退灵敏度关
    public static String COMMAND_CHANGE_SENSITIVITY_LOW = "0800078901";//遇阻回退灵敏度低
    public static String COMMAND_CHANGE_SENSITIVITY_MID = "0800078902";//遇阻回退灵敏度中
    public static String COMMAND_CHANGE_SENSITIVITY_HIGH = "0800078903";//遇阻回退灵敏度高


}
