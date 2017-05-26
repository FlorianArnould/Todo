package fr.socket.flo.todo.database;

import java.util.List;

/**
 * @author Florian Arnould
 * @version 1.0
 */
@FunctionalInterface
public interface OnMultipleObjectsLoadedListener<T> {
	void onObjectsLoaded(List<T> objects);
}
