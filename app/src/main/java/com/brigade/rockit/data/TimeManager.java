package com.brigade.rockit.data;


import android.content.Context;

import com.brigade.rockit.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class TimeManager {


    public String formatDate(String date, Context context) {
        GregorianCalendar calendar = new GregorianCalendar();
        int today = calendar.get(Calendar.DAY_OF_MONTH);
        int curMonth = calendar.get(Calendar.MONTH);
        int curYear = calendar.get(Calendar.YEAR);
        int day = getDay(date);
        int month = getMonth(date);
        int year = getYear(date);
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

    public int getDay(String date) {
        return Integer.parseInt(date.substring(0, date.indexOf(".")));
    }

    public int getMonth(String date) {
        return Integer.parseInt(date.substring(date.indexOf(".") + 1, date.indexOf(".",
                date.indexOf(".") + 1)));
    }

    public int getYear(String date) {
        return Integer.parseInt(date.substring(date.indexOf(".",
                date.indexOf(".") + 1) + 1, date.indexOf("/")));
    }

    public long getDayDiff(String date1, String date2) {
        date1 = date1.substring(0, date1.indexOf("/"));
        date2 = date2.substring(0, date2.indexOf("/"));
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        try {
            Date formattedDate1 = dateFormat.parse(date1);
            Date formattedDate2 = dateFormat.parse(date2);
            long milliseconds = formattedDate1.getTime() - formattedDate2.getTime();
            return milliseconds / (1000 * 60 * 60 * 24);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public String albumDurationFormat(String duration, Context context) {
        int minutes = Integer.valueOf(duration.substring(0, duration.indexOf(":")));
        int seconds = Integer.valueOf(duration.substring(duration.indexOf(":") + 1));
        if (minutes > 60)
            return minutes / 60 + " " + context.getString(R.string.hours) + " " + minutes % 60 + " "
                    + context.getString(R.string.minutes);
        return minutes + " " + context.getString(R.string.minutes) + " " + seconds + " " +
                context.getString(R.string.seconds);
    }
}
