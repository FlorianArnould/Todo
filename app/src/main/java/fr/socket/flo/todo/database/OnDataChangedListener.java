package fr.socket.flo.todo.database;

/**
 * @author Florian Arnould
 * @version 1.0
 */
@FunctionalInterface
public interface OnDataChangedListener {
	void onDataChanged();
}
