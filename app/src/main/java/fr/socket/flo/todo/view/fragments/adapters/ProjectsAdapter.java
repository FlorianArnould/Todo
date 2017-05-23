package fr.socket.flo.todo.view.fragments.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import fr.socket.flo.todo.model.Sorter;
import fr.socket.flo.todo.model.Task;
import fr.socket.flo.todo.view.fragments.filters.NameableFilter;
import fr.socket.flo.todo.view.fragments.filters.OnNameableResultsPublishedListener;
import fr.socket.flo.todo.view.graphics.ColorGenerator;
import fr.socket.flo.todo.view.graphics.PriorityDrawable;
import fr.socket.flo.todo.view.graphics.ProgressTextDrawable;

/**
 * @author Florian Arnould
 * @version 1.0
 */
public class ProjectsAdapter extends SortableAdapter implements Filterable, UpdatableAdapter {
	private final Context _context;
	private List<Project> _projects;
	private List<Project> _filteredProjects;
	private Filter _filter;

	public ProjectsAdapter(Context context, Sorter.Sort sort) {
		super(sort);
		_context = context;
		_projects = new ArrayList<>();
		_filteredProjects = _projects;
		update();
	}

	@Override
	public void update() {
		DataManager.getInstance().getAllProjects(new OnMultipleObjectsLoadedListener<Project>() {
			@Override
			public void onObjectsLoaded(List<Project> projects) {
				_projects = projects;
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
		@ColorInt final int color = project.getColor();

		ImageView iconView = (ImageView)view.findViewById(R.id.icon);
		final Drawable icon = new ProgressTextDrawable(projectName.substring(0, 1), ColorGenerator.darkerColor(color), color, project.getCompleteProgress());
		iconView.setImageDrawable(icon);

		ImageView priorityView = (ImageView)view.findViewById(R.id.priority_label);
		final Drawable priorityLabel = new PriorityDrawable(project.getPriority());
		priorityView.setImageDrawable(priorityLabel);

		TextView nameView = (TextView)view.findViewById(R.id.name);
		nameView.setText(projectName);

		final TextView currentTaskView = (TextView)view.findViewById(R.id.current_task);
		if (project.hasCurrentTask()) {
			DataManager.getInstance().getTaskById(project.getCurrentTaskId(), new OnObjectLoadedListener<Task>() {
				@Override
				public void onObjectLoaded(Task task) {
					currentTaskView.setText(task.getName());
				}
			});
		} else {
			currentTaskView.setText(_context.getString(R.string.no_current_task));
		}

		CheckBox favoriteBox = (CheckBox)view.findViewById(R.id.is_favorite);
		favoriteBox.setChecked(project.isFavorite());
		favoriteBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked != project.isFavorite()) {
					project.setFavorite(isChecked);
					project.save();
				}
			}
		});
		return view;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Filter getFilter() {
		if (_filter == null) {
			_filter = new NameableFilter(_projects, new OnNameableResultsPublishedListener() {
				@Override
				public void onNameableResultsPublished(List<? extends Nameable> objects) {
					_filteredProjects = (List<Project>)objects;
					notifyDataSetChanged();
				}
			});
		}
		return _filter;
	}

	@Override
	public void notifyDataSetChanged() {
		sort(_filteredProjects);
		super.notifyDataSetChanged();
	}
}
