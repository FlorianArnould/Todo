package fr.socket.flo.todo.view.mainFragments.filters;

import java.util.List;

import fr.socket.flo.todo.model.Nameable;

/**
 * @author Florian Arnould
 * @version 1.0
 */
public interface OnNameableResultsPublishedListener {
	void onNameableResultsPublished(List<? extends Nameable> _objects);
}
