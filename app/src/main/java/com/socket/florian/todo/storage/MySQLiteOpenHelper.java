package com.socket.florian.todo.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by florian on 22/11/16.
 */

final class MySQLiteOpenHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "ProjectsDatabase.db";

    MySQLiteOpenHelper(Context context){
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE projects ( id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                                        "name TEXT NOT NULL," +
                                                        "start_date DATE NOT NULL);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
