package fr.socket.flo.todo.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.SparseArray;

import java.util.Collection;

import fr.socket.flo.todo.model.Project;
import fr.socket.flo.todo.model.Savable;
import fr.socket.flo.todo.model.Task;

/**
 * @author Florian Arnould
 * @version 1.0
 */
public class DataManager {
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

	public void getFavorites(OnMultipleObjectsLoadedListener<Project> listener) {
		Collection<String> collection = Project.getColumns();
		String[] columns = collection.toArray(new String[collection.size()]);
		ObjectLoaderParameter<Project> parameter = new ObjectLoaderParameter<Project>(
				Project.TABLE_NAME,
				columns,
				"is_favorite=1",
				null) {
			@Override
			Project createInstance(Cursor cursor) {
				Project project = new Project(cursor);
				project.setCurrentTaskId(getCurrentTask(project.getId()));
				setProjectTasks(project);
				return project;
			}
		};
		new MultipleObjectsLoader<>(_dbOpenHelper.getWritableDatabase(), parameter, listener).execute();
	}

	public void getAllProjects(OnMultipleObjectsLoadedListener<Project> listener) {
		Collection<String> collection = Project.getColumns();
		String[] columns = collection.toArray(new String[collection.size()]);
		ObjectLoaderParameter<Project> parameter = new ObjectLoaderParameter<Project>(
				Project.TABLE_NAME,
				columns,
				null,
				null) {
			@Override
			Project createInstance(Cursor cursor) {
				Project project = new Project(cursor);
				project.setCurrentTaskId(getCurrentTask(project.getId()));
				setProjectTasks(project);
				return project;
			}
		};
		new MultipleObjectsLoader<>(_dbOpenHelper.getWritableDatabase(), parameter, listener).execute();
	}

	public void getProjectById(int projectId, OnObjectLoadedListener<Project> listener) {
		Collection<String> collection = Project.getColumns();
		String[] columns = collection.toArray(new String[collection.size()]);
		ObjectLoaderParameter<Project> parameter = new ObjectLoaderParameter<Project>(
				Project.TABLE_NAME,
				columns,
				"id=?",
				new String[]{String.valueOf(projectId)}) {
			@Override
			Project createInstance(Cursor cursor) {
				Project project = new Project(cursor);
				project.setCurrentTaskId(getCurrentTask(project.getId()));
				setProjectTasks(project);
				return project;
			}
		};
		new ObjectLoader<>(_dbOpenHelper.getWritableDatabase(), parameter, listener).execute();
	}

	public void getTasksByProjectId(int projectId, OnMultipleObjectsLoadedListener<Task> listener) {
		Collection<String> collection = Task.getColumns();
		String[] columns = collection.toArray(new String[collection.size()]);
		ObjectLoaderParameter<Task> parameter = new ObjectLoaderParameter<Task>(
				Task.TABLE_NAME,
				columns,
				"project_id=?",
				new String[]{String.valueOf(projectId)}) {
			@Override
			Task createInstance(Cursor cursor) {
				return new Task(cursor);
			}
		};
		new MultipleObjectsLoader<>(_dbOpenHelper.getWritableDatabase(), parameter, listener).execute();
	}

	public void getTaskById(int taskId, OnObjectLoadedListener<Task> listener) {
		Collection<String> collection = Task.getColumns();
		String[] columns = collection.toArray(new String[collection.size()]);
		ObjectLoaderParameter<Task> parameter = new ObjectLoaderParameter<Task>(
				Task.TABLE_NAME,
				columns,
				"id=?",
				new String[]{String.valueOf(taskId)}) {
			@Override
			Task createInstance(Cursor cursor) {
				return new Task(cursor);
			}
		};
		new ObjectLoader<>(_dbOpenHelper.getWritableDatabase(), parameter, listener).execute();
	}

	public void save(Savable savable, @Nullable final OnNewObjectCreatedListener listener) {
		Saver saver = new Saver(_dbOpenHelper.getWritableDatabase(), savable.getTable(), savable.toContentValues(), new OnNewObjectCreatedListener() {
			@Override
			public void onNewObjectCreated(int objectId) {
				getOnDataChangedListener().onDataChanged();
				if (listener != null) {
					listener.onNewObjectCreated(objectId);
				}
			}
		});
		saver.execute();
	}

	public void update(Savable savable) {
		Updater updater = new Updater(_dbOpenHelper.getWritableDatabase(), savable.getTable(), savable.getId(), savable.toContentValues(), getOnDataChangedListener());
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

	// TODO: 13/05/17 get the current task with another criteria
	private int getCurrentTask(int projectId) {
		String query = "SELECT id FROM tasks " +
				"WHERE state = '" + Task.State.IN_PROGRESS.name() + "' AND project_id='" + projectId + "'";
		SQLiteDatabase db = _dbOpenHelper.getWritableDatabase();
		Cursor cursor = db.rawQuery(query, null, null);
		int taskId;
		if (cursor.moveToNext()) {
			taskId = cursor.getInt(0);
		} else {
			taskId = Project.NONE;
		}
		cursor.close();
		return taskId;
	}

	private void setProjectTasks(Project project) {
		SQLiteDatabase db = _dbOpenHelper.getWritableDatabase();
		Cursor cursor = db.query(Task.TABLE_NAME, new String[]{"state", "COUNT(state)"}, null, null, "state", null, null);
		while (cursor.moveToNext()) {
			project.setNumberOfTasks(Task.State.valueOf(cursor.getString(0)), cursor.getInt(1));
		}
		cursor.close();
	}
}
