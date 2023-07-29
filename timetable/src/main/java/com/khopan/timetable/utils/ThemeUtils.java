package com.khopan.timetable.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.TypedValue;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.sec.sesl.khopan.timetable.R;

public class ThemeUtils {
	public static final int LIGHT_THEME = 0;
	public static final int DARK_THEME = 1;
	public static final int DEFAULT_THEME = 2;

	public static boolean isLightTheme(@NonNull Context context) {
		TypedValue value = new TypedValue();
		context.getTheme().resolveAttribute(R.attr.isLightTheme, value, true);
		return value.data != 0;
	}

	public static boolean isDarkTheme(@NonNull Context context) {
		return !ThemeUtils.isLightTheme(context);
	}

	public static int getTheme(@NonNull Context context) {
		return ThemeUtils.isLightTheme(context) ? ThemeUtils.LIGHT_THEME : ThemeUtils.DARK_THEME;
	}

	public static int getThemePreference(@NonNull Context context) {
		return context.getSharedPreferences("ThemeUtils", Context.MODE_PRIVATE).getInt("themePreference", ThemeUtils.DEFAULT_THEME);
	}

	public static void setThemePreference(AppCompatActivity activity, int theme) {
		if(ThemeUtils.getThemePreference(activity) != theme) {
			SharedPreferences.Editor editor = activity.getSharedPreferences("ThemeUtils", Context.MODE_PRIVATE).edit();
			editor.putInt("themePreference", theme);
			editor.apply();
		}

		int mode;

		if(theme == ThemeUtils.DEFAULT_THEME) {
			mode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
		} else {
			if(theme == ThemeUtils.DARK_THEME) {
				mode = AppCompatDelegate.MODE_NIGHT_YES;
			} else {
				mode = AppCompatDelegate.MODE_NIGHT_NO;
			}
		}

		AppCompatDelegate.setDefaultNightMode(mode);
		activity.getDelegate().applyDayNight();
	}
}
