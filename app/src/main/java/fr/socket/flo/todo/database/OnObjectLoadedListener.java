package fr.socket.flo.todo.database;

/**
 * @author Florian Arnould
 * @version 1.0
 */
@FunctionalInterface
public interface OnObjectLoadedListener<T> {
	void onObjectLoaded(T object);
}
