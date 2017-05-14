package fr.socket.flo.todo.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;

import fr.socket.flo.todo.model.Project;
import fr.socket.flo.todo.model.Task;

/**
 * @author Florian Arnould
 * @version 1.0
 */
public class DataManager {
	// TODO: 14/05/17 Refactor queries
	private static DataManager _dataManager;
	private SQLiteOpenHelper _dbOpenHelper;

	private DataManager() {

	}

	public static DataManager getInstance() {
		if (_dataManager == null) {
			_dataManager = new DataManager();
		}
		return _dataManager;
	}

	public void initialize(Context context) {
		_dbOpenHelper = new ProjectsSQLiteOpenHelper(context);
	}
	// TODO: 13/05/17 get the current task with another criteria
	public void getFavorites(OnMultipleObjectsLoadedListener<Project> listener) {
		String query = "SELECT p.id, p.name, p.color, CASE WHEN t.id IS NULL THEN '-1' ELSE t.id END " +
				"FROM projects AS p " +
				"LEFT JOIN ( " +
				"SELECT id, project_id FROM tasks WHERE state = '" + Task.State.IN_PROGRESS.name() + "' GROUP BY project_id " +
				") AS t ON p.id=t.project_id " +
				"WHERE p.is_favorite=1";
		ObjectLoaderParameter<Project> parameter = new ObjectLoaderParameter<Project>(query) {
			@Override
			Project createInstance(Cursor c) {
				return new Project(c.getInt(0), c.getString(1), c.getInt(2), c.getInt(3));
			}
		};
		new MultipleObjectsLoader<>(_dbOpenHelper.getWritableDatabase(), parameter, listener).execute();
	}

	public void getAllProjects(OnMultipleObjectsLoadedListener<Project> listener) {
		String query = "SELECT p.id, p.name, p.color, CASE WHEN t.id IS NULL THEN '-1' ELSE t.id END FROM projects AS p " +
				"LEFT JOIN ( SELECT id, project_id FROM tasks WHERE state = '" + Task.State.IN_PROGRESS.name() + "' GROUP BY project_id ) AS t ON p.id=t.project_id";
		ObjectLoaderParameter<Project> parameter = new ObjectLoaderParameter<Project>(query) {
			@Override
			Project createInstance(Cursor c) {
				return new Project(c.getInt(0), c.getString(1), c.getInt(2), c.getInt(3));
			}
		};
		new MultipleObjectsLoader<>(_dbOpenHelper.getWritableDatabase(), parameter, listener).execute();
	}

	public void getTasksByProjectId(int projectId, OnMultipleObjectsLoadedListener<Task> listener) {
		String query = "SELECT id, project_id, name, color, state FROM tasks WHERE project_id='" + projectId + "'";
		ObjectLoaderParameter<Task> parameter = new ObjectLoaderParameter<Task>(query) {
			@Override
			Task createInstance(Cursor c) {
				return new Task(c.getInt(0), c.getInt(1), c.getString(2), c.getInt(3), Task.State.valueOf(c.getString(4)));
			}
		};
		new MultipleObjectsLoader<>(_dbOpenHelper.getWritableDatabase(), parameter, listener).execute();
	}

	public void getProjectById(int projectId, OnObjectLoadedListener<Project> listener) {
		String query = "SELECT p.id, p.name, p.color, CASE WHEN t.id IS NULL THEN '-1' ELSE t.id END FROM projects AS p " +
				"LEFT JOIN ( SELECT id, project_id FROM tasks WHERE state = '" + Task.State.IN_PROGRESS.name() + "' GROUP BY project_id ) AS t ON p.id=t.project_id " +
				"WHERE p.id='" + projectId + "'";
		ObjectLoaderParameter<Project> parameter = new ObjectLoaderParameter<Project>(query) {
			@Override
			Project createInstance(Cursor c) {
				return new Project(c.getInt(0), c.getString(1), c.getInt(2), c.getInt(3));
			}
		};
		new ObjectLoader<>(_dbOpenHelper.getWritableDatabase(), parameter, listener).execute();
	}

	public void getTaskById(int taskId, OnObjectLoadedListener<Task> listener) {
		String query = "SELECT id, project_id, name, color, state FROM tasks WHERE id='" + taskId + "'";
		ObjectLoaderParameter<Task> parameter = new ObjectLoaderParameter<Task>(query) {
			@Override
			Task createInstance(Cursor c) {
				return new Task(c.getInt(0), c.getInt(1), c.getString(2), c.getInt(3), Task.State.valueOf(c.getString(4)));
			}
		};
		new ObjectLoader<>(_dbOpenHelper.getWritableDatabase(), parameter, listener).execute();
	}

	public void save(Project project) {
		ContentValues values = new ContentValues();
		values.put("name", project.getName());
		values.put("color", project.getColor());
		values.put("is_favorite", false);
		new Saver(_dbOpenHelper.getWritableDatabase(), "projects", values).execute();
	}

	public void save(Task task) {
		ContentValues values = new ContentValues();
		values.put("project_id", task.getProjectId());
		values.put("name", task.getName());
		values.put("color", task.getColor());
		values.put("state", task.getState().name());
		new Saver(_dbOpenHelper.getWritableDatabase(), "tasks", values).execute();
	}

	public void closeDatabase() {
		_dbOpenHelper.close();
	}
}
