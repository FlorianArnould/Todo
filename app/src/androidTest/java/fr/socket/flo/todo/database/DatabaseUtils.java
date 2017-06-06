package fr.socket.flo.todo.database;

import android.graphics.Color;
import android.support.test.InstrumentationRegistry;

import java.util.concurrent.CountDownLatch;

import fr.socket.flo.todo.model.CursorMock;
import fr.socket.flo.todo.model.Project;
import fr.socket.flo.todo.model.Task;

/**
 * @author Florian Arnould
 */
public class DatabaseUtils {
	private DatabaseUtils() {

	}

	public static void cleanDatabase() throws Exception {
		InstrumentationRegistry.getTargetContext().deleteDatabase("ProjectsDatabase.db");
		DataManager.getInstance().initialize(InstrumentationRegistry.getTargetContext());
	}

	public static void insertBanana() throws Exception {
		cleanDatabase();
		final CountDownLatch signal = new CountDownLatch(1);
		Project banana = new Project(CursorMock.createProjectCursorMock(Project.NONE, "banana", null, null, "02-04-2017", null, 3, Color.BLACK, 0));
		DataManager.getInstance().save(banana, new OnNewObjectCreatedListener() {
			@Override
			public void onNewObjectCreated(int objectId) {
				signal.countDown();
			}
		});
		signal.await();
	}

	public static void insertFruits() throws Exception {
		cleanDatabase();
		final CountDownLatch signal = new CountDownLatch(3);
		Project banana = new Project(CursorMock.createProjectCursorMock(Project.NONE, "banana", null, null, "02-04-2017", null, 3, Color.BLACK, 0));
		OnNewObjectCreatedListener listener = new OnNewObjectCreatedListener() {
			@Override
			public void onNewObjectCreated(int objectId) {
				signal.countDown();
			}
		};
		DataManager.getInstance().save(banana, listener);
		Project orange = new Project(CursorMock.createProjectCursorMock(Project.NONE, "orange", null, null, null, null, 1, Color.BLACK, 0));
		DataManager.getInstance().save(orange, listener);
		Project raspberry = new Project(CursorMock.createProjectCursorMock(Project.NONE, "raspberry", null, null, "02-04-2017", "15:15:00", 5, Color.BLACK, 0));
		DataManager.getInstance().save(raspberry, listener);
		signal.await();
	}

	public static void insertColors(int parentId) throws Exception {
		final CountDownLatch signal = new CountDownLatch(3);
		OnNewObjectCreatedListener listener = new OnNewObjectCreatedListener() {
			@Override
			public void onNewObjectCreated(int objectId) {
				signal.countDown();
			}
		};
		Task red = new Task(CursorMock.createTaskCursorMock(Task.NONE, "red", null, null, "02-04-2017", null, 3, parentId, Task.State.WAITING.name()));
		DataManager.getInstance().save(red, listener);
		Task green = new Task(CursorMock.createTaskCursorMock(Task.NONE, "green", null, null, null, null, 1, parentId, Task.State.WAITING.name()));
		DataManager.getInstance().save(green, listener);
		Task blue = new Task(CursorMock.createTaskCursorMock(Task.NONE, "blue", null, null, "02-04-2017", "15:15:00", 5, parentId, Task.State.WAITING.name()));
		DataManager.getInstance().save(blue, listener);
		signal.await();
	}
}
