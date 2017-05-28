package fr.socket.flo.todo.model;

import android.database.Cursor;
import android.graphics.Color;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Florian Arnould
 */
public class SorterTest {
	private static List<Project> projects;

	@BeforeClass
	public static void prepareList() {
		projects = new ArrayList<>();
		Cursor cursor = Mockito.mock(Cursor.class);
		Mockito.when(cursor.getInt(0)).thenReturn(1);
		Mockito.when(cursor.getString(1)).thenReturn("Chloe");
		Mockito.when(cursor.getString(2)).thenReturn("03-04-2017 12:03:00");
		Mockito.when(cursor.getInt(3)).thenReturn(3);
		Mockito.when(cursor.getInt(4)).thenReturn(Color.BLACK);
		Mockito.when(cursor.getInt(5)).thenReturn(1);
		projects.add(new Project(cursor));
		Cursor cursor2 = Mockito.mock(Cursor.class);
		Mockito.when(cursor2.getInt(0)).thenReturn(2);
		Mockito.when(cursor2.getString(1)).thenReturn("Paul");
		Mockito.when(cursor2.getString(2)).thenReturn("01-04-2017 12:03:00");
		Mockito.when(cursor2.getInt(3)).thenReturn(2);
		Mockito.when(cursor2.getInt(4)).thenReturn(Color.BLACK);
		Mockito.when(cursor2.getInt(5)).thenReturn(1);
		projects.add(new Project(cursor2));
		Cursor cursor3 = Mockito.mock(Cursor.class);
		Mockito.when(cursor3.getInt(0)).thenReturn(3);
		Mockito.when(cursor3.getString(1)).thenReturn("Amandine");
		Mockito.when(cursor3.getString(2)).thenReturn("02-04-2017 12:03:00");
		Mockito.when(cursor3.getInt(3)).thenReturn(5);
		Mockito.when(cursor3.getInt(4)).thenReturn(Color.BLACK);
		Mockito.when(cursor3.getInt(5)).thenReturn(1);
		projects.add(new Project(cursor3));
		Cursor cursor4 = Mockito.mock(Cursor.class);
		Mockito.when(cursor4.getInt(0)).thenReturn(4);
		Mockito.when(cursor4.getString(1)).thenReturn("Camille");
		Mockito.when(cursor4.getString(2)).thenReturn("02-04-2017 13:03:00");
		Mockito.when(cursor4.getInt(3)).thenReturn(1);
		Mockito.when(cursor4.getInt(4)).thenReturn(Color.BLACK);
		Mockito.when(cursor4.getInt(5)).thenReturn(1);
		projects.add(new Project(cursor4));
	}

	@Test
	public void orderByPriorityTest(){
		Sorter.sort(Sorter.Sort.BY_PRIORITY, projects);
		Assert.assertEquals(4, projects.get(0).getId());
		Assert.assertEquals(2, projects.get(1).getId());
		Assert.assertEquals(1, projects.get(2).getId());
		Assert.assertEquals(3, projects.get(3).getId());
	}

	@Test
	public void orderByNameTest(){
		Sorter.sort(Sorter.Sort.BY_NAME, projects);
		Assert.assertEquals(3, projects.get(0).getId());
		Assert.assertEquals(4, projects.get(1).getId());
		Assert.assertEquals(1, projects.get(2).getId());
		Assert.assertEquals(2, projects.get(3).getId());
	}

	@Test
	public void orderByDeadline(){
		Sorter.sort(Sorter.Sort.BY_DEADLINE, projects);
		Assert.assertEquals(2, projects.get(0).getId());
		Assert.assertEquals(3, projects.get(1).getId());
		Assert.assertEquals(4, projects.get(2).getId());
		Assert.assertEquals(1, projects.get(3).getId());
	}
}