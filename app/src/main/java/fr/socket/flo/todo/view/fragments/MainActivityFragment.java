package fr.socket.flo.todo.view.fragments;

import android.content.Context;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import fr.socket.flo.todo.view.activity.MainActivity;

/**
 * @author Florian Arnould
 * @version 1.0
 */
public abstract class MainActivityFragment extends ListFragment {
	protected MainActivity getMainActivity() {
		return (MainActivity)super.getActivity();
	}

	public abstract void onActivityBackPressed();

	protected void cleanView(View view) {
		InputMethodManager input = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		input.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		getMainActivity().closeSearch();
	}

	@Override
	public void onStart() {
		super.onStart();
		MainActivity activity = getMainActivity();
		activity.getFloatingActionButton().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onFloatingActionButtonClicked();
			}
		});
	}

	public void setHasFloatingAction(boolean hasFab) {
		if (hasFab) {
			getMainActivity().getFloatingActionButton().setVisibility(View.VISIBLE);
		} else {
			getMainActivity().getFloatingActionButton().setVisibility(View.GONE);
		}
	}

	public void onFloatingActionButtonClicked() {
	}
}
