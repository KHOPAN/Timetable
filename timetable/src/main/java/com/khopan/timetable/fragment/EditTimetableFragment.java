package com.khopan.timetable.fragment;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;

import com.khopan.timetable.FragmentInfo;
import com.khopan.timetable.settings.FragmentSettingsActivity;
import com.khopan.timetable.settings.FragmentTitle;
import com.sec.sesl.khopan.timetable.R;

public class EditTimetableFragment extends PreferenceFragmentCompat implements FragmentInfo {
	private Context context;
	private Resources resources;

	@Override
	public void onCreatePreferences(Bundle bundle, String rootKey) {
		PreferenceManager manager = this.getPreferenceManager();
		PreferenceScreen screen = manager.createPreferenceScreen(this.context);
		PreferenceCategory category = new PreferenceCategory(this.context);
		screen.addPreference(category);
		String[] name = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
		String[] identifier = {"sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday"};

		for(int i = 0; i < name.length; i++) {
			String dayName = name[i];
			String dayIdentifier = identifier[i];
			Preference preference = new Preference(this.context);
			preference.setTitle(dayName);
			preference.setOnPreferenceClickListener(instance -> {
				Bundle extras = new Bundle();
				extras.putString("dayName", dayName);
				extras.putString("dayIdentifier", dayIdentifier);
				return FragmentSettingsActivity.start(this, FragmentTitle.title("Edit Timetable", dayName), DaySettingsFragment.class, extras);
			});

			category.addPreference(preference);
		}

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
		return R.drawable.ic_oui_calendar_period_time;
	}

	@Override
	public String getTitle() {
		return this.resources.getString(R.string.editTimetable);
	}
}
