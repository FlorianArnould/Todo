package fr.socket.flo.todo.view.fragments.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import fr.socket.flo.todo.view.drawable.ColorGenerator;
import fr.socket.flo.todo.view.drawable.ProgressTextDrawable;
import fr.socket.flo.todo.view.drawable.TextDrawable;
import fr.socket.flo.todo.view.fragments.filters.NameableFilter;
import fr.socket.flo.todo.view.fragments.filters.OnNameableResultsPublishedListener;

/**
 * @author Florian Arnould
 * @version 1.0
 */
public class TasksAdapter extends SortableAdapter implements Filterable {
	private final Context _context;
	private final int _projectId;
	private List<Task> _tasks;
	private List<Task> _filteredTasks;
	private Filter _filter;

	public TasksAdapter(Context context, int projectId, Sorter.SortingWay sortingWay) {
		super(sortingWay);
		_context = context;
		_projectId = projectId;
		_tasks = new ArrayList<>();
		_filteredTasks = _tasks;
		update();
	}

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
			LayoutInflater inflater = (LayoutInflater)_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.fragment_task_row, parent, false);
		}
		Task task = _filteredTasks.get(position);
		TextView nameView = (TextView)view.findViewById(R.id.name);
		nameView.setText(task.getName());
		TextView stateView = (TextView)view.findViewById(R.id.state);
		stateView.setText(task.getStringResIdState());
		TextView deadlineView = (TextView)view.findViewById(R.id.deadline);
		if(task.hasDeadline()) {
			deadlineView.setText(task.getDeadlineAsString());
		}else{
			deadlineView.setText(_context.getString(R.string.unlimited));
		}
		ImageView priorityLabelView = (ImageView)view.findViewById(R.id.priority_label);
		int priority = task.getPriority();
		priorityLabelView.setImageDrawable(new TextDrawable(String.valueOf(priority), ColorGenerator.priorityColor(priority)));
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
					Log.d("taskAdpater", "callback");
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
