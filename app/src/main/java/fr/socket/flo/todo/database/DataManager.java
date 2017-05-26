package fr.socket.flo.todo.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import fr.socket.flo.todo.model.Project;
import fr.socket.flo.todo.model.Savable;
import fr.socket.flo.todo.model.Task;

/**
 * @author Florian Arnould
 * @version 1.0
 */
public class DataManager {
	private static DataManager _dataManager;
	private final Set<OnDataChangedListener> _listeners;
	private SQLiteOpenHelper _dbOpenHelper;

	private DataManager() {
		_listeners = new HashSet<>();
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
		ObjectLoaderParameter<Project> parameter = new ObjectLoaderParameter<Project>(
				Project.TABLE_NAME,
				toArray(Project.getColumns()),
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
		ObjectLoaderParameter<Project> parameter = new ObjectLoaderParameter<Project>(
				Project.TABLE_NAME,
				toArray(Project.getColumns()),
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
		ObjectLoaderParameter<Project> parameter = new ObjectLoaderParameter<Project>(
				Project.TABLE_NAME,
				toArray(Project.getColumns()),
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
		ObjectLoaderParameter<Task> parameter = new ObjectLoaderParameter<Task>(
				Task.TABLE_NAME,
				toArray(Task.getColumns()),
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
		ObjectLoaderParameter<Task> parameter = new ObjectLoaderParameter<Task>(
				Task.TABLE_NAME,
				toArray(Task.getColumns()),
				"id=?",
				new String[]{String.valueOf(taskId)}) {
			@Override
			Task createInstance(Cursor cursor) {
				return new Task(cursor);
			}
		};
		new ObjectLoader<>(_dbOpenHelper.getWritableDatabase(), parameter, listener).execute();
	}

	public void getInProgressTasks(OnMultipleObjectsLoadedListener<Task> listener) {
		ObjectLoaderParameter<Task> parameter = new ObjectLoaderParameter<Task>(
				Task.TABLE_NAME,
				toArray(Task.getColumns()),
				"state=?",
				new String[]{Task.State.IN_PROGRESS.name()}) {
			@Override
			Task createInstance(Cursor cursor) {
				return new Task(cursor);
			}
		};
		new MultipleObjectsLoader<>(_dbOpenHelper.getWritableDatabase(), parameter, listener).execute();
	}

	public void save(Savable savable, @Nullable final OnNewObjectCreatedListener listener) {
		Saver saver = new Saver(_dbOpenHelper.getWritableDatabase(), savable.getTable(), savable.toContentValues(), objectId -> {
			getOnDataChangedListener().onDataChanged();
			if (listener != null) {
				listener.onNewObjectCreated(objectId);
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

	public void addOnDataChangedListener(OnDataChangedListener listener) {
		_listeners.add(listener);
	}

	public void removeOnDataChangedListener(OnDataChangedListener listener) {
		_listeners.remove(listener);
	}

	private void notifyListeners() {
		_listeners.forEach(OnDataChangedListener::onDataChanged);
	}

	private OnDataChangedListener getOnDataChangedListener() {
		return this::notifyListeners;
	}

	private int getCurrentTask(int projectId) {
		SQLiteDatabase db = _dbOpenHelper.getWritableDatabase();
		Cursor cursor = db.query(
				Task.TABLE_NAME,
				new String[]{"id"}, "state=? AND project_id=?",
				new String[]{Task.State.IN_PROGRESS.name(), String.valueOf(projectId)},
				null,
				null,
				"priority, deadline IS NULL");
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
		Cursor cursor = db.query(Task.TABLE_NAME, new String[]{"state", "COUNT(state)"}, "project_id=?", new String[]{String.valueOf(project.getId())}, "state", null, null);
		while (cursor.moveToNext()) {
			project.setNumberOfTasks(Task.State.valueOf(cursor.getString(0)), cursor.getInt(1));
		}
		cursor.close();
	}

	@NonNull
	private String[] toArray(@NonNull Collection<String> collection) {
		return collection.toArray(new String[collection.size()]);
	}
}
