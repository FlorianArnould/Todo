package fr.socket.flo.todo.view.activity;

import fr.socket.flo.todo.model.Sorter;

/**
 * @author Florian Arnould
 * @version 1.0
 */
@FunctionalInterface
public interface OnSortChangedListener {
	void onSortChangedListener(Sorter.Sort sort);
}
