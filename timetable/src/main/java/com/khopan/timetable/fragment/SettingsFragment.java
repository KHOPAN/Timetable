package com.khopan.timetable.fragment;

import android.Manifest;
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
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceDataStore;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;

import com.khopan.timetable.FragmentInfo;
import com.khopan.timetable.activity.SyncTimetableActivity;
import com.khopan.timetable.secret.SecretKeyProcessor;
import com.khopan.timetable.widgets.EditDateFormatPreference;
import com.sec.sesl.khopan.timetable.R;

import dev.oneuiproject.oneui.widget.Toast;

public class SettingsFragment extends PreferenceFragmentCompat implements FragmentInfo {
	private Context context;
	private Resources resources;
	private ActivityResultLauncher<String> internetLauncher;

	private SharedPreferences preferences;
	private SharedPreferencesDataStore dataStore;
	private String secretKeyValue;

	@Override
	public void onCreatePreferences(Bundle bundle, String rootKey) {
		Activity activity = this.getActivity();
		this.preferences = activity.getPreferences(Activity.MODE_PRIVATE);
		this.dataStore = new SharedPreferencesDataStore(this.preferences);
		PreferenceManager manager = this.getPreferenceManager();
		PreferenceScreen screen = manager.createPreferenceScreen(this.context);
		PreferenceCategory timeCategory = new PreferenceCategory(this.context);
		timeCategory.setTitle("Time");
		screen.addPreference(timeCategory);
		EditDateFormatPreference timeFormatPreference = new EditDateFormatPreference(this.context);
		timeFormatPreference.setKey("timeFormat");
		timeFormatPreference.setTitle("Time Format");
		timeFormatPreference.setDefaultValue("HH:mm.ss");
		timeFormatPreference.setPersistent(true);
		timeFormatPreference.setOnDateFormatChangeListener(format -> TimetableFragment.TimeFormat = format);
		timeCategory.addPreference(timeFormatPreference);
		EditDateFormatPreference dateFormatPreference = new EditDateFormatPreference(this.context);
		dateFormatPreference.setKey("dateFormat");
		dateFormatPreference.setTitle("Date Format");
		dateFormatPreference.setDefaultValue("dd EEEE MMMM yyyy");
		dateFormatPreference.setPersistent(true);
		dateFormatPreference.setOnDateFormatChangeListener(format -> TimetableFragment.DateFormat = format);
		timeCategory.addPreference(dateFormatPreference);
		PreferenceCategory timetableCategory = new PreferenceCategory(this.context);
		timetableCategory.setTitle("Timetable");
		screen.addPreference(timetableCategory);
		Preference syncTimetablePreference = new Preference(this.context);
		syncTimetablePreference.setTitle("Sync Timetable");
		syncTimetablePreference.setOnPreferenceClickListener(preference -> {
			activity.startActivity(new Intent(activity, SyncTimetableActivity.class));
			return true;
		});

		timetableCategory.addPreference(syncTimetablePreference);
		Preference clearTimetablePreference = new Preference(this.context);
		clearTimetablePreference.setTitle("Clear Timetable");
		clearTimetablePreference.setOnPreferenceClickListener(preference -> {
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.context);
			SharedPreferences.Editor editor = preferences.edit();
			editor.putString("subjectList", "");
			editor.putString("sunday", "");
			editor.putString("monday", "");
			editor.putString("tuesday", "");
			editor.putString("wednesday", "");
			editor.putString("thursday", "");
			editor.putString("friday", "");
			editor.putString("saturday", "");
			editor.apply();
			Toast.makeText(this.context, "Successfully reset Timetable data", Toast.LENGTH_SHORT).show();
			return true;
		});

		timetableCategory.addPreference(clearTimetablePreference);
		PreferenceCategory secretCategory = new PreferenceCategory(this.context);
		screen.addPreference(secretCategory);
		EditTextPreference secretKeyPreference = new EditTextPreference(this.context);
		secretKeyPreference.setPreferenceDataStore(new PreferenceDataStore() {
			private boolean flag;

			@Nullable
			@Override
			public String getString(String key, @Nullable String defaultValue) {
				return defaultValue;
			}

			@Override
			public void putString(String key, @Nullable String value) {
				if(this.flag) {
					this.flag = false;
				} else {
					this.flag = true;
					secretKeyPreference.setText("");
					SettingsFragment.this.secretKeyValue = value;
					SecretKeyProcessor.process(SettingsFragment.this.secretKeyValue, SettingsFragment.this);
				}
			}
		});

		secretKeyPreference.setKey("secret");
		secretKeyPreference.setTitle(this.resources.getString(R.string.secretKeyTitle));
		String enterSecretKeys = this.resources.getString(R.string.enterSecretKeys);
		secretKeyPreference.setSummary(enterSecretKeys);
		secretKeyPreference.setDialogTitle(enterSecretKeys);
		secretCategory.addPreference(secretKeyPreference);
		this.setPreferenceScreen(screen);
	}

	public void requestPermission() {
		this.internetLauncher.launch(Manifest.permission.INTERNET);
	}

	@Override
	public void onAttach(@NonNull Context context) {
		super.onAttach(context);
		this.context = context;
		this.resources = this.getResources();
		this.internetLauncher = this.registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
			if(isGranted) {
				SecretKeyProcessor.process(this.secretKeyValue, SettingsFragment.this);
			} else {
				this.internetLauncher.launch(Manifest.permission.INTERNET);
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

	@Override
	public int getLayoutResourceIdentifier() {
		return -1;
	}

	@Override
	public int getIconResourceIdentifier() {
		return R.drawable.ic_oui_settings_outline;
	}

	@Override
	public String getTitle() {
		return this.resources.getString(R.string.settings);
	}

	private static class SharedPreferencesDataStore extends PreferenceDataStore {
		private final SharedPreferences preferences;

		private SharedPreferencesDataStore(SharedPreferences preferences) {
			this.preferences = preferences;
		}

		@Nullable
		@Override
		public String getString(String key, @Nullable String defaultValue) {
			return this.preferences.getString(key, defaultValue);
		}

		@Override
		public void putString(String key, @Nullable String value) {
			SharedPreferences.Editor editor = this.preferences.edit();
			editor.putString(key, value);
			editor.apply();
		}
	}
}
