package fr.socket.flo.todo.storage.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author Florian Arnould
 * @version 1.0
 */
class ProjectsSQLiteOpenHelper extends SQLiteOpenHelper {
	private static final int VERSION = 1;
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
				"is_favorite BOOLEAN NOT NULL);");
		db.execSQL("CREATE TABLE tasks(" +
				"id INTEGER PRIMARY KEY AUTOINCREMENT," +
				"name VARCHAR(20) NOT NULL," +
				"color INTEGER NOT NULL);");
		db.execSQL("CREATE TABLE link(" +
				"project_id INTEGER REFERENCES projects (id)," +
				"task_id INTEGER REFERENCES tasks (id)," +
				"PRIMARY KEY (project_id, task_id));");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if(oldVersion < newVersion) {
			Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
			while (c.moveToNext()) {
				db.execSQL("DROP TABLE IF EXISTS " + c.getString(0));
			}
			c.close();
			onCreate(db);
		}
	}
}
