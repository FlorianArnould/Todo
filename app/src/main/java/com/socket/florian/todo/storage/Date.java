package com.socket.florian.todo.storage;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;



public final class Date{
    private int _year;
    private int _month;
    private int _dayOfMonth;

    public Date(int year, int month, int dayOfMonth) {
        _year = year;
        _month = month;
        _dayOfMonth = dayOfMonth;
    }
    Date(String date){
        String[] strings = date.split(Character.toString('-'));
        _dayOfMonth = Integer.valueOf(strings[0]);
        _month = Integer.valueOf(strings[1]);
        _year = Integer.valueOf(strings[2]);
    }

    public String toVerboseString(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(_year, _month-1, _dayOfMonth);
        return new SimpleDateFormat("EEEE dd MMMM yyyy", Locale.FRENCH).format(calendar.getTime());
    }

    public static Date currentDate(){
        Calendar calendar = Calendar.getInstance();
        return new Date(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DAY_OF_MONTH));
    }

    public String toString(){
        return String.valueOf(_dayOfMonth) + '-' + String.valueOf(_month) + '-' + String.valueOf(_year);
    }

    public int year(){
        return _year;
    }
    public int month(){
        return _month;
    }
    public int dayOfMonth(){
        return _dayOfMonth;
    }
    int equals(Date other){
        int dif = _year - other._year;
        if(dif != 0){
            return dif;
        } else {
            dif = _month - other._month;
            if(dif != 0) {
                return dif;
            }
        }
        return _dayOfMonth - other._dayOfMonth;
    }
}
