package com.liduo.dayswithyou;

import android.content.Context;

import com.liduo.dayswithyou.menu.ColorSelector;
import com.liduo.dayswithyou.storage.EventDataBase;
import com.liduo.dayswithyou.storage.StorageBox;

/**
 * Created by LeeDuo on 2020/3/5.
 */

public class AppDefaultEvent {
    private AppDefaultEvent(){}
    private static volatile AppDefaultEvent INSTANCE;
    private static  Context context;

    public static AppDefaultEvent getINSTANCE(Context context){
        if(INSTANCE == null)
            synchronized (AppDefaultEvent.class){
                if(INSTANCE == null)
                    INSTANCE = new AppDefaultEvent();
            }
        AppDefaultEvent.context = context;
        return INSTANCE;
    }

    public void setDefault(){
        boolean isFirstTime = StorageBox.getBox(context).get("default1",true);
        if(isFirstTime){
            StorageBox.getBox(context).put("default1",false);
            EventDataBase.getDataBase(context).insert(1,"一生一世在一起","大连","李铎和韩晓玲", ColorSelector.COLORS[0],"2019-05-03");
            StorageBox.getBox(context).put("position",1);
        }
    }
}
