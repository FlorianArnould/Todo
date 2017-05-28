package fr.socket.flo.todo.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Color;
import android.support.test.runner.AndroidJUnit4;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

/**
 * @author Florian Arnould
 */
@RunWith(AndroidJUnit4.class)
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
	public void wrongDateStringTest(){
		Cursor cursor = Mockito.mock(Cursor.class);
		Mockito.when(cursor.getInt(0)).thenReturn(1);
		Mockito.when(cursor.getString(1)).thenReturn("name");
		Mockito.when(cursor.getString(2)).thenReturn("02-04- 12:03:00");
		Mockito.when(cursor.getInt(3)).thenReturn(3);
		Mockito.when(cursor.getInt(4)).thenReturn(Color.BLACK);
		Mockito.when(cursor.getInt(5)).thenReturn(1);
		Work work = new Project(cursor);
		Assert.assertNull(work.getDeadline());
	}

	@Test
	public void contentValuesTest(){
		ContentValues values = _work.toContentValues();
		Assert.assertEquals("name", values.getAsString("name"));
		Assert.assertEquals("02-04-2017 12:03:00", values.getAsString("deadline"));
		Assert.assertEquals(3, (int)values.getAsInteger("priority"));
	}
}