package fr.socket.flo.todo.model;

import android.database.Cursor;

import junit.framework.Assert;

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
	public void stateTest() {
		Cursor cursor = CursorMock.createTaskCursorMock(1, "name", null, null, null, null, 3, 1, Task.State.IN_PROGRESS.name());
		Task task = new Task(cursor);
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
		Cursor cursor = CursorMock.createTaskCursorMock(1, "name", null, null, null, null, 3, 1, Task.State.IN_PROGRESS.name());
		Task task = new Task(cursor);
		Assert.assertEquals(Task.TABLE_NAME, task.getTable());
	}

	@Test
	public void columnsTest() {
		Collection<String> columns = Task.getColumns();
		Assert.assertEquals(9, columns.size());
		List<String> list = new ArrayList<>(columns);
		Assert.assertEquals("project_id", list.get(7));
		Assert.assertEquals("state", list.get(8));
	}
}