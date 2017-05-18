package fr.socket.flo.todo.database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.annotation.Nullable;

/**
 * @author Florian Arnould
 * @version 1.0
 */

// TODO: 14/05/17 create abstraction on saver and updater
class Saver extends AsyncTask<Void, Void, Void> {
	private final SQLiteDatabase _db;
	private final String _tableName;
	private final ContentValues _values;
	private final OnDataChangedListener _listener;

	Saver(SQLiteDatabase db, String tableName, ContentValues values, @Nullable OnDataChangedListener listener) {
		_db = db;
		_tableName = tableName;
		_values = values;
		_listener = listener;
	}

	@Override
	protected Void doInBackground(Void... params) {
		_db.insert(_tableName, null, _values);
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		if (_listener != null) {
			_listener.onDataChanged();
		}
	}
}
