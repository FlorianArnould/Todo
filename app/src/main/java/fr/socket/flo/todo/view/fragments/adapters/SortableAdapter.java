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
	private Sorter.SortingWay _sortingWay;

	SortableAdapter(Sorter.SortingWay sortingWay) {
		_sortingWay = sortingWay;
	}

	public void changeSortingWay(Sorter.SortingWay sortingWay) {
		_sortingWay = sortingWay;
		notifyDataSetChanged();
	}

	protected void sort(List<? extends Sortable> sortables) {
		Sorter.sortByWay(_sortingWay, sortables);
	}
}
