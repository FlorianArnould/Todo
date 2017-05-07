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
import fr.socket.flo.todo.storage.Project;

/**
 * @author Florian Arnould
 * @version 1.0
 */
public class DialogManager {
	public static void showNewProjectDialog(final Activity activity, @Nullable final OnDialogFinishedListener listener) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		LayoutInflater inflater = activity.getLayoutInflater();
		final boolean result = false;
		@SuppressLint("InflateParams") final View view = inflater.inflate(R.layout.new_project_dialog, null);
		final EditText editText = (EditText)view.findViewById(R.id.projectName);
		builder.setView(view)
				.setCancelable(true)
				.setTitle(activity.getString(R.string.new_project))
				.setNegativeButton(activity.getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (listener != null) {
							listener.OnDialogFinished(false);
						}
					}
				})
				.setPositiveButton(activity.getString(android.R.string.ok), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						final String name = editText.getText().toString();
						if (!name.isEmpty()) {
							Project.newProject(activity, name);
							if (listener != null) {
								listener.OnDialogFinished(true);
							}
						} else if (listener != null) {
							listener.OnDialogFinished(false);
						}
					}
				});
		final AlertDialog dialog = builder.create();
		editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus && dialog.getWindow() != null){
					dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
				}
			}
		});
		dialog.show();

	}
}
