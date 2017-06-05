package fr.socket.flo.todo.model;

import android.database.Cursor;
import android.graphics.Color;

import junit.framework.Assert;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Florian Arnould
 */
public class WorkTest {
	@Test
	public void idTest() {
		Cursor cursor = CursorMock.createProjectCursorMock(1, "name", null, null, null, null, 3, Color.BLACK, 1);
		Work work = new Project(cursor);
		Assert.assertEquals(1, work.getId());
	}

	@Test
	public void nameTest() {
		Cursor cursor = CursorMock.createProjectCursorMock(1, "name", null, null, null, null, 3, Color.BLACK, 1);
		Work work = new Project(cursor);
		Assert.assertEquals("name", work.getName());
		work.setName("other name");
		Assert.assertEquals("other name", work.getName());
	}

	@Test
	public void priorityTest() {
		Cursor cursor = CursorMock.createProjectCursorMock(1, "name", null, null, null, null, 3, Color.BLACK, 1);
		Work work = new Project(cursor);
		Assert.assertEquals(3, work.getPriority());
		work.setPriority(5);
		Assert.assertEquals(5, work.getPriority());
	}

	@Test
	public void getDeadlineTest() {
		Cursor cursor = CursorMock.createProjectCursorMock(1, "name", null, null, "02-04-2017", null, 3, Color.BLACK, 1);
		Work work = new Project(cursor);
		DateTime deadline = work.getDeadline();
		Assert.assertEquals("02-04-2017", deadline.toString());
	}

	@Test
	public void columnsTest() {
		List<String> list = new ArrayList<>(Project.getColumns());
		int index = 0;
		Assert.assertEquals("id", list.get(index++));
		Assert.assertEquals("name", list.get(index++));
		Assert.assertEquals("start_date", list.get(index++));
		Assert.assertEquals("start_time", list.get(index++));
		Assert.assertEquals("deadline_date", list.get(index++));
		Assert.assertEquals("deadline_time", list.get(index++));
		Assert.assertEquals("priority", list.get(index));
	}

	@Test
	public void compareTest() {
		Cursor cursor = CursorMock.createProjectCursorMock(1, "name", null, null, "24-05-2017", null, 3, Color.BLACK, 1);
		Work work = new Project(cursor);
		Cursor cursor2 = CursorMock.createProjectCursorMock(1, "water", null, null, "25-05-2017", null, 5, Color.BLACK, 1);
		Work other = new Project(cursor2);
		Assert.assertTrue(work.compareByName(other) < 0);
		Assert.assertTrue(work.compareByPriority(other) < 0);
		Assert.assertTrue(work.compareByDeadline(other) < 0);
	}
}