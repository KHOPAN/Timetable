package com.khopan.timetable.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Toast;

import androidx.preference.EditTextPreference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class EditDateFormatPreference extends EditTextPreference {
	private OnDateFormatChangeListener listener;
	private DateFormat defaultDateFormat;

	public EditDateFormatPreference(Context context, AttributeSet attribute, int styleAttribute, int styleResource) {
		super(context, attribute, styleAttribute, styleResource);
	}

	public EditDateFormatPreference(Context context, AttributeSet attribute, int styleAttribute) {
		super(context, attribute, styleAttribute);
	}

	public EditDateFormatPreference(Context context, AttributeSet attribute) {
		super(context, attribute);
	}

	public EditDateFormatPreference(Context context) {
		super(context);
	}

	public void setOnDateFormatChangeListener(OnDateFormatChangeListener listener) {
		this.listener = listener;
	}

	public void setDefaultDateFormat(DateFormat format) {
		this.defaultDateFormat = format;
	}

	@Override
	public void setText(String text) {
		super.setText(text);
		this.setSummary(text);

		if(this.listener != null) {
			DateFormat format;

			try {
				format = new SimpleDateFormat(text, Locale.getDefault());
				format.format(Calendar.getInstance().getTime());
			} catch(Throwable Errors) {
				format = this.defaultDateFormat;
				Toast.makeText(this.getContext(), "Invalid date format, using the default format.", Toast.LENGTH_SHORT).show();
			}

			if(format != null) {
				this.listener.dateFormatChanged(format);
			}
		}
	}

	public interface OnDateFormatChangeListener {
		void dateFormatChanged(DateFormat format);
	}
}
