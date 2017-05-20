package fr.socket.flo.todo.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.CallSuper;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Florian Arnould
 * @version 1.0
 */
public abstract class Savable {
	Savable() {
	}

	Savable(Cursor cursor) {
		fromCursor(cursor);
	}

	static Collection<String> getColumns() {
		return new ArrayList<>();
	}

	@CallSuper
	protected int fromCursor(Cursor cursor) {
		return 0;
	}

	@CallSuper
	public ContentValues toContentValues() {
		return new ContentValues();
	}

	public abstract int getId();

	public abstract String getTable();
}
