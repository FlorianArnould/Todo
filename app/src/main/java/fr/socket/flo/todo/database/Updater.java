package fr.socket.flo.todo.database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.annotation.Nullable;

/**
 * @author Florian Arnould
 * @version 1.0
 */
class Updater extends AsyncTask<Void, Void, Void> {
	private final SQLiteDatabase _db;
	private final int _id;
	private final String _tableName;
	private final ContentValues _values;
	private final OnDataChangedListener _listener;

	Updater(SQLiteDatabase db, String tableName, int id, ContentValues values, @Nullable OnDataChangedListener listener) {
		_db = db;
		_tableName = tableName;
		_id = id;
		_values = values;
		_listener = listener;
	}

	@Override
	protected Void doInBackground(Void... params) {
		_db.update(_tableName, _values, "id=?", new String[]{String.valueOf(_id)});
		return null;
	}

	@Override
	protected void onPostExecute(Void result){
		if(_listener != null){
			_listener.onDataChanged();
		}
	}
}
