package fr.socket.flo.todo.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.Log;

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
	private int _color;
	private Date _deadline;
	private int _priority;
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-mm-yyyy HH:mm:ss", Locale.FRENCH);

	Work(Cursor cursor){
		super(cursor);
	}

	Work(int id, String name, @ColorInt int color, @Nullable Date deadline, int priority) {
		_id = id;
		_name = name;
		_color = color;
		_deadline = deadline;
		_priority = priority;
	}

	@Override
	public int getId() {
		return _id;
	}

	@Override
	public String getName() {
		return _name;
	}

	@ColorInt
	public int getColor() {
		return _color;
	}

	public int getPriority() {
		return _priority;
	}

	public boolean hasDeadline() {
		return _deadline != null;
	}

	public Date getDeadline() {
		return _deadline;
	}

	public CharSequence getDeadlineAsString() {
		if (_deadline == null) {
			return "";
		} else {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-mm-yyyy HH:mm", Locale.FRENCH);
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
		_id = cursor.getInt(index++);
		_name = cursor.getString(index++);
		_color = cursor.getInt(index++);
		_deadline = stringToDate(cursor.getString(index++));
		_priority = cursor.getInt(index++);
		return index;
	}

	protected static Collection<String> getColumns(){
		Collection<String> columns = Savable.getColumns();
		columns.add("id");
		columns.add("name");
		columns.add("color");
		columns.add("deadline");
		columns.add("priority");
		return columns;
	}

	@Override
	public ContentValues toContentValues() {
		ContentValues values = super.toContentValues();
		values.put("name", _name);
		values.put("color", _color);
		if (_deadline != null) {
			values.put("deadline", DATE_FORMAT.format(_deadline));
		} else {
			values.putNull("deadline");
		}
		values.put("priority", _priority);
		return values;
	}

	private Date stringToDate(String string) {
		Date date;
		try {
			date = DATE_FORMAT.parse(string);
		} catch (Exception e) {
			Log.d("Parse sql string", "Date wasn't parse");
			date = null;
		}
		return date;
	}
}
