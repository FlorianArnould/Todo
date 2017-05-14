package fr.socket.flo.todo.model;

import android.support.annotation.ColorInt;

import fr.socket.flo.todo.database.DataManager;
import fr.socket.flo.todo.view.drawable.ColorGenerator;

/**
 * @author Florian Arnould
 * @version 1.0
 */
public class Task extends Work {
	private final int _projectId;
	private final State _state;

	public Task(int id, int projectId, String name, @ColorInt int color, State state) {
		super(id, name, color);
		_projectId = projectId;
		_state = state;
	}

	public static void newTask(int projectId, String name) {
		Task task = new Task(NONE, projectId, name, ColorGenerator.randomColor(), State.WAITING);
		DataManager.getInstance().save(task);
	}

	public int getProjectId() {
		return _projectId;
	}

	public State getState() {
		return _state;
	}

	public enum State {WAITING, IN_PROGRESS, COMPLETED}
}
