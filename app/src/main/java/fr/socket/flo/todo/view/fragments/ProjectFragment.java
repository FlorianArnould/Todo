package fr.socket.flo.todo.view.fragments;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;

import fr.socket.flo.todo.R;
import fr.socket.flo.todo.database.DataManager;
import fr.socket.flo.todo.database.OnNewObjectCreatedListener;
import fr.socket.flo.todo.model.Project;
import fr.socket.flo.todo.model.Sorter;
import fr.socket.flo.todo.view.activity.EditProjectActivity;
import fr.socket.flo.todo.view.dialog.DialogManager;
import fr.socket.flo.todo.view.fragments.adapters.TasksAdapter;

/**
 * @author Florian Arnould
 * @version 1.0
 */
public class ProjectFragment extends MainActivityFragment implements OnNewObjectCreatedListener {
	private static final String PROJECT_ID_KEY = "PROJECT_ID";
	private static final String SORT_PREFERENCES_KEY = "project_fragment_sort";
	private static final int PROGRESS_MAX = 1000;
	private int _projectId;
	private View _view;

	public ProjectFragment() {
		super(SORT_PREFERENCES_KEY);
	}

	public static ProjectFragment newInstance(int projectId) {
		ProjectFragment fragment = new ProjectFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(PROJECT_ID_KEY, projectId);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		_view = inflater.inflate(R.layout.fragment_project, container, false);
		cleanView(_view);
		setHasOptionsMenu(true);
		return _view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		_projectId = getArguments().getInt(PROJECT_ID_KEY);
		final Activity activity = getActivity();
		final SharedPreferences pref = activity.getSharedPreferences(getString(R.string.preferences_name_key), Context.MODE_PRIVATE);
		String sort = pref.getString(SORT_PREFERENCES_KEY, Sorter.Sort.BY_NAME.name());
		Sorter.Sort sortingWay = Sorter.Sort.valueOf(sort);
		setListAdapter(new TasksAdapter(this, _projectId, sortingWay));

		DataManager.getInstance().getProjectById(_projectId, project -> {
			String projectName = project.getName();
			activity.setTitle(projectName);
			animateProjectProgress(project, 600);
		});
	}

	@Override
	@CallSuper
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		// TODO: 21/05/17 find a way to animate the edit option item
		MenuItem editItem = menu.findItem(R.id.action_edit);
		editItem.setEnabled(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_edit) {
			Intent intent = new Intent(getActivity(), EditProjectActivity.class);
			intent.putExtra(EditProjectActivity.PROJECT_ID, _projectId);
			getActivity().startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onStart() {
		super.onStart();
		setHasFloatingAction(true);
	}

	@Override
	public void onFloatingActionButtonClicked() {
		Activity activity = getActivity();
		DialogManager dialogManager = new DialogManager(activity);
		dialogManager.showNewTaskDialog(_projectId, this);
	}

	@Override
	public void onActivityBackPressed() {
		Fragment fragment = new AllProjectsFragment();
		getActivity()
				.getSupportFragmentManager()
				.beginTransaction()
				.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
				.replace(R.id.fragmentContent, fragment)
				.commit();
	}

	private void animateProjectProgress(Project project, long delay) {
		ProgressBar waiting = (ProgressBar)_view.findViewById(R.id.waiting_progress);
		ProgressBar inProgress = (ProgressBar)_view.findViewById(R.id.in_progress);
		ProgressBar complete = (ProgressBar)_view.findViewById(R.id.complete_progress);
		complete.setMax(PROGRESS_MAX);
		inProgress.setMax(PROGRESS_MAX);
		waiting.setMax(PROGRESS_MAX);
		ObjectAnimator waitingAnimator = initializeAnimation(waiting, 1, delay - 200);
		ObjectAnimator inProgressAnimator = initializeAnimation(inProgress, project.getInProgress(), delay);
		ObjectAnimator completeAnimator = initializeAnimation(complete, project.getCompleteProgress(), delay + 200);
		waitingAnimator.start();
		inProgressAnimator.start();
		completeAnimator.start();
	}

	private ObjectAnimator initializeAnimation(ProgressBar progressBar, double progress, long delay) {
		ObjectAnimator animator = ObjectAnimator.ofInt(progressBar, "progress", (int)(PROGRESS_MAX * progress));
		animator.setDuration(1000);
		animator.setStartDelay(delay);
		animator.setInterpolator(new DecelerateInterpolator());
		return animator;
	}

	@Override
	public void onNewObjectCreated(final int objectId) {
		Snackbar snackbar;
		if (objectId == -1) {
			snackbar = Snackbar.make(getMainActivity().getRootView(), R.string.new_task_was_not_created, Snackbar.LENGTH_SHORT);
		} else {
			TasksAdapter adapter = (TasksAdapter)getListAdapter();
			adapter.update();
			adapter.notifyDataSetChanged();
			update();
			snackbar = Snackbar.make(getMainActivity().getRootView(), R.string.new_task_created, Snackbar.LENGTH_LONG);
			snackbar.setAction(R.string.configure, v -> {
				// TODO: 13/05/17 Open the edit task fragment
				Log.d("SnackBar", "action clicked");
			});
		}
		snackbar.show();
	}

	public void update() {
		DataManager.getInstance().getProjectById(_projectId, project -> animateProjectProgress(project, 0));
	}
}
