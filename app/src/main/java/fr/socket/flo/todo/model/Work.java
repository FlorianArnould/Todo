package fr.socket.flo.todo.model;

import android.support.annotation.ColorInt;

/**
 * @author Florian Arnould
 * @version 1.0
 */
abstract class Work implements Nameable {
	protected final static int NONE = -1;
	private final int _id;
	private final String _name;
	private final int _color;

	Work(int id, String name, @ColorInt int color) {
		_id = id;
		_name = name;
		_color = color;
	}

	public int getId() {
		return _id;
	}

	@Override
	public String getName() {
		return _name;
	}

	public
	@ColorInt
	int getColor() {
		return _color;
	}
}
