package fr.socket.flo.todo.model;

import android.content.ContentValues;
import android.database.Cursor;
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
	private SimpleDateFormat _dateFormat;
	private int _id;
	private String _name;
	private Date _deadline;
	private int _priority;

	Work(Cursor cursor) {
		super(cursor);
	}

	Work(int id, String name, @Nullable Date deadline, int priority) {
		_id = id;
		_name = name;
		_deadline = deadline;
		_priority = priority;
		_dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.FRENCH);
	}

	protected static Collection<String> getColumns() {
		Collection<String> columns = Savable.getColumns();
		columns.add("id");
		columns.add("name");
		columns.add("deadline");
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

	public int getPriority() {
		return _priority;
	}

	public void setPriority(int priority) {
		_priority = priority;
	}

	public boolean hasDeadline() {
		return _deadline != null;
	}

	public Date getDeadline() {
		return _deadline;
	}

	public void setDeadline(Date dateTime) {
		_deadline = dateTime;
	}

	public CharSequence getDeadlineAsString() {
		if (_deadline == null) {
			return "";
		} else {
			return getDeadlineDateAsString() + " " + getDeadlineTimeAsString();
		}
	}

	public CharSequence getDeadlineDateAsString() {
		if (_deadline == null) {
			return "";
		} else {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.FRENCH);
			return simpleDateFormat.format(_deadline);
		}
	}

	public CharSequence getDeadlineTimeAsString() {
		if (_deadline == null) {
			return "";
		} else {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.FRENCH);
			return simpleDateFormat.format(_deadline);
		}
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
		_dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.FRENCH);
		_id = cursor.getInt(index++);
		_name = cursor.getString(index++);
		_deadline = stringToDate(cursor.getString(index++));
		_priority = cursor.getInt(index++);
		return index;
	}

	@Override
	public ContentValues toContentValues() {
		ContentValues values = super.toContentValues();
		values.put("name", _name);
		if (_deadline != null) {
			values.put("deadline", _dateFormat.format(_deadline));
		} else {
			values.putNull("deadline");
		}
		values.put("priority", _priority);
		return values;
	}

	private Date stringToDate(@Nullable String string) {
		if (string != null && !string.isEmpty()) {
			try {
				return _dateFormat.parse(string);
			} catch (ParseException e) {
				Log.w("Parse sql string", "Date wasn't parse", e);
			}
		}
		return null;
	}
}
