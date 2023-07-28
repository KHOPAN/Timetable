package com.khopan.timetable.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.EditTextPreference;
import androidx.preference.MultiSelectListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceDataStore;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;

import com.khopan.timetable.data.SubjectData;
import com.khopan.timetable.settings.FragmentSettingsActivity;
import com.sec.sesl.khopan.timetable.R;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class AddSubjectFragment extends PreferenceFragmentCompat {
	private final DataStore store;
	private final List<EditTextPreference> preferenceList;

	private Context context;

	private EditTextPreference namePreference;
	private EditTextPreference subjectIdPreference;
	private PreferenceCategory teacherCategory;
	private EditTextPreference addTeacherPreference;
	private MultiSelectListPreference removeTeacherPreference;

	private String subjectName;
	private String subjectId;

	public AddSubjectFragment() {
		this.store = new DataStore();
		this.preferenceList = new ArrayList<>();
	}

	@Override
	public void onAttach(@NonNull Context context) {
		super.onAttach(context);
		this.context = context;
	}

	@Override
	public void onCreatePreferences(Bundle bundle, String rootKey) {
		PreferenceManager manager = this.getPreferenceManager();
		PreferenceScreen screen = manager.createPreferenceScreen(this.context);
		PreferenceCategory subjectCategory = new PreferenceCategory(this.context);
		subjectCategory.setTitle("Subject");
		screen.addPreference(subjectCategory);
		this.namePreference = new EditTextPreference(this.context);
		this.namePreference.setPreferenceDataStore(this.store);
		this.namePreference.setKey("subjectName");
		this.namePreference.setSummary("Subject Name");
		this.namePreference.setText("New Subject");
		this.namePreference.setDialogTitle("Subject Name:");
		this.store.putString("subjectName", "New Subject");
		subjectCategory.addPreference(this.namePreference);
		this.subjectIdPreference = new EditTextPreference(this.context);
		this.subjectIdPreference.setPreferenceDataStore(this.store);
		this.subjectIdPreference.setKey("subjectId");
		this.subjectIdPreference.setSummary("Subject Identifier");
		this.subjectIdPreference.setText("None");
		this.subjectIdPreference.setDialogTitle("Subject Identifier:");
		this.store.putString("subjectId", "None");
		subjectCategory.addPreference(this.subjectIdPreference);
		this.teacherCategory = new PreferenceCategory(this.context);
		this.teacherCategory.setTitle("Teacher");
		screen.addPreference(this.teacherCategory);
		this.teacherCategory.setVisible(false);
		PreferenceCategory addRemoveCategory = new PreferenceCategory(this.context);
		screen.addPreference(addRemoveCategory);
		this.addTeacherPreference = new EditTextPreference(this.context);
		this.addTeacherPreference.setPreferenceDataStore(this.store);
		this.addTeacherPreference.setKey("addTeacher");
		this.addTeacherPreference.setTitle("Add Teacher");
		this.addTeacherPreference.setDialogTitle("Teacher Name:");
		this.addTeacherPreference.setIcon(R.drawable.add_icon);
		addRemoveCategory.addPreference(this.addTeacherPreference);
		this.removeTeacherPreference = new MultiSelectListPreference(this.context);
		this.removeTeacherPreference.setPreferenceDataStore(new PreferenceDataStore() {
			@Override
			public String getString(String key, @Nullable String defaultValue) {
				return defaultValue;
			}

			@Override
			public void putStringSet(String key, @Nullable Set<String> values) {
				if(values != null && values.size() > 0) {
					List<EditTextPreference> removeList = new ArrayList<>();

					for(String value : values) {
						int index = Integer.parseInt(value);
						EditTextPreference preference = AddSubjectFragment.this.preferenceList.get(index);
						removeList.add(preference);
						AddSubjectFragment.this.teacherCategory.removePreference(preference);
					}

					AddSubjectFragment.this.preferenceList.removeAll(removeList);
					AddSubjectFragment.this.updateRemoveTeacher();
				}
			}
		});

		this.removeTeacherPreference.setKey("removeTeacher");
		this.removeTeacherPreference.setTitle("Remove Teacher");
		this.removeTeacherPreference.setIcon(R.drawable.remove_icon);
		this.removeTeacherPreference.setDefaultValue(new LinkedHashSet<>());
		this.removeTeacherPreference.setVisible(false);
		addRemoveCategory.addPreference(this.removeTeacherPreference);
		PreferenceCategory buttonCategory = new PreferenceCategory(this.context);
		screen.addPreference(buttonCategory);
		Preference donePreference = new Preference(this.context);
		donePreference.setTitle("Done");
		donePreference.setOnPreferenceClickListener(preference -> {
			Intent intent = new Intent();
			SubjectData data = new SubjectData();
			data.subjectName = this.subjectName;
			data.subjectIdentifier = this.subjectId;
			data.teacherList = new String[this.preferenceList.size()];

			for(int i = 0; i < data.teacherList.length; i++) {
				data.teacherList[i] = this.preferenceList.get(i).getText();
			}

			intent.putExtra("subjectData", data);
			return FragmentSettingsActivity.finish(this, Activity.RESULT_OK, intent);
		});

		buttonCategory.addPreference(donePreference);
		Preference cancelPreference = new Preference(this.context);
		cancelPreference.setTitle("Cancel");
		cancelPreference.setOnPreferenceClickListener(preference -> FragmentSettingsActivity.finish(this));
		buttonCategory.addPreference(cancelPreference);
		this.setPreferenceScreen(screen);
	}

	private void addTeacher(String teacherName) {
		EditTextPreference preference = new EditTextPreference(this.context);
		this.preferenceList.add(preference);
		preference.setPreferenceDataStore(new PreferenceDataStore() {
			@Nullable
			@Override
			public String getString(String key, @Nullable String defaultValue) {
				return teacherName;
			}

			@Override
			public void putString(String key, @Nullable String value) {
				if(value != null && !value.isEmpty()) {
					preference.setTitle(value);
					AddSubjectFragment.this.updateRemoveTeacher();
				}
			}
		});

		preference.setKey("teacher" + this.teacherCategory.getPreferenceCount());
		preference.setSummary("Teacher Name");
		preference.setTitle(teacherName);
		preference.setDialogTitle("Teacher Name:");
		preference.setText(teacherName);
		this.teacherCategory.addPreference(preference);
		this.updateRemoveTeacher();
	}

	private void updateRemoveTeacher() {
		CharSequence[] entryList = new CharSequence[this.preferenceList.size()];
		CharSequence[] entryValueList = new CharSequence[entryList.length];

		for(int i = 0; i < entryList.length; i++) {
			entryList[i] = this.preferenceList.get(i).getText();
			entryValueList[i] = Integer.toString(i);
		}

		this.removeTeacherPreference.setEntries(entryList);
		this.removeTeacherPreference.setEntryValues(entryValueList);
		this.removeTeacherPreference.setValues(new LinkedHashSet<>());
		int count = this.preferenceList.size();
		boolean categoryVisible = this.teacherCategory.isVisible();
		boolean preferenceVisible = this.removeTeacherPreference.isVisible();

		if(count > 0) {
			if(!preferenceVisible) {
				this.removeTeacherPreference.setVisible(true);
			}

			if(!categoryVisible) {
				this.teacherCategory.setVisible(true);
			}
		} else {
			if(preferenceVisible) {
				this.removeTeacherPreference.setVisible(false);
			}

			if(categoryVisible) {
				this.teacherCategory.setVisible(false);
			}
		}

		if(count > 1) {
			this.teacherCategory.setTitle("Teachers");
		} else {
			this.teacherCategory.setTitle("Teacher");
		}
	}

	private class DataStore extends PreferenceDataStore {
		private boolean flag;

		@Nullable
		@Override
		public String getString(String key, @Nullable String defaultValue) {
			if("subjectName".equals(key)) {
				if(AddSubjectFragment.this.subjectName != null) {
					return AddSubjectFragment.this.subjectName;
				}
			} else if("subjectId".equals(key)) {
				if(AddSubjectFragment.this.subjectId != null) {
					return AddSubjectFragment.this.subjectId;
				}
			}

			return defaultValue;
		}

		@Override
		public void putString(String key, @Nullable String value) {
			if("subjectName".equals(key)) {
				AddSubjectFragment.this.subjectName = value;
				AddSubjectFragment.this.namePreference.setTitle(AddSubjectFragment.this.subjectName);
			} else if("subjectId".equals(key)) {
				AddSubjectFragment.this.subjectId = value;
				AddSubjectFragment.this.subjectIdPreference.setTitle(AddSubjectFragment.this.subjectId);
			} else if("addTeacher".equals(key)) {
				if(this.flag) {
					this.flag = false;
				} else {
					this.flag = true;
					String name = AddSubjectFragment.this.addTeacherPreference.getText();

					if(name != null && !name.isEmpty()) {
						AddSubjectFragment.this.addTeacher(name);
					}

					AddSubjectFragment.this.addTeacherPreference.setText("");
				}
			}
		}
	}
}
