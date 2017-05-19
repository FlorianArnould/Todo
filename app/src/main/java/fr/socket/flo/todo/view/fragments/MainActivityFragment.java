package fr.socket.flo.todo.view.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Filterable;

import fr.socket.flo.todo.R;
import fr.socket.flo.todo.model.Sorter;
import fr.socket.flo.todo.view.activity.MainActivity;
import fr.socket.flo.todo.view.activity.OnSortChangedListener;
import fr.socket.flo.todo.view.fragments.adapters.SortableAdapter;

/**
 * @author Florian Arnould
 * @version 1.0
 */
public abstract class MainActivityFragment extends ListFragment {
	private final String _sortPreferenceKey;

	public MainActivityFragment(String sortPreferenceKey) {
		_sortPreferenceKey = sortPreferenceKey;
	}

	protected MainActivity getMainActivity() {
		return (MainActivity)super.getActivity();
	}

	public abstract void onActivityBackPressed();

	protected void cleanView(View view) {
		InputMethodManager input = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		input.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		getMainActivity().closeSearch();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		MainActivity activity = getMainActivity();
		final SharedPreferences pref = activity.getSharedPreferences(getString(R.string.preferences_name_key), Context.MODE_PRIVATE);
		String sort = pref.getString(_sortPreferenceKey, Sorter.SortingWay.BY_NAME.name());
		Sorter.SortingWay sortingWay = Sorter.SortingWay.valueOf(sort);
		activity.setSortWay(sortingWay);
		activity.setOnSortChangedListener(new OnSortChangedListener() {
			@Override
			public void onSortChangedListener(Sorter.SortingWay way) {
				pref.edit().putString(_sortPreferenceKey, way.name()).apply();
				SortableAdapter adapter = (SortableAdapter)getListAdapter();
				adapter.changeSortingWay(way);
			}
		});
	}

	@Override
	public void onStart() {
		super.onStart();
		MainActivity activity = getMainActivity();
		activity.getFloatingActionButton().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onFloatingActionButtonClicked();
			}
		});
		MainActivity mainActivity = getMainActivity();
		final Filterable adapter = (Filterable)getListAdapter();
		mainActivity.getSearchView().setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				adapter.getFilter().filter(newText);
				return true;
			}
		});
	}

	public void setHasFloatingAction(boolean hasFab) {
		if (hasFab) {
			getMainActivity().getFloatingActionButton().setVisibility(View.VISIBLE);
		} else {
			getMainActivity().getFloatingActionButton().setVisibility(View.GONE);
		}
	}

	public void onFloatingActionButtonClicked() {
	}
}
