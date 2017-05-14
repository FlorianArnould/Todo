package fr.socket.flo.todo.database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

/**
 * @author Florian Arnould
 * @version 1.0
 */
class Saver extends AsyncTask<Void, Void, Void> {
	private final SQLiteDatabase _db;
	private final String _tableName;
	private final ContentValues _values;

	Saver(SQLiteDatabase db, String tableName, ContentValues values) {
		_db = db;
		_tableName = tableName;
		_values = values;
	}

	@Override
	protected Void doInBackground(Void... params) {
		_db.insert(_tableName, null, _values);
		return null;
	}
}
