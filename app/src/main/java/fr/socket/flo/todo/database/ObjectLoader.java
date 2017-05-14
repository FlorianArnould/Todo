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
		Cursor c = _db.rawQuery(_parameter.getQuery(), null);
		if (c.moveToNext()) {
			_object = _parameter.createInstance(c);
		}
		c.close();
		return null;
	}

	@Override
	protected void onPostExecute(Void param) {
		_listener.OnObjectLoaded(_object);
	}
}
