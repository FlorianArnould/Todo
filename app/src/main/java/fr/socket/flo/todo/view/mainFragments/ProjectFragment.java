package fr.socket.flo.todo.view.mainFragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import fr.socket.flo.todo.R;
import fr.socket.flo.todo.database.DataManager;
import fr.socket.flo.todo.database.OnObjectLoadedListener;
import fr.socket.flo.todo.model.Project;
import fr.socket.flo.todo.view.dialog.DialogManager;
import fr.socket.flo.todo.view.dialog.OnDialogFinishedListener;
import fr.socket.flo.todo.view.drawable.ColorGenerator;
import fr.socket.flo.todo.view.drawable.ProgressTextDrawable;
import fr.socket.flo.todo.view.mainFragments.adapters.TasksAdapter;

/**
 * @author Florian Arnould
 * @version 1.0
 */
public class ProjectFragment extends MainActivityFragment {
	private final static String PROJECT_ID_KEY = "PROJECT_ID";
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
		return _view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		_projectId = getArguments().getInt(PROJECT_ID_KEY);
		setListAdapter(new TasksAdapter(getContext(), _projectId));
		DataManager.getInstance().getProjectById(_projectId, new OnObjectLoadedListener<Project>() {
			@Override
			public void OnObjectLoaded(Project project) {
				String projectName = project.getName();
				@ColorInt int projectColor = project.getColor();
				getActivity().setTitle(projectName);
				ImageView iconView = (ImageView)_view.findViewById(R.id.project_icon);
				iconView.setImageDrawable(new ProgressTextDrawable(projectName.substring(0, 1), ColorGenerator.darkerColor(projectColor), projectColor, project.getProgress()));
			}
		});
	}

	@Override
	public void onStart() {
		super.onStart();
		getMainActivity().getFloatingActionButton().setOnClickListener(new View.OnClickListener() {
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

}
