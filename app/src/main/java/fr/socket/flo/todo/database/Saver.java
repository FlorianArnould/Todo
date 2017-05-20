package fr.socket.flo.todo.database;

import android.os.AsyncTask;
import android.support.annotation.Nullable;

/**
 * @author Florian Arnould
 * @version 1.0
 */

abstract class Saver extends AsyncTask<Void, Void, Void> {
	private final OnDataChangedListener _listener;

	Saver(@Nullable OnDataChangedListener listener) {
		_listener = listener;
	}

	@Override
	protected void onPostExecute(Void result) {
		if (_listener != null) {
			_listener.onDataChanged();
		}
	}
}
