package fr.socket.flo.todo.model;

import android.database.Cursor;
import android.graphics.Color;

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

import fr.socket.flo.todo.database.DataManager;
import fr.socket.flo.todo.database.OnNewObjectCreatedListener;

/**
 * @author Florian Arnould
 */
@RunWith(PowerMockRunner.class)
public class ProjectTest {
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
		Cursor cursor = CursorMock.createProjectCursorMock(1, "name", null, null, null, null, 3, Color.BLACK, 1);
		Project project = new Project(cursor);
		Assert.assertEquals(Color.BLACK, project.getColor());
		project.setColor(Color.WHITE);
		Assert.assertEquals(Color.WHITE, project.getColor());
	}

	@Test
	public void favoriteTest() {
		Cursor cursor = CursorMock.createProjectCursorMock(1, "name", null, null, null, null, 3, Color.BLACK, 1);
		Project project = new Project(cursor);
		Assert.assertTrue(project.isFavorite());
		project.setFavorite(false);
		Assert.assertFalse(project.isFavorite());
	}

	@Test
	public void currentTaskTest() {
		Cursor cursor = CursorMock.createProjectCursorMock(1, "name", null, null, null, null, 3, Color.BLACK, 1);
		Project project = new Project(cursor);
		Assert.assertFalse(project.hasCurrentTask());
		project.setCurrentTaskId(3);
		Assert.assertTrue(project.hasCurrentTask());
		Assert.assertEquals(3, project.getCurrentTaskId());
	}

	@Test
	public void columnsTest() {
		Collection<String> columns = Project.getColumns();
		Assert.assertEquals(9, columns.size());
		List<String> list = new ArrayList<>(columns);
		int index = 0;
		Assert.assertEquals("id", list.get(index++));
		Assert.assertEquals("name", list.get(index++));
		Assert.assertEquals("start_date", list.get(index++));
		Assert.assertEquals("start_time", list.get(index++));
		Assert.assertEquals("deadline_date", list.get(index++));
		Assert.assertEquals("deadline_time", list.get(index++));
		Assert.assertEquals("priority", list.get(index++));
		Assert.assertEquals("color", list.get(index++));
		Assert.assertEquals("is_favorite", list.get(index));
	}

	@Test
	public void progressTest() {
		Cursor cursor = CursorMock.createProjectCursorMock(1, "name", null, null, null, null, 3, Color.BLACK, 1);
		Project project = new Project(cursor);
		project.setNumberOfTasks(Task.State.WAITING, 2);
		project.setNumberOfTasks(Task.State.IN_PROGRESS, 3);
		project.setNumberOfTasks(Task.State.COMPLETED, 4);
		Assert.assertEquals(4.0 / 9, project.getCompleteProgress());
		Assert.assertEquals((3 + 4.0) / 9, project.getInProgress());
	}

	@Test
	public void getTableTest() {
		Cursor cursor = CursorMock.createProjectCursorMock(1, "name", null, null, null, null, 3, Color.BLACK, 1);
		Project project = new Project(cursor);
		Assert.assertEquals(Project.TABLE_NAME, project.getTable());
	}
}