package com.example.gallerymanager.utils;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class StatusBar {
    public static void hideStatusBar(Activity activity){
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M){
            return;
        }
        Window window=activity.getWindow();
        View decorView=window.getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);  //最后一个属性将状态栏变为白底黑字
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(Color.TRANSPARENT);   //这里又将状态栏背景变为透明，而布局背景又是黑的，这样就看不到状态栏，但是又确实占位置，返回的时候就不会产生上下抖动了
    }
}
