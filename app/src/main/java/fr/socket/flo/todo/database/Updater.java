package fr.socket.flo.todo.database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;

/**
 * @author Florian Arnould
 * @version 1.0
 */
class Updater extends Saver {
	private final SQLiteDatabase _db;
	private final int _id;
	private final String _tableName;
	private final ContentValues _values;

	Updater(SQLiteDatabase db, String tableName, int id, ContentValues values, @Nullable OnDataChangedListener listener) {
		super(listener);
		_db = db;
		_tableName = tableName;
		_id = id;
		_values = values;
	}

	@Override
	protected Void doInBackground(Void... params) {
		_db.update(_tableName, _values, "id=?", new String[]{String.valueOf(_id)});
		return null;
	}
}
