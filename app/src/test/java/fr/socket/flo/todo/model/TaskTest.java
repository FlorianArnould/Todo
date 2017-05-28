package fr.socket.flo.todo.model;

import android.database.Cursor;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import fr.socket.flo.todo.R;

/**
 * @author Florian Arnould
 */
public class TaskTest {
	private static Task task;

	@BeforeClass
	public static void createInstanceFromCursorTest() {
		Cursor cursor = Mockito.mock(Cursor.class);
		Mockito.when(cursor.getInt(0)).thenReturn(1);
		Mockito.when(cursor.getString(1)).thenReturn("name");
		Mockito.when(cursor.getString(2)).thenReturn("02-04-2017 12:03:00");
		Mockito.when(cursor.getInt(3)).thenReturn(3);
		Mockito.when(cursor.getInt(4)).thenReturn(1);
		Mockito.when(cursor.getString(5)).thenReturn(Task.State.IN_PROGRESS.name());
		task = new Task(cursor);
	}

	@Test
	public void stateTest(){
		Assert.assertEquals(R.string.in_progress, task.getStringResIdState());
		task.nextState();
		Assert.assertEquals(R.string.completed, task.getStringResIdState());
		task.nextState();
		Assert.assertEquals(R.string.waiting, task.getStringResIdState());
		task.nextState();
		Assert.assertEquals(R.string.in_progress, task.getStringResIdState());
	}

	@Test
	public void getTableTest() {
		Assert.assertEquals(Task.TABLE_NAME, task.getTable());
	}

	@Test
	public void columnsTest() {
		Collection<String> columns = Task.getColumns();
		Assert.assertEquals(6, columns.size());
		List<String> list = new ArrayList<>(columns);
		int index = 0;
		Assert.assertEquals("id", list.get(index++));
		Assert.assertEquals("name", list.get(index++));
		Assert.assertEquals("deadline", list.get(index++));
		Assert.assertEquals("priority", list.get(index++));
		Assert.assertEquals("project_id", list.get(index++));
		Assert.assertEquals("state", list.get(index));
	}
}