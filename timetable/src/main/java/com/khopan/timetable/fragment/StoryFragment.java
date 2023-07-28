package com.khopan.timetable.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.khopan.story.Episode;
import com.khopan.story.Season;
import com.khopan.story.Story;
import com.khopan.timetable.settings.FragmentSettingsActivity;
import com.khopan.timetable.settings.FragmentTitle;
import com.khopan.timetable.utils.SeparatedStringBuilder;

import java.util.List;

import dev.oneuiproject.oneui.widget.Toast;

public class StoryFragment extends PreferenceFragmentCompat {
	private Context context;

	@Override
	public void onAttach(@NonNull Context context) {
		super.onAttach(context);
		this.context = context;
	}

	@Override
	public void onCreatePreferences(Bundle bundle, String rootKey) {
		bundle = this.getArguments();
		JsonNode node = null;

		try {
			ObjectMapper mapper = new ObjectMapper();
			node = mapper.readTree(bundle.getString("story"));
		} catch(Throwable ignored) {
			Toast.makeText(this.context, "Error while parsing JSON", Toast.LENGTH_SHORT).show();
		}

		Story story = Story.deserialize(node);
		PreferenceManager manager = this.getPreferenceManager();
		PreferenceScreen screen = manager.createPreferenceScreen(this.context);

		for(Season season : story.seasonList()) {
			PreferenceCategory category = new PreferenceCategory(this.context);
			category.setTitle(season.title().get() + " : " + season.name().get());
			screen.addPreference(category);

			for(Episode episode : season.episodeList()) {
				Preference episodePreference = new Preference(this.context);
				episodePreference.setTitle(episode.name().get());
				episodePreference.setSummary(episode.title().get());
				episodePreference.setOnPreferenceClickListener(preference -> {
					Bundle extras = new Bundle();
					extras.putString("episode", Episode.serialize(episode).toString());
					return FragmentSettingsActivity.start(this, FragmentTitle.title(episode.title().get(), episode.name().get()), EpisodeFragment.class, extras);
				});

				category.addPreference(episodePreference);
			}
		}

		List<String> additional = story.additionalInformationList();
		PreferenceCategory additionalCategory = new PreferenceCategory(this.context);
		screen.addPreference(additionalCategory);
		Preference additionalPreference = new Preference(this.context);
		additionalPreference.setTitle(additional.get(0));
		SeparatedStringBuilder builder = new SeparatedStringBuilder();
		builder.setSeparateText("\n");

		for(int i = 1; i < additional.size(); i++) {
			builder.append(additional.get(i));
		}

		additionalPreference.setSummary(builder.toString());
		additionalCategory.addPreference(additionalPreference);
		this.setPreferenceScreen(screen);
	}
}
