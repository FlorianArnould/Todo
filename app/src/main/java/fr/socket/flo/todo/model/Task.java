package fr.socket.flo.todo.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import java.util.Collection;
import java.util.Date;

import fr.socket.flo.todo.R;
import fr.socket.flo.todo.database.DataManager;
import fr.socket.flo.todo.database.OnNewObjectCreatedListener;

/**
 * @author Florian Arnould
 * @version 1.0
 */
public class Task extends Work {
	public enum State {WAITING, IN_PROGRESS, COMPLETED}

	public static final String TABLE_NAME = "tasks";
	private int _projectId;
	private State _state;

	public Task(Cursor cursor) {
		super(cursor);
	}

	private Task(int id, int projectId, String name, @Nullable Date deadline, int priority, State state) {
		super(id, name, deadline, priority);
		_projectId = projectId;
		_state = state;
	}

	public static void newTask(int projectId, String name, @Nullable OnNewObjectCreatedListener listener) {
		Task task = new Task(NONE, projectId, name, null, 1, State.WAITING);
		DataManager.getInstance().save(task, listener);
	}

	public static Collection<String> getColumns() {
		Collection<String> columns = Work.getColumns();
		columns.add("project_id");
		columns.add("state");
		return columns;
	}

	public void nextState() {
		int ordinal = _state.ordinal() + 1;
		if (ordinal >= State.values().length) {
			ordinal = 0;
		}
		_state = State.values()[ordinal];
	}

	@StringRes
	public int getStringResIdState() {
		switch (_state) {
			case WAITING:
				return R.string.waiting;
			case IN_PROGRESS:
				return R.string.in_progress;
			case COMPLETED:
				return R.string.completed;
		}
		return -1;
	}

	@Override
	protected int fromCursor(Cursor cursor) {
		int index = super.fromCursor(cursor);
		_projectId = cursor.getInt(index++);
		_state = Task.State.valueOf(cursor.getString(index++));
		return index;
	}

	@Override
	public ContentValues toContentValues() {
		ContentValues values = super.toContentValues();
		values.put("project_id", _projectId);
		values.put("state", _state.name());
		return values;
	}

	@Override
	public String getTable() {
		return TABLE_NAME;
	}
}
