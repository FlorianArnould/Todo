package fr.socket.flo.todo.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.SparseArray;

import fr.socket.flo.todo.model.Project;
import fr.socket.flo.todo.model.Task;

/**
 * @author Florian Arnould
 * @version 1.0
 */
public class DataManager {
	// TODO: 14/05/17 Refactor queries
	private static DataManager _dataManager;
	private final SparseArray<OnDataChangedListener> _listeners;
	private SQLiteOpenHelper _dbOpenHelper;

	private DataManager() {
		_listeners = new SparseArray<>();
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
		String query = "SELECT p.id, p.name, p.color, CASE WHEN t.id IS NULL THEN '-1' ELSE t.id END, p.is_favorite " +
				"FROM projects AS p " +
				"LEFT JOIN ( " +
				"SELECT id, project_id FROM tasks WHERE state = '" + Task.State.IN_PROGRESS.name() + "' GROUP BY project_id " +
				") AS t ON p.id=t.project_id " +
				"WHERE p.is_favorite=1";
		ObjectLoaderParameter<Project> parameter = new ObjectLoaderParameter<Project>(query) {
			@Override
			Project createInstance(Cursor c) {
				// TODO: 16/05/17 store deadline and priority value in database
				return new Project(c.getInt(0), c.getString(1), c.getInt(2), null, 0, c.getInt(3), c.getInt(4) == 1);
			}
		};
		new MultipleObjectsLoader<>(_dbOpenHelper.getWritableDatabase(), parameter, listener).execute();
	}

	public void getAllProjects(OnMultipleObjectsLoadedListener<Project> listener) {
		String query = "SELECT p.id, p.name, p.color, CASE WHEN t.id IS NULL THEN '-1' ELSE t.id END, p.is_favorite FROM projects AS p " +
				"LEFT JOIN ( SELECT id, project_id FROM tasks WHERE state = '" + Task.State.IN_PROGRESS.name() + "' GROUP BY project_id ) AS t ON p.id=t.project_id";
		ObjectLoaderParameter<Project> parameter = new ObjectLoaderParameter<Project>(query) {
			@Override
			Project createInstance(Cursor c) {
				return new Project(c.getInt(0), c.getString(1), c.getInt(2), null, 0, c.getInt(3), c.getInt(4) == 1);
			}
		};
		new MultipleObjectsLoader<>(_dbOpenHelper.getWritableDatabase(), parameter, listener).execute();
	}

	public void getTasksByProjectId(int projectId, OnMultipleObjectsLoadedListener<Task> listener) {
		String query = "SELECT id, project_id, name, color, state FROM tasks WHERE project_id='" + projectId + "'";
		ObjectLoaderParameter<Task> parameter = new ObjectLoaderParameter<Task>(query) {
			@Override
			Task createInstance(Cursor c) {
				return new Task(c.getInt(0), c.getInt(1), c.getString(2), c.getInt(3), null, 0, Task.State.valueOf(c.getString(4)));
			}
		};
		new MultipleObjectsLoader<>(_dbOpenHelper.getWritableDatabase(), parameter, listener).execute();
	}

	public void getProjectById(int projectId, OnObjectLoadedListener<Project> listener) {
		String query = "SELECT p.id, p.name, p.color, CASE WHEN t.id IS NULL THEN '-1' ELSE t.id END, p.is_favorite FROM projects AS p " +
				"LEFT JOIN ( SELECT id, project_id FROM tasks WHERE state = '" + Task.State.IN_PROGRESS.name() + "' GROUP BY project_id ) AS t ON p.id=t.project_id " +
				"WHERE p.id='" + projectId + "'";
		ObjectLoaderParameter<Project> parameter = new ObjectLoaderParameter<Project>(query) {
			@Override
			Project createInstance(Cursor c) {
				return new Project(c.getInt(0), c.getString(1), c.getInt(2), null, 0, c.getInt(3), c.getInt(4) == 1);
			}
		};
		new ObjectLoader<>(_dbOpenHelper.getWritableDatabase(), parameter, listener).execute();
	}

	public void getTaskById(int taskId, OnObjectLoadedListener<Task> listener) {
		String query = "SELECT id, project_id, name, color, state FROM tasks WHERE id='" + taskId + "'";
		ObjectLoaderParameter<Task> parameter = new ObjectLoaderParameter<Task>(query) {
			@Override
			Task createInstance(Cursor c) {
				return new Task(c.getInt(0), c.getInt(1), c.getString(2), c.getInt(3), null, 0, Task.State.valueOf(c.getString(4)));
			}
		};
		new ObjectLoader<>(_dbOpenHelper.getWritableDatabase(), parameter, listener).execute();
	}

	public void save(Project project) {
		ContentValues values = new ContentValues();
		values.put("name", project.getName());
		values.put("color", project.getColor());
		values.put("is_favorite", project.isFavorite());
		Saver saver = new Saver(_dbOpenHelper.getWritableDatabase(), "projects", values, getOnDataChangedListener());
		saver.execute();
	}

	public void save(Task task) {
		ContentValues values = new ContentValues();
		values.put("project_id", task.getProjectId());
		values.put("name", task.getName());
		values.put("color", task.getColor());
		values.put("state", task.getState().name());
		Saver saver = new Saver(_dbOpenHelper.getWritableDatabase(), "tasks", values, getOnDataChangedListener());
		saver.execute();
	}

	public void update(Project project) {
		ContentValues values = new ContentValues();
		values.put("name", project.getName());
		values.put("color", project.getColor());
		values.put("is_favorite", project.isFavorite());
		Updater updater = new Updater(_dbOpenHelper.getWritableDatabase(), "projects", project.getId(), values, getOnDataChangedListener());
		updater.execute();
	}

	public void update(Task task) {
		ContentValues values = new ContentValues();
		values.put("name", task.getName());
		values.put("color", task.getColor());
		values.put("state", task.getState().name());
		Updater updater = new Updater(_dbOpenHelper.getWritableDatabase(), "tasks", task.getId(), values, getOnDataChangedListener());
		updater.execute();
	}

	public void closeDatabase() {
		_dbOpenHelper.close();
	}

	public int addOnDataChangedListener(OnDataChangedListener listener) {
		int id = _listeners.size();
		_listeners.put(id, listener);
		return id;
	}

	public void removeOnDataChangedListener(int listenerId) {
		_listeners.delete(listenerId);
	}

	private void notifyListeners() {
		for (int i = 0; i < _listeners.size(); i++) {
			_listeners.valueAt(i).onDataChanged();
		}
	}

	private OnDataChangedListener getOnDataChangedListener() {
		return new OnDataChangedListener() {
			@Override
			public void onDataChanged() {
				notifyListeners();
			}
		};
	}
}
