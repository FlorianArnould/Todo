package fr.socket.flo.todo.utils;

import android.database.Cursor;
import android.support.annotation.ColorInt;

import org.mockito.Mockito;

/**
 * @author Florian Arnould
 */
public class MockUtils {
	public static Cursor createProjectCursorMock(int id, String name, String start_date, String start_time, String deadline_date, String deadline_time, int priority, @ColorInt int color, int isFavorite) {
		Cursor cursor = createWorkCursorMock(id, name, start_date, start_time, deadline_date, deadline_time, priority);
		Mockito.when(cursor.getInt(7)).thenReturn(color);
		Mockito.when(cursor.getInt(8)).thenReturn(isFavorite);
		return cursor;
	}

	public static Cursor createTaskCursorMock(int id, String name, String start_date, String start_time, String deadline_date, String deadline_time, int priority, int projectId, String state) {
		Cursor cursor = createWorkCursorMock(id, name, start_date, start_time, deadline_date, deadline_time, priority);
		Mockito.when(cursor.getInt(7)).thenReturn(projectId);
		Mockito.when(cursor.getString(8)).thenReturn(state);
		return cursor;
	}

	private static Cursor createWorkCursorMock(int id, String name, String start_date, String start_time, String deadline_date, String deadline_time, int priority) {
		Cursor cursor = Mockito.mock(Cursor.class);
		Mockito.when(cursor.getInt(0)).thenReturn(id);
		Mockito.when(cursor.getString(1)).thenReturn(name);
		Mockito.when(cursor.getString(2)).thenReturn(start_date);
		Mockito.when(cursor.getString(3)).thenReturn(start_time);
		Mockito.when(cursor.getString(4)).thenReturn(deadline_date);
		Mockito.when(cursor.getString(5)).thenReturn(deadline_time);
		Mockito.when(cursor.getInt(6)).thenReturn(priority);
		return cursor;
	}
}
