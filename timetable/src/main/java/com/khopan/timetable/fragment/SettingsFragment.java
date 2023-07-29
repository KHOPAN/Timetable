package com.khopan.timetable.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceDataStore;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;

import com.khopan.timetable.FragmentInfo;
import com.khopan.timetable.activity.SyncTimetableActivity;
import com.khopan.timetable.secret.SecretKeyProcessor;
import com.khopan.timetable.utils.ThemeUtils;
import com.khopan.timetable.widgets.EditDateFormatPreference;
import com.sec.sesl.khopan.timetable.R;

import dev.oneuiproject.oneui.preference.HorizontalRadioPreference;
import dev.oneuiproject.oneui.widget.Toast;

public class SettingsFragment extends PreferenceFragmentCompat implements FragmentInfo {
	private Context context;
	private Resources resources;

	private String secretKeyValue;

	@Override
	public void onCreatePreferences(Bundle bundle, String rootKey) {
		Activity activity = this.requireActivity();
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.context);
		int theme = ThemeUtils.getThemePreference(this.context);
		PreferenceManager manager = this.getPreferenceManager();
		PreferenceScreen screen = manager.createPreferenceScreen(this.context);
		PreferenceCategory timeCategory = new PreferenceCategory(this.context);
		timeCategory.setTitle(this.resources.getString(R.string.time));
		screen.addPreference(timeCategory);
		EditDateFormatPreference timeFormatPreference = new EditDateFormatPreference(this.context);
		timeFormatPreference.setKey("timeFormat");
		timeFormatPreference.setTitle(this.resources.getString(R.string.timeFormat));
		timeFormatPreference.setDefaultValue("HH:mm.ss");
		timeFormatPreference.setPersistent(true);
		timeFormatPreference.setOnDateFormatChangeListener(format -> TimetableFragment.TimeFormat = format);
		timeCategory.addPreference(timeFormatPreference);
		EditDateFormatPreference dateFormatPreference = new EditDateFormatPreference(this.context);
		dateFormatPreference.setKey("dateFormat");
		dateFormatPreference.setTitle(this.resources.getString(R.string.dateFormat));
		dateFormatPreference.setDefaultValue("dd EEEE MMMM yyyy");
		dateFormatPreference.setPersistent(true);
		dateFormatPreference.setOnDateFormatChangeListener(format -> TimetableFragment.DateFormat = format);
		timeCategory.addPreference(dateFormatPreference);
		PreferenceCategory themeCategory = new PreferenceCategory(this.context);
		themeCategory.setTitle(this.resources.getString(R.string.theme));
		screen.addPreference(themeCategory);
		HorizontalRadioPreference themePreference = new HorizontalRadioPreference(this.context, null);
		themePreference.setKey("theme");
		themePreference.setTitle(this.resources.getString(R.string.themeSettings));
		themePreference.setEntries(new String[] {this.resources.getString(R.string.lightTheme), this.resources.getString(R.string.darkTheme)});
		themePreference.setEntriesImage(new int[] {R.drawable.display_help_light_mode, R.drawable.display_help_dark_mode});
		themePreference.setEntryValues(new String[] {"0", "1"});
		themePreference.setType(0);
		themePreference.setDividerEnabled(false);
		themePreference.setTouchEffectEnabled(false);
		themePreference.setEnabled(theme != ThemeUtils.DEFAULT_THEME);
		themePreference.setValue(ThemeUtils.isLightTheme(this.context) ? "0" : "1");
		themePreference.setOnPreferenceChangeListener((preference, value) -> {
			String currentTheme = Integer.toString(ThemeUtils.getThemePreference(this.context));

			if(!currentTheme.equals(value)) {
				ThemeUtils.setThemePreference((AppCompatActivity) this.requireActivity(), String.valueOf(value).equals("0") ? ThemeUtils.LIGHT_THEME : ThemeUtils.DARK_THEME);
			}

			return true;
		});

		themeCategory.addPreference(themePreference);
		SwitchPreference defaultThemePreference = new SwitchPreference(this.context);
		defaultThemePreference.setKey("systemTheme");
		defaultThemePreference.setTitle(this.resources.getString(R.string.defaultTheme));
		defaultThemePreference.setOnPreferenceChangeListener((preference, value) -> {
			boolean state = (boolean) value;
			themePreference.setEnabled(!state);

			if(state) {
				ThemeUtils.setThemePreference((AppCompatActivity) this.requireActivity(), ThemeUtils.DEFAULT_THEME);
			}

			return true;
		});

		themeCategory.addPreference(defaultThemePreference);
		defaultThemePreference.setChecked(theme == ThemeUtils.DEFAULT_THEME);
		PreferenceCategory timetableCategory = new PreferenceCategory(this.context);
		timetableCategory.setTitle(this.resources.getString(R.string.timetable));
		screen.addPreference(timetableCategory);
		Preference syncTimetablePreference = new Preference(this.context);
		syncTimetablePreference.setTitle(this.resources.getString(R.string.syncTimetable));
		syncTimetablePreference.setOnPreferenceClickListener(preference -> {
			activity.startActivity(new Intent(activity, SyncTimetableActivity.class));
			return true;
		});

		timetableCategory.addPreference(syncTimetablePreference);
		Preference clearTimetablePreference = new Preference(this.context);
		clearTimetablePreference.setTitle(this.resources.getString(R.string.clearTimetable));
		clearTimetablePreference.setOnPreferenceClickListener(preference -> {
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
			Toast.makeText(this.context, this.resources.getString(R.string.clearSuccess), Toast.LENGTH_SHORT).show();
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

	@Override
	public void onAttach(@NonNull Context context) {
		super.onAttach(context);
		this.context = context;
		this.resources = this.getResources();
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
}
