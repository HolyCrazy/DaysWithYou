package com.liduo.dayswithyou.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.liduo.dayswithyou.utils.TimeUtils;
import com.liduo.dayswithyou.beans.EventCardBean;
import com.liduo.dayswithyou.storage.EventDataBase;


/**
 * Created by LeeDuo on 2020/3/6.
 */

public class NotificationAlarmManager {

    private NotificationAlarmManager(){}
    private static volatile NotificationAlarmManager INSTANCE;
    private AlarmManager alarmManager;
    private Intent intent;
    private PendingIntent pendingIntent;
    private Context context;
    public static final int FAIL = 0;
    public static final int HAS_SET = 1;
    public static final int IS_PAST = 2;
    public static final int SUCCESS = 3;
    public static NotificationAlarmManager getInstance(){
        if(INSTANCE == null)
            synchronized (NotificationAlarmManager.class){
                if(INSTANCE == null)
                    INSTANCE = new NotificationAlarmManager();
            }
        return INSTANCE;
    }

    public NotificationAlarmManager init(Context context){
        this.context = context;
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        intent = new Intent(context,NotificationService.class);
        return INSTANCE;
    }
    public int set(EventCardBean eventCardBean){
        if(eventCardBean == null) return FAIL;
        if(EventDataBase.getDataBase(context).getNotificationStateByNumber(eventCardBean.getNumber())) return HAS_SET;
        if(TimeUtils.isPastDay(eventCardBean.getDate())) return IS_PAST;
        StringBuilder sb = new StringBuilder(eventCardBean.getDate());
        sb.append(" 00:00:00");
        intent.putExtra("number",eventCardBean.getNumber());
        pendingIntent = PendingIntent.getService(context,eventCardBean.getNumber(),intent,PendingIntent.FLAG_UPDATE_CURRENT);
        long timeMills = TimeUtils.dateToStamp(sb.toString());
        alarmManager.setExact(AlarmManager.RTC_WAKEUP,timeMills,pendingIntent);
        EventDataBase.getDataBase(context).updateNotificationByNumber(eventCardBean.getNumber(),true);
        return SUCCESS;
    }

    public void notifyNow(EventCardBean eventCardBean){
        if(eventCardBean == null) return;
        intent.putExtra("number",eventCardBean.getNumber());
        pendingIntent = PendingIntent.getService(context,eventCardBean.getNumber(),intent,PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(),pendingIntent);
    }

    public void cancel(EventCardBean eventCardBean){
        if(!EventDataBase.getDataBase(context).getNotificationStateByNumber(eventCardBean.getNumber())) return;
        pendingIntent = PendingIntent.getService(context,eventCardBean.getNumber(),intent,PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
        EventDataBase.getDataBase(context).updateNotificationByNumber(eventCardBean.getNumber(),false);
    }
}
