package fr.socket.flo.todo.model;

import android.database.Cursor;
import android.graphics.Color;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Florian Arnould
 */
public class SorterTest {
	private static List<Project> _projects;

	@BeforeClass
	public static void prepareList() {
		_projects = new ArrayList<>();
		Cursor cursor = CursorMock.createProjectCursorMock(1, "Chloe", null, null, "03-04-2017", "12:03", 3, Color.BLACK, 1);
		_projects.add(new Project(cursor));
		Cursor cursor2 = CursorMock.createProjectCursorMock(2, "Paul", null, null, "01-04-2017", "12:03", 2, Color.BLACK, 1);
		_projects.add(new Project(cursor2));
		Cursor cursor3 = CursorMock.createProjectCursorMock(3, "Amandine", null, null, "02-04-2017", "12:03", 5, Color.BLACK, 1);
		_projects.add(new Project(cursor3));
		Cursor cursor4 = CursorMock.createProjectCursorMock(4, "Camille", null, null, "02-04-2017", "13:03", 1, Color.BLACK, 1);
		_projects.add(new Project(cursor4));
	}

	@Test
	public void orderByPriorityTest() {
		new Sorter<Project>().sort(Sorter.Sort.BY_PRIORITY, _projects);
		Assert.assertEquals(4, _projects.get(0).getId());
		Assert.assertEquals(2, _projects.get(1).getId());
		Assert.assertEquals(1, _projects.get(2).getId());
		Assert.assertEquals(3, _projects.get(3).getId());
	}

	@Test
	public void orderByNameTest() {
		new Sorter<Project>().sort(Sorter.Sort.BY_NAME, _projects);
		Assert.assertEquals(3, _projects.get(0).getId());
		Assert.assertEquals(4, _projects.get(1).getId());
		Assert.assertEquals(1, _projects.get(2).getId());
		Assert.assertEquals(2, _projects.get(3).getId());
	}

	@Test
	public void orderByDeadline() {
		new Sorter<Project>().sort(Sorter.Sort.BY_DEADLINE, _projects);
		Assert.assertEquals(2, _projects.get(0).getId());
		Assert.assertEquals(3, _projects.get(1).getId());
		Assert.assertEquals(4, _projects.get(2).getId());
		Assert.assertEquals(1, _projects.get(3).getId());
	}
}