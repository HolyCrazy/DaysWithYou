package com.liduo.dayswithyou.menu;

import android.app.Activity;
import android.app.Dialog;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.liduo.dayswithyou.dialog.CommonDialog;
import com.liduo.dayswithyou.dialog.OnCommonDialogClickListener;
import com.liduo.dayswithyou.R;
import com.liduo.dayswithyou.utils.TimeUtils;
import com.liduo.dayswithyou.utils.ViewUtils;
import com.liduo.dayswithyou.beans.EventCardBean;
import com.liduo.dayswithyou.notification.NotificationAlarmManager;
import com.liduo.dayswithyou.storage.EventDataBase;

/**
 * Created by LeeDuo on 2020/3/5.
 */

public class PopupWindowMenu implements View.OnClickListener,OnCommonDialogClickListener {

    private PopupWindowMenu(){}
    private View view;
    private PopupWindow popupWindow;
    private Activity activity;
    private EventCardBean eventCardBean;
    private OnDataChangeListener onDataChangeListener;
    private static final int MENU_WIDTH = 140;
    private static final int MENU_HEIGHT = 100;



    private static volatile PopupWindowMenu INSTANCE;
    public static PopupWindowMenu getInstance(){
        if(INSTANCE == null)
            synchronized (PopupWindowMenu.class){
                if(INSTANCE == null)
                    INSTANCE = new PopupWindowMenu();
            }
        return INSTANCE;
    }

    public PopupWindowMenu init(Activity activity,EventCardBean eventCardBean){
        this.activity = activity;
        this.eventCardBean = eventCardBean;
        if(view == null){
            view = LayoutInflater.from(activity).inflate(R.layout.view_long_click_menu,null,false);
            setClickListener(view);
        }
        setTextByNumber(view,eventCardBean);
        if(popupWindow == null)
            popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,true);
        popupWindowInit(popupWindow,activity);
        return INSTANCE;
    }

    private void setTextByNumber(View v ,EventCardBean eventCardBean){
        boolean hasNotify = EventDataBase.getDataBase(activity).getNotificationStateByNumber(eventCardBean.getNumber());
        String text = null;
        if(!TimeUtils.isPastDay(eventCardBean.getDate()) && hasNotify)
            text = "取消提醒";
        else
            text = "开启提醒";
        ((TextView)v.findViewById(R.id.app_menu_tv_notification)).setText(text);
    }

    public void show(View position,int x,int y){
        int positionX = x;
        int positionY = y;
        int screenHeight = ViewUtils.getScreenHeight(activity);
        int screenWidth = ViewUtils.getScreenWidth(activity);
        int menuHeight = (int)ViewUtils.dp2px(activity,MENU_HEIGHT);
        int menuWidth = (int) ViewUtils.dp2px(activity,MENU_WIDTH);
        if((x+menuWidth)>=screenWidth)
            positionX = x-menuWidth;
        if((y+menuHeight)>=screenHeight)
            positionY = y-menuHeight;
        popupWindow.setAnimationStyle(getAnimStyle(positionX,positionY,x,y));
        popupWindow.showAtLocation(position,Gravity.NO_GRAVITY,positionX,positionY);
    }

    private int getAnimStyle(int positionX,int positionY,int x,int y){
        if(positionX != x && positionY != y) return R.style.MenuAnimationRB;
        if(positionX != x) return R.style.MenuAnimationRT;
        if(positionY != y) return R.style.MenuAnimationLB;
        return R.style.MenuAnimationLT;
    }

    private void setClickListener(View v){
        v.findViewById(R.id.app_menu_tv_notification).setOnClickListener(this);
        v.findViewById(R.id.app_menu_tv_delete).setOnClickListener(this);
    }

    private void popupWindowInit(PopupWindow popupWindow, final Activity activity){
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setTouchable(true);
        popupWindow.setWidth((int) ViewUtils.dp2px(activity,MENU_WIDTH));
        popupWindow.setElevation((int)ViewUtils.dp2px(activity,2));
        //popupWindow.setAnimationStyle(R.style.PopupAnimation);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
            }
        });
    }

    public void setOnDataChangeListener(OnDataChangeListener onDataChangeListener){
        this.onDataChangeListener = onDataChangeListener;
    }

    private void notifyDataChange(){
        if(onDataChangeListener != null)
            onDataChangeListener.onDateChange();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.app_menu_tv_notification:
                boolean hasNotify = EventDataBase.getDataBase(activity).getNotificationStateByNumber(eventCardBean.getNumber());
                boolean hasPast = TimeUtils.isPastDay(eventCardBean.getDate());
                if(!hasPast && hasNotify){
                    NotificationAlarmManager.getInstance().init(activity).cancel(eventCardBean);
                    dismissAndToast(view,"取消成功");
                }else {
                    int state = NotificationAlarmManager.getInstance().init(activity).set(eventCardBean);
                    switch (state){
                        case NotificationAlarmManager.FAIL:
                            dismissAndToast(view,"开启失败");
                            break;
                        case NotificationAlarmManager.HAS_SET:
                            dismissAndToast(view,"已经开启了");
                            break;
                        case NotificationAlarmManager.IS_PAST:
                            dismissAndToast(view,"时光不能倒流");
                            break;
                        case NotificationAlarmManager.SUCCESS:
                            dismissAndToast(view,"开启成功");
                            break;
                    }
                }
                break;
            case R.id.app_menu_tv_delete:
                    CommonDialog commonDialog = new CommonDialog();
                    commonDialog.setDialogContent("真的要删除吗?");
                    commonDialog.setDialogLeftText("取消");
                    commonDialog.setDialogRightText("确定");
                    commonDialog.show(((FragmentActivity)activity).getSupportFragmentManager(),"deleteDialogFragment");
                    commonDialog.setOnDialogClickListener(this);
                break;
        }
    }

    private void dismissAndToast(View v ,String text){
        v.postDelayed(new Runnable() {
            @Override
            public void run() {
             popupWindow.dismiss();
            }
        },200);
        Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void clickLeft(Dialog dialog) {
        dialog.dismiss();
    }

    @Override
    public void clickRight(Dialog dialog) {
        dialog.dismiss();
        popupWindow.dismiss();
        NotificationAlarmManager.getInstance().init(activity).cancel(eventCardBean);
        EventDataBase.getDataBase(activity).deleteByNumber(eventCardBean.getNumber());
        Toast.makeText(activity, "删除成功", Toast.LENGTH_SHORT).show();
        notifyDataChange();
    }
}
