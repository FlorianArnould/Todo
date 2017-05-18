package fr.socket.flo.todo.model;

import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import java.util.Date;

import fr.socket.flo.todo.R;
import fr.socket.flo.todo.database.DataManager;
import fr.socket.flo.todo.view.drawable.ColorGenerator;

/**
 * @author Florian Arnould
 * @version 1.0
 */
public class Task extends Work {
	public enum State {WAITING, IN_PROGRESS, COMPLETED}

	private final int _projectId;
	private final State _state;

	public Task(int id, int projectId, String name, @ColorInt int color, @Nullable Date deadline, int priority, State state) {
		super(id, name, color, deadline, priority);
		_projectId = projectId;
		_state = state;
	}

	public static void newTask(int projectId, String name) {
		Task task = new Task(NONE, projectId, name, ColorGenerator.randomColor(), null, 0, State.WAITING);
		DataManager.getInstance().save(task);
	}

	public int getProjectId() {
		return _projectId;
	}

	public State getState() {
		return _state;
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

	public void save() {
		DataManager.getInstance().update(this);
	}
}
