package fr.socket.flo.todo.storage.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import fr.socket.flo.todo.storage.Project;
import fr.socket.flo.todo.storage.Task;

/**
 * @author Florian Arnould
 * @version 1.0
 */
public class Projects {
	private static Projects _projects;
	private SQLiteDatabase _db;
	private SQLiteOpenHelper _dbOpenHelper;

	private Projects(Context context) {
		_dbOpenHelper = new ProjectsSQLiteOpenHelper(context);
		_db = _dbOpenHelper.getWritableDatabase();
	}

	public static Projects getInstance(Context context) {
		if (_projects == null) {
			_projects = new Projects(context);
		}
		return _projects;
	}

	public void getFavorites(OnMultipleObjectsLoadedListener<Project> listener) {
		String query = "SELECT id, name, color FROM projects WHERE is_favorite=1";
		LoaderParameter<Project> parameter = new LoaderParameter<Project>(query) {
			@Override
			Project createInstance(Cursor c) {
				return new Project(c.getInt(0), c.getString(1), c.getInt(2));
			}
		};
		new MultipleObjectsLoader<>(_db, parameter, listener).execute();
	}

	public void getAllProjects(OnMultipleObjectsLoadedListener<Project> listener) {
		String query = "SELECT id, name, color FROM projects";
		LoaderParameter<Project> parameter = new LoaderParameter<Project>(query) {
			@Override
			Project createInstance(Cursor c) {
				return new Project(c.getInt(0), c.getString(1), c.getInt(2));
			}
		};
		new MultipleObjectsLoader<>(_db, parameter, listener).execute();
	}

	public void save(Project project) {
		ContentValues values = new ContentValues();
		values.put("name", project.getName());
		values.put("color", project.getColor());
		values.put("is_favorite", false);
		new Saver(_db, "projects", values).execute();
	}

	public void save(Task task) {
		ContentValues values = new ContentValues();
		values.put("name", task.getName());
		values.put("color", task.getColor());
		new Saver(_db, "tasks", values).execute();
	}

	private abstract class LoaderParameter<E> {
		private String _query;

		LoaderParameter(String query) {
			_query = query;
		}

		String getQuery() {
			return _query;
		}

		abstract E createInstance(Cursor cursor);
	}

	private class MultipleObjectsLoader<E> extends AsyncTask<Void, Void, Void> {
		private final SQLiteDatabase _db;
		private final LoaderParameter<E> _parameter;
		private final OnMultipleObjectsLoadedListener<E> _listener;
		private final List<E> _list;

		MultipleObjectsLoader(SQLiteDatabase db, LoaderParameter<E> parameter, OnMultipleObjectsLoadedListener<E> listener) {
			_db = db;
			_parameter = parameter;
			_listener = listener;
			_list = new ArrayList<>();
		}

		@Override
		protected Void doInBackground(Void... params) {
			Cursor c = _db.rawQuery(_parameter.getQuery(), null);
			while (c.moveToNext()) {
				_list.add(_parameter.createInstance(c));
			}
			c.close();
			return null;
		}

		@Override
		protected void onPostExecute(Void param) {
			_listener.OnObjectsLoaded(_list);
		}
	}

	private class ObjectLoader<E> extends AsyncTask<Void, Void, Void> {
		private final SQLiteDatabase _db;
		private final LoaderParameter<E> _parameter;
		private final OnObjectLoadedListener<E> _listener;
		private E _object;

		ObjectLoader(SQLiteDatabase db, LoaderParameter<E> parameter, OnObjectLoadedListener<E> listener) {
			_db = db;
			_parameter = parameter;
			_listener = listener;
		}

		@Override
		protected Void doInBackground(Void... params) {
			Cursor c = _db.rawQuery(_parameter.getQuery(), null);
			if (c.moveToNext()) {
				_object = _parameter.createInstance(c);
			}
			c.close();
			return null;
		}

		@Override
		protected void onPostExecute(Void param) {
			_listener.OnObjectLoaded(_object);
		}
	}

	private class Saver extends AsyncTask<Void, Void, Void> {
		private final SQLiteDatabase _db;
		private final String _tableName;
		private final ContentValues _values;

		Saver(SQLiteDatabase db, String tableName, ContentValues values){
			_db = db;
			_tableName = tableName;
			_values = values;
		}

		@Override
		protected Void doInBackground(Void... params) {
			_db.insert(_tableName, null, _values);
			return null;
		}
	}
}
