package fr.socket.flo.todo.database;

import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.annotation.Nullable;

/**
 * @author Florian Arnould
 */
class Remover extends AsyncTask<Void, Void, Void> {
	private final SQLiteDatabase _db;
	private final String _tableName;
	private final int _id;
	private final OnDataChangedListener _listener;

	Remover(SQLiteDatabase db, String tableName, int id, @Nullable OnDataChangedListener listener) {
		_db = db;
		_tableName = tableName;
		_id = id;
		_listener = listener;
	}

	@Override
	protected Void doInBackground(Void... params) {
		_db.delete(_tableName, "id=?", new String[]{String.valueOf(_id)});
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		if (_listener != null) {
			_listener.onDataChanged();
		}
	}
}
