package fr.socket.flo.todo.model;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Florian Arnould
 * @version 1.0
 */
public class Sorter {
	public enum SortingWay {BY_NAME, BY_DEADLINE, BY_PRIORITY}

	private Sorter() {
	}

	public static void sortByWay(SortingWay way, List<? extends Sortable> list) {
		switch (way) {
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
		Collections.sort(list, new Comparator<Sortable>() {
			@Override
			public int compare(Sortable o1, Sortable o2) {
				return o1.compareByName(o2);
			}
		});
	}

	private static void sortByDeadline(List<? extends Sortable> list) {
		Collections.sort(list, new Comparator<Sortable>() {
			@Override
			public int compare(Sortable o1, Sortable o2) {
				return o1.compareByDeadline(o2);
			}
		});
	}

	private static void sortByPriority(List<? extends Sortable> list) {
		Collections.sort(list, new Comparator<Sortable>() {
			@Override
			public int compare(Sortable o1, Sortable o2) {
				return o1.compareByPriority(o2);
			}
		});
	}
}
