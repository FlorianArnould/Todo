package fr.socket.flo.todo.database;

import android.database.Cursor;

/**
 * @author Florian Arnould
 * @version 1.0
 */
abstract class ObjectLoaderParameter<E> {
	private final String _table;
	private final String[] _columns;
	private final String _selection;
	private final String[] _selectionArgs;

	ObjectLoaderParameter(String table, String[] columns, String selection, String[] selectionArgs) {
		_table = table;
		_columns = columns;
		_selection = selection;
		_selectionArgs = selectionArgs;
	}

	abstract E createInstance(Cursor cursor);

	String getTable() {
		return _table;
	}

	String[] getColumns() {
		return _columns;
	}

	String getSelection() {
		return _selection;
	}

	String[] getSelectionArgs() {
		return _selectionArgs;
	}
}
