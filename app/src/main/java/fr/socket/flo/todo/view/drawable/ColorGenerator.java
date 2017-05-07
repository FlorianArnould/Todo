package fr.socket.flo.todo.view.drawable;

import android.graphics.Color;
import android.support.annotation.ColorInt;

import java.util.Arrays;
import java.util.List;

/**
 * @author Florian Arnould
 * @version 1.0
 */
public class ColorGenerator {
	private static float _darkerCoef = (float)0.2;
	private static List<Integer> _colors = Arrays.asList(
			0xffe57373,
			0xfff06292,
			0xffba68c8,
			0xff9575cd,
			0xff7986cb,
			0xff64b5f6,
			0xff4fc3f7,
			0xff4dd0e1,
			0xff4db6ac,
			0xff81c784,
			0xffaed581,
			0xffff8a65,
			0xffd4e157,
			0xffffd54f,
			0xffffb74d,
			0xffa1887f,
			0xff90a4ae
	);

	public static
	@ColorInt
	int randomColor() {
		return _colors.get((int)(Math.random() * _colors.size()));
	}

	public static
	@ColorInt
	int darkerColor(@ColorInt int color) {
		float hsv[] = new float[3];
		Color.colorToHSV(color, hsv);
		hsv[2] -= _darkerCoef;
		return Color.HSVToColor(hsv);
	}
}
