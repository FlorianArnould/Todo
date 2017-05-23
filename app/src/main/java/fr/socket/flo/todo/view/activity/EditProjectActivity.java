package fr.socket.flo.todo.view.activity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import fr.socket.flo.todo.R;
import fr.socket.flo.todo.database.DataManager;
import fr.socket.flo.todo.database.OnObjectLoadedListener;
import fr.socket.flo.todo.model.Project;
import fr.socket.flo.todo.view.dialog.DialogManager;
import fr.socket.flo.todo.view.dialog.OnDialogFinishedListener;
import fr.socket.flo.todo.view.graphics.PriorityDrawable;
import fr.socket.flo.todo.view.graphics.ProgressTextDrawable;
import fr.socket.flo.todo.view.graphics.TextDrawable;

/**
 * @author Florian Arnould
 * @version 1.0
 */
public class EditProjectActivity extends AppCompatActivity {
	public static final String PROJECT_ID = "project_id";
	private Project _project;

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

		DataManager.getInstance().getProjectById(projectId, new OnObjectLoadedListener<Project>() {
			@Override
			public void onObjectLoaded(final Project project) {
				_project = project;
				EditText editName = (EditText)findViewById(R.id.name);
				editName.setText(project.getName());
				ListView listView = (ListView)findViewById(R.id.list);
				final BaseAdapter adapter = new EditProjectAdapter(project);
				listView.setAdapter(adapter);
				listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						DialogManager dialogManager = new DialogManager(EditProjectActivity.this);
						switch (position) {
							case 0:
								break;
							case 1:
								dialogManager.showPriorityPickerDialog(project.getPriority(), new OnDialogFinishedListener<Integer>() {
									@Override
									public void onDialogFinished(Integer result) {
										project.setPriority(result);
										adapter.notifyDataSetChanged();
									}
								});
								break;
							case 2:
								break;
							default:
								Log.w("ListView items", "An item selected was not handle by the EditProjectActivity");
						}
					}
				});
			}
		});
	}

	@Override
	public void onStop() {
		super.onStop();
		if (_project != null) {
			_project.save();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			this.finish();
		}
		return super.onOptionsItemSelected(item);
	}

	private class EditProjectAdapter extends BaseAdapter {
		private List<String> _titles;
		private List<String> _summaries;

		EditProjectAdapter(Project project) {
			_project = project;
			_titles = Arrays.asList(getResources().getStringArray(R.array.edit_project_titles));
			_summaries = Arrays.asList(getResources().getStringArray(R.array.edit_project_summary));
		}

		@Override
		public int getCount() {
			return _titles.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			if (view == null) {
				LayoutInflater inflater = (LayoutInflater)EditProjectActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(R.layout.edit_row, parent, false);
			}

			TextView titleView = (TextView)view.findViewById(R.id.title);
			titleView.setText(_titles.get(position));

			TextView summaryView = (TextView)view.findViewById(R.id.summary);
			summaryView.setText(_summaries.get(position));

			ImageView imageView = (ImageView)view.findViewById(R.id.image);
			imageView.setImageDrawable(getDrawable(position));
			return view;
		}

		private Drawable getDrawable(int position) {
			switch (position) {
				case 0:
					return new TextDrawable("", _project.getColor(), 0);
				case 1:
					return new PriorityDrawable(_project.getPriority());
				default:
					return null;
			}
		}
	}
}
