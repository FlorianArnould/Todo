package fr.socket.flo.todo.database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;

/**
 * @author Florian Arnould
 * @version 1.0
 */
class Inserter extends  Saver{
	private final SQLiteDatabase _db;
	private final String _tableName;
	private final ContentValues _values;

	Inserter(SQLiteDatabase db, String tableName, ContentValues values, @Nullable OnDataChangedListener listener){
		super(listener);
		_db = db;
		_tableName = tableName;
		_values = values;
	}

	@Override
	protected Void doInBackground(Void... params) {
		_db.insert(_tableName, null, _values);
		return null;
	}
}
