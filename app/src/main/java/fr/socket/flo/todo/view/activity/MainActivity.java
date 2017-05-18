package fr.socket.flo.todo.view.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.List;

import fr.socket.flo.todo.R;
import fr.socket.flo.todo.database.DataManager;
import fr.socket.flo.todo.database.OnDataChangedListener;
import fr.socket.flo.todo.database.OnMultipleObjectsLoadedListener;
import fr.socket.flo.todo.model.Project;
import fr.socket.flo.todo.model.Sorter;
import fr.socket.flo.todo.view.drawable.ColorGenerator;
import fr.socket.flo.todo.view.drawable.ProgressTextDrawable;
import fr.socket.flo.todo.view.fragments.AllProjectsFragment;
import fr.socket.flo.todo.view.fragments.MainActivityFragment;
import fr.socket.flo.todo.view.fragments.ProjectFragment;
import fr.socket.flo.todo.view.settings.SettingsActivity;

public class MainActivity extends SearchActivity
		implements NavigationView.OnNavigationItemSelectedListener {
	private enum DrawerGroup {DEFAULT, PROJECTS, TASKS}

	private FloatingActionButton _fab;
	private Menu _sortSubMenu;
	private OnSortChangedListener _sortListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_main);
		Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		super.onCreate(savedInstanceState);
		DataManager.getInstance().initialize(this);
		DataManager.getInstance().addOnDataChangedListener(new OnDataChangedListener() {
			@Override
			public void onDataChanged() {
				updateDrawer();
			}
		});

		_fab = (FloatingActionButton)findViewById(R.id.fab);

		DrawerLayout drawer = (DrawerLayout)findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
				this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		drawer.addDrawerListener(toggle);
		toggle.syncState();

		NavigationView navigationView = (NavigationView)findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(this);
		navigationView.setItemIconTintList(null);
		updateDrawer();
		getSupportFragmentManager()
				.beginTransaction()
				.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
				.replace(R.id.fragmentContent, new AllProjectsFragment())
				.commit();
	}

	@Override
	public void onStart() {
		super.onStart();
		invalidateOptionsMenu();
	}

	@Override
	public void onStop() {
		super.onStop();
		DataManager.getInstance().closeDatabase();
	}

	@Override
	public void onBackPressed() {
		DrawerLayout drawer = (DrawerLayout)findViewById(R.id.drawer_layout);
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
		} else if (isSearchViewOpen()) {
			closeSearch();
		} else {
			MainActivityFragment fragment = (MainActivityFragment)getSupportFragmentManager().findFragmentById(R.id.fragmentContent);
			fragment.onActivityBackPressed();
		}
	}

	private void updateDrawer() {
		final NavigationView navView = (NavigationView)findViewById(R.id.nav_view);
		navView.getMenu().clear();
		navView.inflateMenu(R.menu.drawer_menu);
		final Menu menu = navView.getMenu().getItem(0).getSubMenu();
		DataManager.getInstance().getFavorites(new OnMultipleObjectsLoadedListener<Project>() {
			@Override
			public void onObjectsLoaded(List<Project> objects) {
				for (Project project : objects) {
					@ColorInt int color = project.getColor();
					final Drawable icon = new ProgressTextDrawable(project.getName().substring(0, 1), ColorGenerator.darkerColor(color), color, project.getProgress());
					menu.add(DrawerGroup.PROJECTS.ordinal(), project.getId(), Menu.NONE, project.getName()).setIcon(icon);
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_menu, menu);
		_sortSubMenu = menu.findItem(R.id.action_sort).getSubMenu();
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch (id) {
			case R.id.action_search:
				openSearch();
				break;
			case R.id.sort_by_name:
				item.setChecked(true);
				callSortListener(Sorter.SortingWay.BY_NAME);
				break;
			case R.id.sort_by_deadline:
				item.setChecked(true);
				callSortListener(Sorter.SortingWay.BY_DEADLINE);
				break;
			case R.id.sort_by_priority:
				item.setChecked(true);
				callSortListener(Sorter.SortingWay.BY_PRIORITY);
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void callSortListener(Sorter.SortingWay way) {
		if (_sortListener != null) {
			_sortListener.onSortChangedListener(way);
		}
	}

	@SuppressWarnings("StatementWithEmptyBody")
	@Override
	public boolean onNavigationItemSelected(@NonNull MenuItem item) {
		DrawerGroup group = DrawerGroup.values()[item.getGroupId()];
		switch (group) {
			case PROJECTS:
				inflateProjectFragment(item.getItemId());
				break;
			case TASKS:
				break;
			case DEFAULT:
				onFixedNavigationItemSelected(item);
				break;
		}
		DrawerLayout drawer = (DrawerLayout)findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);
		return true;
	}

	private void onFixedNavigationItemSelected(@NonNull MenuItem item) {
		int id = item.getItemId();
		switch (id) {
			case R.id.settings:
				showSettings();
				break;
			case R.id.about:
				// TODO: 18/05/17 create about activity to show here
				break;
			default:
				Log.w("Drawer items", "A static item selected was not handle by the MainActivity");
		}
	}

	private void showSettings() {
		Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
		startActivity(intent);
	}

	private void inflateProjectFragment(int projectId) {
		Fragment fragment = ProjectFragment.newInstance(projectId);
		getSupportFragmentManager()
				.beginTransaction()
				.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
				.replace(R.id.fragmentContent, fragment)
				.commit();
	}

	public FloatingActionButton getFloatingActionButton() {
		return _fab;
	}

	public View getRootView() {
		return findViewById(R.id.fragmentContent);
	}

	public void setOnSortChangedListener(@Nullable OnSortChangedListener listener) {
		_sortListener = listener;
	}

	public void setSortWay(Sorter.SortingWay way) {
		@IdRes int itemId;
		switch (way) {
			case BY_DEADLINE:
				itemId = R.id.sort_by_deadline;
				break;
			case BY_PRIORITY:
				itemId = R.id.sort_by_priority;
				break;
			default:
				itemId = R.id.sort_by_name;
		}
		_sortSubMenu.findItem(itemId).setChecked(true);
	}
}
