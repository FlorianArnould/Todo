package com.socket.florian.todo.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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
    private SQLiteOpenHelper _sqlManager;

    private DataManager(){
        _sqlManager = null;
    }

    public void  open(Context context){
         _sqlManager= new MySQLiteOpenHelper(context);
        _database = _sqlManager.getWritableDatabase();
    }

    public void createNewProject(Project project){
        ContentValues values = new ContentValues();
        values.put("name", project.getName());
        values.put("start_date", project.getStartDate().toString());
        _database.insertOrThrow("projects", null, values);
    }

    public void close(){
        _database.close();
    }

    public void removeProject(Project project){
        _database.delete("projects", "id = " + String.valueOf(project.getId()), null);
    }

    public List<Project> getProjects(){
        Cursor cursor = _database.query("projects", null, null, null, null, null, null);
        List<Project> projects = new ArrayList<>();
        while(cursor.moveToNext()){
            projects.add(cursorToProject(cursor));
        }
        cursor.close();
        return projects;
    }

    public Project getProject(int id){
        Cursor cursor = _database.query("projects", null, "id = " + id, null, null, null, null);
        cursor.moveToFirst();
        Project project =  cursorToProject(cursor);
        cursor.close();
        return project;
    }

    private Project cursorToProject(Cursor cursor){
        Date date  = new Date(cursor.getString(2));
        Cursor taskCursor = _database.query("tasks", null, "idProject = " + cursor.getInt(0), null, null, null, null);
        List<Task> tasks= new ArrayList<>();
        while(taskCursor.moveToNext()){
            tasks.add(cursorToTask(taskCursor));
        }
        taskCursor.close();
        return new Project(cursor.getInt(0), cursor.getString(1), date, tasks);
    }

    private Task cursorToTask(Cursor cursor){
        return new Task(cursor.getInt(0), cursor.getString(2));
    }

    public void createNewTask(Project project, Task task){
        ContentValues values = new ContentValues();
        values.put("name", task.getName());
        values.put("idProject", project.getId());
        _database.insert("tasks", null, values);
    }

    public void removeTask(Task task){
        _database.delete("tasks", "id = " + task.getId(), null);
    }
}
