package com.khopan.timetable.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.MultiSelectListPreference;
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
import com.khopan.timetable.data.SubjectList;
import com.khopan.timetable.settings.FragmentSettingsActivity;
import com.khopan.timetable.settings.FragmentTitle;
import com.sec.sesl.khopan.timetable.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import dev.oneuiproject.oneui.widget.Toast;

public class DaySettingsFragment extends PreferenceFragmentCompat {
	private final List<Preference> preferenceList;
	private final List<Subject> subjectList;

	private Context context;
	private ActivityResultLauncher<Intent> launcher;
	private boolean enableAddingData;
	private String dayIdentifier;

	private SharedPreferences preferences;
	private PreferenceCategory subjectCategory;
	private MultiSelectListPreference removeButton;

	public DaySettingsFragment() {
		this.preferenceList = new ArrayList<>();
		this.subjectList = new ArrayList<>();
	}

	@Override
	public void onAttach(@NonNull Context context) {
		super.onAttach(context);
		this.context = context;
		this.launcher = this.registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
			if(result.getResultCode() == Activity.RESULT_OK) {
				Intent data = result.getData();

				if(data != null) {
					Serializable hopefullySubject = data.getSerializableExtra("subject");

					if(hopefullySubject instanceof Subject) {
						this.addSubject((Subject) hopefullySubject);
					}
				}
			}
		});
	}

	@Override
	public void onCreatePreferences(Bundle bundle, String rootKey) {
		Bundle arguments = this.getArguments();
		String dayName = arguments.getString("dayName");
		this.dayIdentifier = arguments.getString("dayIdentifier");
		this.enableAddingData = true;
		this.preferences = PreferenceManager.getDefaultSharedPreferences(this.context);
		PreferenceManager manager = this.getPreferenceManager();
		PreferenceScreen screen = manager.createPreferenceScreen(this.context);
		this.subjectCategory = new PreferenceCategory(this.context);
		screen.addPreference(this.subjectCategory);
		PreferenceCategory controlCategory = new PreferenceCategory(this.context);
		screen.addPreference(controlCategory);
		Preference addSubjectPreference = new Preference(this.context);
		addSubjectPreference.setTitle("Add Subject");
		addSubjectPreference.setIcon(R.drawable.add_icon);
		addSubjectPreference.setOnPreferenceClickListener(preference -> FragmentSettingsActivity.launch(this, FragmentTitle.title(dayName, "Add Subject"), this.launcher, DaySubjectSettingsFragment.class, null));
		controlCategory.addPreference(addSubjectPreference);
		this.removeButton = new MultiSelectListPreference(this.context);
		this.removeButton.setPreferenceDataStore(new PreferenceDataStore() {
			@Override
			public String getString(String key, @Nullable String defaultValue) {
				return defaultValue;
			}

			@Override
			public void putStringSet(String key, @Nullable Set<String> values) {
				if(values != null && values.size() > 0) {
					List<Preference> removePreferenceList = new ArrayList<>();
					List<Subject> removeSubjectList = new ArrayList<>();

					for(String value : values) {
						int index = Integer.parseInt(value);
						Preference preference = DaySettingsFragment.this.preferenceList.get(index);
						removePreferenceList.add(DaySettingsFragment.this.preferenceList.get(index));
						removeSubjectList.add(DaySettingsFragment.this.subjectList.get(index));
						DaySettingsFragment.this.subjectCategory.removePreference(preference);
					}

					DaySettingsFragment.this.preferenceList.removeAll(removePreferenceList);
					DaySettingsFragment.this.subjectList.removeAll(removeSubjectList);
					DaySettingsFragment.this.updateRemoveButton();
				}
			}
		});

		this.removeButton.setKey("removeSubject");
		this.removeButton.setTitle("Remove Subject");
		this.removeButton.setIcon(R.drawable.remove_icon);
		this.removeButton.setDefaultValue(new LinkedHashSet<>());
		this.removeButton.setVisible(false);
		controlCategory.addPreference(this.removeButton);
		this.setPreferenceScreen(screen);
		this.enableAddingData = false;

		if(this.preferences.contains(this.dayIdentifier)) {
			String subject = this.preferences.getString(this.dayIdentifier, "");

			if(subject != null && !subject.isEmpty()) {
				try {
					ObjectMapper mapper = new ObjectMapper();
					SubjectList list = mapper.readValue(subject, SubjectList.class);

					if(list.subjectList != null) {
						for(int i = 0; i < list.subjectList.length; i++) {
							this.addSubject(list.subjectList[i]);
						}
					}
				} catch(Throwable ignored) {

				}
			}
		}

		this.enableAddingData = true;
	}

	private void addSubject(Subject subject) {
		SubjectDataList list = this.getSubjectDataList();

		if(list == null) {
			Toast.makeText(this.context, "Internal Error: Null subject list", Toast.LENGTH_SHORT).show();
			return;
		}

		SubjectData data = list.dataList[subject.subjectDataIndex];
		Preference preference = new Preference(this.context);
		StringBuilder summary = new StringBuilder();
		summary.append(String.format(Locale.getDefault(), "%02d:%02d - %02d:%02d", subject.startTimeHour, subject.startTimeMinute, subject.endTimeHour, subject.endTimeMinute));

		if(data.subjectIdentifier != null && !data.subjectIdentifier.isEmpty()) {
			summary.append('\n');
			summary.append(data.subjectIdentifier);
		}

		if(data.teacherList != null) {
			int length = data.teacherList.length;

			if(length > 0) {
				summary.append('\n');

				if(length == 1) {
					summary.append(data.teacherList[0]);
				} else {
					for(int i = 0; i < length; i++) {
						String teacherName = data.teacherList[i];

						if(i != 0) {
							summary.append(", ");
						}

						summary.append(teacherName);
					}
				}
			}
		}

		preference.setSummary(summary);
		preference.setTitle(data.subjectName);
		this.preferenceList.add(preference);
		this.subjectList.add(subject);
		this.subjectCategory.addPreference(preference);
		this.updateRemoveButton();
	}

	private void updateRemoveButton() {
		CharSequence[] entryList = new CharSequence[this.preferenceList.size()];
		CharSequence[] entryValueList = new CharSequence[entryList.length];

		for(int i = 0; i < entryList.length; i++) {
			entryList[i] = this.preferenceList.get(i).getTitle();
			entryValueList[i] = Integer.toString(i);
		}

		this.removeButton.setEntries(entryList);
		this.removeButton.setEntryValues(entryValueList);
		this.removeButton.setValues(new LinkedHashSet<>());
		boolean visible = this.removeButton.isVisible();

		if(entryList.length > 0) {
			if(!visible) {
				this.removeButton.setVisible(true);
			}
		} else {
			if(visible) {
				this.removeButton.setVisible(false);
			}
		}

		if(this.enableAddingData) {
			try {
				ObjectMapper mapper = new ObjectMapper();
				SubjectList list = new SubjectList();
				list.subjectList = new Subject[this.subjectList.size()];

				for(int i = 0; i < list.subjectList.length; i++) {
					list.subjectList[i] = this.subjectList.get(i);
				}

				SharedPreferences.Editor editor = this.preferences.edit();
				editor.putString(this.dayIdentifier, mapper.writeValueAsString(list));
				editor.apply();
			} catch(Throwable ignored) {

			}
		}
	}

	private SubjectDataList getSubjectDataList() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.context);

		if(preferences.contains("subjectList")) {
			String subject = preferences.getString("subjectList", "");

			if(subject != null && !subject.isEmpty()) {
				try {
					return new ObjectMapper().readValue(subject, SubjectDataList.class);
				} catch(Throwable ignored) {

				}
			}
		}

		return null;
	}
}
