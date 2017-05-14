package fr.socket.flo.todo.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;

import fr.socket.flo.todo.R;

/**
 * @author Florian Arnould
 * @version 1.0
 */
abstract class SearchActivity extends AppCompatActivity {
	private Menu _searchMenu;
	private MenuItem _itemSearch;
	private SearchView _searchView;
	private boolean isOpen;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setSearchToolbar();
		isOpen = false;
	}

	private void setSearchToolbar() {
		Toolbar searchToolbar = (Toolbar)findViewById(R.id.search_toolbar);
		if (searchToolbar != null) {
			searchToolbar.inflateMenu(R.menu.search_menu);
			_searchMenu = searchToolbar.getMenu();
			searchToolbar.setNavigationOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					circleReveal(R.id.search_toolbar, 1, true, false);
				}
			});
			_itemSearch = _searchMenu.findItem(R.id.action_filter_search);
			MenuItemCompat.setOnActionExpandListener(_itemSearch, new MenuItemCompat.OnActionExpandListener() {
				@Override
				public boolean onMenuItemActionCollapse(MenuItem item) {
					circleReveal(R.id.search_toolbar, 1, true, false);
					return true;
				}

				@Override
				public boolean onMenuItemActionExpand(MenuItem item) {
					// Do something when expanded
					return true;
				}
			});
			initSearchView();
		}
	}

	private void initSearchView() {
		_searchView =
				(SearchView)_searchMenu.findItem(R.id.action_filter_search).getActionView();

		_searchView.setSubmitButtonEnabled(false);

		ImageView closeButton = (ImageView)_searchView.findViewById(R.id.search_close_btn);
		closeButton.setImageResource(R.drawable.ic_close);

		EditText txtSearch = ((EditText)_searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text));
		txtSearch.setHint(getString(R.string.action_search) + " ...");
		txtSearch.setHintTextColor(Color.DKGRAY);
		txtSearch.setTextColor(getResources().getColor(R.color.colorPrimary, getTheme()));
	}

	// TODO: 14/05/17 May use others method like the closeSearch() to handle this
	protected void circleReveal(int viewID, int posFromRight, boolean containsOverflow, final boolean isShow) {
		isOpen = isShow;
		final View myView = findViewById(viewID);
		int width = myView.getWidth();
		// TODO: 14/05/17 find a way to avoid the use of these dimens resources
		if (posFromRight > 0) {
			width -= (posFromRight * getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_material)) - (getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_material) / 2);
		}
		if (containsOverflow) {
			width -= getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_overflow_material);
		}
		int cx = width;
		int cy = myView.getHeight() / 2;
		Animator anim;
		if (isShow) {
			anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0, width);
		} else {
			anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, width, 0);
		}
		anim.setDuration(220);
		anim.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				if (!isShow) {
					super.onAnimationEnd(animation);
					myView.setVisibility(View.INVISIBLE);
				}
			}
		});
		if (isShow) {
			myView.setVisibility(View.VISIBLE);
		}
		anim.start();
		_itemSearch.expandActionView();
	}

	public SearchView getSearchView(){
		return _searchView;
	}

	public void closeSearch(){
		if(isOpen) {
			circleReveal(R.id.search_toolbar, 1, true, false);
		}
	}
}
