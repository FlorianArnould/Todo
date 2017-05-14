package fr.socket.flo.todo.database;

import java.util.List;

/**
 * @author Florian Arnould
 * @version 1.0
 */
public interface OnMultipleObjectsLoadedListener<T> {
	void OnObjectsLoaded(List<T> objects);
}
