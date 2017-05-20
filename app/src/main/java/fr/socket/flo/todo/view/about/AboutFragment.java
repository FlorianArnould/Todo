package fr.socket.flo.todo.view.about;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import fr.socket.flo.todo.R;

/**
 * @author Florian Arnould
 * @version 1.0
 */
public class AboutFragment extends PreferenceFragment {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.about);
	}
}
