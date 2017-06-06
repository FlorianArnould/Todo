package fr.socket.flo.todo.view.about;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import junit.framework.Assert;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import fr.socket.flo.todo.R;

/**
 * @author Florian Arnould
 */
@RunWith(AndroidJUnit4.class)
public class AboutActivityTest {
	@Rule
	public ActivityTestRule<AboutActivity> _activityTestRule = new ActivityTestRule<>(AboutActivity.class);

	@Test
	public void displayTest() {
		Espresso.onView(ViewMatchers.withText(R.string.developer)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
		Espresso.onView(ViewMatchers.withText(R.string.developer_name)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
		Espresso.onView(ViewMatchers.withText(R.string.version)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
		Espresso.onView(ViewMatchers.withText(R.string.version_number)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
	}

	@Test
	public void returnButtonTest() {
		Espresso.onView(ViewMatchers.withContentDescription("Navigate up")).perform(ViewActions.click());
		Assert.assertTrue(_activityTestRule.getActivity().isFinishing());
	}
}
