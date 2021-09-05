package com.liduo.dayswithyou.activities;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.TimePickerView;
import com.bigkoo.pickerview.lib.WheelView;
import com.liduo.dayswithyou.menu.ColorSelector;
import com.liduo.dayswithyou.core.DateCalculator;
import com.liduo.dayswithyou.beans.EventCardBean;
import com.liduo.dayswithyou.menu.OnColorChangeListener;
import com.liduo.dayswithyou.R;
import com.liduo.dayswithyou.utils.TimeUtils;
import com.liduo.dayswithyou.utils.ViewUtils;

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
 * Created by LeeDuo on 2020/3/2.
 */

public class AddEventActivity extends AppCompatActivity implements OnColorChangeListener {

    private Unbinder unbinder;
    private String eventColor = ColorSelector.COLORS[0];
    private GradientDrawable gradientDrawable;

    @BindView(R.id.app_iv_finish)
    ImageView finishIV;
    @BindView(R.id.app_et_event_name)
    EditText eventNameET;
    @BindView(R.id.app_et_event_place)
    EditText eventPlaceET;
    @BindView(R.id.app_et_event_people)
    EditText eventPeopleET;
    @BindView(R.id.app_tv_event_date)
    TextView eventDateTV;
    @BindView(R.id.app_tv_event_color)
    TextView eventColorTV;

    @OnTouch(R.id.app_iv_finish)
    public boolean onTouch(View v, MotionEvent e){
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
                    return onTouch(v,e);
                }
                finishIV.setBackgroundResource(R.drawable.finish_normal);
                String eventName = eventNameET.getText().toString().trim();
                String eventPlace = eventPlaceET.getText().toString().trim();
                String eventPeople = eventPeopleET.getText().toString().trim();
                String eventDate = eventDateTV.getText().toString().trim();
                if(!TextUtils.equals(eventName,"") && !TextUtils.equals(eventDate,"点我设置时间")){
                    DateCalculator dateCalculator = new DateCalculator(eventDate);
                    EventCardBean eventCardBean = new EventCardBean();
                    eventCardBean.setName(eventName);
                    eventCardBean.setDate(eventDate);
                    eventCardBean.setPeople(eventPeople);
                    eventCardBean.setPlace(eventPlace);
                    eventCardBean.setColor(eventColor);
                    eventCardBean.setDays(dateCalculator.calculateDays());
                    EventBus.getDefault().post(eventCardBean);
                    AddEventActivity.this.finish();
                }else{
                    Toast.makeText(this,"请填写名称并设置时间",Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return true;
    }



    @OnClick({R.id.app_tv_event_date,R.id.app_tv_event_color})
    public void onClick(View v){
        switch(v.getId()){
            case R.id.app_tv_event_date:
                ViewUtils.hideKeyBoard(eventNameET,this);
                ViewUtils.hideKeyBoard(eventPlaceET,this);
                ViewUtils.hideKeyBoard(eventPeopleET,this);
                timePickerSetAndShow(this,eventDateTV.getText().toString());
                break;
            case R.id.app_tv_event_color:
                ColorSelector.getInstance().init(AddEventActivity.this).setOnColorChangeListener(this).show(v);
                break;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        unbinder = ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        gradientDrawable.setColor(Color.parseColor(eventColor));
        gradientDrawable.setCornerRadius(ViewUtils.dp2px(this,5));
        gradientDrawable.setStroke((int)ViewUtils.dp2px(this,2),getResources().getColor(R.color.colorPrimary));
        eventColorTV.setBackground(gradientDrawable);

        eventNameET.postDelayed(new Runnable() {
            @Override
            public void run() {
                ViewUtils.showKeyBoard(eventNameET,AddEventActivity.this);
            }
        },500);

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

    private void timePickerSetAndShow(Context context,String selectedTime){
        Calendar selectTime = Calendar.getInstance();
        if(selectedTime != null && !TextUtils.equals(selectedTime,"点我设置时间")){
            String time  = selectedTime.split(" ")[0];
            String[] date = time.split("-");
            selectTime.set(Integer.parseInt(date[0]),Integer.parseInt(date[1]),Integer.parseInt(date[2]));
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
}
