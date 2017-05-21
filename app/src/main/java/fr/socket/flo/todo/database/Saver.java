package fr.socket.flo.todo.database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.annotation.Nullable;

/**
 * @author Florian Arnould
 * @version 1.0
 */
class Saver extends AsyncTask<Void, Void, Integer> {
	private final SQLiteDatabase _db;
	private final String _tableName;
	private final ContentValues _values;
	private final OnNewObjectCreatedListener _listener;

	Saver(SQLiteDatabase db, String tableName, ContentValues values, @Nullable OnNewObjectCreatedListener listener) {
		_db = db;
		_tableName = tableName;
		_values = values;
		_listener = listener;
	}

	@Override
	protected Integer doInBackground(Void... params) {
		return (int)_db.insert(_tableName, null, _values);
	}

	@Override
	protected void onPostExecute(Integer result) {
		if (_listener != null) {
			_listener.onNewObjectCreated(result);
		}
	}
}
