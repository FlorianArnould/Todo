package fr.socket.flo.todo.view.activity;

import android.graphics.Color;
import android.support.design.widget.NavigationView;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;

import junit.framework.Assert;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;

import fr.socket.flo.todo.R;
import fr.socket.flo.todo.database.DataManager;
import fr.socket.flo.todo.database.DatabaseUtils;
import fr.socket.flo.todo.database.OnNewObjectCreatedListener;
import fr.socket.flo.todo.model.CursorMock;
import fr.socket.flo.todo.model.Nameable;
import fr.socket.flo.todo.model.Project;
import fr.socket.flo.todo.view.fragments.adapters.UpdatableAdapter;

import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;

/**
 * @author Florian Arnould
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
	@Rule
	public ActivityTestRule<MainActivity> _activityTestRule = new ActivityTestRule<>(MainActivity.class);

	@Test
	public void addProjectTest() throws Exception {
		DatabaseUtils.cleanDatabase();
		ListFragment fragment = (ListFragment)_activityTestRule.getActivity().getSupportFragmentManager().getFragments().get(0);
		UpdatableAdapter adapter = (UpdatableAdapter)fragment.getListAdapter();
		adapter.update();
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
	public void setProjectAsFavoriteTest() throws Exception {
		DatabaseUtils.cleanDatabase();
		Project.newProject("Test", null);
		Espresso.onData(Matchers.anything()).inAdapterView(ViewMatchers.withId(android.R.id.list)).atPosition(0).onChildView(ViewMatchers.withId(R.id.is_favorite)).perform(ViewActions.click());
		Espresso.onView(ViewMatchers.withId(R.id.nav_view)).check(ViewAssertions.matches(new BaseMatcher<View>() {
			@Override
			public boolean matches(Object item) {
				NavigationView navView = (NavigationView)item;
				return navView.getMenu().getItem(1).getSubMenu().size() == 1;
			}

			@Override
			public void describeTo(Description description) {

			}
		}));
	}

	@Test
	public void newProjectSnackBarTest() throws Exception {
		addProjectTest();
		Espresso.onView(Matchers.allOf(ViewMatchers.withId(android.support.design.R.id.snackbar_text), ViewMatchers.withText(R.string.new_project_created))).check(ViewAssertions.matches(isDisplayed()));
		Espresso.onView(ViewMatchers.withId(android.support.design.R.id.snackbar_action)).perform(ViewActions.click());
		Espresso.onView(ViewMatchers.withId(R.id.name)).check(ViewAssertions.matches(ViewMatchers.withText("test")));
	}

	@Test
	public void displayAboutScreenTest() {
		Espresso.onView(ViewMatchers.withId(R.id.drawer_layout)).perform(DrawerActions.open());
		Espresso.onView(ViewMatchers.withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.about));
		Espresso.onView(ViewMatchers.withText(R.string.action_about)).check(ViewAssertions.matches(isDisplayed()));
	}

	@Test
	public void displaySettingsScreenTest() {
		Espresso.onView(ViewMatchers.withId(R.id.drawer_layout)).perform(DrawerActions.open());
		Espresso.onView(ViewMatchers.withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.settings));
		Espresso.onView(ViewMatchers.withText(R.string.action_settings)).check(ViewAssertions.matches(isDisplayed()));
	}

	@Test
	public void onBackPressedOnSearchBarTest() {
		Espresso.onView(ViewMatchers.withId(R.id.action_search)).perform(ViewActions.click());
		Espresso.pressBack();
		Espresso.onView(ViewMatchers.withId(R.id.search_toolbar)).check(ViewAssertions.matches(isDisplayed()));
		Espresso.pressBack();
		Espresso.onView(ViewMatchers.withId(R.id.search_toolbar)).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
	}

	@Test
	public void searchProjectTest() throws Exception {
		DatabaseUtils.insertFruits();
		Espresso.onView(ViewMatchers.withId(R.id.action_search)).perform(ViewActions.click());
		Espresso.onView(ViewMatchers.withId(android.support.v7.appcompat.R.id.search_src_text)).perform(ViewActions.typeText("an"));
		Espresso.closeSoftKeyboard();
		Espresso.onView(ViewMatchers.withId(android.R.id.list)).check(ViewAssertions.matches(new BaseMatcher<View>() {
			@Override
			public boolean matches(Object item) {
				ListView listView = (ListView)item;
				return listView.getAdapter().getCount() == 2;
			}

			@Override
			public void describeTo(Description description) {

			}
		}));
	}

	@Test
	public void orderProjectByNameTest() throws Exception {
		DatabaseUtils.insertFruits();
		Espresso.onView(ViewMatchers.withId(R.id.action_sort)).perform(ViewActions.click());
		Espresso.onView(ViewMatchers.withText(R.string.name)).perform(ViewActions.click());
		Espresso.onView(ViewMatchers.withId(android.R.id.list)).check(ViewAssertions.matches(new BaseMatcher<View>() {
			@Override
			public boolean matches(Object item) {
				ListView listView = (ListView)item;
				Assert.assertEquals(3, listView.getAdapter().getCount());
				Nameable first = (Nameable)listView.getAdapter().getItem(0);
				Nameable second = (Nameable)listView.getAdapter().getItem(1);
				Nameable third = (Nameable)listView.getAdapter().getItem(2);
				Assert.assertEquals("banana", first.getName());
				Assert.assertEquals("orange", second.getName());
				Assert.assertEquals("raspberry", third.getName());

				return true;
			}

			@Override
			public void describeTo(Description description) {

			}
		}));
	}

	@Test
	public void orderProjectByPriorityTest() throws Exception {
		DatabaseUtils.insertFruits();
		Espresso.onView(ViewMatchers.withId(R.id.action_sort)).perform(ViewActions.click());
		Espresso.onView(ViewMatchers.withText(R.string.priority)).perform(ViewActions.click());
		Espresso.onView(ViewMatchers.withId(android.R.id.list)).check(ViewAssertions.matches(new BaseMatcher<View>() {
			@Override
			public boolean matches(Object item) {
				ListView listView = (ListView)item;
				Assert.assertEquals(3, listView.getAdapter().getCount());
				Nameable first = (Nameable)listView.getAdapter().getItem(0);
				Nameable second = (Nameable)listView.getAdapter().getItem(1);
				Nameable third = (Nameable)listView.getAdapter().getItem(2);
				Assert.assertEquals("orange", first.getName());
				Assert.assertEquals("banana", second.getName());
				Assert.assertEquals("raspberry", third.getName());

				return true;
			}

			@Override
			public void describeTo(Description description) {

			}
		}));
	}

	@Test
	public void orderProjectByDeadlineTest() throws Exception {
		DatabaseUtils.insertFruits();
		Espresso.onView(ViewMatchers.withId(R.id.action_sort)).perform(ViewActions.click());
		Espresso.onView(ViewMatchers.withText(R.string.deadline)).perform(ViewActions.click());
		Espresso.onView(ViewMatchers.withId(android.R.id.list)).check(ViewAssertions.matches(new BaseMatcher<View>() {
			@Override
			public boolean matches(Object item) {
				ListView listView = (ListView)item;
				Assert.assertEquals(3, listView.getAdapter().getCount());
				Nameable first = (Nameable)listView.getAdapter().getItem(0);
				Nameable second = (Nameable)listView.getAdapter().getItem(1);
				Nameable third = (Nameable)listView.getAdapter().getItem(2);
				Assert.assertEquals("raspberry", first.getName());
				Assert.assertEquals("banana", second.getName());
				Assert.assertEquals("orange", third.getName());

				return true;
			}

			@Override
			public void describeTo(Description description) {

			}
		}));
	}

	@Test
	public void inProjectListClickTest() throws Exception {
		DatabaseUtils.insertFruits();
		Espresso.onView(ViewMatchers.withId(R.id.action_sort)).perform(ViewActions.click());
		Espresso.onView(ViewMatchers.withText(R.string.name)).perform(ViewActions.click());
		Espresso.onData(Matchers.anything()).inAdapterView(ViewMatchers.withId(android.R.id.list)).atPosition(0).perform(ViewActions.click());
		Espresso.onView(ViewMatchers.withText("raspberry")).check(ViewAssertions.doesNotExist());
		Espresso.onView(ViewMatchers.withText("banana")).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
	}

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
	public void projectShortcutInDrawerTest() throws Exception {
		DatabaseUtils.cleanDatabase();
		final CountDownLatch signal = new CountDownLatch(1);
		Project banana = new Project(CursorMock.createProjectCursorMock(Project.NONE, "banana", null, null, "02-04-2017", null, 3, Color.BLACK, 1));
		DataManager.getInstance().save(banana, new OnNewObjectCreatedListener() {
			@Override
			public void onNewObjectCreated(int projectId) {
				signal.countDown();
			}
		});
		signal.await();
		Espresso.onView(ViewMatchers.withId(R.id.drawer_layout)).perform(DrawerActions.open());
		Espresso.onView(Matchers.allOf(ViewMatchers.withText("banana"), ViewMatchers.isNotChecked())).perform(ViewActions.click());
	}
}
