package fr.socket.flo.todo.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

import fr.socket.flo.todo.database.DataManager;

/**
 * @author Florian Arnould
 * @version 1.0
 */
abstract class Work extends Savable implements Nameable, Sortable<Work> {
	public static final int NONE = -1;
	private int _id;
	private String _name;
	private DateTime _startDate;
	private DateTime _deadline;
	private int _priority;

	Work(Cursor cursor) {
		super(cursor);
	}

	Work(int id, String name, @NonNull DateTime startDate, @NonNull DateTime deadline, int priority) {
		_id = id;
		_name = name;
		_startDate = startDate;
		_deadline = deadline;
		_priority = priority;
	}

	protected static Collection<String> getColumns() {
		Collection<String> columns = Savable.getColumns();
		columns.add("id");
		columns.add("name");
		columns.add("start_date");
		columns.add("start_time");
		columns.add("deadline_date");
		columns.add("deadline_time");
		columns.add("priority");
		return columns;
	}

	@Override
	public int getId() {
		return _id;
	}

	@Override
	public String getName() {
		return _name;
	}

	public void setName(String name) {
		_name = name;
	}

	public int getPriority() {
		return _priority;
	}

	public void setPriority(int priority) {
		_priority = priority;
	}

	public boolean hasDeadline() {
		return _deadline != null;
	}

	public DateTime getDeadline() {
		return _deadline;
	}

	@Override
	public int compareByName(Work other) {
		return _name.compareToIgnoreCase(other._name);
	}

	@Override
	public int compareByDeadline(Work other) {
		if (other._deadline == null && _deadline == null) {
			return 0;
		}
		if (other._deadline == null) {
			return -1;
		}
		if (_deadline == null) {
			return 1;
		}
		return _deadline.compareTo(other._deadline);
	}

	@Override
	public int compareByPriority(Work other) {
		return _priority - other._priority;
	}

	public void save() {
		DataManager.getInstance().update(this);
	}

	@Override
	protected int fromCursor(Cursor cursor) {
		int index = super.fromCursor(cursor);
		_id = cursor.getInt(index++);
		_name = cursor.getString(index++);
		_startDate = new DateTime(cursor.getString(index++), cursor.getString(index++));
		_deadline = new DateTime(cursor.getString(index++), cursor.getString(index++));
		_priority = cursor.getInt(index++);
		return index;
	}

	@Override
	public ContentValues toContentValues() {
		ContentValues values = super.toContentValues();
		values.put("name", _name);
		_startDate.addToContentValues(values, "start_date", "start_time");
		_deadline.addToContentValues(values, "deadline_date", "deadline_time");
		values.put("priority", _priority);
		return values;
	}
}
