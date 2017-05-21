package fr.socket.flo.todo.view.graphics;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.annotation.ColorInt;

/**
 * @author Florian Arnould
 * @version 1.0
 */
abstract class TextDrawable extends ShapeDrawable {
	private final Paint _textPaint;
	private final String _text;

	TextDrawable(String letter, @ColorInt int backgroundColor, @ColorInt int textColor) {
		super(new OvalShape());
		_text = letter.toUpperCase();
		_textPaint = new Paint();
		_textPaint.setColor(Color.argb(170, Color.red(textColor), Color.green(textColor), Color.blue(textColor)));
		_textPaint.setAntiAlias(true);
		_textPaint.setFakeBoldText(true);
		_textPaint.setStyle(Paint.Style.FILL);
		_textPaint.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
		_textPaint.setTextAlign(Paint.Align.CENTER);
		Paint paint = getPaint();
		paint.setColor(backgroundColor);
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		Rect r = getBounds();
		int count = canvas.save();
		canvas.translate(r.left, r.top);

		// draw text
		_textPaint.setTextSize(Math.min(r.width(), r.height()) / 2);
		canvas.drawText(_text, r.width() / 2, r.height() / 2 - ((_textPaint.descent() + _textPaint.ascent()) / 2), _textPaint);

		canvas.restoreToCount(count);
	}

	@Override
	public void setAlpha(int alpha) {
		_textPaint.setAlpha(alpha);
	}

	@Override
	public void setColorFilter(ColorFilter cf) {
		_textPaint.setColorFilter(cf);
	}

	@Override
	public int getOpacity() {
		return PixelFormat.TRANSLUCENT;
	}

	@Override
	public int getIntrinsicWidth() {
		return getBounds().width();
	}

	@Override
	public int getIntrinsicHeight() {
		return getBounds().height();
	}
}