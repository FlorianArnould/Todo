package com.socket.florian.todo.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class DataManager {
    private static DataManager ourInstance = new DataManager();

    public static DataManager getInstance() {
        return ourInstance;
    }

    private SQLiteDatabase _database;
    SQLiteOpenHelper _sqlManager;

    private DataManager(){}

    public void  open(Context context){
         _sqlManager= new MySQLiteOpenHelper(context);
        _database = _sqlManager.getWritableDatabase();
    }

    public void createNewProject(String name, Date start_date){
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("start_date", start_date.toString('-'));

        _database.insertOrThrow("projects", null, values);
    }

    public void close(){
        _database.close();
    }

    public void removeProject(Project i){
        _database.delete("projects", "id = " + String.valueOf(i.getId()), null);
    }

    public List<Project> getProjects(){
        Cursor cursor = _database.query("projects", null, null, null, null, null, null);
        List<Project> projects = new ArrayList<>();
        while(cursor.moveToNext()){
            Date date  = new Date(cursor.getString(2), '-');
            projects.add(new Project(cursor.getInt(0), cursor.getString(1), date));
        }
        cursor.close();
        return projects;
    }
}
