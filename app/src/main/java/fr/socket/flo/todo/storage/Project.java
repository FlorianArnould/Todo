package fr.socket.flo.todo.storage;

import android.content.Context;
import android.support.annotation.ColorInt;

import fr.socket.flo.todo.storage.database.Projects;
import fr.socket.flo.todo.view.drawable.ColorGenerator;

/**
 * @author Florian Arnould
 * @version 1.0
 */
public class Project extends Work {


	public Project(int id, String name, @ColorInt int color) {
		super(id, name, color);
	}

	public static void newProject(Context context, String name){
		Project project = new Project(NONE, name, ColorGenerator.randomColor());
		Projects.getInstance(context).save(project);
	}
}
