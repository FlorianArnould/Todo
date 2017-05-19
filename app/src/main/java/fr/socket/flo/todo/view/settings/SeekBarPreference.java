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
	// ------------------------------------------------------------------------------------------
	// Private attributes :
	private static final String ANDROID_NS = "http://schemas.android.com/apk/res/android";
	private SeekBar _seekBar;
	private TextView _splashText, _valueText;
	private Context _context;
	private String _dialogMessage, _suffix;
	private int _default, _min, _max, _value = 0;
	// ------------------------------------------------------------------------------------------

	// ------------------------------------------------------------------------------------------
	// Constructor :
	public SeekBarPreference(Context context, AttributeSet attrs) {

		super(context, attrs);
		_context = context;

		// Get string value for dialogMessage :
		int dialogMessageId = attrs.getAttributeResourceValue(ANDROID_NS, "dialogMessage", 0);
		if (dialogMessageId == 0)
			_dialogMessage = attrs.getAttributeValue(ANDROID_NS, "dialogMessage");
		else _dialogMessage = _context.getString(dialogMessageId);

		// Get string value for suffix (text attribute in xml file) :
		int suffixId = attrs.getAttributeResourceValue(ANDROID_NS, "text", 0);
		if (suffixId == 0) _suffix = attrs.getAttributeValue(ANDROID_NS, "text");
		else _suffix = _context.getString(suffixId);

		// Get default and max seekbar values :
		_default = attrs.getAttributeIntValue(ANDROID_NS, "defaultValue", 0);
		_max = attrs.getAttributeIntValue(ANDROID_NS, "max", 20);
		_min = 5;
	}
	// ------------------------------------------------------------------------------------------

	// ------------------------------------------------------------------------------------------
	// DialogPreference methods :
	@Override
	protected View onCreateDialogView() {

		LinearLayout.LayoutParams params;
		LinearLayout layout = new LinearLayout(_context);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setPadding(6, 6, 6, 6);

		_splashText = new TextView(_context);
		_splashText.setPadding(30, 10, 30, 10);
		if (_dialogMessage != null)
			_splashText.setText(_dialogMessage);
		layout.addView(_splashText);

		_valueText = new TextView(_context);
		_valueText.setGravity(Gravity.CENTER_HORIZONTAL);
		_valueText.setTextSize(32);
		params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		layout.addView(_valueText, params);

		_seekBar = new SeekBar(_context);
		_seekBar.setOnSeekBarChangeListener(this);
		layout.addView(_seekBar, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

		if (shouldPersist())
			_value = getPersistedInt(_default);

		_seekBar.setMax(_max-_min);
		_seekBar.setProgress(_value-_min);

		return layout;
	}

	@Override
	protected void onBindDialogView(View v) {
		super.onBindDialogView(v);
		_seekBar.setMax(_max-_min);
		_seekBar.setProgress(_value-_min);
	}

	@Override
	protected void onSetInitialValue(boolean restore, Object defaultValue) {
		super.onSetInitialValue(restore, defaultValue);
		if (restore)
			_value = shouldPersist() ? getPersistedInt(_default) : 0;
		else
			_value = (Integer)defaultValue;
	}
	// ------------------------------------------------------------------------------------------

	// ------------------------------------------------------------------------------------------
	// OnSeekBarChangeListener methods :
	@Override
	public void onProgressChanged(SeekBar seek, int value, boolean fromTouch) {
		String t = String.valueOf(value);
		_valueText.setText(_suffix == null ? t : t.concat(" " + _suffix));
	}

	@Override
	public void onStartTrackingTouch(SeekBar seek) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar seek) {
	}

	// ------------------------------------------------------------------------------------------

	// ------------------------------------------------------------------------------------------
	// Set the positive button listener and onClick action :
	@Override
	public void showDialog(Bundle state) {

		super.showDialog(state);

		Button positiveButton = ((AlertDialog)getDialog()).getButton(AlertDialog.BUTTON_POSITIVE);
		positiveButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (shouldPersist()) {

			_value = _seekBar.getProgress();
			persistInt(_seekBar.getProgress());
			callChangeListener(_seekBar.getProgress()+_min);
		}
		getDialog().dismiss();
	}
	// ------------------------------------------------------------------------------------------
}
