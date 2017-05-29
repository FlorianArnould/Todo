package fr.socket.flo.todo.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Color;
import android.support.test.runner.AndroidJUnit4;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

import fr.socket.flo.todo.utils.MockUtils;

/**
 * @author Florian Arnould
 */
@RunWith(AndroidJUnit4.class)
public class ProjectTest {
	@Test
	public void contentValuesTest() {
		Cursor cursor = MockUtils.createProjectCursorMock(1, "name", null, null, null, null, 3, Color.BLACK, 1);
		Project project = new Project(cursor);
		project.setFavorite(true);
		project.setColor(Color.WHITE);
		ContentValues values = project.toContentValues();
		Assert.assertTrue(values.getAsBoolean("is_favorite"));
		Assert.assertEquals(Color.WHITE, values.get("color"));
	}
}