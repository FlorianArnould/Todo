package fr.socket.flo.todo.model;

import android.support.annotation.ColorInt;

import fr.socket.flo.todo.database.DataManager;
import fr.socket.flo.todo.view.drawable.ColorGenerator;

/**
 * @author Florian Arnould
 * @version 1.0
 */
public class Project extends Work {
	private final int _currentTaskId;
	private boolean _isFavorite;

	public Project(int id, String name, @ColorInt int color, int currentTaskId, boolean isFavorite) {
		super(id, name, color);
		_currentTaskId = currentTaskId;
		_isFavorite = isFavorite;
	}

	public static void newProject(String name) {
		Project project = new Project(NONE, name, ColorGenerator.randomColor(), NONE, false);
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

	public boolean isFavorite(){
		return _isFavorite;
	}

	public void setFavorite(boolean isFavorite){
		_isFavorite = isFavorite;
	}

	public void save(){
		DataManager.getInstance().update(this);
	}
}
