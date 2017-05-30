package fr.socket.flo.todo.model;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Florian Arnould
 * @version 1.0
 */
public class Sorter<E extends Sortable<? super E>> {
	public enum Sort {BY_NAME, BY_DEADLINE, BY_PRIORITY}

	public void sort(Sort sort, List<E> list) {
		switch (sort) {
			case BY_NAME:
				sortByName(list);
				break;
			case BY_DEADLINE:
				sortByDeadline(list);
				break;
			case BY_PRIORITY:
				sortByPriority(list);
				break;
		}
	}

	private void sortByName(List<E> list) {
		Collections.sort(list, new Comparator<E>() {
			@Override
			public int compare(E o1, E o2) {
				return o1.compareByName(o2);
			}
		});
	}

	private void sortByDeadline(List<E> list) {
		Collections.sort(list, new Comparator<E>() {
			@Override
			public int compare(E o1, E o2) {
				return o1.compareByDeadline(o2);
			}
		});
	}

	private void sortByPriority(List<E> list) {
		Collections.sort(list, new Comparator<E>() {
			@Override
			public int compare(E o1, E o2) {
				return o1.compareByPriority(o2);
			}
		});
	}
}
