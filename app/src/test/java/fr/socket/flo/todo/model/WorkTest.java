package fr.socket.flo.todo.model;

import android.database.Cursor;
import android.graphics.Color;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Calendar;
import java.util.Date;

/**
 * @author Florian Arnould
 */
public class WorkTest {
	private static Work work;

	@BeforeClass
	public static void createInstanceFromCursorTest() {
		Cursor cursor = Mockito.mock(Cursor.class);
		Mockito.when(cursor.getInt(0)).thenReturn(1);
		Mockito.when(cursor.getString(1)).thenReturn("name");
		Mockito.when(cursor.getString(2)).thenReturn("02-04-2017 12:03:00");
		Mockito.when(cursor.getInt(3)).thenReturn(3);
		Mockito.when(cursor.getInt(4)).thenReturn(Color.BLACK);
		Mockito.when(cursor.getInt(5)).thenReturn(1);
		work = new Project(cursor);
	}

	@Test
	public void idTest() {
		Assert.assertEquals(1, work.getId());
	}

	@Test
	public void nameTest() {
		Assert.assertEquals("name", work.getName());
		work.setName("other name");
		Assert.assertEquals("other name", work.getName());
	}

	@Test
	public void deadlineTest() {
		Assert.assertTrue(work.hasDeadline());
		Assert.assertEquals("02-04-2017 12:03", work.getDeadlineAsString());
		work.setDeadline(null);
		Assert.assertFalse(work.hasDeadline());
		Assert.assertNull(work.getDeadline());
		Assert.assertTrue(work.getDeadlineAsString().toString().isEmpty());
		Assert.assertTrue(work.getDeadlineDateAsString().toString().isEmpty());
		Assert.assertTrue(work.getDeadlineTimeAsString().toString().isEmpty());
		Date date = Calendar.getInstance().getTime();
		work.setDeadline(date);
		Assert.assertEquals(0, date.compareTo(work.getDeadline()));
	}

	@Test
	public void priorityTest() {
		Assert.assertEquals(3, work.getPriority());
		work.setPriority(5);
		Assert.assertEquals(5, work.getPriority());
	}

	@Test
	public void compareTest(){
		Work other = work;
		other.setName("water");
		createInstanceFromCursorTest();
		Assert.assertTrue(work.compareByName(other) < 0);
		other.setPriority(5);
		Assert.assertTrue(work.compareByPriority(other) < 0);
		other.setDeadline(Calendar.getInstance().getTime());
		Assert.assertTrue(work.compareByDeadline(other) < 0);
	}
}