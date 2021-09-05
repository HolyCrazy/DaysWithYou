package com.liduo.dayswithyou.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by LeeDuo on 2020/3/2.
 */

public class ViewUtils {

    private static WindowManager windowManager;
    private static Display display;

    public static int getScreenWidth( Context context){
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
        return display.getWidth();
    }
    public static int getScreenHeight( Context context){
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
        return display.getHeight();
    }

    public static void setStatusBarBackgroundColor(int color , Activity activity){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        }
    }
    public static void setStatusBarTextColor(boolean textBlack,Activity activity){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            View decorView = activity.getWindow().getDecorView();
            int ui = decorView.getSystemUiVisibility();
            if(textBlack)
                ui |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            else
                ui &= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;

            decorView.setSystemUiVisibility(ui);
        }
    }

    public static void showKeyBoard(View editText,Context context){
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(inputMethodManager != null)
            inputMethodManager.showSoftInput(editText,0);
    }

    public  static void hideKeyBoard(View editText,Context context){
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(inputMethodManager != null)
            inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(),0);
    }

    public static float dp2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return dpValue * scale + 0.5f;
    }

    public static float px2dp(Context context, float pxValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return pxValue / scale + 0.5f;
    }

    public static boolean isCancel(MotionEvent event, View v) {
        float touchX = event.getX();
        float touchY = event.getY();
        float maxX = v.getWidth();
        float maxY = v.getHeight();

        return touchX<0 || touchX>maxX || touchY < 0 || touchY > maxY;
    }



}
