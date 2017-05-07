package fr.socket.flo.todo.storage;

import android.content.Context;
import android.support.annotation.ColorInt;

import fr.socket.flo.todo.storage.database.Projects;
import fr.socket.flo.todo.view.drawable.ColorGenerator;

/**
 * @author Florian Arnould
 * @version 1.0
 */
public class Task extends Work{
	Task(int id, String name, @ColorInt int color){
		super(id, name, color);
	}

	public static void newTask(Context context, String name){
		Task task = new Task(NONE, name, ColorGenerator.randomColor());
		Projects.getInstance(context).save(task);
	}
}
