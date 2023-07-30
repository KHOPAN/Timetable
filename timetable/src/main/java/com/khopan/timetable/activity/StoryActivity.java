package com.khopan.timetable.activity;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;

import com.khopan.story.Episode;
import com.khopan.story.Season;
import com.khopan.story.Story;
import com.khopan.timetable.fragment.EpisodeFragment;
import com.khopan.timetable.fragment.LoadingFragment;
import com.khopan.timetable.secret.SecretKeyProcessor;
import com.khopan.timetable.settings.FragmentSettingsActivity;
import com.khopan.timetable.settings.FragmentTitle;
import com.khopan.timetable.utils.SeparatedStringBuilder;
import com.sec.sesl.khopan.timetable.R;
import com.sec.sesl.khopan.timetable.databinding.FragmentSettingsActivityBinding;

import java.util.List;

public class StoryActivity extends AppCompatActivity {
	public FragmentSettingsActivityBinding binding;

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		this.binding = FragmentSettingsActivityBinding.inflate(this.getLayoutInflater());
		this.setContentView(this.binding.getRoot());
		this.binding.drawerLayout.setNavigationButtonAsBack();
		String storyText = this.getString(R.string.storyText);
		this.binding.drawerLayout.setTitle(this.getString(R.string.secret), storyText);
		this.binding.drawerLayout.setExpandedSubtitle(storyText);
		this.getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.main_content, new LoadingFragment(this.getString(R.string.loading)))
				.commit();

		SecretKeyProcessor.processStory(this);
	}

	public static class StoryFragment extends PreferenceFragmentCompat {
		private final Story story;
		private final StoryActivity activity;

		private Context context;

		public StoryFragment(@NonNull Story story, @NonNull StoryActivity activity) {
			this.story = story;
			this.activity = activity;
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

			for(Season season : this.story.seasonList()) {
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

			List<String> additional = this.story.additionalInformationList();
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
			this.activity.binding.drawerLayout.setBackgroundColor(activity.getColor(R.color.oui_background_color));
		}
	}
}
