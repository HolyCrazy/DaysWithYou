package com.liduo.dayswithyou.core;


import com.liduo.dayswithyou.utils.TimeUtils;

/**
 * Created by LeeDuo on 2020/3/1.
 */

public class DateCalculator {
    private int year;
    private int month;
    private int day;
    private static final String TAG = "DateCalculator";
    private static final int PAST = 0;
    private static final int TODAY = 1;
    private static final int FUTURE = 2;

    public DateCalculator(){}
    public DateCalculator(int year,int month,int day){
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public DateCalculator(String formatDate){
        String date = formatDate.split(" ")[0];
        String[] dateElements = date.split("-");
        year = Integer.parseInt(dateElements[0]);
        month = Integer.parseInt(dateElements[1]);
        day = Integer.parseInt(dateElements[2]);
    }

    private int timeQuantum(){
        int nowYear = TimeUtils.getYear();
        int nowMonth = TimeUtils.getMonth();
        int nowDay = TimeUtils.getDay();
        if(nowYear<year)
            return FUTURE;
        else if(nowYear>year)
            return PAST;
        else{
            if(nowMonth<month)
                return FUTURE;
            else if(nowMonth>month)
                return PAST;
            else{
                if(nowDay<day)
                    return FUTURE;
                else if(nowDay>day)
                    return PAST;
                else
                    return TODAY;
            }
        }
    }

    public void setDate(int year,int month,int day){
        this.year = year;
        this.month = month;
        this.day = day;
    }
    public void setDate(String formatDate){
        String date = formatDate.split(" ")[0];
        String[] dateElements = date.split("-");
        year = Integer.parseInt(dateElements[0]);
        month = Integer.parseInt(dateElements[1]);
        day = Integer.parseInt(dateElements[2]);
    }

    public Integer calculateDays(){
        if(checkYear(year)&&checkMonth(month)&&checkDay(year,month,day)){
            int nowYear = TimeUtils.getYear();
            int nowMonth = TimeUtils.getMonth();
            int nowDay = TimeUtils.getDay();
            if(timeQuantum() == TODAY) return 0;
            if(year == nowYear && month == nowMonth) return day-nowDay;
            if(year == nowYear) return currentYearLeftDay(nowYear,nowMonth,nowDay)-currentYearLeftDay(year,month,day);
            if(timeQuantum() == FUTURE){
                int totalDays = currentYearLeftDay(nowYear,nowMonth,nowDay);
                for(int i = nowYear+1;i<=year;i++)
                    totalDays+=TimeUtils.getDaysOfYear(i);
                totalDays-=currentYearLeftDay(year,month,day);
                return totalDays;
            }
            if(timeQuantum() == PAST){
                int totalDays = currentYearLeftDay(year,month,day);
                for(int i= year+1;i<=nowYear;i++)
                    totalDays+=TimeUtils.getDaysOfYear(i);
                totalDays-=currentYearLeftDay(nowYear,nowMonth,nowDay);
                return -totalDays;
            }

        }
        return null;
    }

    private int currentYearLeftDay(int year ,int month ,int day){
        int totalDays = currentMonthLeftDay(year, month, day);
        for(int i = month+1;i<=12;i++){
            totalDays+=TimeUtils.getDaysOfMonth(year,i);
        }
        return totalDays;
    }
    private int currentYearPastDay(int year,int month,int day){
        return TimeUtils.getDaysOfYear(year)-currentYearLeftDay(year, month, day);
    }
    private int currentMonthLeftDay(int year,int month ,int day){
        return TimeUtils.getDaysOfMonth(year,month) -day;
    }
    private int currentMonthPastDay(int year,int month,int day){
        return TimeUtils.getDaysOfMonth(year, month)-currentMonthLeftDay(year, month, day);
    }


    private boolean checkYear(int year){
        if(year>0) return true;
        return false;
    }
    private boolean checkMonth(int month){
        if(month>=1 && month<=12) return true;
        return false;
    }
    private boolean checkDay(int year,int month,int day){
        if(day>=1 && day<=TimeUtils.getDaysOfMonth(year,month))
            return true;
        return false;
    }

}
