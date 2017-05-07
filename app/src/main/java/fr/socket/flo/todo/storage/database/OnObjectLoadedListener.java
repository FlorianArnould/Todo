package fr.socket.flo.todo.storage.database;

/**
 * @author Florian Arnould
 * @version 1.0
 */
interface OnObjectLoadedListener<T> {
	void OnObjectLoaded(T object);
}
