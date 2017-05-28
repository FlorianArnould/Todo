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
	private static Work _work;

	@BeforeClass
	public static void createInstanceFromCursorTest() {
		Cursor cursor = Mockito.mock(Cursor.class);
		Mockito.when(cursor.getInt(0)).thenReturn(1);
		Mockito.when(cursor.getString(1)).thenReturn("name");
		Mockito.when(cursor.getString(2)).thenReturn("02-04-2017 12:03:00");
		Mockito.when(cursor.getInt(3)).thenReturn(3);
		Mockito.when(cursor.getInt(4)).thenReturn(Color.BLACK);
		Mockito.when(cursor.getInt(5)).thenReturn(1);
		_work = new Project(cursor);
	}

	@Test
	public void idTest() {
		Assert.assertEquals(1, _work.getId());
	}

	@Test
	public void nameTest() {
		Assert.assertEquals("name", _work.getName());
		_work.setName("other name");
		Assert.assertEquals("other name", _work.getName());
	}

	@Test
	public void deadlineTest() {
		Assert.assertTrue(_work.hasDeadline());
		Assert.assertEquals("02-04-2017 12:03", _work.getDeadlineAsString());
		_work.setDeadline(null);
		Assert.assertFalse(_work.hasDeadline());
		Assert.assertNull(_work.getDeadline());
		Assert.assertTrue(_work.getDeadlineAsString().toString().isEmpty());
		Assert.assertTrue(_work.getDeadlineDateAsString().toString().isEmpty());
		Assert.assertTrue(_work.getDeadlineTimeAsString().toString().isEmpty());
		Date date = Calendar.getInstance().getTime();
		_work.setDeadline(date);
		Assert.assertEquals(0, date.compareTo(_work.getDeadline()));
	}

	@Test
	public void priorityTest() {
		Assert.assertEquals(3, _work.getPriority());
		_work.setPriority(5);
		Assert.assertEquals(5, _work.getPriority());
	}

	@Test
	public void compareTest(){
		Work other = _work;
		other.setName("water");
		createInstanceFromCursorTest();
		Assert.assertTrue(_work.compareByName(other) < 0);
		other.setPriority(5);
		Assert.assertTrue(_work.compareByPriority(other) < 0);
		other.setDeadline(Calendar.getInstance().getTime());
		Assert.assertTrue(_work.compareByDeadline(other) < 0);
	}
}