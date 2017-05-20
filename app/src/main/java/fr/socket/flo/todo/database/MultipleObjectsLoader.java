package fr.socket.flo.todo.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Florian Arnould*
 * @version 1.0
 */
class MultipleObjectsLoader<E> extends AsyncTask<Void, Void, Void> {
	private final SQLiteDatabase _db;
	private final ObjectLoaderParameter<E> _parameter;
	private final OnMultipleObjectsLoadedListener<E> _listener;
	private final List<E> _list;

	MultipleObjectsLoader(SQLiteDatabase db, ObjectLoaderParameter<E> parameter, OnMultipleObjectsLoadedListener<E> listener) {
		_db = db;
		_parameter = parameter;
		_listener = listener;
		_list = new ArrayList<>();
	}

	@Override
	protected Void doInBackground(Void... params) {
		Cursor cursor = _db.query(_parameter.getTable(), _parameter.getColumns(), _parameter.getSelection(), _parameter.getSelectionArgs(), null, null, null);
		while (cursor.moveToNext()) {
			_list.add(_parameter.createInstance(cursor));
		}
		cursor.close();
		return null;
	}

	@Override
	protected void onPostExecute(Void param) {
		_listener.onObjectsLoaded(_list);
	}
}
