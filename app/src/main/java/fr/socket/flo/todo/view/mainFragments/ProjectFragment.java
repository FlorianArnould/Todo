package fr.socket.flo.todo.view.mainFragments;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import fr.socket.flo.todo.R;
import fr.socket.flo.todo.database.DataManager;
import fr.socket.flo.todo.database.OnObjectLoadedListener;
import fr.socket.flo.todo.model.Project;
import fr.socket.flo.todo.model.Sorter;
import fr.socket.flo.todo.view.activity.MainActivity;
import fr.socket.flo.todo.view.activity.OnSortChangedListener;
import fr.socket.flo.todo.view.dialog.DialogManager;
import fr.socket.flo.todo.view.dialog.OnDialogFinishedListener;
import fr.socket.flo.todo.view.mainFragments.adapters.SortableAdapter;
import fr.socket.flo.todo.view.mainFragments.adapters.TasksAdapter;

/**
 * @author Florian Arnould
 * @version 1.0
 */
public class ProjectFragment extends MainActivityFragment {
	private final static String PROJECT_ID_KEY = "PROJECT_ID";
	private final static String SORT_PREFERENCES_KEY = "project_fragment_sort";
	private final static int PROGRESS_MAX = 1000;
	private int _projectId;
	private View _view;

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
		ImageButton returnButton = (ImageButton)_view.findViewById(R.id.return_button);
		returnButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onActivityBackPressed();
			}
		});
		return _view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		_projectId = getArguments().getInt(PROJECT_ID_KEY);
		final Activity activity = getActivity();
		final SharedPreferences pref = activity.getSharedPreferences(getString(R.string.preferences_name_key), Context.MODE_PRIVATE);
		String sort = pref.getString(SORT_PREFERENCES_KEY, Sorter.SortingWay.BY_NAME.name());
		Sorter.SortingWay sortingWay = Sorter.SortingWay.valueOf(sort);
		setListAdapter(new TasksAdapter(getContext(), _projectId, sortingWay));

		DataManager.getInstance().getProjectById(_projectId, new OnObjectLoadedListener<Project>() {
			@Override
			public void OnObjectLoaded(Project project) {
				String projectName = project.getName();
				activity.setTitle(projectName);
				animateProjectProgress(project);
			}
		});
	}

	// TODO: 16/05/17 refactor this method in an abstraction
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		MainActivity activity = getMainActivity();
		final SharedPreferences pref = activity.getSharedPreferences(getString(R.string.preferences_name_key), Context.MODE_PRIVATE);
		String sort = pref.getString(SORT_PREFERENCES_KEY, Sorter.SortingWay.BY_NAME.name());
		Sorter.SortingWay sortingWay = Sorter.SortingWay.valueOf(sort);
		activity.setSortWay(sortingWay);
		activity.setOnSortChangedListener(new OnSortChangedListener() {
			@Override
			public void onSortChangedListener(Sorter.SortingWay way) {
				pref.edit().putString(SORT_PREFERENCES_KEY, way.name()).apply();
				SortableAdapter adapter = (SortableAdapter)getListAdapter();
				adapter.changeSortingWay(way);
			}
		});
	}

	@Override
	public void onStart() {
		super.onStart();
		MainActivity mainActivity = getMainActivity();
		mainActivity.getFloatingActionButton().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Activity activity = getActivity();
				DialogManager dialogManager = new DialogManager(activity);
				dialogManager.showNewTaskDialog(_projectId, new OnDialogFinishedListener() {
					@Override
					public void OnDialogFinished(boolean state) {
						if (state) {
							TasksAdapter adapter = (TasksAdapter)getListAdapter();
							adapter.update();
							adapter.notifyDataSetChanged();
							Snackbar snackbar = Snackbar.make(getMainActivity().getRootView(), R.string.new_task_created, Snackbar.LENGTH_LONG);
							snackbar.setAction(R.string.configure, new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									// TODO: 13/05/17 Open the edit task fragment
									Log.d("Snackbar", "action clicked");
								}
							});
							snackbar.show();
						}
					}
				});
			}
		});
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
	public void onActivityBackPressed() {
		Fragment fragment = new AllProjectsFragment();
		getActivity()
				.getSupportFragmentManager()
				.beginTransaction()
				.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
				.replace(R.id.fragmentContent, fragment)
				.commit();
	}

	private void animateProjectProgress(Project project) {
		ProgressBar waiting = (ProgressBar)_view.findViewById(R.id.waiting_progress);
		ProgressBar inProgress = (ProgressBar)_view.findViewById(R.id.in_progress);
		ProgressBar complete = (ProgressBar)_view.findViewById(R.id.complete_progress);
		complete.setMax(PROGRESS_MAX);
		inProgress.setMax(PROGRESS_MAX);
		waiting.setMax(PROGRESS_MAX);
		ObjectAnimator waitingAnimator = initializeAnimation(waiting, 1, 400);
		ObjectAnimator inProgressAnimator = initializeAnimation(inProgress, 0.7, 600);
		ObjectAnimator completeAnimator = initializeAnimation(complete, project.getProgress(), 800);
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
}
