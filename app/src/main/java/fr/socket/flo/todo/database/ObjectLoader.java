package fr.socket.flo.todo.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

/**
 * @author Florian Arnould
 * @version 1.0
 */
class ObjectLoader<E> extends AsyncTask<Void, Void, Void> {
	private final SQLiteDatabase _db;
	private final ObjectLoaderParameter<E> _parameter;
	private final OnObjectLoadedListener<E> _listener;
	private E _object;

	ObjectLoader(SQLiteDatabase db, ObjectLoaderParameter<E> parameter, OnObjectLoadedListener<E> listener) {
		_db = db;
		_parameter = parameter;
		_listener = listener;
	}

	@Override
	protected Void doInBackground(Void... params) {
		Cursor cursor = _db.query(_parameter.getTable(), _parameter.getColumns(), _parameter.getSelection(), _parameter.getSelectionArgs(), null, null, null);
		if (cursor.moveToNext()) {
			_object = _parameter.createInstance(cursor);
		}
		cursor.close();
		return null;
	}

	@Override
	protected void onPostExecute(Void param) {
		_listener.onObjectLoaded(_object);
	}
}
