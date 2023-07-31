package com.khopan.timetable.fragment;

import android.app.Activity;
import android.app.AlertDialog;
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
import com.sec.sesl.khopan.timetable.BuildConfig;
import com.sec.sesl.khopan.timetable.R;

import dev.oneuiproject.oneui.preference.HorizontalRadioPreference;
import dev.oneuiproject.oneui.widget.Toast;

public class SettingsFragment extends PreferenceFragmentCompat implements FragmentInfo {
	private Context context;
	private Resources resources;

	private String secretKeyValue;
	private long lastPressTime;
	private int pressCount;

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
		PreferenceCategory notificationCategory = new PreferenceCategory(this.context);
		notificationCategory.setTitle(this.resources.getString(R.string.notification));
		screen.addPreference(notificationCategory);
		SwitchPreference vibratePreference = new SwitchPreference(this.context);
		vibratePreference.setKey("vibrateEnable");
		vibratePreference.setTitle(this.resources.getString(R.string.vibrate));
		vibratePreference.setDefaultValue(true);
		notificationCategory.addPreference(vibratePreference);
		EditTextPreference vibrateDurationPreference = new EditTextPreference(this.context);

		if(!preferences.contains("vibrateDuration")) {
			SharedPreferences.Editor editor = preferences.edit();
			editor.putInt("vibrateDuration", 500);
			editor.apply();
		}

		vibrateDurationPreference.setPreferenceDataStore(new PreferenceDataStore() {
			private boolean flag;

			@Override
			public String getString(String key, @Nullable String defaultValue) {
				return Integer.toString(preferences.getInt("vibrateDuration", 0));
			}

			@Override
			public void putString(String key, String value) {
				if(this.flag) {
					this.flag = false;
				} else {
					this.flag = true;

					try {
						int duration = Integer.parseInt(value);
						vibrateDurationPreference.setText(Integer.toString(duration));
						vibrateDurationPreference.setSummary(SettingsFragment.this.resources.getString(R.string.milliseconds, duration));
						SharedPreferences.Editor editor = preferences.edit();
						editor.putInt("vibrateDuration", duration);
						editor.apply();
					} catch(Throwable ignored) {
						int duration = preferences.getInt("vibrateDuration", 0);
						Toast.makeText(SettingsFragment.this.context, SettingsFragment.this.resources.getString(R.string.integerError), Toast.LENGTH_LONG).show();
						vibrateDurationPreference.setText(Integer.toString(duration));
						vibrateDurationPreference.setSummary(SettingsFragment.this.resources.getString(R.string.milliseconds, duration));
						this.flag = false;
					}
				}
			}
		});

		vibrateDurationPreference.setKey("vibrateDuration");
		vibrateDurationPreference.setTitle(this.resources.getString(R.string.vibrateDuration));
		vibrateDurationPreference.setDialogTitle(this.resources.getString(R.string.vibrateDuration) + ":");
		vibrateDurationPreference.setSummary(this.resources.getString(R.string.milliseconds, preferences.getInt("vibrateDuration", 0)));
		vibrateDurationPreference.setEnabled(vibratePreference.isChecked());
		notificationCategory.addPreference(vibrateDurationPreference);
		vibratePreference.setOnPreferenceChangeListener((preference, value) -> {
			boolean state = (boolean) value;
			vibrateDurationPreference.setEnabled(state);
			return true;
		});

		PreferenceCategory earlyNotificationCategory = new PreferenceCategory(this.context);
		earlyNotificationCategory.setTitle(this.resources.getString(R.string.earlyNotification));
		screen.addPreference(earlyNotificationCategory);
		EditTextPreference startEarlyPreference = new EditTextPreference(this.context);

		if(!preferences.contains("startEarlyNotificationTime")) {
			SharedPreferences.Editor editor = preferences.edit();
			editor.putInt("startEarlyNotificationTime", 3);
			editor.apply();
		}

		startEarlyPreference.setPreferenceDataStore(new PreferenceDataStore() {
			private boolean flag;

			@Override
			public String getString(String key, @Nullable String defaultValue) {
				return Integer.toString(preferences.getInt("startEarlyNotificationTime", 3));
			}

			@Override
			public void putString(String key, String value) {
				if(this.flag) {
					this.flag = false;
				} else {
					this.flag = true;

					try {
						int time = Integer.parseInt(value);
						startEarlyPreference.setText(Integer.toString(time));
						startEarlyPreference.setSummary(SettingsFragment.this.resources.getString(R.string.minute, time));
						SharedPreferences.Editor editor = preferences.edit();
						editor.putInt("startEarlyNotificationTime", time);
						editor.apply();
					} catch(Throwable ignored) {
						int time = preferences.getInt("startEarlyNotificationTime", 0);
						Toast.makeText(SettingsFragment.this.context, SettingsFragment.this.resources.getString(R.string.integerError), Toast.LENGTH_LONG).show();
						startEarlyPreference.setText(Integer.toString(time));
						startEarlyPreference.setSummary(SettingsFragment.this.resources.getString(R.string.minute, time));
						this.flag = false;
					}
				}
			}
		});

		startEarlyPreference.setKey("startEarlyTime");
		startEarlyPreference.setTitle(this.resources.getString(R.string.startEarly));
		startEarlyPreference.setDialogTitle(this.resources.getString(R.string.startEarly) + ":");
		startEarlyPreference.setSummary(this.resources.getString(R.string.minute, preferences.getInt("startEarlyNotificationTime", 3)));
		earlyNotificationCategory.addPreference(startEarlyPreference);
		EditTextPreference endEarlyPreference = new EditTextPreference(this.context);

		if(!preferences.contains("endEarlyNotificationTime")) {
			SharedPreferences.Editor editor = preferences.edit();
			editor.putInt("endEarlyNotificationTime", 3);
			editor.apply();
		}

		endEarlyPreference.setPreferenceDataStore(new PreferenceDataStore() {
			private boolean flag;

			@Override
			public String getString(String key, @Nullable String defaultValue) {
				return Integer.toString(preferences.getInt("endEarlyNotificationTime", 3));
			}

			@Override
			public void putString(String key, String value) {
				if(this.flag) {
					this.flag = false;
				} else {
					this.flag = true;

					try {
						int time = Integer.parseInt(value);
						endEarlyPreference.setText(Integer.toString(time));
						endEarlyPreference.setSummary(SettingsFragment.this.resources.getString(R.string.minute, time));
						SharedPreferences.Editor editor = preferences.edit();
						editor.putInt("endEarlyNotificationTime", time);
						editor.apply();
					} catch(Throwable ignored) {
						int time = preferences.getInt("endEarlyNotificationTime", 0);
						Toast.makeText(SettingsFragment.this.context, SettingsFragment.this.resources.getString(R.string.integerError), Toast.LENGTH_LONG).show();
						endEarlyPreference.setText(Integer.toString(time));
						endEarlyPreference.setSummary(SettingsFragment.this.resources.getString(R.string.minute, time));
						this.flag = false;
					}
				}
			}
		});

		endEarlyPreference.setKey("endEarlyTime");
		endEarlyPreference.setTitle(this.resources.getString(R.string.endEarly));
		endEarlyPreference.setDialogTitle(this.resources.getString(R.string.endEarly) + ":");
		endEarlyPreference.setSummary(this.resources.getString(R.string.minute, preferences.getInt("endEarlyNotificationTime", 3)));
		earlyNotificationCategory.addPreference(endEarlyPreference);
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
			new AlertDialog.Builder(this.context)
					.setTitle(this.resources.getString(R.string.clearTimetable))
					.setMessage(this.resources.getString(R.string.clearTimetableConfirm))
					.setPositiveButton(this.resources.getString(R.string.ok), (dialogInterface, someInteger) -> {
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
					})

					.setNegativeButton(this.resources.getString(R.string.cancel), null)
					.show();

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
		secretCategory.setVisible(false);
		PreferenceCategory versionCategory = new PreferenceCategory(this.context);
		screen.addPreference(versionCategory);
		Preference versionPreference = new Preference(this.context);
		versionPreference.setTitle("Version " + BuildConfig.VERSION_NAME);
		versionPreference.setSummary("Build " + BuildConfig.BUILD_NUMBER);
		versionPreference.setOnPreferenceClickListener(preference -> {
			long time = System.currentTimeMillis();

			if(this.pressCount == 0) {
				this.lastPressTime = time;
				this.pressCount++;
			} else if(time - this.lastPressTime >= 1000) {
				this.pressCount = 0;
			} else {
				this.pressCount++;
			}

			if(this.pressCount >= 4) {
				this.pressCount = 0;
				secretCategory.setVisible(true);
			}

			return true;
		});

		versionCategory.addPreference(versionPreference);
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
