package fr.socket.flo.todo.view.mainFragments.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fr.socket.flo.todo.R;
import fr.socket.flo.todo.database.DataManager;
import fr.socket.flo.todo.database.OnMultipleObjectsLoadedListener;
import fr.socket.flo.todo.model.Task;
import fr.socket.flo.todo.view.drawable.ProgressTextDrawable;

/**
 * @author Florian Arnould
 * @version 1.0
 */
public class TasksAdapter extends BaseAdapter {
	private final Context _context;
	private final int _projectId;
	private List<Task> _tasks;

	public TasksAdapter(Context context, int projectId) {
		_context = context;
		_projectId = projectId;
		_tasks = new ArrayList<>();
		update();
	}

	public void update() {
		DataManager.getInstance().getTasksByProjectId(_projectId, new OnMultipleObjectsLoadedListener<Task>() {
			@Override
			public void OnObjectsLoaded(List<Task> tasks) {
				_tasks = tasks;
				Log.d("test", "tasks" + _tasks.size() + "found");
				notifyDataSetChanged();
			}
		});
	}

	@Override
	public int getCount() {
		return _tasks.size();
	}

	@Override
	public Object getItem(int position) {
		return _tasks.get(position);
	}

	@Override
	public long getItemId(int position) {
		return _tasks.get(position).getId();
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater)_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.fragment_task_row, parent, false);
		}
		Task task = _tasks.get(position);
		ImageView icon = (ImageView)view.findViewById(R.id.icon);
		icon.setImageDrawable(new ProgressTextDrawable(task.getName().substring(0, 1), task.getColor()));
		TextView nameView = (TextView)view.findViewById(R.id.name);
		nameView.setText(task.getName());
		TextView stateView = (TextView)view.findViewById(R.id.state);
		stateView.setText(task.getStringResIdState());
		return view;
	}
}
