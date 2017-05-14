package fr.socket.flo.todo.view.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.List;

import fr.socket.flo.todo.R;
import fr.socket.flo.todo.database.DataManager;
import fr.socket.flo.todo.database.OnMultipleObjectsLoadedListener;
import fr.socket.flo.todo.model.Project;
import fr.socket.flo.todo.view.drawable.ColorGenerator;
import fr.socket.flo.todo.view.drawable.ProgressTextDrawable;
import fr.socket.flo.todo.view.mainFragments.AllProjectsFragment;
import fr.socket.flo.todo.view.mainFragments.MainActivityFragment;
import fr.socket.flo.todo.view.settings.SettingsActivity;

// TODO: 14/05/17 find a way to color in white the menuitems

public class MainActivity extends SearchActivity
		implements NavigationView.OnNavigationItemSelectedListener {
	private FloatingActionButton _fab;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_main);
		Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		super.onCreate(savedInstanceState);

		DataManager.getInstance().initialize(this);


		//setSearchToolbar();

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
		} else {
			MainActivityFragment fragment = (MainActivityFragment)getSupportFragmentManager().findFragmentById(R.id.fragmentContent);
			fragment.onActivityBackPressed();
		}
	}

	private void updateDrawer() {
		final NavigationView navView = (NavigationView)findViewById(R.id.nav_view);
		navView.inflateMenu(R.menu.drawer_menu);
		final Menu menu = navView.getMenu().getItem(0).getSubMenu();
		DataManager.getInstance().getFavorites(new OnMultipleObjectsLoadedListener<Project>() {
			@Override
			public void OnObjectsLoaded(List<Project> objects) {
				for (Project project : objects) {
					@ColorInt int color = project.getColor();
					final Drawable icon = new ProgressTextDrawable(project.getName().substring(0, 1), ColorGenerator.darkerColor(color), color, project.getProgress());
					menu.add(Menu.NONE, Menu.NONE, Menu.NONE, project.getName()).setIcon(icon);
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_menu, menu);
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
				circleReveal(R.id.search_toolbar, 1, false, true);
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@SuppressWarnings("StatementWithEmptyBody")
	@Override
	public boolean onNavigationItemSelected(@NonNull MenuItem item) {
		// Handle navigation view item clicks here.
		int id = item.getItemId();
		switch (id){
			case R.id.settings:
				Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
				startActivity(intent);
				break;
		}
		DrawerLayout drawer = (DrawerLayout)findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);
		return true;
	}

	public FloatingActionButton getFloatingActionButton() {
		return _fab;
	}

	public View getRootView() {
		return findViewById(R.id.fragmentContent);
	}
}