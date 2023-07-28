package com.khopan.timetable.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.picker.app.SeslTimePickerDialog;
import androidx.preference.DropDownPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceDataStore;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.khopan.timetable.data.Subject;
import com.khopan.timetable.data.SubjectData;
import com.khopan.timetable.data.SubjectDataList;
import com.khopan.timetable.settings.FragmentSettingsActivity;

import dev.oneuiproject.oneui.widget.Toast;

public class DaySubjectSettingsFragment extends PreferenceFragmentCompat {
	private Context context;
	private SharedPreferences preferences;
	private int startTimeHour;
	private int startTimeMinute;
	private int endTimeHour;
	private int endTimeMinute;

	private DropDownPreference subjectSelectPreference;

	@Override
	public void onAttach(@NonNull Context context) {
		super.onAttach(context);
		this.context = context;
	}

	@Override
	public void onCreatePreferences(Bundle bundle, String rootKey) {
		this.preferences = PreferenceManager.getDefaultSharedPreferences(this.context);
		SubjectData[] dataList = null;

		if(this.preferences.contains("subjectList")) {
			String subject = this.preferences.getString("subjectList", "");

			if(subject != null && !subject.isEmpty()) {
				try {
					ObjectMapper mapper = new ObjectMapper();
					SubjectDataList list = mapper.readValue(subject, SubjectDataList.class);

					if(list.dataList != null) {
						dataList = list.dataList;
					}
				} catch(Throwable ignored) {

				}
			}
		}

		if(dataList == null || dataList.length == 0) {
			Toast.makeText(this.context, "Error: Empty Subject", Toast.LENGTH_SHORT).show();
			this.getActivity().finish();
			return;
		}

		PreferenceManager manager = this.getPreferenceManager();
		PreferenceScreen screen = manager.createPreferenceScreen(this.context);
		PreferenceCategory category = new PreferenceCategory(this.context);
		screen.addPreference(category);
		this.subjectSelectPreference = new DropDownPreference(this.context);
		this.subjectSelectPreference.setPreferenceDataStore(new PreferenceDataStore() {
			@Nullable
			@Override
			public String getString(String key, @Nullable String defaultValue) {
				return defaultValue;
			}

			@Override
			public void putString(String key, @Nullable String value) {
				DaySubjectSettingsFragment.this.subjectSelectPreference.setSummary(DaySubjectSettingsFragment.this.subjectSelectPreference.getEntry());
			}
		});

		this.subjectSelectPreference.setKey("subject");
		this.subjectSelectPreference.setTitle("Subject");
		CharSequence[] entryList = new CharSequence[dataList.length];
		CharSequence[] entryValueList = new CharSequence[entryList.length];

		for(int i = 0; i < entryList.length; i++) {
			entryList[i] = dataList[i].subjectName;
			entryValueList[i] = Integer.toString(i);
		}

		this.subjectSelectPreference.setEntries(entryList);
		this.subjectSelectPreference.setEntryValues(entryValueList);
		this.subjectSelectPreference.setDefaultValue(entryValueList[0]);
		category.addPreference(this.subjectSelectPreference);
		Preference startTimePickerPreference = new Preference(this.context);
		startTimePickerPreference.setTitle("Start Time");
		startTimePickerPreference.setSummary("00:00");
		SeslTimePickerDialog startTimePicker = new SeslTimePickerDialog(this.context, (view, hour, minute) -> {
			startTimePickerPreference.setSummary(String.format("%02d:%02d", hour, minute));
			this.startTimeHour = hour;
			this.startTimeMinute = minute;
		}, 0, 0, true);

		startTimePickerPreference.setOnPreferenceClickListener(preference -> {
			startTimePicker.show();
			return true;
		});

		category.addPreference(startTimePickerPreference);
		Preference endTimePickerPreference = new Preference(this.context);
		endTimePickerPreference.setTitle("End Time");
		endTimePickerPreference.setSummary("00:00");
		SeslTimePickerDialog endTimePicker = new SeslTimePickerDialog(this.context, (view, hour, minute) -> {
			endTimePickerPreference.setSummary(String.format("%02d:%02d", hour, minute));
			this.endTimeHour = hour;
			this.endTimeMinute = minute;
		}, 0, 0, true);

		endTimePickerPreference.setOnPreferenceClickListener(preference -> {
			endTimePicker.show();
			return true;
		});

		category.addPreference(endTimePickerPreference);
		PreferenceCategory controlCategory = new PreferenceCategory(this.context);
		screen.addPreference(controlCategory);
		Preference donePreference = new Preference(this.context);
		donePreference.setTitle("Done");
		donePreference.setOnPreferenceClickListener(preference -> {
			Intent intent = new Intent();
			Subject subject = new Subject();
			subject.subjectDataIndex = this.subjectSelectPreference.findIndexOfValue(this.subjectSelectPreference.getValue());
			subject.startTimeHour = this.startTimeHour;
			subject.startTimeMinute = this.startTimeMinute;
			subject.endTimeHour = this.endTimeHour;
			subject.endTimeMinute = this.endTimeMinute;
			intent.putExtra("subject", subject);
			return FragmentSettingsActivity.finish(this, Activity.RESULT_OK, intent);
		});

		controlCategory.addPreference(donePreference);
		Preference cancelPreference = new Preference(this.context);
		cancelPreference.setTitle("Cancel");
		cancelPreference.setOnPreferenceClickListener(preference -> FragmentSettingsActivity.finish(this));
		controlCategory.addPreference(cancelPreference);
		this.setPreferenceScreen(screen);
	}
}
