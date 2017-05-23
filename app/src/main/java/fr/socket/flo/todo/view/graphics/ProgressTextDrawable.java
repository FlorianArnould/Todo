package fr.socket.flo.todo.view.graphics;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.ColorInt;

/**
 * @author Florian Arnould
 * @version 1.0
 */
public class ProgressTextDrawable extends TextDrawable {
	private final Paint _progressPaint;
	private final double _progress;

	public ProgressTextDrawable(String letter, @ColorInt int backgroundColor, @ColorInt int progressColor, double progress) {
		super(letter, backgroundColor, Color.WHITE);
		_progressPaint = new Paint();
		_progressPaint.setColor(progressColor);
		_progress = progress;
	}

	@Override
	public void drawMore(Canvas canvas) {
		Rect r = getBounds();
		RectF rectF = new RectF(r);
		canvas.drawArc(rectF, -90, (int)(_progress * 360), true, _progressPaint);
		super.drawMore(canvas);
	}
}
