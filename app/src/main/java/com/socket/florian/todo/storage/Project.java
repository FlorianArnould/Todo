package com.socket.florian.todo.storage;

import android.os.Parcel;
import android.os.Parcelable;



public final class Project implements Parcelable {
    private int _id;
    private String _name;
    private Date _startDate;

    Project(int id, String name, Date startDate){
        _id = id;
        _name = name;
        _startDate = startDate;
    }

    protected Project(Parcel in) {
        _id = in.readInt();
        _name = in.readString();
        _startDate = in.readParcelable(Date.class.getClassLoader());
    }

    public static final Creator<Project> CREATOR = new Creator<Project>() {
        @Override
        public Project createFromParcel(Parcel in) {
            return new Project(in);
        }

        @Override
        public Project[] newArray(int size) {
            return new Project[size];
        }
    };

    public String getName(){
        return _name;
    }

    public Date getStartDate(){
        return _startDate;
    }

    int getId(){
        return _id;
    }

    public String toString(){
        return _name;
    }

    public boolean isStarting(){
        return _startDate.equals(Date.currentDate()) == 0;
    }

    public boolean isInProgress(){
        return _startDate.equals(Date.currentDate()) > 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(_id);
        parcel.writeString(_name);
        parcel.writeParcelable(_startDate, 0);
    }
}
