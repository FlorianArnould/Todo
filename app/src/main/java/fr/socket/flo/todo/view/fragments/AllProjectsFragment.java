package fr.socket.flo.todo.view.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filterable;
import android.widget.ListView;

import fr.socket.flo.todo.R;
import fr.socket.flo.todo.model.Project;
import fr.socket.flo.todo.model.Sorter;
import fr.socket.flo.todo.view.activity.MainActivity;
import fr.socket.flo.todo.view.dialog.DialogManager;
import fr.socket.flo.todo.view.dialog.OnDialogFinishedListener;
import fr.socket.flo.todo.view.fragments.adapters.ProjectsAdapter;

/**
 * @author Florian Arnould
 * @version 1.0
 */
public class AllProjectsFragment extends MainActivityFragment {
	private static final String SORT_PREFERENCES_KEY = "all_projects_fragment_sort";

	public AllProjectsFragment() {
		super(SORT_PREFERENCES_KEY);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_all_projects, container, false);
		cleanView(view);
		setHasOptionsMenu(true);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Activity activity = getActivity();
		activity.setTitle(R.string.project_title);
		final SharedPreferences pref = activity.getSharedPreferences(getString(R.string.preferences_name_key), Context.MODE_PRIVATE);
		String sort = pref.getString(SORT_PREFERENCES_KEY, Sorter.SortingWay.BY_NAME.name());
		Sorter.SortingWay sortingWay = Sorter.SortingWay.valueOf(sort);
		setListAdapter(new ProjectsAdapter(getContext(), sortingWay));
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position, long id) {
		Project project = (Project)getListAdapter().getItem(position);
		Fragment fragment = ProjectFragment.newInstance(project.getId());
		getActivity()
				.getSupportFragmentManager()
				.beginTransaction()
				.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
				.replace(R.id.fragmentContent, fragment)
				.commit();
	}

	@Override
	public void onStart() {
		super.onStart();
		setHasFloatingAction(true);
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

	@Override
	public void onFloatingActionButtonClicked() {
		DialogManager dialogManager = new DialogManager(getActivity());
		dialogManager.showNewProjectDialog(new OnDialogFinishedListener() {
			@Override
			public void onDialogFinished(boolean state) {
				if (state) {
					ProjectsAdapter adapter = (ProjectsAdapter)getListAdapter();
					adapter.update();
					Snackbar snackbar = Snackbar.make(getMainActivity().getRootView(), R.string.new_project_created, Snackbar.LENGTH_LONG);
					snackbar.setAction(R.string.configure, new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							// TODO: 13/05/17 Open the edit project fragment
							Log.d("Snackbar", "action clicked");
						}
					});
					snackbar.show();
				}
			}
		});
	}

	@Override
	public void onActivityBackPressed() {
		getMainActivity().moveTaskToBack(true);
	}

}
