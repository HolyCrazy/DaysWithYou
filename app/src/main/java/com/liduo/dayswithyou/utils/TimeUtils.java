package com.liduo.dayswithyou.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by LeeDuo on 2020/3/1.
 */

public class TimeUtils {

    private static int year;
    private static int month;
    private static int day;
    private static boolean hasCalculated = false;

    public static long dateToStamp(String time){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = simpleDateFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }
    public static String stampToDate(long timeMillis){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(timeMillis);
        return simpleDateFormat.format(date);
    }

    public static long  getSystemStamp(){
        long timeStamp = System.currentTimeMillis();
        return timeStamp;
    }
    private static void calculate(){
        String date = stampToDate(getSystemStamp());
        date = date.split(" ")[0];
        String[] dates = date.split("-");
        year = Integer.valueOf(dates[0]);
        month = Integer.valueOf(dates[1]);
        day = Integer.valueOf(dates[2]);
        hasCalculated = true;
    }

    public static boolean isPastDay(String date){
        String dates[] = date.split("-");
        int year = Integer.valueOf(dates[0]);
        int month = Integer.valueOf(dates[1]);
        int day = Integer.valueOf(dates[2]);
        if(year<TimeUtils.getYear()) return true;
        if(year == TimeUtils.getYear() && month<TimeUtils.getMonth()) return true;
        if(year == TimeUtils.getYear() && month == TimeUtils.getMonth() && day<=TimeUtils.getDay()) return true;
        return false;
    }


    public static int getYear(){
        if(!hasCalculated)
            calculate();
        return year;
    }
    public static int getMonth(){
        if(!hasCalculated)
            calculate();
        return month;
    }
    public static int getDay(){
        if(!hasCalculated)
            calculate();
        return day;
    }
    public static int getDaysOfMonth(int year ,int month){
        switch(month){
            case 1:
                return 31;
            case 2:
                return isLeapYear(year)?29:28;
            case 3:
                return 31;
            case 4:
                return 30;
            case 5:
                return 31;
            case 6:
                return 30;
            case 7:
                return 31;
            case 8:
                return 31;
            case 9:
                return 30;
            case 10:
                return 31;
            case 11:
                return 30;
            case 12:
                return 31;
        }
        return 0;
    }
    public static int getDaysOfYear(int year){
        return isLeapYear(year)?366:365;
    }
    public  static boolean isLeapYear(int year){
        if(year%4 != 0)
            return false;
        else if(year %100 == 0&&year % 400 != 0)
            return false;
        else
            return true;
    }



}
