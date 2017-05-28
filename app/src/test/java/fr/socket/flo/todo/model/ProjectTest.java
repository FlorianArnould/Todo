package fr.socket.flo.todo.model;

import android.database.Cursor;
import android.graphics.Color;

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

import fr.socket.flo.todo.database.DataManager;
import fr.socket.flo.todo.database.OnNewObjectCreatedListener;

/**
 * @author Florian Arnould
 */
@RunWith(PowerMockRunner.class)
public class ProjectTest {
	private static Project _project;

	@BeforeClass
	public static void createInstanceFromCursorTest() {
		Cursor cursor = Mockito.mock(Cursor.class);
		Mockito.when(cursor.getInt(0)).thenReturn(1);
		Mockito.when(cursor.getString(1)).thenReturn("name");
		Mockito.when(cursor.getString(2)).thenReturn("02-04-2017 12:03:00");
		Mockito.when(cursor.getInt(3)).thenReturn(3);
		Mockito.when(cursor.getInt(4)).thenReturn(Color.BLACK);
		Mockito.when(cursor.getInt(5)).thenReturn(1);
		_project = new Project(cursor);
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
				Assert.assertTrue(savable instanceof Project);
				Nameable project = (Nameable)savable;
				Assert.assertEquals("name", project.getName());
				return null;
			}
		}).when(dataManager).save(Mockito.any(Savable.class), Mockito.any(OnNewObjectCreatedListener.class));
		PowerMockito.mockStatic(DataManager.class);
		PowerMockito.when(DataManager.getInstance()).thenReturn(dataManager);
		Project.newProject("name", null);
	}

	@Test
	public void colorTest() {
		Assert.assertEquals(Color.BLACK, _project.getColor());
		_project.setColor(Color.WHITE);
		Assert.assertEquals(Color.WHITE, _project.getColor());
	}

	@Test
	public void favoriteTest() {
		Assert.assertTrue(_project.isFavorite());
		_project.setFavorite(false);
		Assert.assertFalse(_project.isFavorite());
	}

	@Test
	public void currentTaskTest() {
		Assert.assertFalse(_project.hasCurrentTask());
		_project.setCurrentTaskId(3);
		Assert.assertTrue(_project.hasCurrentTask());
		Assert.assertEquals(3, _project.getCurrentTaskId());
	}

	@Test
	public void columnsTest() {
		Collection<String> columns = Project.getColumns();
		Assert.assertEquals(6, columns.size());
		List<String> list = new ArrayList<>(columns);
		int index = 0;
		Assert.assertEquals("id", list.get(index++));
		Assert.assertEquals("name", list.get(index++));
		Assert.assertEquals("deadline", list.get(index++));
		Assert.assertEquals("priority", list.get(index++));
		Assert.assertEquals("color", list.get(index++));
		Assert.assertEquals("is_favorite", list.get(index));
	}

	@Test
	public void progressTest() {
		_project.setNumberOfTasks(Task.State.WAITING, 2);
		_project.setNumberOfTasks(Task.State.IN_PROGRESS, 3);
		_project.setNumberOfTasks(Task.State.COMPLETED, 4);
		Assert.assertEquals(4.0 / 9, _project.getCompleteProgress());
		Assert.assertEquals((3 + 4.0) / 9, _project.getInProgress());
	}

	@Test
	public void getTableTest() {
		Assert.assertEquals(Project.TABLE_NAME, _project.getTable());
	}
}