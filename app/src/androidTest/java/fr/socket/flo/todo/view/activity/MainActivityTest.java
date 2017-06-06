package fr.socket.flo.todo.view.activity;

import android.graphics.Color;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.DrawerMatchers;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.TextView;

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
import fr.socket.flo.todo.model.Project;

import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;

/**
 * @author Florian Arnould
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
	@Rule
	public ActivityTestRule<MainActivity> _activityTestRule = new ActivityTestRule<>(MainActivity.class);
	private int _id;

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
	public void showAllProjectTest() throws Exception {
		DatabaseUtils.insertBanana();
		Espresso.onData(Matchers.anything()).inAdapterView(ViewMatchers.withId(android.R.id.list)).atPosition(0).perform(ViewActions.click());
		Espresso.onView(ViewMatchers.withId(R.id.drawer_layout)).perform(DrawerActions.open());
		Espresso.onView(ViewMatchers.withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.all_projects));
		Espresso.onView(ViewMatchers.withText("banana")).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
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
	public void projectShortcutInDrawerTest() throws Exception {
		DatabaseUtils.cleanDatabase();
		final CountDownLatch signal = new CountDownLatch(1);
		Project banana = new Project(CursorMock.createProjectCursorMock(Project.NONE, "banana", null, null, "02-04-2017", null, 3, Color.BLACK, 1));
		DataManager.getInstance().save(banana, new OnNewObjectCreatedListener() {
			@Override
			public void onNewObjectCreated(int projectId) {
				_id = projectId;
				signal.countDown();
			}
		});
		signal.await();
		Espresso.onView(ViewMatchers.withId(R.id.drawer_layout)).perform(DrawerActions.open());
		Espresso.onView(ViewMatchers.withId(R.id.drawer_layout)).check(ViewAssertions.matches(DrawerMatchers.isOpen()));
		Espresso.onView(ViewMatchers.withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(_id));
		Espresso.onView(ViewMatchers.withId(R.id.action_edit)).check(ViewAssertions.matches(ViewMatchers.isEnabled()));
		Espresso.onView(Matchers.allOf(Matchers.instanceOf(TextView.class), ViewMatchers.withParent(ViewMatchers.withId(R.id.toolbar)))).check(ViewAssertions.matches(ViewMatchers.withText("banana")));
	}
}
