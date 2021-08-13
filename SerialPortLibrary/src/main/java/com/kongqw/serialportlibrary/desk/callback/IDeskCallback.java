package com.kongqw.serialportlibrary.desk.callback;

import org.jetbrains.annotations.NotNull;

/**
 * Description:
 * Created by loctek on 2021/8/4.
 */
public interface IDeskCallback {
    void deviceError(@NotNull String error);
    void showHeight(@NotNull String height);
    void onRST(@NotNull String rst);
    void onTop(@NotNull String onTop);
    void onBottom(@NotNull String onBottom);
    //结构下滑
    void onSlideDownward(@NotNull String onBottom);
    void onDeviceInfo(int unit, int sensitivity, int maxHeight, int minHeight, String data);
}
