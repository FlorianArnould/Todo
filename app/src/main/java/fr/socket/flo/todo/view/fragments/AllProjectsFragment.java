package fr.socket.flo.todo.view.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import fr.socket.flo.todo.R;
import fr.socket.flo.todo.database.OnNewObjectCreatedListener;
import fr.socket.flo.todo.model.Project;
import fr.socket.flo.todo.model.Sorter;
import fr.socket.flo.todo.view.activity.EditProjectActivity;
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
		String sort = pref.getString(SORT_PREFERENCES_KEY, Sorter.Sort.BY_NAME.name());
		Sorter.Sort sortingWay = Sorter.Sort.valueOf(sort);
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
	}

	@Override
	public void onFloatingActionButtonClicked() {
		DialogManager dialogManager = new DialogManager(getActivity());
		dialogManager.showNewProjectDialog(new OnNewObjectCreatedListener() {
			@Override
			public void onNewObjectCreated(final int objectId) {
				Snackbar snackbar;
				if (objectId == -1) {
					snackbar = Snackbar.make(getMainActivity().getRootView(), R.string.new_project_was_not_created, Snackbar.LENGTH_SHORT);
				} else {
					ProjectsAdapter adapter = (ProjectsAdapter)getListAdapter();
					adapter.update();
					snackbar = Snackbar.make(getMainActivity().getRootView(), R.string.new_project_created, Snackbar.LENGTH_LONG);
					snackbar.setAction(R.string.configure, new View.OnClickListener() {
						@Override
						public void onClick(View v){
							Intent intent = new Intent(getContext(), EditProjectActivity.class);
							intent.putExtra(EditProjectActivity.PROJECT_ID, objectId);
							getContext().startActivity(intent);
						}
					});
				}
				snackbar.show();
			}
		});
	}

	@Override
	public void onActivityBackPressed() {
		getMainActivity().moveTaskToBack(true);
	}

}
