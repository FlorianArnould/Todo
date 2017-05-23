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
import fr.socket.flo.todo.view.about.AboutActivity;
import fr.socket.flo.todo.view.fragments.AllProjectsFragment;
import fr.socket.flo.todo.view.fragments.MainActivityFragment;
import fr.socket.flo.todo.view.fragments.ProjectFragment;
import fr.socket.flo.todo.view.graphics.ProgressTextDrawable;
import fr.socket.flo.todo.view.settings.SettingsActivity;

public class MainActivity extends SearchActivity
		implements NavigationView.OnNavigationItemSelectedListener, OnDataChangedListener {
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
		showAllProjects();
	}

	@Override
	public void onStart() {
		super.onStart();
		DataManager.getInstance().addOnDataChangedListener(this);
	}

	@Override
	public void onRestart() {
		super.onRestart();
		DataManager.getInstance().addOnDataChangedListener(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		DataManager.getInstance().closeDatabase();
		DataManager.getInstance().removeOnDataChangedListener(this);
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
		final Menu menu = navView.getMenu().getItem(1).getSubMenu();
		DataManager.getInstance().getFavorites(new OnMultipleObjectsLoadedListener<Project>() {
			@Override
			public void onObjectsLoaded(List<Project> objects) {
				for (Project project : objects) {
					@ColorInt int color = project.getColor();
					final Drawable icon = new ProgressTextDrawable(project.getName().substring(0, 1), color, project.getCompleteProgress());
					menu.add(R.id.favorites_group, project.getId(), Menu.NONE, project.getName()).setIcon(icon);
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
		int id = item.getItemId();
		switch (id) {
			case R.id.action_search:
				openSearch();
				break;
			case R.id.sort_by_name:
				item.setChecked(true);
				callSortListener(Sorter.Sort.BY_NAME);
				break;
			case R.id.sort_by_deadline:
				item.setChecked(true);
				callSortListener(Sorter.Sort.BY_DEADLINE);
				break;
			case R.id.sort_by_priority:
				item.setChecked(true);
				callSortListener(Sorter.Sort.BY_PRIORITY);
				break;
			default:
				Log.w("Options items", "An option item selected was not handle by the MainActivity");
		}
		return super.onOptionsItemSelected(item);
	}

	private void callSortListener(Sorter.Sort sort) {
		if (_sortListener != null) {
			_sortListener.onSortChangedListener(sort);
		}
	}

	@Override
	public boolean onNavigationItemSelected(@NonNull MenuItem item) {
		int groupId = item.getGroupId();
		switch (groupId) {
			case R.id.favorites_group:
				inflateProjectFragment(item.getItemId());
				break;
			case R.id.header_group:
			case R.id.footer_group:
				onFixedNavigationItemSelected(item);
				break;
			default:
				Log.w("Drawer groups", "A group selected was not handle by the MainActivity");
		}
		DrawerLayout drawer = (DrawerLayout)findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);
		return true;
	}

	private void onFixedNavigationItemSelected(@NonNull MenuItem item) {
		int id = item.getItemId();
		switch (id) {
			case R.id.all_projects:
				showAllProjects();
				break;
			case R.id.settings:
				showSettings();
				break;
			case R.id.about:
				showAbout();
				break;
			default:
				Log.w("Drawer items", "A static item selected was not handle by the MainActivity");
		}
	}

	private void showAllProjects() {
		getSupportFragmentManager()
				.beginTransaction()
				.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
				.replace(R.id.fragmentContent, new AllProjectsFragment())
				.commit();
	}

	private void showSettings() {
		Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
		startActivity(intent);
	}

	private void showAbout() {
		Intent intent = new Intent(MainActivity.this, AboutActivity.class);
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

	public void setSortWay(Sorter.Sort sort) {
		@IdRes int itemId;
		switch (sort) {
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

	@Override
	public void onDataChanged() {
		updateDrawer();
	}
}
