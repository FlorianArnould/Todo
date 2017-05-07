package fr.socket.flo.todo.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fr.socket.flo.todo.R;
import fr.socket.flo.todo.storage.Project;
import fr.socket.flo.todo.storage.database.OnMultipleObjectsLoadedListener;
import fr.socket.flo.todo.storage.database.Projects;
import fr.socket.flo.todo.view.dialog.DialogManager;
import fr.socket.flo.todo.view.dialog.OnDialogFinishedListener;
import fr.socket.flo.todo.view.drawable.ColorGenerator;
import fr.socket.flo.todo.view.drawable.ProgressTextDrawable;

/**
 * @author Florian Arnould
 * @version 1.0
 */
public class AllProjectsFragment extends Fragment {
	private ProjectsAdapter _adapter;

	public AllProjectsFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_project, container, false);
		getActivity().setTitle(R.string.project_title);
		ListView listView = (ListView)view.findViewById(R.id.list);
		_adapter = new ProjectsAdapter();
		listView.setAdapter(_adapter);
		FloatingActionButton fab = (FloatingActionButton)view.findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogManager.showNewProjectDialog(getActivity(), new OnDialogFinishedListener() {
					@Override
					public void OnDialogFinished(boolean state) {
						if(state) {
							_adapter.update();
						}
					}
				});
			}
		});
		return view;
	}

	private class ProjectsAdapter extends BaseAdapter {
		private List<Project> _projects;

		ProjectsAdapter() {
			_projects = new ArrayList<>();
			update();
		}

		void update() {
			Projects.getInstance(getContext()).getAllProjects(new OnMultipleObjectsLoadedListener<Project>() {
				@Override
				public void OnObjectsLoaded(List<Project> objects) {
					_projects = objects;
				}
			});
		}

		@Override
		public int getCount() {
			return _projects.size();
		}

		@Override
		public Object getItem(int position) {
			return _projects.get(position);
		}

		@Override
		public long getItemId(int position) {
			return _projects.get(position).getId();
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			if (view == null) {
				LayoutInflater inflater = (LayoutInflater)AllProjectsFragment.this.getActivity().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(R.layout.fragment_project_row, parent, false);
			}
			final Project project = _projects.get(position);
			final String projectName = project.getName();
			final @ColorInt int color = project.getColor();

			ImageView iconView = (ImageView)view.findViewById(R.id.icon);
			//TODO get real values form the project
			final Drawable icon = new ProgressTextDrawable(projectName.substring(0, 1), ColorGenerator.darkerColor(color), color, Math.random(), 1);
			iconView.setImageDrawable(icon);

			TextView nameView = (TextView)view.findViewById(R.id.name);
			nameView.setText(projectName);

			// TODO: 07/05/17 set the real value from the project
			TextView currentTaskView = (TextView)view.findViewById(R.id.current_task);
			currentTaskView.setText("No current Task");

			return view;
		}
	}
}
