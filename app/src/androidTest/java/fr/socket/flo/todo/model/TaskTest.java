package fr.socket.flo.todo.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.test.runner.AndroidJUnit4;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

/**
 * @author Florian Arnould
 */
@RunWith(AndroidJUnit4.class)
public class TaskTest {
	private static Task _task;

	@BeforeClass
	public static void createInstanceFromCursorTest() {
		Cursor cursor = Mockito.mock(Cursor.class);
		Mockito.when(cursor.getInt(0)).thenReturn(1);
		Mockito.when(cursor.getString(1)).thenReturn("name");
		Mockito.when(cursor.getString(2)).thenReturn("02-04-2017 12:03:00");
		Mockito.when(cursor.getInt(3)).thenReturn(3);
		Mockito.when(cursor.getInt(4)).thenReturn(1);
		Mockito.when(cursor.getString(5)).thenReturn(Task.State.IN_PROGRESS.name());
		_task = new Task(cursor);
	}

	@Test
	public void contentValuesTest(){
		ContentValues values = _task.toContentValues();
		Assert.assertEquals(1, (int)values.getAsInteger("project_id"));
		Assert.assertEquals(Task.State.IN_PROGRESS.name(), values.getAsString("state"));
	}
}
