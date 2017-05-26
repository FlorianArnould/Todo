package fr.socket.flo.todo.view.fragments.filters;

import java.util.List;

import fr.socket.flo.todo.model.Nameable;

/**
 * @author Florian Arnould
 * @version 1.0
 */
@FunctionalInterface
public interface OnNameableResultsPublishedListener {
	void onNameableResultsPublished(List<? extends Nameable> objects);
}
