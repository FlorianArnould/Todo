package fr.socket.flo.todo.view.activity;

import android.support.design.widget.NavigationView;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.widget.ListView;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import fr.socket.flo.todo.R;
import fr.socket.flo.todo.database.DataManager;
import fr.socket.flo.todo.database.OnDataChangedListener;
import fr.socket.flo.todo.database.OnMultipleObjectsLoadedListener;
import fr.socket.flo.todo.model.Project;
import fr.socket.flo.todo.utils.BetweenThreadLock;

/**
 * @author Florian Arnould
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
	@Rule
	public ActivityTestRule<MainActivity> _acMainActivityActivityTestRule = new ActivityTestRule<>(MainActivity.class);

	public void cleanDatabase() throws Exception {
		final BetweenThreadLock lock = new BetweenThreadLock();
		lock.lock();
		DataManager.getInstance().getAllProjects(new OnMultipleObjectsLoadedListener<Project>() {
			@Override
			public void onObjectsLoaded(List<Project> projects) {
				for (Project project : projects) {
					DataManager.getInstance().remove(project, new OnDataChangedListener() {
						@Override
						public void onDataChanged() {
							lock.unlock();
						}
					});
				}
			}
		});
		if (!lock.tryLock(500, TimeUnit.MILLISECONDS)) {
			throw new Exception("The database clean up was too slow");
		}
	}

	@Test
	public void addProjectTest() throws Exception {
		cleanDatabase();
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
		addProjectTest();
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
}
