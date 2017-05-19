package fr.socket.flo.todo.view.settings;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * @author Florian Arnould
 * @version 1.0
 */
public class SeekBarPreference extends DialogPreference implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {
	private static final String ANDROID_NS = "http://schemas.android.com/apk/res/android";
	private final Context _context;
	private final int _default;
	private final int _min;
	private final int _max;
	private SeekBar _seekBar;
	private TextView _valueText;
	private int _value;

	public SeekBarPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		_context = context;
		_default = attrs.getAttributeIntValue(ANDROID_NS, "defaultValue", 5);
		_max = attrs.getAttributeIntValue(ANDROID_NS, "max", 20);
		_min = 5;
		_value = 5;
	}

	@Override
	protected View onCreateDialogView() {

		LinearLayout.LayoutParams params;
		LinearLayout layout = new LinearLayout(_context);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setPadding(6, 6, 6, 6);

		_valueText = new TextView(_context);
		_valueText.setGravity(Gravity.CENTER_HORIZONTAL);
		_valueText.setTextSize(20);
		_valueText.setPadding(0, 20, 0, 0);
		params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		layout.addView(_valueText, params);

		_seekBar = new SeekBar(_context);
		_seekBar.setOnSeekBarChangeListener(this);
		layout.addView(_seekBar, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

		if (shouldPersist()) {
			_value = getPersistedInt(_default);
		}
		_seekBar.setMax(_max - _min);
		_seekBar.setProgress(_value - _min);

		return layout;
	}

	@Override
	protected void onBindDialogView(View v) {
		super.onBindDialogView(v);
		_seekBar.setMax(_max - _min);
		_seekBar.setProgress(_value - _min);
	}

	@Override
	protected void onSetInitialValue(boolean restore, Object defaultValue) {
		super.onSetInitialValue(restore, defaultValue);
		if (restore) {
			_value = shouldPersist() ? getPersistedInt(_default) : 0;
		} else {
			_value = (Integer)defaultValue;
		}
	}

	@Override
	public void onProgressChanged(SeekBar seek, int value, boolean fromTouch) {
		_valueText.setText(String.valueOf(value + _min));
	}

	@Override
	public void onStartTrackingTouch(SeekBar seek) {
		//onProgressChanged is the only listener needed
	}

	@Override
	public void onStopTrackingTouch(SeekBar seek) {
		//onProgressChanged is the only listener needed
	}

	@Override
	public void showDialog(Bundle state) {
		super.showDialog(state);
		Button positiveButton = ((AlertDialog)getDialog()).getButton(AlertDialog.BUTTON_POSITIVE);
		positiveButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (shouldPersist()) {
			_value = _seekBar.getProgress() + _min;
			persistInt(_value);
			callChangeListener(_value);
		}
		getDialog().dismiss();
	}
}
