package fr.socket.flo.todo.model;

import android.database.Cursor;
import android.graphics.Color;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Florian Arnould
 */
public class ProjectTest {
	private static Project project;

	@BeforeClass
	public static void createInstanceFromCursorTest() {
		Cursor cursor = Mockito.mock(Cursor.class);
		Mockito.when(cursor.getInt(0)).thenReturn(1);
		Mockito.when(cursor.getString(1)).thenReturn("name");
		Mockito.when(cursor.getString(2)).thenReturn("02-04-2017 12:03:00");
		Mockito.when(cursor.getInt(3)).thenReturn(3);
		Mockito.when(cursor.getInt(4)).thenReturn(Color.BLACK);
		Mockito.when(cursor.getInt(5)).thenReturn(1);
		project = new Project(cursor);
	}

	@Test
	public void colorTest() {
		Assert.assertEquals(Color.BLACK, project.getColor());
		project.setColor(Color.WHITE);
		Assert.assertEquals(Color.WHITE, project.getColor());
	}

	@Test
	public void favoriteTest() {
		Assert.assertTrue(project.isFavorite());
		project.setFavorite(false);
		Assert.assertFalse(project.isFavorite());
	}

	@Test
	public void currentTaskTest() {
		Assert.assertFalse(project.hasCurrentTask());
		project.setCurrentTaskId(3);
		Assert.assertTrue(project.hasCurrentTask());
		Assert.assertEquals(3, project.getCurrentTaskId());
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
		project.setNumberOfTasks(Task.State.WAITING, 2);
		project.setNumberOfTasks(Task.State.IN_PROGRESS, 3);
		project.setNumberOfTasks(Task.State.COMPLETED, 4);
		Assert.assertEquals(4.0 / 9, project.getCompleteProgress());
		Assert.assertEquals((3 + 4.0) / 9, project.getInProgress());
	}

	@Test
	public void getTableTest() {
		Assert.assertEquals(Project.TABLE_NAME, project.getTable());
	}
}