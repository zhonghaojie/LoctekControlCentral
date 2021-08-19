package com.kongqw.serialportlibrary.airclean;

/**
 * Description:
 * Created by loctek on 2021/8/17.
 */
public class Constants {
    public static final int GEAR_CLOSE = 0;
    public static final int GEAR_SMART = 1;
    public static final int GEAR_1 = 2;
    public static final int GEAR_2 = 3;
    public static final int GEAR_3 = 4;
    public static final int GEAR_4 = 5;
    public static final int GEAR_5 = 6;
    /**
     * 空净状态查询
     */
    public static final String COMMAND_STATE_SEARCH = "06ff0101";
    public static final String COMMAND_NO_BUTTON = "";
    /**
     * 风机档位
     */
    public static final String COMMAND_GEAR_CLOSE = "07ff010200";
    public static final String COMMAND_GEAR_SMART = "07ff010210";
    public static final String COMMAND_GEAR_1 = "07ff010201";
    public static final String COMMAND_GEAR_2 = "07ff010202";
    public static final String COMMAND_GEAR_3 = "07ff010203";
    public static final String COMMAND_GEAR_4 = "07ff010204";
    public static final String COMMAND_GEAR_5 = "07ff010205";
    /**
     * 紫外线灯关闭
     */
    public static final String COMMAND_UV_CLOSE = "07ff010300";
    /**
     * 紫外线灯打开
     */
    public static final String COMMAND_UV_OPEN= "07ff010301";
    /**
     * 重置滤网寿命
     */
    public static final String COMMAND_RESET_FILTER_SCREEN_LIFE= "06ff0104";
    /**
     * 休眠
     */
    public static final String COMMAND_SLEEP= "07ff010500";
    /**
     * 唤醒
     */
    public static final String COMMAND_WAKEUP= "07ff010501";




}
