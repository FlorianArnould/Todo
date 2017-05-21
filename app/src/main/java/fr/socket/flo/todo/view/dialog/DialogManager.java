package fr.socket.flo.todo.view.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import fr.socket.flo.todo.R;
import fr.socket.flo.todo.database.OnNewObjectCreatedListener;
import fr.socket.flo.todo.model.Project;
import fr.socket.flo.todo.model.Task;

/**
 * @author Florian Arnould
 * @version 1.0
 */
public class DialogManager {
	private final Activity _activity;

	public DialogManager(final Activity activity) {
		_activity = activity;
	}

	public void showNewProjectDialog(@Nullable final OnNewObjectCreatedListener listener) {
		showNewObjectDialog(
				_activity.getString(R.string.new_project),
				_activity.getString(R.string.project_name),
				new OnEditTextDialogClickListener() {
					@Override
					public void onEditTextDialogClickListener(String text) {
						Project.newProject(text, listener);
					}
				});
	}

	public void showNewTaskDialog(final int projectId, @Nullable final OnNewObjectCreatedListener listener) {
		showNewObjectDialog(
				_activity.getString(R.string.new_task),
				_activity.getString(R.string.task_name),
				new OnEditTextDialogClickListener() {
					@Override
					public void onEditTextDialogClickListener(String text) {
						Task.newTask(projectId, text, listener);
					}
				});
	}

	private void showNewObjectDialog(CharSequence title, CharSequence nameHint, final OnEditTextDialogClickListener onPositiveButtonWithTextClickListener) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(_activity);
		LayoutInflater inflater = _activity.getLayoutInflater();
		@SuppressLint("InflateParams") final View view = inflater.inflate(R.layout.new_object_dialog, null);
		final EditText editText = (EditText)view.findViewById(R.id.projectName);
		editText.setHint(nameHint);
		builder.setView(view)
				.setCancelable(true)
				.setTitle(title)
				.setPositiveButton(_activity.getString(android.R.string.ok), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String name = editText.getText().toString();
						if (!name.isEmpty()) {
							onPositiveButtonWithTextClickListener.onEditTextDialogClickListener(name);
						}
					}
				});
		final AlertDialog dialog = builder.create();
		editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus && dialog.getWindow() != null) {
					dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
				}
			}
		});
		dialog.show();
	}
}
