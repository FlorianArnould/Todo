package fr.socket.flo.todo.view.fragments.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fr.socket.flo.todo.R;
import fr.socket.flo.todo.database.DataManager;
import fr.socket.flo.todo.database.OnMultipleObjectsLoadedListener;
import fr.socket.flo.todo.model.Nameable;
import fr.socket.flo.todo.model.Sorter;
import fr.socket.flo.todo.model.Task;
import fr.socket.flo.todo.view.fragments.ProjectFragment;
import fr.socket.flo.todo.view.fragments.filters.NameableFilter;
import fr.socket.flo.todo.view.fragments.filters.OnNameableResultsPublishedListener;
import fr.socket.flo.todo.view.graphics.PriorityDrawable;

/**
 * @author Florian Arnould
 * @version 1.0
 */
public class TasksAdapter extends SortableAdapter implements Filterable, UpdatableAdapter {
	private final ProjectFragment _fragment;
	private final int _projectId;
	private List<Task> _tasks;
	private List<Task> _filteredTasks;
	private Filter _filter;

	public TasksAdapter(ProjectFragment fragment, int projectId, Sorter.Sort sort) {
		super(sort);
		_fragment = fragment;
		_projectId = projectId;
		_tasks = new ArrayList<>();
		_filteredTasks = _tasks;
		update();
	}

	@Override
	public void update() {
		DataManager.getInstance().getTasksByProjectId(_projectId, new OnMultipleObjectsLoadedListener<Task>() {
			@Override
			public void onObjectsLoaded(List<Task> tasks) {
				_tasks = tasks;
				_filteredTasks = _tasks;
				notifyDataSetChanged();
			}
		});
	}

	@Override
	public int getCount() {
		return _filteredTasks.size();
	}

	@Override
	public Object getItem(int position) {
		return _filteredTasks.get(position);
	}

	@Override
	public long getItemId(int position) {
		return _filteredTasks.get(position).getId();
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater)_fragment.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.fragment_task_row, parent, false);
		}
		final Task task = _filteredTasks.get(position);
		TextView nameView = (TextView)view.findViewById(R.id.name);
		nameView.setText(task.getName());
		final Button stateView = (Button)view.findViewById(R.id.state);
		stateView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				task.nextState();
				task.save();
				_fragment.update();
				stateView.setText(task.getStringResIdState());
			}
		});
		stateView.setText(task.getStringResIdState());
		TextView deadlineView = (TextView)view.findViewById(R.id.deadline);
		if (task.hasDeadline()) {
			deadlineView.setText(task.getDeadline().toString());
		} else {
			deadlineView.setText(_fragment.getString(R.string.unlimited));
		}
		ImageView priorityLabelView = (ImageView)view.findViewById(R.id.priority_label);
		priorityLabelView.setImageDrawable(new PriorityDrawable(task.getPriority()));
		return view;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Filter getFilter() {
		if (_filter == null) {
			_filter = new NameableFilter(_tasks, new OnNameableResultsPublishedListener() {
				@Override
				public void onNameableResultsPublished(List<? extends Nameable> objects) {
					_filteredTasks = (List<Task>)objects;
					notifyDataSetChanged();
				}
			});
		}
		return _filter;
	}

	@Override
	public void notifyDataSetChanged() {
		sort(_filteredTasks);
		super.notifyDataSetChanged();
	}
}
