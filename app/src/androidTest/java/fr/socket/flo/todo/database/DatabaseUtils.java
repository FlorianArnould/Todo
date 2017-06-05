package fr.socket.flo.todo.database;

import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.view.View;
import android.widget.ListView;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import java.util.concurrent.CountDownLatch;

import fr.socket.flo.todo.model.CursorMock;
import fr.socket.flo.todo.model.Project;

/**
 * @author Florian Arnould
 */
public class DatabaseUtils {
	private DatabaseUtils() {

	}

	public static void cleanDatabase() throws Exception {
		SQLiteOpenHelper helper = new DatabaseHelper(InstrumentationRegistry.getTargetContext());
		helper.onUpgrade(helper.getWritableDatabase(), 0, 1);
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
		DatabaseUtils.cleanDatabase();
		final CountDownLatch signal = new CountDownLatch(3);
		Project project1 = new Project(CursorMock.createProjectCursorMock(Project.NONE, "banana", null, null, "02-04-2017", null, 3, Color.BLACK, 0));
		OnNewObjectCreatedListener listener = new OnNewObjectCreatedListener() {
			@Override
			public void onNewObjectCreated(int objectId) {
				signal.countDown();
			}
		};
		DataManager.getInstance().save(project1, listener);
		Project project2 = new Project(CursorMock.createProjectCursorMock(Project.NONE, "orange", null, null, null, null, 1, Color.BLACK, 0));
		DataManager.getInstance().save(project2, listener);
		Project project3 = new Project(CursorMock.createProjectCursorMock(Project.NONE, "raspberry", null, null, "02-04-2017", "15:15:00", 5, Color.BLACK, 0));
		DataManager.getInstance().save(project3, listener);
		signal.await();
		Espresso.onView(ViewMatchers.withId(android.R.id.list)).check(ViewAssertions.matches(new BaseMatcher<View>() {
			@Override
			public boolean matches(Object item) {
				ListView listView = (ListView)item;
				return listView.getAdapter().getCount() == 3;
			}

			@Override
			public void describeTo(Description description) {

			}
		}));
	}
}
