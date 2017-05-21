package fr.socket.flo.todo.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * @author Florian Arnould
 * @version 1.0
 */
class ProjectsSQLiteOpenHelper extends SQLiteOpenHelper {
	private static final int VERSION = 7;
	private static final String DATABASE_NAME = "ProjectsDatabase.db";

	ProjectsSQLiteOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE projects(" +
				"id INTEGER PRIMARY KEY AUTOINCREMENT," +
				"name VARCHAR(20) NOT NULL," +
				"color INTEGER NOT NULL," +
				"is_favorite BOOLEAN NOT NULL," +
				"priority INTEGER NOT NULL," +
				"deadline DATETIME);");
		db.execSQL("CREATE TABLE tasks(" +
				"id INTEGER PRIMARY KEY AUTOINCREMENT," +
				"project_id INTEGER NOT NULL REFERENCES projects(id)," +
				"name VARCHAR(20) NOT NULL," +
				"color INTEGER NOT NULL," +
				"state INTEGER NOT NULL," +
				"priority INTEGER NOT NULL," +
				"deadline DATETIME);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion < newVersion) {
			Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
			while (c.moveToNext()) {
				if (!"sqlite_sequence".equals(c.getString(0))) {
					db.execSQL("DROP TABLE IF EXISTS " + c.getString(0));
					Log.d("DROP TABLE", c.getString(0));
				}
			}
			c.close();
			onCreate(db);
		}
	}
}
