package fr.socket.flo.todo.database;

import android.database.Cursor;

/**
 * @author Florian Arnould
 * @version 1.0
 */
abstract class ObjectLoaderParameter<E> {
	private final String _query;

	ObjectLoaderParameter(String query) {
		_query = query;
	}

	String getQuery() {
		return _query;
	}

	abstract E createInstance(Cursor cursor);
}
