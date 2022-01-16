package com.brigade.rockit.database;


import android.content.Context;

import com.brigade.rockit.R;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class TimeManager {


    public String formatDate(String date, Context context) {
        GregorianCalendar calendar = new GregorianCalendar();
        int today = calendar.get(Calendar.DAY_OF_MONTH);
        int curMonth = calendar.get(Calendar.MONTH);
        int curYear = calendar.get(Calendar.YEAR);
        int day = Integer.parseInt(date.substring(0, date.indexOf(".")));
        int month = Integer.parseInt(date.substring(date.indexOf(".") + 1, date.indexOf(".",
                date.indexOf(".") + 1)));
        int year = Integer.parseInt(date.substring(date.indexOf(".",
                date.indexOf(".") + 1) + 1, date.indexOf("/")));
        String time = date.substring(date.indexOf("/") + 1);
        String formattedDate = "";
        String[] months = context.getResources().getStringArray(R.array.months);
        if (today == day)
            formattedDate = context.getString(R.string.today);
        else if (today == day - 1)
            formattedDate = context.getString(R.string.yesterday);
        else {
            formattedDate += day + " " + months[month];
            if (year != curYear) {
                formattedDate += " " + year;
            }
        }
        formattedDate += " " + context.getString(R.string.at) + " " + time;
        return formattedDate;
    }

    public String getDate() {
        GregorianCalendar calendar = new GregorianCalendar();
        String minute = String.valueOf(calendar.get(Calendar.MINUTE));
        if (calendar.get(Calendar.MINUTE) < 10)
            minute = "0" + minute;
        String date = calendar.get(Calendar.DAY_OF_MONTH) + "." + calendar.get(Calendar.MONTH) + "." +
                calendar.get(Calendar.YEAR) + "/" + calendar.get(Calendar.HOUR_OF_DAY) + ":" +
                minute;
        return date;
    }

    public String formatDuration(int millis) {
        int seconds = millis / 1000;
        String duration = seconds / 60 + ":" + seconds % 60;
        return duration;
    }

    public int getMillis(String duration) {
        int seconds = Integer.parseInt(duration.substring(0, duration.indexOf(":")))*60 +
                Integer.parseInt(duration.substring(duration.indexOf(":")+1));
        return seconds*1000;

    }
}
