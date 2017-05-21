package fr.socket.flo.todo.view.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import java.util.ArrayList;
import java.util.List;

import fr.socket.flo.todo.R;
import fr.socket.flo.todo.database.DataManager;
import fr.socket.flo.todo.database.OnObjectLoadedListener;
import fr.socket.flo.todo.model.Project;

/**
 * @author Florian Arnould
 * @version 1.0
 */
public class EditProjectActivity extends AppCompatActivity {
	public static final String PROJECT_ID = "project_id";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final int projectId = getIntent().getIntExtra(PROJECT_ID, -1);
		if (projectId == -1) {
			finish();
		}
		setContentView(R.layout.edit_project);
		Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		if (getSupportActionBar() != null) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}
		final Spinner prioritySpinner = (Spinner)findViewById(R.id.priority_spinner);
		List<String> list = new ArrayList<>();
		final SharedPreferences pref = getSharedPreferences(getString(R.string.preferences_name_key), MODE_PRIVATE);
		int max = pref.getInt("max_priority", 5);
		for (int i = 1; i <= max; i++) {
			list.add(String.valueOf(i));
		}
		SpinnerAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
		prioritySpinner.setAdapter(adapter);

		DataManager.getInstance().getProjectById(projectId, new OnObjectLoadedListener<Project>() {
			@Override
			public void onObjectLoaded(final Project project) {
				prioritySpinner.setSelection(project.getPriority() - 1);
				setTitle(project.getName());
				prioritySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
						project.setPriority(position + 1);
						project.save();
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {
						// This method can't be called : we just preselect the project priority, default -> 1
					}
				});
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			this.finish();
		}
		return super.onOptionsItemSelected(item);
	}
}
