package fr.socket.flo.todo.model;

import android.database.Cursor;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import fr.socket.flo.todo.R;
import fr.socket.flo.todo.database.DataManager;
import fr.socket.flo.todo.database.OnNewObjectCreatedListener;

/**
 * @author Florian Arnould
 */
@RunWith(PowerMockRunner.class)
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
	@PrepareForTest(DataManager.class)
	public void newProjectTest() {
		DataManager dataManager = Mockito.mock(DataManager.class);
		Mockito.doAnswer(new Answer() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				Savable savable = invocation.getArgumentAt(0, Savable.class);
				OnNewObjectCreatedListener listener = invocation.getArgumentAt(1, OnNewObjectCreatedListener.class);
				Assert.assertNull(listener);
				Assert.assertTrue(savable instanceof Task);
				Nameable task = (Nameable)savable;
				Assert.assertEquals("name", task.getName());
				return null;
			}
		}).when(dataManager).save(Mockito.any(Savable.class), Mockito.any(OnNewObjectCreatedListener.class));
		PowerMockito.mockStatic(DataManager.class);
		PowerMockito.when(DataManager.getInstance()).thenReturn(dataManager);
		Task.newTask(2, "name", null);
	}

	@Test
	public void stateTest(){
		Assert.assertEquals(R.string.in_progress, _task.getStringResIdState());
		_task.nextState();
		Assert.assertEquals(R.string.completed, _task.getStringResIdState());
		_task.nextState();
		Assert.assertEquals(R.string.waiting, _task.getStringResIdState());
		_task.nextState();
		Assert.assertEquals(R.string.in_progress, _task.getStringResIdState());
	}

	@Test
	public void getTableTest() {
		Assert.assertEquals(Task.TABLE_NAME, _task.getTable());
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