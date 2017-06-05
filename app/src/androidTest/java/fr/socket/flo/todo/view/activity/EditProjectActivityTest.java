package fr.socket.flo.todo.view.activity;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.GeneralClickAction;
import android.support.test.espresso.action.GeneralLocation;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Tap;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.contrib.PickerActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.DatePicker;
import android.widget.TimePicker;

import junit.framework.Assert;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import fr.socket.flo.todo.R;
import fr.socket.flo.todo.database.DataManager;
import fr.socket.flo.todo.database.DatabaseUtils;
import fr.socket.flo.todo.database.OnMultipleObjectsLoadedListener;
import fr.socket.flo.todo.model.Project;

/**
 * @author Florian Arnould
 */
@RunWith(AndroidJUnit4.class)
public class EditProjectActivityTest {
	@Rule
	public ActivityTestRule<MainActivity> _activityTestRule = new ActivityTestRule<>(MainActivity.class);

	@Before
	public void launchActivity() throws Exception {
		DatabaseUtils.insertBanana();
		Espresso.onData(Matchers.anything()).inAdapterView(ViewMatchers.withId(android.R.id.list)).atPosition(0).perform(ViewActions.click());
	}

	public void openActivity() {
		Espresso.onView(ViewMatchers.withId(R.id.action_edit)).perform(ViewActions.click());
	}

	public void closeActivity() {
		Espresso.onView(ViewMatchers.withContentDescription("Navigate up")).perform(ViewActions.click());
	}

	@Test
	public void changeNameTest() throws Exception {
		openActivity();
		Espresso.onView(ViewMatchers.withId(R.id.name)).perform(ViewActions.clearText());
		Espresso.onView(ViewMatchers.withId(R.id.name)).perform(ViewActions.typeText("raspberry"));
		closeActivity();
		final CountDownLatch signal = new CountDownLatch(1);
		DataManager.getInstance().getAllProjects(new OnMultipleObjectsLoadedListener<Project>() {
			@Override
			public void onObjectsLoaded(List<Project> projects) {
				Assert.assertEquals(1, projects.size());
				Assert.assertEquals("raspberry", projects.get(0).getName());
				signal.countDown();
			}
		});
		signal.await();
	}

	@Test
	public void changePriorityTest() throws Exception {
		openActivity();
		Espresso.onView(ViewMatchers.withId(R.id.priority_property)).perform(ViewActions.click());
		Espresso.onView(ViewMatchers.withId(R.id.seek_bar)).perform(new GeneralClickAction(Tap.SINGLE, GeneralLocation.CENTER_LEFT, Press.FINGER));
		Espresso.onView(ViewMatchers.withId(android.R.id.button1)).perform(ViewActions.click());
		closeActivity();
		final CountDownLatch signal = new CountDownLatch(1);
		DataManager.getInstance().getAllProjects(new OnMultipleObjectsLoadedListener<Project>() {
			@Override
			public void onObjectsLoaded(List<Project> projects) {
				Assert.assertEquals(1, projects.size());
				Assert.assertEquals(1, projects.get(0).getPriority());
				signal.countDown();
			}
		});
		signal.await();
	}

	@Test
	public void changeDeadlineDate() throws Exception {
		openActivity();
		Espresso.onView(ViewMatchers.withId(R.id.deadline_date_property)).perform(ViewActions.click());
		Espresso.onView(ViewMatchers.withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(2017, 5, 4));
		Espresso.onView(ViewMatchers.withId(android.R.id.button1)).perform(ViewActions.click());
		closeActivity();
		final CountDownLatch signal = new CountDownLatch(1);
		DataManager.getInstance().getAllProjects(new OnMultipleObjectsLoadedListener<Project>() {
			@Override
			public void onObjectsLoaded(List<Project> projects) {
				Assert.assertEquals(1, projects.size());
				Assert.assertEquals("04-05-2017", projects.get(0).getDeadline().toDateString());
				signal.countDown();
			}
		});
		signal.await();
	}

	@Test
	public void changeDeadlineTime() throws Exception {
		openActivity();
		Espresso.onView(ViewMatchers.withId(R.id.deadline_time_property)).perform(ViewActions.click());
		Espresso.onView(ViewMatchers.withClassName(Matchers.equalTo(TimePicker.class.getName()))).perform(PickerActions.setTime(15, 12));
		Espresso.onView(ViewMatchers.withId(android.R.id.button1)).perform(ViewActions.click());
		closeActivity();
		final CountDownLatch signal = new CountDownLatch(1);
		DataManager.getInstance().getAllProjects(new OnMultipleObjectsLoadedListener<Project>() {
			@Override
			public void onObjectsLoaded(List<Project> projects) {
				Assert.assertEquals(1, projects.size());
				Assert.assertEquals("15:12", projects.get(0).getDeadline().toTimeString());
				signal.countDown();
			}
		});
		signal.await();
	}

	@Test
	public void changeDeadlineWholeDay() throws Exception {
		openActivity();
		Espresso.onView(ViewMatchers.withId(R.id.deadline_time_property)).perform(ViewActions.click());
		Espresso.onView(ViewMatchers.withClassName(Matchers.equalTo(TimePicker.class.getName()))).perform(PickerActions.setTime(15, 12));
		Espresso.onView(ViewMatchers.withId(android.R.id.button1)).perform(ViewActions.click());
		Espresso.onView(ViewMatchers.withId(R.id.deadline_whole_day_property)).perform(ViewActions.click());
		Espresso.onView(ViewMatchers.withId(R.id.deadline_whole_day_property)).perform(ViewActions.click());
		closeActivity();
		final CountDownLatch signal = new CountDownLatch(1);
		DataManager.getInstance().getAllProjects(new OnMultipleObjectsLoadedListener<Project>() {
			@Override
			public void onObjectsLoaded(List<Project> projects) {
				Assert.assertEquals(1, projects.size());
				Assert.assertTrue(projects.get(0).getDeadline().isWholeDay());
				signal.countDown();
			}
		});
		signal.await();
	}
}
