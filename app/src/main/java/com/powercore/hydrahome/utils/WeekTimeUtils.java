package com.powercore.hydrahome.utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class WeekTimeUtils {
    public static String  getMondayTime(String time) {
        long timeMillis=DateUtil.INSTANCE.formatDateStamp(time);
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(timeMillis);
//⼀周第⼀天是否为星期天
        boolean isFirstSunday = (now.getFirstDayOfWeek() == Calendar.SUNDAY);
//获取周⼏
        int weekDay = now.get(Calendar.DAY_OF_WEEK);
//若⼀周第⼀天为星期天，则-1
        if(isFirstSunday){
            weekDay = weekDay - 1;
            if(weekDay == 0){
                weekDay = 7;
            }
        }

//打印周⼏
        SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
        Date beginDate = new Date(timeMillis);

        Calendar date = Calendar.getInstance();
        date.setTime(beginDate);
        date.set(Calendar.DATE, date.get(Calendar.DATE) -weekDay+1);
        Date endDate = null;
        try {
            endDate = dft.parse(dft.format(date.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dft.format(endDate);
    }
    public static String  getSundayTime(String time) {
        long timeMillis=DateUtil.INSTANCE.formatDateStamp(time);
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(timeMillis);
//⼀周第⼀天是否为星期天
        boolean isFirstSunday = (now.getFirstDayOfWeek() == Calendar.SUNDAY);
//获取周⼏
        int weekDay = now.get(Calendar.DAY_OF_WEEK);
//若⼀周第⼀天为星期天，则-1
        if(isFirstSunday){
            weekDay = weekDay - 1;
            if(weekDay == 0){
                weekDay = 7;
            }
        }

//打印周⼏
        SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
        Date beginDate = new Date(timeMillis);

        Calendar date = Calendar.getInstance();
        date.setTime(beginDate);
        date.set(Calendar.DATE, date.get(Calendar.DATE) +(7-weekDay));
        Date endDate = null;
        try {
            endDate = dft.parse(dft.format(date.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dft.format(endDate);
    }
}
