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

import fr.socket.flo.todo.utils.MockUtils;

/**
 * @author Florian Arnould
 */
@RunWith(AndroidJUnit4.class)
public class WorkTest {

	@Test
	public void contentValuesTest(){
		Cursor cursor = MockUtils.createProjectCursorMock(1, "name", "01-04-2017", null, "02-04-2017", "12:03", 3, Color.BLACK, 1);
		Work work = new Project(cursor);
		ContentValues values = work.toContentValues();
		Assert.assertEquals("name", values.getAsString("name"));
		Assert.assertEquals("01-04-2017", values.getAsString("start_date"));
		Assert.assertNull(values.getAsString("start_time"));
		Assert.assertEquals("02-04-2017", values.getAsString("deadline_date"));
		Assert.assertEquals("12:03", values.getAsString("deadline_time"));
		Assert.assertEquals(3, (int)values.getAsInteger("priority"));
	}
}