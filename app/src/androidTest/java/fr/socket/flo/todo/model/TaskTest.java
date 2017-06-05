package fr.socket.flo.todo.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.test.runner.AndroidJUnit4;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Florian Arnould
 */
@RunWith(AndroidJUnit4.class)
public class TaskTest {
	@Test
	public void contentValuesTest() {
		Cursor cursor = CursorMock.createTaskCursorMock(1, "name", null, null, null, null, 3, 1, Task.State.IN_PROGRESS.name());
		Task task = new Task(cursor);
		ContentValues values = task.toContentValues();
		Assert.assertEquals(1, (int)values.getAsInteger("project_id"));
		Assert.assertEquals(Task.State.IN_PROGRESS.name(), values.getAsString("state"));
	}
}
