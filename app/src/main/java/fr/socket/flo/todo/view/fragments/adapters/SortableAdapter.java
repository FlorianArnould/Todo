package fr.socket.flo.todo.view.fragments.adapters;

import android.widget.BaseAdapter;

import java.util.List;

import fr.socket.flo.todo.model.Sortable;
import fr.socket.flo.todo.model.Sorter;

/**
 * @author Florian Arnould
 * @version 1.0
 */
public abstract class SortableAdapter extends BaseAdapter {
	private Sorter.Sort _sort;

	SortableAdapter(Sorter.Sort sort) {
		_sort = sort;
	}

	public void changeSortingWay(Sorter.Sort sort) {
		_sort = sort;
		notifyDataSetChanged();
	}

	protected void sort(List<? extends Sortable> sortableList) {
		Sorter.sortByWay(_sort, sortableList);
	}
}
