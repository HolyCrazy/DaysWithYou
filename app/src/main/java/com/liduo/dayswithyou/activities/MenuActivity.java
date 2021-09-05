package com.liduo.dayswithyou.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.liduo.dayswithyou.AppDefaultEvent;
import com.liduo.dayswithyou.beans.EventCardBean;
import com.liduo.dayswithyou.adapters.EventCardListAdapter;
import com.liduo.dayswithyou.storage.EventDataBase;
import com.liduo.dayswithyou.menu.OnDataChangeListener;
import com.liduo.dayswithyou.adapters.OnItemClickListener;
import com.liduo.dayswithyou.adapters.OnItemLongClickListener;
import com.liduo.dayswithyou.menu.PopupWindowMenu;
import com.liduo.dayswithyou.R;
import com.liduo.dayswithyou.storage.StorageBox;
import com.liduo.dayswithyou.utils.ViewUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTouch;
import butterknife.Unbinder;

public class MenuActivity extends AppCompatActivity implements OnItemClickListener,OnItemLongClickListener,OnDataChangeListener {

    private Unbinder unbinder;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<EventCardBean> eventCardBeanArrayList;
    private EventCardListAdapter eventCardListAdapter;


    @BindView(R.id.app_iv_add)
    ImageView addIV;
    @BindView(R.id.app_rv_show)
    RecyclerView showRV;

    @OnTouch(R.id.app_iv_add)
    public boolean onTouch(View v, MotionEvent event){
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                addIV.setBackgroundResource(R.drawable.add_press);
                break;
            case MotionEvent.ACTION_CANCEL:
                addIV.setBackgroundResource(R.drawable.add_normal);
                break;
            case MotionEvent.ACTION_UP:
                if(ViewUtils.isCancel(event, v)){
                    event.setAction(MotionEvent.ACTION_CANCEL);
                    return onTouch(v,event);
                }
                addIV.setBackgroundResource(R.drawable.add_normal);
                Intent intent = new Intent(MenuActivity.this,AddEventActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        unbinder = ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        AppDefaultEvent.getINSTANCE(this).setDefault();
        PopupWindowMenu.getInstance().setOnDataChangeListener(this);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        showRV.setLayoutManager(layoutManager);
        eventCardBeanArrayList = EventDataBase.getDataBase(this).queryAll();
        eventCardListAdapter = new EventCardListAdapter(eventCardBeanArrayList);
        eventCardListAdapter.setOnItemClickListener(this);
        eventCardListAdapter.setOnItemLongClickListener(this);
        showRV.setAdapter(eventCardListAdapter);


    }

    @Override
    protected void onRestart() {
        super.onRestart();
        onDateChange();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetNewEvent(EventCardBean eventCardBean){
        int position = StorageBox.getBox(MenuActivity.this).get("position",0)+1;
        eventCardBean.setNumber(position);
        StorageBox.getBox(MenuActivity.this).put("position",position);
        EventDataBase.getDataBase(this).insert(eventCardBean.getNumber(),eventCardBean.getName()
                ,eventCardBean.getPlace(),eventCardBean.getPeople()
                ,eventCardBean.getColor(),eventCardBean.getDate());
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(MenuActivity.this,EditEventActivity.class);
        intent.putExtra("number",eventCardBeanArrayList.get(position).getNumber());
        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(View view, int position,float x,float y) {
        PopupWindowMenu.getInstance().init(MenuActivity.this,eventCardBeanArrayList.get(position)).show(view,(int)x,(int)y);
        return true;
    }


    @Override
    public void onDateChange() {
        eventCardBeanArrayList = EventDataBase.getDataBase(this).queryAll();
        eventCardListAdapter.setData(eventCardBeanArrayList);
    }
}
