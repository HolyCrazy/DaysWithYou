package com.liduo.dayswithyou.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import com.liduo.dayswithyou.R;
import com.liduo.dayswithyou.activities.MenuActivity;
import com.liduo.dayswithyou.beans.EventCardBean;
import com.liduo.dayswithyou.storage.EventDataBase;

/**
 * Created by LeeDuo on 2020/3/6.
 */

public class NotificationService extends Service {

    private NotificationManager notificationManager;
    private NotificationChannel notificationChannel;
    private static String CHANNEL_ID="1";
    private static String CHANNEL_NAME = "liduo";

    @Override
    public void onCreate() {
        super.onCreate();
        if(Build.VERSION.SDK_INT>=26){
            createNotificationChannel(CHANNEL_ID, CHANNEL_NAME);
        }

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int number = intent.getIntExtra("number",-1);
        if(number != -1){
            EventDataBase.getDataBase(this).updateNotificationByNumber(number,false);
            EventCardBean e = EventDataBase.getDataBase(this).queryByNumber(number);
            if(Build.VERSION.SDK_INT>=26){
                getNotificationManager().notify(number,getNotification_26(e));
            }else{
                getNotificationManager().notify(number,getNotification_25(e));
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private Notification getNotification_26(EventCardBean e){
        Intent i = new Intent(this,MenuActivity.class);
        PendingIntent p = PendingIntent.getActivity(this,e.getNumber(),i,PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.BigTextStyle style = new Notification.BigTextStyle();
        style.setBigContentTitle(e.getName());
        style.bigText(e.getPeople()+"\n"+e.getPlace());
        return  new Notification.Builder(this,CHANNEL_ID)
                .setContentTitle("今天是一个重要的日子")
                .setContentText(e.getDate()+" "+e.getName())
                .setStyle(style)
                .setSmallIcon(R.mipmap.app_icon)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.gift))
                .setWhen(System.currentTimeMillis())
                .setShowWhen(true)
                .setAutoCancel(true)
                .setContentIntent(p)
                .build();
    }

    private Notification getNotification_25(EventCardBean e){
        Intent i = new Intent(this,MenuActivity.class);
        PendingIntent p = PendingIntent.getActivity(this,e.getNumber(),i,PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle();
        style.setBigContentTitle(e.getName());
        style.bigText(e.getPeople()+"\n"+e.getPlace());
        return new NotificationCompat.Builder(this).setContentTitle("今天是一个重要的日子")
                .setContentText(e.getDate()+" "+e.getName())
                .setStyle(style)
                .setSmallIcon(R.mipmap.app_icon)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.gift))
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentIntent(p)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .build();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel(String id ,String name){
        notificationChannel = new NotificationChannel(id,name,NotificationManager.IMPORTANCE_DEFAULT);
        notificationChannel.enableVibration(true);
        getNotificationManager().createNotificationChannel(notificationChannel);
    }

    private NotificationManager getNotificationManager(){
        if(notificationManager == null)
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        return notificationManager;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
