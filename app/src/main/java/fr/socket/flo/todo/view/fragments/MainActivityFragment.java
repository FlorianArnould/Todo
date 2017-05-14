package fr.socket.flo.todo.view.fragments;

import android.content.Context;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import fr.socket.flo.todo.view.MainActivity;

/**
 * @author Florian Arnould
 * @version 1.0
 */
public abstract class MainActivityFragment extends ListFragment {
	protected MainActivity getMainActivity() {
		return (MainActivity)super.getActivity();
	}

	public abstract void onActivityBackPressed();

	protected void cleanView(View view){
		InputMethodManager input = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		input.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		getMainActivity().closeSearch();
	}
}
