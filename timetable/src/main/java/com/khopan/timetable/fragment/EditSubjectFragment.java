package com.khopan.timetable.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.MultiSelectListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceDataStore;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.khopan.timetable.FragmentInfo;
import com.khopan.timetable.data.SubjectData;
import com.khopan.timetable.data.SubjectDataList;
import com.khopan.timetable.settings.FragmentSettingsActivity;
import com.khopan.timetable.settings.FragmentTitle;
import com.sec.sesl.khopan.timetable.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class EditSubjectFragment extends PreferenceFragmentCompat implements FragmentInfo {
	private final List<SubjectData> subjectList;
	private final List<Preference> subjectPreferenceList;

	private Context context;
	private Resources resources;

	private ActivityResultLauncher<Intent> launcher;
	private boolean enableAddingData;

	private SharedPreferences preferences;
	private PreferenceCategory timetableCategory;
	private MultiSelectListPreference removeButton;

	public EditSubjectFragment() {
		this.subjectList = new ArrayList<>();
		this.subjectPreferenceList = new ArrayList<>();
	}

	@Override
	public void onCreatePreferences(Bundle bundle, String rootKey) {
		this.enableAddingData = true;
		this.preferences = PreferenceManager.getDefaultSharedPreferences(this.context);
		PreferenceManager manager = this.getPreferenceManager();
		PreferenceScreen screen = manager.createPreferenceScreen(this.context);
		this.timetableCategory = new PreferenceCategory(this.context);
		screen.addPreference(this.timetableCategory);
		PreferenceCategory controlCategory = new PreferenceCategory(this.context);
		screen.addPreference(controlCategory);
		Preference addButton = new Preference(this.context);
		addButton.setTitle("Add Subject");
		addButton.setIcon(R.drawable.add_icon);
		addButton.setOnPreferenceClickListener(preference -> FragmentSettingsActivity.launch(this, FragmentTitle.title("Edit Subject", "Add Subject"), this.launcher, AddSubjectFragment.class, null));
		controlCategory.addPreference(addButton);
		this.removeButton = new MultiSelectListPreference(this.context);
		this.removeButton.setPreferenceDataStore(new PreferenceDataStore() {
			@Override
			public String getString(String key, @Nullable String defaultValue) {
				return defaultValue;
			}

			@Override
			public void putStringSet(String key, @Nullable Set<String> values) {
				if(values != null && values.size() > 0) {
					List<SubjectData> removeList = new ArrayList<>();
					List<Preference> preferenceRemoveList = new ArrayList<>();

					for(String value : values) {
						int index = Integer.parseInt(value);
						Preference preference = EditSubjectFragment.this.subjectPreferenceList.get(index);
						removeList.add(EditSubjectFragment.this.subjectList.get(index));
						preferenceRemoveList.add(preference);
						EditSubjectFragment.this.timetableCategory.removePreference(preference);
					}

					EditSubjectFragment.this.subjectList.removeAll(removeList);
					EditSubjectFragment.this.subjectPreferenceList.removeAll(preferenceRemoveList);
					EditSubjectFragment.this.updateRemoveButton();
				}
			}
		});

		this.removeButton.setKey("removeSubject");
		this.removeButton.setTitle("Remove Subject");
		this.removeButton.setIcon(R.drawable.remove_icon);
		this.removeButton.setDefaultValue(new LinkedHashSet<>());
		controlCategory.addPreference(this.removeButton);
		this.setPreferenceScreen(screen);
		this.refresh();
	}

	@Override
	public void onResume() {
		super.onResume();
		this.refresh();
	}

	private void refresh() {
		this.enableAddingData = false;
		this.removeButton.setVisible(false);
		this.subjectPreferenceList.clear();
		this.subjectList.clear();
		this.timetableCategory.removeAll();

		if(this.preferences.contains("subjectList")) {
			String subject = this.preferences.getString("subjectList", "");

			if(subject != null && !subject.isEmpty()) {
				try {
					ObjectMapper mapper = new ObjectMapper();
					SubjectDataList list = mapper.readValue(subject, SubjectDataList.class);

					if(list.dataList != null) {
						for(int i = 0; i < list.dataList.length; i++) {
							this.addSubject(list.dataList[i]);
						}
					}
				} catch(Throwable ignored) {

				}
			}
		}

		this.enableAddingData = true;
	}

	@Override
	public void onAttach(@NonNull Context context) {
		super.onAttach(context);
		this.context = context;
		this.resources = this.getResources();
		this.launcher = this.registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
			if(result.getResultCode() == Activity.RESULT_OK) {
				Intent data = result.getData();

				if(data != null) {
					Serializable hopefullySubjectData = data.getSerializableExtra("subjectData");

					if(hopefullySubjectData instanceof SubjectData) {
						this.addSubject((SubjectData) hopefullySubjectData);
					}
				}
			}
		});
	}

	@Override
	public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		view = this.getView();

		if(view != null) {
			view.setBackgroundColor(this.context.getColor(R.color.oui_background_color));
		}
	}

	private void addSubject(SubjectData data) {
		Preference subject = new Preference(this.context);
		subject.setTitle(data.subjectName);
		StringBuilder summary = new StringBuilder();
		boolean addedIdentifier = false;

		if(data.subjectIdentifier != null && !data.subjectIdentifier.isEmpty()) {
			summary.append(data.subjectIdentifier);
			addedIdentifier = true;
		}

		if(data.teacherList != null) {
			int length = data.teacherList.length;

			if(length > 0) {
				if(addedIdentifier) {
					summary.append('\n');
				}

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

		subject.setSummary(summary);
		this.subjectPreferenceList.add(subject);
		this.subjectList.add(data);
		this.timetableCategory.addPreference(subject);
		this.updateRemoveButton();
	}

	private void updateRemoveButton() {
		CharSequence[] entryList = new CharSequence[this.subjectList.size()];
		CharSequence[] entryValueList = new CharSequence[entryList.length];

		for(int i = 0; i < entryList.length; i++) {
			entryList[i] = this.subjectList.get(i).subjectName;
			entryValueList[i] = Integer.toString(i);
		}

		this.removeButton.setEntries(entryList);
		this.removeButton.setEntryValues(entryValueList);
		this.removeButton.setValues(new LinkedHashSet<>());
		int count = this.subjectList.size();
		boolean visible = this.removeButton.isVisible();

		if(count > 0) {
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
				SubjectDataList list = new SubjectDataList();
				list.dataList = new SubjectData[this.subjectList.size()];

				for(int i = 0; i < list.dataList.length; i++) {
					list.dataList[i] = this.subjectList.get(i);
				}

				SharedPreferences.Editor editor = this.preferences.edit();
				editor.putString("subjectList", mapper.writeValueAsString(list));
				editor.apply();
			} catch(Throwable ignored) {

			}
		}
	}

	@Override
	public int getLayoutResourceIdentifier() {
		return -1;
	}

	@Override
	public int getIconResourceIdentifier() {
		return R.drawable.ic_oui_compose_edit;
	}

	@Override
	public String getTitle() {
		return this.resources.getString(R.string.editSubject);
	}
}
