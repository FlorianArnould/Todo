package fr.socket.flo.todo.view.fragments.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fr.socket.flo.todo.R;
import fr.socket.flo.todo.database.DataManager;
import fr.socket.flo.todo.database.OnMultipleObjectsLoadedListener;
import fr.socket.flo.todo.database.OnObjectLoadedListener;
import fr.socket.flo.todo.model.Nameable;
import fr.socket.flo.todo.model.Project;
import fr.socket.flo.todo.model.Task;
import fr.socket.flo.todo.view.drawable.ColorGenerator;
import fr.socket.flo.todo.view.drawable.ProgressTextDrawable;
import fr.socket.flo.todo.view.fragments.filters.NameableFilter;
import fr.socket.flo.todo.view.fragments.filters.OnNameableResultsPublishedListener;

/**
 * @author Florian Arnould
 * @version 1.0
 */
public class ProjectsAdapter extends BaseAdapter implements Filterable {
	private final Context _context;
	private List<Project> _projects;
	private List<Project> _filteredProjects;
	private Filter _filter;

	public ProjectsAdapter(Context context) {
		_context = context;
		_projects = new ArrayList<>();
		_filteredProjects = _projects;
		update();
	}

	public void update() {
		DataManager.getInstance().getAllProjects(new OnMultipleObjectsLoadedListener<Project>() {
			@Override
			public void OnObjectsLoaded(List<Project> objects) {
				_projects = objects;
				_filteredProjects = _projects;
				notifyDataSetChanged();
			}
		});
	}

	@Override
	public int getCount() {
		return _filteredProjects.size();
	}

	@Override
	public Object getItem(int position) {
		return _filteredProjects.get(position);
	}

	@Override
	public long getItemId(int position) {
		return _filteredProjects.get(position).getId();
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater)_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.fragment_project_row, parent, false);
		}
		final Project project = _filteredProjects.get(position);
		final String projectName = project.getName();
		final @ColorInt int color = project.getColor();

		ImageView iconView = (ImageView)view.findViewById(R.id.icon);
		final Drawable icon = new ProgressTextDrawable(projectName.substring(0, 1), ColorGenerator.darkerColor(color), color, project.getProgress());
		iconView.setImageDrawable(icon);

		TextView nameView = (TextView)view.findViewById(R.id.name);
		nameView.setText(projectName);

		final TextView currentTaskView = (TextView)view.findViewById(R.id.current_task);
		if (project.hasCurrentTask()) {
			DataManager.getInstance().getTaskById(project.getCurrentTaskId(), new OnObjectLoadedListener<Task>() {
				@Override
				public void OnObjectLoaded(Task task) {
					currentTaskView.setText(task.getName());
				}
			});
		} else {
			currentTaskView.setText(_context.getString(R.string.no_current_task));
		}
		return view;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Filter getFilter() {
		if (_filter == null) {
			_filter = new NameableFilter(_projects, new OnNameableResultsPublishedListener() {
				@Override
				public void onNameableResultsPublished(List<? extends Nameable> _objects) {
					_filteredProjects = (List<Project>)_objects;
					notifyDataSetChanged();
				}
			});
		}
		return _filter;
	}
}
