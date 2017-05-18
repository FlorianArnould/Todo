package fr.socket.flo.todo.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.SparseArray;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-mm-yyyy HH:mm:ss", Locale.FRENCH);

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
		String query = "SELECT id, name, color, deadline, priority, is_favorite FROM projects " +
				"WHERE is_favorite=1";
		ObjectLoaderParameter<Project> parameter = new ObjectLoaderParameter<Project>(query) {
			@Override
			Project createInstance(Cursor c) {
				return new Project(c.getInt(0), c.getString(1), c.getInt(2), stringToDate(c.getString(3)), c.getInt(4), getCurrentTask(c.getInt(0)), c.getInt(5) == 1);
			}
		};
		new MultipleObjectsLoader<>(_dbOpenHelper.getWritableDatabase(), parameter, listener).execute();
	}

	public void getAllProjects(OnMultipleObjectsLoadedListener<Project> listener) {
		String query = "SELECT id, name, color, deadline, priority, is_favorite FROM projects";
		ObjectLoaderParameter<Project> parameter = new ObjectLoaderParameter<Project>(query) {
			@Override
			Project createInstance(Cursor c) {
				return new Project(c.getInt(0), c.getString(1), c.getInt(2), stringToDate(c.getString(3)), c.getInt(4), getCurrentTask(c.getInt(0)), c.getInt(5) == 1);
			}
		};
		new MultipleObjectsLoader<>(_dbOpenHelper.getWritableDatabase(), parameter, listener).execute();
	}

	public void getTasksByProjectId(int projectId, OnMultipleObjectsLoadedListener<Task> listener) {
		String query = "SELECT id, project_id, name, color, deadline, priority, state FROM tasks WHERE project_id='" + projectId + "'";
		ObjectLoaderParameter<Task> parameter = new ObjectLoaderParameter<Task>(query) {
			@Override
			Task createInstance(Cursor c) {
				return new Task(c.getInt(0), c.getInt(1), c.getString(2), c.getInt(3), stringToDate(c.getString(4)), c.getInt(5), Task.State.valueOf(c.getString(6)));
			}
		};
		new MultipleObjectsLoader<>(_dbOpenHelper.getWritableDatabase(), parameter, listener).execute();
	}

	public void getProjectById(int projectId, OnObjectLoadedListener<Project> listener) {
		String query = "SELECT id, name, color, deadline, priority, is_favorite FROM projects " +
				"WHERE id='" + projectId + "'";
		ObjectLoaderParameter<Project> parameter = new ObjectLoaderParameter<Project>(query) {
			@Override
			Project createInstance(Cursor c) {
				return new Project(c.getInt(0), c.getString(1), c.getInt(2), stringToDate(c.getString(3)), c.getInt(4), getCurrentTask(c.getInt(0)), c.getInt(5) == 1);
			}
		};
		new ObjectLoader<>(_dbOpenHelper.getWritableDatabase(), parameter, listener).execute();
	}

	public void getTaskById(int taskId, OnObjectLoadedListener<Task> listener) {
		String query = "SELECT id, project_id, name, color, deadline, priority, state FROM tasks WHERE id='" + taskId + "'";
		ObjectLoaderParameter<Task> parameter = new ObjectLoaderParameter<Task>(query) {
			@Override
			Task createInstance(Cursor c) {
				return new Task(c.getInt(0), c.getInt(1), c.getString(2), c.getInt(3), stringToDate(c.getString(4)), c.getInt(5), Task.State.valueOf(c.getString(6)));
			}
		};
		new ObjectLoader<>(_dbOpenHelper.getWritableDatabase(), parameter, listener).execute();
	}

	public void save(Project project) {
		ContentValues values = new ContentValues();
		values.put("name", project.getName());
		values.put("color", project.getColor());
		if(project.hasDeadline()) {
			values.put("deadline", DATE_FORMAT.format(project.getDeadline()));
		}else{
			values.putNull("deadline");
		}
		values.put("priority", project.getPriority());
		values.put("is_favorite", project.isFavorite());
		Saver saver = new Saver(_dbOpenHelper.getWritableDatabase(), "projects", values, getOnDataChangedListener());
		saver.execute();
	}

	public void save(Task task) {
		ContentValues values = new ContentValues();
		values.put("project_id", task.getProjectId());
		values.put("name", task.getName());
		values.put("color", task.getColor());
		if(task.hasDeadline()) {
			values.put("deadline", DATE_FORMAT.format(task.getDeadline()));
		}else{
			values.putNull("deadline");
		}
		values.put("priority", task.getPriority());
		values.put("state", task.getState().name());
		Saver saver = new Saver(_dbOpenHelper.getWritableDatabase(), "tasks", values, getOnDataChangedListener());
		saver.execute();
	}

	public void update(Project project) {
		ContentValues values = new ContentValues();
		values.put("name", project.getName());
		values.put("color", project.getColor());
		if(project.hasDeadline()) {
			values.put("deadline", DATE_FORMAT.format(project.getDeadline()));
		}else{
			values.putNull("deadline");
		}
		values.put("priority", project.getPriority());
		values.put("is_favorite", project.isFavorite());
		Updater updater = new Updater(_dbOpenHelper.getWritableDatabase(), "projects", project.getId(), values, getOnDataChangedListener());
		updater.execute();
	}

	public void update(Task task) {
		ContentValues values = new ContentValues();
		values.put("name", task.getName());
		values.put("color", task.getColor());
		if(task.hasDeadline()) {
			values.put("deadline", DATE_FORMAT.format(task.getDeadline()));
		}else{
			values.putNull("deadline");
		}
		values.put("priority", task.getPriority());
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

	// TODO: 13/05/17 get the current task with another criteria
	private int getCurrentTask(int projectId){
		String query = "SELECT id FROM tasks " +
				"WHERE state = '" + Task.State.IN_PROGRESS.name() + "' AND project_id='" + projectId + "'";
		SQLiteDatabase db = _dbOpenHelper.getWritableDatabase();
		Cursor cursor = db.rawQuery(query, null, null);
		int taskId;
		if(cursor.moveToNext()){
			taskId = cursor.getInt(0);
		}else{
			taskId =  Project.NONE;
		}
		cursor.close();
		return taskId;
	}

	private Date stringToDate(String string){
		Date date;
		try{
			date = DATE_FORMAT.parse(string);
		}catch (Exception e){
			date = null;
		}
		return date;
	}
}
