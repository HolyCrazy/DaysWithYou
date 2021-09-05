package com.liduo.dayswithyou.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.liduo.dayswithyou.widgets.DesktopWidgetProvider;
import com.liduo.dayswithyou.beans.EventCardBean;
import com.liduo.dayswithyou.adapters.EventCardListAdapter;
import com.liduo.dayswithyou.storage.EventDataBase;
import com.liduo.dayswithyou.adapters.OnItemClickListener;
import com.liduo.dayswithyou.R;
import com.liduo.dayswithyou.utils.ViewUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTouch;
import butterknife.Unbinder;

/**
 * Created by LeeDuo on 2020/3/9.
 */

public class ChooseEventActivity extends AppCompatActivity implements OnItemClickListener {

    private Unbinder unbinder;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<EventCardBean> eventCardBeanArrayList;
    private EventCardListAdapter eventCardListAdapter;
    private int id;

    @BindView(R.id.app_rv_choose_show)
    RecyclerView chooseShowRV;

    @BindView(R.id.app_iv_back)
    ImageView backIV;

    @OnTouch(R.id.app_iv_back)
    public boolean onTouch(View v , MotionEvent e){
        switch(e.getAction()){
            case MotionEvent.ACTION_DOWN:
                backIV.setBackgroundResource(R.drawable.back_press);
                break;
            case MotionEvent.ACTION_CANCEL:
                backIV.setBackgroundResource(R.drawable.back_normal);
                break;
            case MotionEvent.ACTION_UP:
                backIV.setBackgroundResource(R.drawable.back_normal);
                if(ViewUtils.isCancel(e,v)){
                    e.setAction(MotionEvent.ACTION_CANCEL);
                    return onTouch(v,e);
                }else{
                    ChooseEventActivity.this.finish();
                }
                break;
        }
        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);
        id = getIntent().getIntExtra("id",-1);
        unbinder = ButterKnife.bind(this);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        chooseShowRV.setLayoutManager(layoutManager);
        eventCardBeanArrayList = EventDataBase.getDataBase(this).queryAll();
        eventCardListAdapter = new EventCardListAdapter(eventCardBeanArrayList);
        eventCardListAdapter.setOnItemClickListener(this);
        chooseShowRV.setAdapter(eventCardListAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void onItemClick(int position) {
            Intent i = new Intent();
            i.putExtra("number",eventCardBeanArrayList.get(position).getNumber());
            i.putExtra("id",id);
            i.setAction(DesktopWidgetProvider.PREPARE_ACTION);
            i.setComponent(new ComponentName(this,DesktopWidgetProvider.class));
            sendBroadcast(i);
            this.finish();
    }

}
