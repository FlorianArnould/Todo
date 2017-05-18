package fr.socket.flo.todo.view.settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import fr.socket.flo.todo.R;

/**
 * @author Florian Arnould
 * @version 1.0
 */
public class SettingsFragment extends PreferenceFragment {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
	}
}
