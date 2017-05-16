package fr.socket.flo.todo.view.mainFragments.filters;

import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Filter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import fr.socket.flo.todo.model.Nameable;

/**
 * @author Florian Arnould
 * @version 1.0
 */
public class NameableFilter extends Filter {
	private final List<? extends Nameable> _objects;
	private final OnNameableResultsPublishedListener _listener;

	public NameableFilter(List<? extends Nameable> objects, @NonNull OnNameableResultsPublishedListener listener) {
		_objects = objects;
		_listener = listener;
	}

	@Override
	protected FilterResults performFiltering(CharSequence constraint) {
		FilterResults results = new FilterResults();
		if (constraint != null && constraint.length() > 0) {
			Collection<Nameable> tempList = new ArrayList<>();
			for (Nameable nameable : _objects) {
				// TODO: 13/05/17 change to use Pattern java
				if (nameable.getName().toLowerCase().contains(constraint.toString().toLowerCase())) {
					tempList.add(nameable);
				}
			}
			results.count = tempList.size();
			results.values = tempList;
		} else {
			results.count = _objects.size();
			results.values = _objects;
			Log.d("constraint", "empty");
		}
		return results;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void publishResults(CharSequence constraint, FilterResults results) {
		_listener.onNameableResultsPublished((List<? extends Nameable>)results.values);
	}
}
