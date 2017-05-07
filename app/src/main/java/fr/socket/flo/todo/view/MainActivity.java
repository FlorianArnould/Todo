package fr.socket.flo.todo.view;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

import fr.socket.flo.todo.R;
import fr.socket.flo.todo.storage.Project;
import fr.socket.flo.todo.storage.database.OnMultipleObjectsLoadedListener;
import fr.socket.flo.todo.storage.database.Projects;
import fr.socket.flo.todo.view.drawable.ColorGenerator;
import fr.socket.flo.todo.view.drawable.ProgressTextDrawable;

public class MainActivity extends AppCompatActivity
		implements NavigationView.OnNavigationItemSelectedListener {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		DrawerLayout drawer = (DrawerLayout)findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
				this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		drawer.addDrawerListener(toggle);
		toggle.syncState();

		NavigationView navigationView = (NavigationView)findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(this);
		navigationView.setItemIconTintList(null);
		updateDrawer();

		getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContent, new AllProjectsFragment()).commit();
	}

	@Override
	public void onBackPressed() {
		DrawerLayout drawer = (DrawerLayout)findViewById(R.id.drawer_layout);
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
		} else {
			super.onBackPressed();
		}
	}

	public void updateDrawer() {
		final NavigationView navView = (NavigationView)findViewById(R.id.nav_view);
		navView.inflateMenu(R.menu.drawer_menu);
		final Menu menu = navView.getMenu().getItem(0).getSubMenu();
		Projects.getInstance(getApplicationContext()).getFavorites(new OnMultipleObjectsLoadedListener<Project>() {
			@Override
			public void OnObjectsLoaded(List<Project> objects) {
				for (Project project : objects) {
					//TODO replace with the real project progress values
					@ColorInt int color = project.getColor();
					final Drawable icon = new ProgressTextDrawable(project.getName().substring(0, 1), ColorGenerator.darkerColor(color), color, Math.random(), 1);
					menu.add(Menu.NONE, Menu.NONE, Menu.NONE, project.getName()).setIcon(icon);
				}
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		return super.onOptionsItemSelected(item);
	}

	@SuppressWarnings("StatementWithEmptyBody")
	@Override
	public boolean onNavigationItemSelected(@NonNull MenuItem item) {
		// Handle navigation view item clicks here.
		int id = item.getItemId();

		DrawerLayout drawer = (DrawerLayout)findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);
		return true;
	}
}
