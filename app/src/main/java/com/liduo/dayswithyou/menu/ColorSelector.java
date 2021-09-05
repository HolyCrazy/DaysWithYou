package com.liduo.dayswithyou.menu;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.liduo.dayswithyou.R;

/**
 * Created by LeeDuo on 2020/3/4.
 */

public class ColorSelector implements View.OnClickListener{

    private ColorSelector(){}
    private View view;
    private PopupWindow popupWindow;
    private Activity activity;
    public static final String[] COLORS = {"#FFD60C","#08C261","#E60041","#F689A9","#FF6825","#A13DEB","#3CC8FF","#FF4B42"};
    private static final int[] COLOR_VIEWS = {R.id.app_view_tv_color1,R.id.app_view_tv_color2,R.id.app_view_tv_color3,R.id.app_view_tv_color4,
            R.id.app_view_tv_color5,R.id.app_view_tv_color6,R.id.app_view_tv_color7,R.id.app_view_tv_color8};
    private String selectedColor = COLORS[0];
    private int selectedView = R.id.app_view_fl_color1;
    private OnColorChangeListener onColorChangeListener;

    private static volatile ColorSelector INSTANCE;
    public static ColorSelector getInstance(){
        if(INSTANCE == null)
            synchronized (ColorSelector.class){
                if(INSTANCE == null)
                    INSTANCE = new ColorSelector();
            }
        return INSTANCE;
    }

    public ColorSelector init(Activity activity){
        this.activity = activity;
        if(view == null){
            view = LayoutInflater.from(activity).inflate(R.layout.view_color_selected,null,false);
            setOnClickListener();
            setViewsBackGround(view, COLOR_VIEWS,COLORS);
        }
        if(popupWindow == null)
           popupWindow = new PopupWindow(view,ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,true);
        popupWindowInit(popupWindow,activity);
        return INSTANCE;
    }

    public void show(View position){
        popupWindow.showAtLocation(position, Gravity.BOTTOM,0,0);
        changeWindowAlpha(0.6f,activity);
    }

    public ColorSelector setOnColorChangeListener(OnColorChangeListener onColorChangeListener){
        this.onColorChangeListener = onColorChangeListener;
        return INSTANCE;
    }

    private void setOnClickListener(){
        view.findViewById(R.id.app_view_tv_color_cancel).setOnClickListener(this);
        view.findViewById(R.id.app_view_tv_color_finish).setOnClickListener(this);
        view.findViewById(R.id.app_view_fl_color1).setOnClickListener(this);
        view.findViewById(R.id.app_view_fl_color2).setOnClickListener(this);
        view.findViewById(R.id.app_view_fl_color3).setOnClickListener(this);
        view.findViewById(R.id.app_view_fl_color4).setOnClickListener(this);
        view.findViewById(R.id.app_view_fl_color5).setOnClickListener(this);
        view.findViewById(R.id.app_view_fl_color6).setOnClickListener(this);
        view.findViewById(R.id.app_view_fl_color7).setOnClickListener(this);
        view.findViewById(R.id.app_view_fl_color8).setOnClickListener(this);
    }
    private void setViewsBackGround(View parent,int[] viewIds,String[] colors){
        int i=0;
        for(int id :viewIds)
            parent.findViewById(id).setBackgroundColor(Color.parseColor(colors[i++]));
    }





    private void changeWindowAlpha(float alpha, Activity activity){
        WindowManager.LayoutParams layoutParams = activity.getWindow().getAttributes();
        layoutParams.alpha = alpha;
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        activity.getWindow().setAttributes(layoutParams);
    }
    private void popupWindowInit(PopupWindow popupWindow, final Activity activity){
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setTouchable(true);
        popupWindow.setAnimationStyle(R.style.PopupAnimation);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                changeWindowAlpha(1.0f,activity);
            }
        });
    }
    private void changeSelectedState(View parent,int selectedViewId,int unSelectedViewId){
        parent.findViewById(selectedViewId).setBackgroundResource(R.drawable.tv_color_unselected_bg);
        parent.findViewById(unSelectedViewId).setBackgroundResource(R.drawable.tv_color_selected_bg);
    }
    private void startTransaction(int viewId,int arrayNum){
        if(selectedView != viewId){
            changeSelectedState(view,selectedView,viewId);
            selectedView = viewId;
            selectedColor = COLORS[arrayNum];
        }
    }
    private void onColorChangeEvent(OnColorChangeListener onColorChangeListener,String color){
        if(onColorChangeListener != null){
            onColorChangeListener.onColorChange(color);
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.app_view_tv_color_cancel:
                popupWindow.dismiss();
                break;
            case R.id.app_view_tv_color_finish:
                onColorChangeEvent(onColorChangeListener,selectedColor);
                popupWindow.dismiss();
                break;
            case R.id.app_view_fl_color1:
                startTransaction(R.id.app_view_fl_color1,0);
                break;
            case R.id.app_view_fl_color2:
                startTransaction(R.id.app_view_fl_color2,1);
                break;
            case R.id.app_view_fl_color3:
                startTransaction(R.id.app_view_fl_color3,2);
                break;
            case R.id.app_view_fl_color4:
                startTransaction(R.id.app_view_fl_color4,3);
                break;
            case R.id.app_view_fl_color5:
                startTransaction(R.id.app_view_fl_color5,4);
                break;
            case R.id.app_view_fl_color6:
                startTransaction(R.id.app_view_fl_color6,5);
                break;
            case R.id.app_view_fl_color7:
                startTransaction(R.id.app_view_fl_color7,6);
                break;
            case R.id.app_view_fl_color8:
                startTransaction(R.id.app_view_fl_color8,7);
                break;
        }
    }
}
