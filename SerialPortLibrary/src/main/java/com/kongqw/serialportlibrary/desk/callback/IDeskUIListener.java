package com.kongqw.serialportlibrary.desk.callback;

/**
 * Description:
 * Created by loctek on 2021/8/13.
 */
public interface IDeskUIListener {
    void showHeight(int height);
    void showRSTState();
    void showError(String error);
    void showDeviceInfo(String deviceInfo);
}
