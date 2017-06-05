package fr.socket.flo.todo.view.settings;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import junit.framework.Assert;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Florian Arnould
 */
@RunWith(AndroidJUnit4.class)
public class SettingsActivityTest {
	@Rule
	public ActivityTestRule<SettingsActivity> _activityTestRule = new ActivityTestRule<>(SettingsActivity.class);

	@Test
	public void returnButtonTest() {
		Espresso.onView(ViewMatchers.withContentDescription("Navigate up")).perform(ViewActions.click());
		Assert.assertTrue(_activityTestRule.getActivity().isFinishing());
	}
}
