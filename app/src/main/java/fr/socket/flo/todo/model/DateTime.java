package fr.socket.flo.todo.model;

import android.content.ContentValues;
import android.support.annotation.NonNull;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * @author Florian Arnould
 */
public class DateTime {
	private final SimpleDateFormat _dateFormat;
	private final SimpleDateFormat _timeFormat;
	private Date _date;
	private Date _time;

	DateTime() {
		_dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.FRENCH);
		_timeFormat = new SimpleDateFormat("HH:mm", Locale.FRENCH);
		_date = null;
		_time = null;
	}

	DateTime(String date, String time) {
		_dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.FRENCH);
		_timeFormat = new SimpleDateFormat("HH:mm", Locale.FRENCH);
		try {
			if (date != null) {
				_date = _dateFormat.parse(date);
				if (time != null) {
					_time = _timeFormat.parse(time);
				} else {
					_time = null;
				}
			} else {
				_date = null;
				_time = null;
			}
		} catch (ParseException e) {
			_date = null;
			_time = null;
			Log.w("Creating datetime", "Error when parsing date time", e);
		}
	}

	@Override
	public String toString() {
		String str = toDateString();
		if (_time != null) {
			str += " " + toTimeString();
		}
		return str;
	}

	public String toDateString() {
		if (_date != null) {
			return _dateFormat.format(_date);
		}
		return "";
	}

	public String toTimeString() {
		if (_time != null) {
			return _timeFormat.format(_time);
		}
		return "";
	}

	void addToContentValues(@NonNull ContentValues values, String dateKey, String timeKey) {
		if (_date == null) {
			values.putNull(dateKey);
			values.putNull(timeKey);
		} else {
			values.put(dateKey, _dateFormat.format(_date));
			if (_time == null) {
				values.putNull(timeKey);
			} else {
				values.put(timeKey, _timeFormat.format(_time));
			}
		}
	}

	public Date getTime() {
		return _time;
	}

	public void setTime(Date time) {
		_time = time;
	}

	public Date getDate() {
		return _date;
	}

	public void setDate(Date date) {
		_date = date;
	}

	int compareTo(DateTime other) {
		if (other.isNull() && isNull()) {
			return 0;
		}
		if (other.isNull() && !isNull()) {
			return -1;
		}
		if (!other.isNull() && isNull()) {
			return 1;
		}
		Calendar thisCalendar = Calendar.getInstance();
		thisCalendar.setTime(_date);
		Calendar otherCalendar = Calendar.getInstance();
		otherCalendar.setTime(other._date);
		int[] values = {Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH};
		for (int value : values) {
			int result = thisCalendar.get(value) - otherCalendar.get(value);
			if (result != 0) {
				return result;
			}
		}
		return compareOnTime(other);
	}

	private int compareOnTime(DateTime other) {
		if (other.isWholeDay() && isWholeDay()) {
			return 0;
		}
		if (other.isWholeDay() && !isWholeDay()) {
			return -1;
		}
		if (!other.isWholeDay() && isWholeDay()) {
			return 1;
		}
		Calendar thisCalendar = Calendar.getInstance();
		thisCalendar.setTime(_time);
		Calendar otherCalendar = Calendar.getInstance();
		otherCalendar.setTime(other._time);
		int[] values = {Calendar.HOUR_OF_DAY, Calendar.MINUTE};
		for (int value : values) {
			int result = thisCalendar.get(value) - otherCalendar.get(value);
			if (result != 0) {
				return result;
			}
		}
		return 0;
	}

	public boolean isWholeDay() {
		return _time == null;
	}

	public boolean isNull() {
		return _date == null;
	}

	public void setWholeDay() {
		_time = null;
	}
}
