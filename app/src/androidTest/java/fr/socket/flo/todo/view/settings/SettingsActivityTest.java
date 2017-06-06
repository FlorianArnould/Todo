package fr.socket.flo.todo.view.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.GeneralClickAction;
import android.support.test.espresso.action.GeneralLocation;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Tap;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.SeekBar;

import junit.framework.Assert;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import fr.socket.flo.todo.R;

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

	@Test
	public void maxPriorityValueTest() {
		Espresso.onView(ViewMatchers.withText(R.string.settings_priority_max_value_title)).perform(ViewActions.click());
		Espresso.onView(
				Matchers.allOf(
						Matchers.instanceOf(SeekBar.class),
						ViewMatchers.withParent(ViewMatchers.withParent(ViewMatchers.withParent(ViewMatchers.withParent(
								ViewMatchers.withChild(ViewMatchers.withChild(ViewMatchers.withChild(ViewMatchers.withText(R.string.settings_priority_max_value_title))))
						))))
				))
				.perform(new GeneralClickAction(Tap.SINGLE, GeneralLocation.CENTER_LEFT, Press.FINGER));
		Espresso.onView(ViewMatchers.withId(android.R.id.button1)).perform(ViewActions.click());
		Context context = InstrumentationRegistry.getTargetContext();
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		Assert.assertEquals(5, pref.getInt(context.getString(R.string.settings_priority_max_value_key), 10));
		Espresso.onView(ViewMatchers.withText(R.string.settings_priority_max_value_title)).perform(ViewActions.click());
		Espresso.onView(
				Matchers.allOf(
						Matchers.instanceOf(SeekBar.class),
						ViewMatchers.withParent(ViewMatchers.withParent(ViewMatchers.withParent(ViewMatchers.withParent(
								ViewMatchers.withChild(ViewMatchers.withChild(ViewMatchers.withChild(ViewMatchers.withText(R.string.settings_priority_max_value_title))))
						))))
				))
				.perform(new GeneralClickAction(Tap.SINGLE, GeneralLocation.CENTER, Press.FINGER));
		Espresso.onView(ViewMatchers.withId(android.R.id.button1)).perform(ViewActions.click());
		Assert.assertEquals(13, pref.getInt(context.getString(R.string.settings_priority_max_value_key), 10));
	}
}
