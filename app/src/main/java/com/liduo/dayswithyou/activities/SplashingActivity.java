package com.liduo.dayswithyou.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.liduo.dayswithyou.R;
import com.liduo.dayswithyou.activities.MenuActivity;
import com.liduo.dayswithyou.notification.NotificationService;
import com.liduo.dayswithyou.storage.EventDataBase;

/**
 * Created by LeeDuo on 2020/3/5.
 */

public class SplashingActivity extends AppCompatActivity implements Handler.Callback{
    private Handler handler;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splashing);
        EventDataBase.getDataBase(this).init(this);
        handler = new Handler(this);
        handler.sendEmptyMessageDelayed(0,2000);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what){
            case 0:
                Intent intent = new Intent(this,MenuActivity.class);
                startActivity(intent);
                this.finish();
                break;
        }
        return true;
    }
}
