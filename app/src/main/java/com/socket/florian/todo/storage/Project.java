package com.socket.florian.todo.storage;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;


public final class Project{
    private int _id;
    private String _name;
    private Date _startDate;
    private List<Task> _tasks;

    Project(int id, String name, Date startDate, List<Task> tasks){
        _id = id;
        _name = name;
        _startDate = startDate;
        _tasks = tasks;
    }

    public static Project create(String name, Date startDate){
        return new Project(0, name, startDate, null);
    }

    public String getName(){
        return _name;
    }

    public Date getStartDate(){
        return _startDate;
    }

    public int getId(){
        return _id;
    }

    public String toString(){
        return _name;
    }

    public boolean isStarting(){
        return _startDate.equals(Date.currentDate()) == 0;
    }

    public boolean isInProgress(){
        return _startDate.equals(Date.currentDate()) < 0;
    }

    public List<Task> getTasks(){
        return _tasks;
    }
}
