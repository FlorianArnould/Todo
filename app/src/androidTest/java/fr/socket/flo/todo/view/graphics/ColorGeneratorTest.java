package fr.socket.flo.todo.view.graphics;

import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.test.runner.AndroidJUnit4;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Florian Arnould
 */
@RunWith(AndroidJUnit4.class)
public class ColorGeneratorTest {
	@Test
	public void darkerColorTest(){
		@ColorInt int darker = ColorGenerator.darkerColor(Color.BLUE);
		float[] colorHsv = new float[3];
		float[] darkerHsv = new float[3];
		Color.colorToHSV(Color.BLUE, colorHsv);
		Color.colorToHSV(darker, darkerHsv);
		Assert.assertEquals((float)(colorHsv[2]-0.2), darkerHsv[2]);
	}

	@Test
	public void priorityColorTest(){
		Assert.assertEquals(-2035152, ColorGenerator.priorityColor(6));
	}

	@Test
	public void textColorOfLatestPriorityColorTest(){
		ColorGenerator.priorityColor(2);
		Assert.assertEquals(Color.WHITE, ColorGenerator.textColorOfLatestPriorityColor());
		ColorGenerator.priorityColor(7);
		Assert.assertEquals(Color.BLACK, ColorGenerator.textColorOfLatestPriorityColor());
	}
}
