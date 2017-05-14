package fr.socket.flo.todo.database;

/**
 * @author Florian Arnould
 * @version 1.0
 */
public interface OnObjectLoadedListener<T> {
	void OnObjectLoaded(T object);
}
