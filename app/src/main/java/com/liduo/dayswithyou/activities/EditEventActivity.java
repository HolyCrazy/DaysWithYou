package com.liduo.dayswithyou.activities;

import android.app.Dialog;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.TimePickerView;
import com.bigkoo.pickerview.lib.WheelView;
import com.liduo.dayswithyou.menu.ColorSelector;
import com.liduo.dayswithyou.dialog.CommonDialog;
import com.liduo.dayswithyou.beans.EventCardBean;
import com.liduo.dayswithyou.storage.EventDataBase;
import com.liduo.dayswithyou.notification.NotificationAlarmManager;
import com.liduo.dayswithyou.menu.OnColorChangeListener;
import com.liduo.dayswithyou.dialog.OnCommonDialogClickListener;
import com.liduo.dayswithyou.R;
import com.liduo.dayswithyou.utils.TimeUtils;
import com.liduo.dayswithyou.utils.ViewUtils;
import com.liduo.dayswithyou.widgets.DesktopWidgetProvider;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import butterknife.Unbinder;

/**
 * Created by LeeDuo on 2020/3/4.
 */

public class EditEventActivity extends AppCompatActivity implements OnColorChangeListener,OnCommonDialogClickListener {

    private Unbinder unbinder;
    private String eventColor;
    private GradientDrawable gradientDrawable;
    private EventCardBean eventCardBean;

    @BindView(R.id.app_iv_edit_finish)
    ImageView finishIV;
    @BindView(R.id.app_iv_edit_delete)
    ImageView deleteIV;
    @BindView(R.id.app_et_edit_event_name)
    EditText eventNameET;
    @BindView(R.id.app_et_edit_event_place)
    EditText eventPlaceET;
    @BindView(R.id.app_et_edit_event_people)
    EditText eventPeopleET;
    @BindView(R.id.app_tv_edit_event_date)
    TextView eventDateTV;
    @BindView(R.id.app_tv_edit_event_color)
    TextView eventColorTV;

    @OnTouch(R.id.app_iv_edit_finish)
    public boolean onTouchIVFinish(View v, MotionEvent e){
        switch(e.getAction()){
            case MotionEvent.ACTION_DOWN:
                finishIV.setBackgroundResource(R.drawable.finish_press);
                break;
            case MotionEvent.ACTION_CANCEL:
                finishIV.setBackgroundResource(R.drawable.finish_normal);
                break;
            case MotionEvent.ACTION_UP:
                if(ViewUtils.isCancel(e, v)){
                    e.setAction(MotionEvent.ACTION_CANCEL);
                    return onTouchIVFinish(v,e);
                }
                finishIV.setBackgroundResource(R.drawable.finish_normal);
                if(eventCardBean != null){
                    String eventName = eventNameET.getText().toString().trim();
                    String eventPlace = eventPlaceET.getText().toString().trim();
                    String eventPeople = eventPeopleET.getText().toString().trim();
                    String eventDate = eventDateTV.getText().toString().trim();
                    if(!TextUtils.equals(eventName,"") && !TextUtils.equals(eventDate,"点我设置时间")){

                        if(EventDataBase.getDataBase(this).getNotificationStateByNumber(eventCardBean.getNumber())){
                            NotificationAlarmManager.getInstance().init(this).cancel(eventCardBean);
                            NotificationAlarmManager.getInstance().init(this).set(eventCardBean);
                        }

                            Intent i = new Intent(DesktopWidgetProvider.UPDATE_ACTION);
                            i.putExtra("number",eventCardBean.getNumber());
                            i.setComponent(new ComponentName(this, DesktopWidgetProvider.class));
                            sendBroadcast(i);

                        EventDataBase.getDataBase(EditEventActivity.this).updateByNumber(eventCardBean.getNumber()
                                ,eventName,eventPlace,eventPeople,eventColor,eventDate);


                        EditEventActivity.this.finish();
                    }else{
                        Toast.makeText(this,"请填写名称并设置时间",Toast.LENGTH_SHORT).show();
                    }

                }
                break;
        }
        return true;
    }

    @OnTouch(R.id.app_iv_edit_delete)
    public boolean onTouchIVDelete(View v, MotionEvent e){
        switch(e.getAction()){
            case MotionEvent.ACTION_DOWN:
                deleteIV.setBackgroundResource(R.drawable.delete_press);
                break;
            case MotionEvent.ACTION_CANCEL:
                deleteIV.setBackgroundResource(R.drawable.delete_normal);
                break;
            case MotionEvent.ACTION_UP:
                if(ViewUtils.isCancel(e, v)){
                    e.setAction(MotionEvent.ACTION_CANCEL);
                    return onTouchIVDelete(v,e);
                }
                deleteIV.setBackgroundResource(R.drawable.delete_normal);
                if(eventCardBean != null){
                        CommonDialog commonDialog = new CommonDialog();
                        commonDialog.setDialogContent("真的要删除吗?");
                        commonDialog.setDialogLeftText("取消");
                        commonDialog.setDialogRightText("确定");
                        commonDialog.show(getSupportFragmentManager(),"deleteDialogFragment");
                        commonDialog.setOnDialogClickListener(this);
                }
                break;
        }
        return true;
    }



    @OnClick({R.id.app_tv_edit_event_date,R.id.app_tv_edit_event_color})
    public void onClick(View v){
        switch(v.getId()){
            case R.id.app_tv_edit_event_date:
                ViewUtils.hideKeyBoard(eventNameET,this);
                ViewUtils.hideKeyBoard(eventPlaceET,this);
                ViewUtils.hideKeyBoard(eventPeopleET,this);
                timePickerSetAndShow(this,eventDateTV.getText().toString());
                break;
            case R.id.app_tv_edit_event_color:
                ColorSelector.getInstance().init(EditEventActivity.this).setOnColorChangeListener(this).show(v);
                break;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        unbinder = ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        int number = getIntent().getIntExtra("number",-1);
        if(number == -1) this.finish();
        eventCardBean = EventDataBase.getDataBase(this).queryByNumber(number);

        restoreDataState(eventCardBean);



    }

    public void restoreDataState(EventCardBean eventCardBean){
        if(eventCardBean != null){
            eventNameET.setText(eventCardBean.getName());
            eventNameET.setSelection(eventCardBean.getName().length());
            eventDateTV.setText(eventCardBean.getDate());
            gradientDrawable = new GradientDrawable();
            gradientDrawable.setShape(GradientDrawable.RECTANGLE);
            gradientDrawable.setColor(Color.parseColor(eventCardBean.getColor()));
            gradientDrawable.setCornerRadius(ViewUtils.dp2px(this,5));
            gradientDrawable.setStroke((int)ViewUtils.dp2px(this,2),getResources().getColor(R.color.colorPrimary));
            eventColorTV.setBackground(gradientDrawable);
            eventColor = eventCardBean.getColor();
        }
        String eventPlace = eventCardBean.getPlace();
        String eventPeople = eventCardBean.getPeople();
        if(eventPlace != null) eventPlaceET.setText(eventPlace);
        if(eventPeople != null) eventPeopleET.setText(eventPeople);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onHandleDateSelected(Date date){
        String time = TimeUtils.stampToDate(date.getTime()).split(" ")[0];
        eventDateTV.setText(time);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        EventBus.getDefault().unregister(this);
    }

    private void timePickerSetAndShow(Context context, String selectedTime){
        Calendar selectTime = Calendar.getInstance();
        if(selectedTime != null && !TextUtils.equals(selectedTime,"点我设置时间")){
            String time  = selectedTime.split(" ")[0];
            String[] date = time.split("-");
            selectTime.set(Integer.parseInt(date[0]),Integer.parseInt(date[1])-1,Integer.parseInt(date[2]));
        }else{
            selectTime.set(TimeUtils.getYear(),TimeUtils.getMonth()-1,TimeUtils.getDay());
        }
        Calendar startTime = Calendar.getInstance();
        startTime.set(1949,12,31);
        Calendar stopTime = Calendar.getInstance();
        stopTime.set(2100,12,31);
        TimePickerView timePickerView = new TimePickerView.Builder(context, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                EventBus.getDefault().post(date);
            }
        }).setType(new boolean[]{true,true,true,false,false,false})
                .setLineSpacingMultiplier(2.0f)
                .setDate(selectTime)
                .setRangDate(startTime,stopTime)
                .setLabel("","","","","","")
                .setContentSize(18)
                .setSubmitColor(context.getResources().getColor(R.color.colorPrimary))
                .setCancelColor(Color.parseColor("#666666"))
                .setTitleBgColor(Color.WHITE)
                .setSubCalSize(14)
                .setDividerType(WheelView.DividerType.FILL)
                .build();
        timePickerView.show();
    }

    @Override
    public void onColorChange(String color) {
        eventColor = color;
        gradientDrawable.setColor(Color.parseColor(eventColor));
        eventColorTV.setBackground(gradientDrawable);
    }

    @Override
    public void clickLeft(Dialog dialog) {
        dialog.dismiss();
    }

    @Override
    public void clickRight(Dialog dialog) {
        NotificationAlarmManager.getInstance().init(this).cancel(eventCardBean);
        EventDataBase.getDataBase(EditEventActivity.this).deleteByNumber(eventCardBean.getNumber());
        EditEventActivity.this.finish();
    }
}
