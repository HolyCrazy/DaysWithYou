package com.liduo.dayswithyou.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

/**
 * Created by LeeDuo on 2020/3/4.
 */

public class StorageBox {
    private StorageBox(){}
    private static volatile StorageBox INSTANCE;
    private static volatile SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public static StorageBox getBox(Context context){
        if(INSTANCE == null){
            synchronized (StorageBox.class){
                if(INSTANCE == null){
                    INSTANCE = new StorageBox();
                    sharedPreferences = context.getSharedPreferences("fragment",Context.MODE_PRIVATE);
                }
            }
        }
        return INSTANCE;
    }

    public StorageBox put(String key ,String value){
        editor = sharedPreferences.edit();
        editor.putString(key,value);
        editor.commit();
        return INSTANCE;
    }
    public StorageBox put(String key ,int value){
        editor = sharedPreferences.edit();
        editor.putInt(key,value);
        editor.commit();
        return INSTANCE;
    }
    public StorageBox put(String key,boolean value){
        editor = sharedPreferences.edit();
        editor.putBoolean(key,value);
        editor.commit();
        return INSTANCE;
    }
    public String get(String key,@Nullable String defaultValue){
        return sharedPreferences.getString(key,defaultValue == null ?"":defaultValue);
    }
    public int get(String key ,int defaultValue){
        return sharedPreferences.getInt(key,defaultValue);
    }
    public boolean get(String key,boolean defaultValue){
        return sharedPreferences.getBoolean(key,defaultValue);
    }
}
