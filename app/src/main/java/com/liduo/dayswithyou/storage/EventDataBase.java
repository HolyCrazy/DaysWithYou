package com.liduo.dayswithyou.storage;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;

import com.liduo.dayswithyou.core.DateCalculator;
import com.liduo.dayswithyou.beans.EventCardBean;
import com.liduo.dayswithyou.notification.NotificationAlarmManager;
import com.liduo.dayswithyou.utils.TimeUtils;

import java.util.ArrayList;

/**
 * Created by LeeDuo on 2020/3/3.
 */

public class EventDataBase {
    private static final String DATABASE = "db_event";
    private static final String TABLE = "tab_event";
    private static volatile EventDataBase INSTANCE;
    private static volatile DataBaseHelper dataBaseHelper;
    private static volatile SQLiteDatabase sqLiteDatabase;

    public static EventDataBase getDataBase(Context context){
        if(INSTANCE == null){
            synchronized (EventDataBase.class){
                if(INSTANCE == null){
                    INSTANCE = new EventDataBase();
                    dataBaseHelper = new DataBaseHelper(context,DATABASE,null,1);
                }
            }
        }
        return INSTANCE;
    }

    public void init(Context context){
        sqLiteDatabase = dataBaseHelper.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.query(TABLE,null,null,null,null,null,null);
        cursor.moveToFirst();
        for(int i=0;i<cursor.getCount();i++){
            if(TimeUtils.isPastDay(cursor.getString(5))){
                if(cursor.getInt(6) == 1){
                    EventCardBean eventCardBean = new EventCardBean();
                    eventCardBean.setNumber(cursor.getInt(0));
                    eventCardBean.setName(cursor.getString(1));
                    eventCardBean.setPlace(cursor.getString(2));
                    eventCardBean.setPeople(cursor.getString(3));
                    eventCardBean.setColor(cursor.getString(4));
                    eventCardBean.setDate(cursor.getString(5));
                    eventCardBean.setNotification(cursor.getInt(6) == 1?true:false);
                    NotificationAlarmManager.getInstance().init(context).notifyNow(eventCardBean);
                }
                updateNotificationByNumber(cursor.getInt(0),false);
            }
            cursor.moveToNext();
        }
        cursor.close();
        sqLiteDatabase.close();
    }

    public ArrayList<EventCardBean> queryAll(){
        sqLiteDatabase = dataBaseHelper.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.query(TABLE,null,null,null,null,null,null);
        cursor.moveToFirst();
        ArrayList<EventCardBean> list = new ArrayList<>();
        DateCalculator dateCalculator = new DateCalculator();
        for(int i=0;i<cursor.getCount();i++){
            EventCardBean eventCardBean = new EventCardBean();
            eventCardBean.setNumber(cursor.getInt(0));
            eventCardBean.setName(cursor.getString(1));
            eventCardBean.setPlace(cursor.getString(2));
            eventCardBean.setPeople(cursor.getString(3));
            eventCardBean.setColor(cursor.getString(4));
            eventCardBean.setDate(cursor.getString(5));
            eventCardBean.setNotification(cursor.getInt(6) == 1?true:false);
            dateCalculator.setDate(eventCardBean.getDate());
            eventCardBean.setDays(dateCalculator.calculateDays());
            list.add(eventCardBean);
            cursor.moveToNext();
        }
        cursor.close();
        sqLiteDatabase.close();
        return list;
    }

    public EventDataBase updateByNumber(int number, @Nullable String name , @Nullable String place,
                                        @Nullable String people, @Nullable String color, @Nullable String date){
        sqLiteDatabase = dataBaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues(6);
        if(name != null) contentValues.put("name",name);
        if(place != null) contentValues.put("place",place);
        if(people != null) contentValues.put("people",people);
        if(color != null) contentValues.put("color",color);
        if(date != null) contentValues.put("date",date);
        if(contentValues.size() != 0){
            sqLiteDatabase.update(TABLE,contentValues,"number=?",new String[]{String.valueOf(number)});
        }
        sqLiteDatabase.close();
        return INSTANCE;
    }

    public EventCardBean queryByNumber(int number){
        sqLiteDatabase = dataBaseHelper.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.query(TABLE,null,"number=?",new String[]{String.valueOf(number)},null,null,null);
        EventCardBean eventCardBean = null;
        if(cursor.getCount() != 0){
            eventCardBean = new EventCardBean();
            cursor.moveToFirst();
            eventCardBean.setNumber(cursor.getInt(0));
            eventCardBean.setName(cursor.getString(1));
            eventCardBean.setPlace(cursor.getString(2));
            eventCardBean.setPeople(cursor.getString(3));
            eventCardBean.setColor(cursor.getString(4));
            eventCardBean.setDate(cursor.getString(5));
            eventCardBean.setNotification(cursor.getInt(6) == 1?true:false);
        }
        cursor.close();
        sqLiteDatabase.close();
        return eventCardBean;
    }

    public EventDataBase insert(int number,String name ,String place,String people,String color, String date){
        sqLiteDatabase = dataBaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues(8);
        contentValues.put("number",number);
        contentValues.put("name",name);
        contentValues.put("place",place);
        contentValues.put("people",people);
        contentValues.put("color",color);
        contentValues.put("date",date);
        contentValues.put("notification",false);
        sqLiteDatabase.insert(TABLE,null,contentValues);
        sqLiteDatabase.close();
        return INSTANCE;
    }
    public EventDataBase insert(int number,String name ,String place,String people,String color,
                                String date,boolean notification){
        sqLiteDatabase = dataBaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues(8);
        contentValues.put("number",number);
        contentValues.put("name",name);
        contentValues.put("place",place);
        contentValues.put("people",people);
        contentValues.put("color",color);
        contentValues.put("date",date);
        contentValues.put("notification",notification);
        sqLiteDatabase.insert(TABLE,null,contentValues);
        sqLiteDatabase.close();
        return INSTANCE;
    }

    public EventDataBase deleteByNumber(int number){
        sqLiteDatabase = dataBaseHelper.getWritableDatabase();
        sqLiteDatabase.delete(TABLE,"number=?",new String[]{String.valueOf(number)});
        sqLiteDatabase.close();
        return INSTANCE;
    }

    public EventDataBase updateNotificationByNumber(int number,boolean notification){
        sqLiteDatabase = dataBaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues(1);
        contentValues.put("notification",notification);
        sqLiteDatabase.update(TABLE,contentValues,"number=?",new String[]{String.valueOf(number)});
        sqLiteDatabase.close();
        return INSTANCE;
    }

    public boolean getNotificationStateByNumber(int number){
        sqLiteDatabase = dataBaseHelper.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.query(TABLE,null,"number=?",new String[]{String.valueOf(number)},null,null,null);
        cursor.moveToFirst();
        boolean state = cursor.getInt(6) == 1?true:false;
        cursor.close();
        sqLiteDatabase.close();
        return state;
    }

    public static class AppWidgetAgent{
        private AppWidgetAgent(){}

        private static volatile AppWidgetAgent INSTANCE;
        private static volatile SharedPreferences sharedPreferences;
        private SharedPreferences.Editor editor;

        public static AppWidgetAgent getAgent(Context context){
            if(INSTANCE == null){
                synchronized (AppWidgetAgent.class){
                    if(INSTANCE == null){
                        INSTANCE = new AppWidgetAgent();
                        sharedPreferences = context.getSharedPreferences("app_widget_agent",Context.MODE_PRIVATE);
                    }
                }
            }
            return INSTANCE;
        }


        public int getNumberByAppWidgetId(int id){
            return sharedPreferences.getInt("appWidgetId"+id,-1);
        }

        public int getAppWidgetIdByNumber(int number){
            return sharedPreferences.getInt("number"+number,-1);
        }

        public void updateNumberAndAppWidgetId(int id ,int number){
            editor = sharedPreferences.edit();
            editor.putInt("appWidgetId"+id,number);
            editor.putInt("number"+number,id);
            editor.commit();
        }

        public void updateAppWidgetColorById(int id ,boolean isBlack){
            editor = sharedPreferences.edit();
            editor.putBoolean("isBlack"+id,isBlack);
            editor.commit();
        }

        public boolean getAppWidgetColorById(int id){
            return sharedPreferences.getBoolean("isBlack"+id,true);
        }

        public void deleteAppWidgetIdAndNumber(int id){
            editor = sharedPreferences.edit();
            int number = sharedPreferences.getInt("appWidgetId"+id,-1);
            if(number != -1)
                editor.remove("number"+number);
            editor.remove("appWidgetId"+id);
            editor.remove("isBlack"+id);
            editor.commit();
        }
    }

    private static class DataBaseHelper extends SQLiteOpenHelper{

        public DataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        public DataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
            super(context, name, factory, version, errorHandler);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table "+TABLE+"(number int unsigned not null primary key," +
                    "name text not null," +
                    "place text," +
                    "people text," +
                    "color char(8)," +
                    "date char(10)," +
                    "notification boolean);" );
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}

