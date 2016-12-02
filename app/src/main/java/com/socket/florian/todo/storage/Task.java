package com.socket.florian.todo.storage;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.ArraySet;

import java.util.Set;


public class Task{

    private Duration _duration;
    private int _id;
    private String _name;
    private String _comments;
    private Set<Task> _predecessors;
    private boolean _isFinished;

    Task(int id, String name){
        _id = id;
        _name = name;
        _predecessors = new ArraySet<>();
    }

    public static Task create(String name){
        return new Task(0, name);
    }

    public String getName(){
        return _name;
    }

    public int getId(){
        return _id;
    }

    public Duration getDuration(){
        return _duration;
    }

    public void setComments(String comments){
        _comments = comments;
    }

    public String getComments(){
        return _comments;
    }

    public boolean isFinished(){
        return _isFinished;
    }

    public boolean couldStart(){
        if(_predecessors.size() == 0){
            return true;
        }
        for(Task i : _predecessors){
            if(!i._isFinished){
                return false;
            }
        }
        return true;
    }
}
