package fr.socket.flo.todo.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;

import java.util.Collection;
import java.util.Date;

import fr.socket.flo.todo.database.DataManager;
import fr.socket.flo.todo.view.drawable.ColorGenerator;

/**
 * @author Florian Arnould
 * @version 1.0
 */
public class Project extends Work {
	private int _currentTaskId;
	private boolean _isFavorite;

	public Project(Cursor cursor){
		super(cursor);
	}

	private Project(int id, String name, @ColorInt int color, @Nullable Date deadline, int priority, int currentTaskId, boolean isFavorite) {
		super(id, name, color, deadline, priority);
		_currentTaskId = currentTaskId;
		_isFavorite = isFavorite;
	}

	public static void newProject(String name) {
		Project project = new Project(NONE, name, ColorGenerator.randomColor(), null, 0, NONE, false);
		DataManager.getInstance().save(project);
	}

	public double getProgress() {
		// TODO: 13/05/17 return the real value
		return 0.5;
	}

	public int getCurrentTaskId() {
		return _currentTaskId;
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
		_currentTaskId = NONE;
		_isFavorite = cursor.getInt(index) == 1;
		return index;
	}

	public static Collection<String> getColumns(){
		Collection<String> columns = Work.getColumns();
		columns.add("is_favorite");
		return columns;
	}

	@Override
	public ContentValues toContentValues() {
		ContentValues values = super.toContentValues();
		values.put("is_favorite", isFavorite());
		return values;
	}

	public static String getDatabaseTable(){
		return "projects";
	}

	public String getTable() {
		return getDatabaseTable();
	}

	public void setCurrentTaskId(int taskId){
		_currentTaskId = taskId;
	}
}
