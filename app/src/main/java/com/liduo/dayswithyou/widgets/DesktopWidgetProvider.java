package com.liduo.dayswithyou.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import com.liduo.dayswithyou.core.DateCalculator;
import com.liduo.dayswithyou.R;
import com.liduo.dayswithyou.activities.ChooseEventActivity;
import com.liduo.dayswithyou.beans.EventCardBean;
import com.liduo.dayswithyou.storage.EventDataBase;

/**
 * Created by LeeDuo on 2020/3/8.
 */

public class DesktopWidgetProvider extends AppWidgetProvider{
    public static final String PREPARE_ACTION="com.liduo.dayswithyou.prepare";
    public static final String CLICK_ACTION = "com.liduo.dayswithyou.click";
    public static final String UPDATE_ACTION = "com.liduo.dayswithyou.update";
    private static final String TAG = "----------------";
    private static int appId;
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if(intent.getAction().equals(PREPARE_ACTION)){
            int number = intent.getIntExtra("number",-1);
            int id = intent.getIntExtra("id",-1);
            if(number != -1 && id != -1){
                EventDataBase.AppWidgetAgent.getAgent(context).updateNumberAndAppWidgetId(id,number);
                EventDataBase.AppWidgetAgent.getAgent(context).updateAppWidgetColorById(id,true);
                update(id,number,context,true);
            }
        }
        
        if(intent.getAction().equals(CLICK_ACTION)){
            int number = intent.getIntExtra("number",-1);
            int id = intent.getIntExtra("id",-1);
            if(number != -1 && id != -1){
                boolean isBlack = EventDataBase.AppWidgetAgent.getAgent(context).getAppWidgetColorById(id);
                update(id,number,context,!isBlack);
                EventDataBase.AppWidgetAgent.getAgent(context).updateAppWidgetColorById(id,!isBlack);
            }
        }
        if(intent.getAction().equals(UPDATE_ACTION)){
            int number = intent.getIntExtra("number",-1);
            if(number != -1){
                int id = EventDataBase.AppWidgetAgent.getAgent(context).getAppWidgetIdByNumber(number);
                if(id != -1){
                    boolean isBlack = EventDataBase.AppWidgetAgent.getAgent(context).getAppWidgetColorById(id);
                    update(id,number,context,isBlack);
                }
            }

        }
    }

    private void update(int id ,int number,Context context,boolean isBlack){
        RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.remote_view);
        EventCardBean eventCardBean = EventDataBase.getDataBase(context).queryByNumber(number);
        if(eventCardBean == null) return;
        DateCalculator dateCalculator = new DateCalculator(eventCardBean.getDate());
        int days = dateCalculator.calculateDays();
        if(days != 0){
            remoteView.setTextViewText(R.id.app_remote_view_days,Math.abs(days)+"天");
        }else{
            remoteView.setTextViewText(R.id.app_remote_view_days,"今天");
        }
        remoteView.setTextViewText(R.id.app_remote_view_date,eventCardBean.getDate());
        remoteView.setTextViewText(R.id.app_remote_view_name,eventCardBean.getName());
        Intent i = new Intent(CLICK_ACTION);
        i.putExtra("number",number);
        i.putExtra("id",id);
        i.setComponent(new ComponentName(context,DesktopWidgetProvider.class));
        PendingIntent pi = PendingIntent.getBroadcast(context,id,i,PendingIntent.FLAG_UPDATE_CURRENT);
        remoteView.setOnClickPendingIntent(R.id.app_remote_view_bg,pi);

        if(isBlack){
            remoteView.setTextColor(R.id.app_remote_view_days,context.getResources().getColor(R.color.colorAccent));
            remoteView.setTextColor(R.id.app_remote_view_date,context.getResources().getColor(R.color.textColorGray));
            remoteView.setTextColor(R.id.app_remote_view_name,context.getResources().getColor(R.color.textColorBlack));
        }else {
            remoteView.setTextColor(R.id.app_remote_view_days,context.getResources().getColor(R.color.pureWhite));
            remoteView.setTextColor(R.id.app_remote_view_date,context.getResources().getColor(R.color.pureWhite));
            remoteView.setTextColor(R.id.app_remote_view_name,context.getResources().getColor(R.color.pureWhite));
        }
        AppWidgetManager.getInstance(context).updateAppWidget(id,remoteView);

    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
        if(EventDataBase.AppWidgetAgent.getAgent(context).getNumberByAppWidgetId(appWidgetId) != -1) return;
        if(appId != appWidgetId){
            appId = appWidgetId;
            Intent i = new Intent(context,ChooseEventActivity.class);
            i.putExtra("id",appWidgetId);
            context.startActivity(i);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        int number = EventDataBase.AppWidgetAgent.getAgent(context).getNumberByAppWidgetId(appWidgetIds[0]);
        if(number != -1){
            boolean isBlack = EventDataBase.AppWidgetAgent.getAgent(context).getAppWidgetColorById(appWidgetIds[0]);
            update(appWidgetIds[0],number,context,isBlack);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        Log.d(TAG, "onDeleted: -----------");
        int number = EventDataBase.AppWidgetAgent.getAgent(context).getNumberByAppWidgetId(appWidgetIds[0]);
        if(number != -1){
            EventDataBase.AppWidgetAgent.getAgent(context).deleteAppWidgetIdAndNumber(appWidgetIds[0]);
        }
    }

}
