package fr.socket.flo.todo.model;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Florian Arnould
 * @version 1.0
 */
public class Sorter {
	public enum Sort {BY_NAME, BY_DEADLINE, BY_PRIORITY}

	private Sorter() {
	}

	public static void sortByWay(Sort sort, List<? extends Sortable> list) {
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

	private static void sortByName(List<? extends Sortable> list) {
		Collections.sort(list, (Comparator<Sortable>)Sortable::compareByName);
	}

	private static void sortByDeadline(List<? extends Sortable> list) {
		Collections.sort(list, (Comparator<Sortable>)Sortable::compareByDeadline);
	}

	private static void sortByPriority(List<? extends Sortable> list) {
		Collections.sort(list, (Comparator<Sortable>)Sortable::compareByPriority);
	}
}
