package fr.socket.flo.todo.model;

import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author Florian Arnould
 * @version 1.0
 */
abstract class Work implements Nameable, Sortable<Work> {
	public static final int NONE = -1;
	private final int _id;
	private final String _name;
	private final int _color;
	private final Date _deadline;
	private final int _priority;

	Work(int id, String name, @ColorInt int color, @Nullable Date deadline, int priority) {
		_id = id;
		_name = name;
		_color = color;
		_deadline = deadline;
		_priority = priority;
	}

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

	public String getDeadlineAsString() {
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
}
