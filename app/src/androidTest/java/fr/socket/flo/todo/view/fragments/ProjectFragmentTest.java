package fr.socket.flo.todo.view.fragments;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.widget.ListView;

import junit.framework.Assert;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import fr.socket.flo.todo.R;
import fr.socket.flo.todo.database.DataManager;
import fr.socket.flo.todo.database.DatabaseUtils;
import fr.socket.flo.todo.database.OnMultipleObjectsLoadedListener;
import fr.socket.flo.todo.database.OnNewObjectCreatedListener;
import fr.socket.flo.todo.model.Nameable;
import fr.socket.flo.todo.model.Project;
import fr.socket.flo.todo.model.Task;
import fr.socket.flo.todo.view.activity.MainActivity;

/**
 * @author Florian Arnould
 */
@RunWith(AndroidJUnit4.class)
public class ProjectFragmentTest {
	@Rule
	public ActivityTestRule<MainActivity> _activityTestRule = new ActivityTestRule<>(MainActivity.class);
	private int _id;

	@Test
	public void addTaskTest() throws Exception {
		DatabaseUtils.insertBanana();
		Espresso.onData(Matchers.anything()).inAdapterView(ViewMatchers.withId(android.R.id.list)).atPosition(0).perform(ViewActions.click());
		Espresso.onView(ViewMatchers.withId(android.R.id.list)).check(ViewAssertions.matches(new BaseMatcher<View>() {
			@Override
			public boolean matches(Object item) {
				ListView listView = (ListView)item;
				return listView.getAdapter().getCount() == 0;
			}

			@Override
			public void describeTo(Description description) {

			}
		}));
		Espresso.onView(ViewMatchers.withId(R.id.fab)).perform(ViewActions.click());
		Espresso.onView(ViewMatchers.withId(R.id.name)).perform(ViewActions.typeText("test"));
		Espresso.onView(ViewMatchers.withId(android.R.id.button1)).perform(ViewActions.click());
		Espresso.onView(ViewMatchers.withId(android.R.id.list)).check(ViewAssertions.matches(new BaseMatcher<View>() {
			@Override
			public boolean matches(Object item) {
				ListView listView = (ListView)item;
				return listView.getAdapter().getCount() == 1;
			}

			@Override
			public void describeTo(Description description) {

			}
		}));
	}

	@Test
	public void openEditTest() throws Exception {
		DatabaseUtils.insertBanana();
		Espresso.onData(Matchers.anything()).inAdapterView(ViewMatchers.withId(android.R.id.list)).atPosition(0).perform(ViewActions.click());
		Espresso.onView(ViewMatchers.withId(R.id.action_edit)).perform(ViewActions.click());
		Espresso.onView(ViewMatchers.withId(R.id.name)).check(ViewAssertions.matches(ViewMatchers.withText("banana")));
	}

	@Test
	public void onBackPressedFromProjectFragmentTest() throws Exception {
		DatabaseUtils.insertBanana();
		Espresso.onData(Matchers.anything()).inAdapterView(ViewMatchers.withId(android.R.id.list)).atPosition(0).perform(ViewActions.click());
		Espresso.onView(ViewMatchers.withId(R.id.action_edit)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
		Espresso.pressBack();
		Espresso.onView(ViewMatchers.withText("banana")).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
		Espresso.onView(ViewMatchers.withId(R.id.action_edit)).check(ViewAssertions.matches(Matchers.not(ViewMatchers.isEnabled())));
	}

	@Test
	public void changeTaskStateTest() throws Exception {
		DatabaseUtils.cleanDatabase();
		final CountDownLatch signal = new CountDownLatch(1);
		Project.newProject("banana", new OnNewObjectCreatedListener() {
			@Override
			public void onNewObjectCreated(int projectId) {
				Task.newTask(projectId, "green", new OnNewObjectCreatedListener() {
					@Override
					public void onNewObjectCreated(int taskId) {
						signal.countDown();
					}
				});
			}
		});
		signal.await();
		Espresso.onData(Matchers.anything()).inAdapterView(ViewMatchers.withId(android.R.id.list)).atPosition(0).perform(ViewActions.click());
		Espresso.onView(ViewMatchers.withText(R.string.waiting));
		Espresso.onData(Matchers.anything()).inAdapterView(ViewMatchers.withId(android.R.id.list)).atPosition(0).onChildView(ViewMatchers.withId(R.id.state)).perform(ViewActions.click());
		Espresso.onView(ViewMatchers.withText(R.string.in_progress));
		Espresso.onData(Matchers.anything()).inAdapterView(ViewMatchers.withId(android.R.id.list)).atPosition(0).onChildView(ViewMatchers.withId(R.id.state)).perform(ViewActions.click());
		Espresso.onView(ViewMatchers.withText(R.string.completed));
		Espresso.onData(Matchers.anything()).inAdapterView(ViewMatchers.withId(android.R.id.list)).atPosition(0).onChildView(ViewMatchers.withId(R.id.state)).perform(ViewActions.click());
		Espresso.onView(ViewMatchers.withText(R.string.waiting));
	}

	@Test
	public void orderTaskByNameTest() throws Exception {
		DatabaseUtils.insertBanana();
		final CountDownLatch signal = new CountDownLatch(1);
		DataManager.getInstance().getAllProjects(new OnMultipleObjectsLoadedListener<Project>() {
			@Override
			public void onObjectsLoaded(List<Project> projects) {
				_id = projects.get(0).getId();
				signal.countDown();
			}
		});
		signal.await();
		DatabaseUtils.insertColors(_id);
		Espresso.onData(Matchers.anything()).inAdapterView(ViewMatchers.withId(android.R.id.list)).atPosition(0).perform(ViewActions.click());
		Espresso.onView(ViewMatchers.withId(R.id.action_sort)).perform(ViewActions.click());
		Espresso.onView(ViewMatchers.withText(R.string.name)).perform(ViewActions.click());
		Espresso.onView(ViewMatchers.withId(android.R.id.list)).check(ViewAssertions.matches(new BaseMatcher<View>() {
			@Override
			public void describeTo(Description description) {

			}

			@Override
			public boolean matches(Object item) {
				ListView listView = (ListView)item;
				Assert.assertEquals(3, listView.getAdapter().getCount());
				Nameable first = (Nameable)listView.getAdapter().getItem(0);
				Nameable second = (Nameable)listView.getAdapter().getItem(1);
				Nameable third = (Nameable)listView.getAdapter().getItem(2);
				Assert.assertEquals("blue", first.getName());
				Assert.assertEquals("green", second.getName());
				Assert.assertEquals("red", third.getName());
				return true;
			}
		}));
	}

	@Test
	public void orderTaskByPriorityTest() throws Exception {
		DatabaseUtils.insertBanana();
		final CountDownLatch signal = new CountDownLatch(1);
		DataManager.getInstance().getAllProjects(new OnMultipleObjectsLoadedListener<Project>() {
			@Override
			public void onObjectsLoaded(List<Project> projects) {
				_id = projects.get(0).getId();
				signal.countDown();
			}
		});
		signal.await();
		DatabaseUtils.insertColors(_id);
		Espresso.onData(Matchers.anything()).inAdapterView(ViewMatchers.withId(android.R.id.list)).atPosition(0).perform(ViewActions.click());
		Espresso.onView(ViewMatchers.withId(R.id.action_sort)).perform(ViewActions.click());
		Espresso.onView(ViewMatchers.withText(R.string.priority)).perform(ViewActions.click());
		Espresso.onView(ViewMatchers.withId(android.R.id.list)).check(ViewAssertions.matches(new BaseMatcher<View>() {
			@Override
			public void describeTo(Description description) {

			}

			@Override
			public boolean matches(Object item) {
				ListView listView = (ListView)item;
				Assert.assertEquals(3, listView.getAdapter().getCount());
				Nameable first = (Nameable)listView.getAdapter().getItem(0);
				Nameable second = (Nameable)listView.getAdapter().getItem(1);
				Nameable third = (Nameable)listView.getAdapter().getItem(2);
				Assert.assertEquals("green", first.getName());
				Assert.assertEquals("red", second.getName());
				Assert.assertEquals("blue", third.getName());
				return true;
			}
		}));
	}

	@Test
	public void orderTaskByDeadlineTest() throws Exception {
		DatabaseUtils.insertBanana();
		final CountDownLatch signal = new CountDownLatch(1);
		DataManager.getInstance().getAllProjects(new OnMultipleObjectsLoadedListener<Project>() {
			@Override
			public void onObjectsLoaded(List<Project> projects) {
				_id = projects.get(0).getId();
				signal.countDown();
			}
		});
		signal.await();
		DatabaseUtils.insertColors(_id);
		Espresso.onData(Matchers.anything()).inAdapterView(ViewMatchers.withId(android.R.id.list)).atPosition(0).perform(ViewActions.click());
		Espresso.onView(ViewMatchers.withId(R.id.action_sort)).perform(ViewActions.click());
		Espresso.onView(ViewMatchers.withText(R.string.deadline)).perform(ViewActions.click());
		Espresso.onView(ViewMatchers.withId(android.R.id.list)).check(ViewAssertions.matches(new BaseMatcher<View>() {
			@Override
			public void describeTo(Description description) {

			}

			@Override
			public boolean matches(Object item) {
				ListView listView = (ListView)item;
				Assert.assertEquals(3, listView.getAdapter().getCount());
				Nameable first = (Nameable)listView.getAdapter().getItem(0);
				Nameable second = (Nameable)listView.getAdapter().getItem(1);
				Nameable third = (Nameable)listView.getAdapter().getItem(2);
				Assert.assertEquals("blue", first.getName());
				Assert.assertEquals("red", second.getName());
				Assert.assertEquals("green", third.getName());
				return true;
			}
		}));
	}
}
