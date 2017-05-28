package fr.socket.flo.todo.view.activity;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

import fr.socket.flo.todo.R;
import fr.socket.flo.todo.database.DataManager;
import fr.socket.flo.todo.database.OnObjectLoadedListener;
import fr.socket.flo.todo.model.DateTime;
import fr.socket.flo.todo.model.Project;
import fr.socket.flo.todo.view.dialog.DialogManager;
import fr.socket.flo.todo.view.dialog.OnDialogFinishedListener;
import fr.socket.flo.todo.view.graphics.PriorityDrawable;
import fr.socket.flo.todo.view.graphics.TextDrawable;

/**
 * @author Florian Arnould
 * @version 1.0
 */
public class EditProjectActivity extends AppCompatActivity implements OnObjectLoadedListener<Project> {
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

		DataManager.getInstance().getProjectById(projectId, this);
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

	@Override
	public void onObjectLoaded(Project project) {
		_project = project;
		EditText editName = (EditText)findViewById(R.id.name);
		editName.setText(project.getName());
		updateColorProperty();
		updatePriorityProperty();
		updateDeadlineDateProperty();
		updateDeadlineWholeDayProperty();
		updateDeadlineTimeProperty();
	}

	private void updateColorProperty() {
		ImageView colorView = (ImageView)findViewById(R.id.color);
		colorView.setImageDrawable(new TextDrawable("", _project.getColor(), 0));
	}

	private void updatePriorityProperty() {
		setOnPropertyClickListener(R.id.priority_property, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new DialogManager(EditProjectActivity.this).showPriorityPickerDialog(_project.getPriority(), new OnDialogFinishedListener<Integer>() {
					@Override
					public void onDialogFinished(Integer result) {
						_project.setPriority(result);
						updatePriorityProperty();
					}
				});
			}
		});
		ImageView priorityView = (ImageView)findViewById(R.id.priority_label);
		priorityView.setImageDrawable(new PriorityDrawable(_project.getPriority()));
	}

	private void updateDeadlineDateProperty() {
		setOnPropertyClickListener(R.id.deadline_date_property, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new DialogManager(EditProjectActivity.this).showDatePickerDialog(_project.getDeadline().getDate(), new OnDialogFinishedListener<Date>() {
					@Override
					public void onDialogFinished(Date date) {
						_project.getDeadline().setDate(date);
						updateDeadlineDateProperty();
					}
				});
			}
		});
		fillSummary(R.id.summary_deadline_date, _project.getDeadline().toDateString());
	}

	private void updateDeadlineWholeDayProperty() {
		final DateTime deadline = _project.getDeadline();
		final CheckBox checkBox = (CheckBox)findViewById(R.id.deadline_whole_day_checkbox);
		checkBox.setChecked(deadline.isWholeDay());
		checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					deadline.setWholeDay();
				}else{
					deadline.setTime(Calendar.getInstance().getTime());
				}
			}
		});
		setOnPropertyClickListener(R.id.deadline_whole_day_property, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				checkBox.setChecked(!checkBox.isChecked());
			}
		});
	}

	private void updateDeadlineTimeProperty() {
		setOnPropertyClickListener(R.id.deadline_time_property, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new DialogManager(EditProjectActivity.this).showTimePickerDialog(_project.getDeadline().getTime(), new OnDialogFinishedListener<Date>() {
					@Override
					public void onDialogFinished(Date time) {
						_project.getDeadline().setTime(time);
						updateDeadlineTimeProperty();
					}
				});
			}
		});
		fillSummary(R.id.summary_deadline_time, _project.getDeadline().toTimeString());
	}

	private void fillSummary(@IdRes int summaryRes, CharSequence summaryString) {
		TextView summaryView = (TextView)findViewById(summaryRes);
		summaryView.setText(summaryString);
	}

	private void setOnPropertyClickListener(@IdRes int res, View.OnClickListener listener) {
		View view = findViewById(res);
		view.setOnClickListener(listener);
	}
}
