package fr.socket.flo.todo.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;

import java.util.Collection;
import java.util.Date;

import fr.socket.flo.todo.database.DataManager;
import fr.socket.flo.todo.database.OnNewObjectCreatedListener;
import fr.socket.flo.todo.view.graphics.ColorGenerator;

/**
 * @author Florian Arnould
 * @version 1.0
 */
public class Project extends Work {
	public static final String TABLE_NAME = "projects";
	@ColorInt
	private int _color;
	private int _currentTaskId;
	private boolean _isFavorite;
	private final int[] _numberOfTasks;
	private int _totalOfTasks;

	public Project(Cursor cursor) {
		super(cursor);
		_numberOfTasks = new int[3];
	}

	private Project(int id, String name, @ColorInt int color, @Nullable Date deadline, int priority, int currentTaskId, boolean isFavorite) {
		super(id, name, deadline, priority);
		_color = color;
		_currentTaskId = currentTaskId;
		_isFavorite = isFavorite;
		_numberOfTasks = new int[3];
	}

	public static void newProject(String name, @Nullable OnNewObjectCreatedListener listener) {
		Project project = new Project(NONE, name, ColorGenerator.randomColor(), null, 1, NONE, false);
		DataManager.getInstance().save(project, listener);
	}

	public static Collection<String> getColumns() {
		Collection<String> columns = Work.getColumns();
		columns.add("color");
		columns.add("is_favorite");
		return columns;
	}

	@ColorInt
	public int getColor() {
		return _color;
	}

	public double getCompleteProgress() {
		return ((double)_numberOfTasks[Task.State.COMPLETED.ordinal()]) / _totalOfTasks;
	}

	public double getInProgress() {
		return ((double)_numberOfTasks[Task.State.COMPLETED.ordinal()] + _numberOfTasks[Task.State.IN_PROGRESS.ordinal()]) / _totalOfTasks;
	}

	public int getCurrentTaskId() {
		return _currentTaskId;
	}

	public void setCurrentTaskId(int taskId) {
		_currentTaskId = taskId;
	}

	public boolean hasCurrentTask() {
		return _currentTaskId != NONE;
	}

	public boolean isFavorite() {
		return _isFavorite;
	}

	public void setFavorite(boolean isFavorite) {
		_isFavorite = isFavorite;
	}

	@Override
	protected int fromCursor(Cursor cursor) {
		int index = super.fromCursor(cursor);
		_color = cursor.getInt(index++);
		_currentTaskId = NONE;
		_isFavorite = cursor.getInt(index++) == 1;
		return index;
	}

	@Override
	public ContentValues toContentValues() {
		ContentValues values = super.toContentValues();
		values.put("color", _color);
		values.put("is_favorite", isFavorite());
		return values;
	}

	@Override
	public String getTable() {
		return TABLE_NAME;
	}

	public void setNumberOfTasks(Task.State state, int number) {
		_numberOfTasks[state.ordinal()] = number;
		_totalOfTasks = 0;
		for (int _numberOfTask : _numberOfTasks) {
			_totalOfTasks += _numberOfTask;
		}
	}
}
