package fr.socket.flo.todo.model;

/**
 * @author Florian Arnould
 * @version 1.0
 */
public interface Sortable<E> {
	int compareByName(E other);

	int compareByDeadline(E other);

	int compareByPriority(E other);
}
