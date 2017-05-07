package fr.socket.flo.todo.storage;

import android.support.annotation.ColorInt;

/**
 * @author Florian Arnould
 * @version 1.0
 */
abstract class Work {
	protected static int NONE = -1;
	private int _id;
	private String _name;
	private int _color;

	Work(int id, String name, @ColorInt int color) {
		_id = id;
		_name = name;
		_color = color;
	}

	public int getId() {
		return _id;
	}

	public String getName() {
		return _name;
	}

	public
	@ColorInt
	int getColor() {
		return _color;
	}
}
