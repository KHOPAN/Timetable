package com.khopan.timetable.activity;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

import com.fasterxml.jackson.databind.JsonNode;
import com.khopan.timetable.fragment.LoadingFragment;
import com.khopan.timetable.secret.SecretTimetableFetcher;
import com.sec.sesl.khopan.timetable.R;
import com.sec.sesl.khopan.timetable.databinding.FragmentSettingsActivityBinding;

public class SyncTimetableActivity extends AppCompatActivity {
	public FragmentSettingsActivityBinding binding;

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		this.binding = FragmentSettingsActivityBinding.inflate(this.getLayoutInflater());
		this.setContentView(this.binding.getRoot());
		this.binding.drawerLayout.setNavigationButtonAsBack();
		String syncText = this.getString(R.string.syncTimetable);
		this.binding.drawerLayout.setTitle(this.getString(R.string.settings), syncText);
		this.binding.drawerLayout.setExpandedSubtitle(syncText);
		this.getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.main_content, new LoadingFragment(this.getString(R.string.fetchGitHub)))
				.commit();

		new Thread(() -> SecretTimetableFetcher.fetch(this)).start();
	}

	public static class SyncTimetableFragment extends PreferenceFragmentCompat {
		private final JsonNode node;

		private Context context;

		public SyncTimetableFragment(JsonNode node) {
			this.node = node;
		}

		@Override
		public void onAttach(@NonNull Context context) {
			super.onAttach(context);
			this.context = context;
		}

		@Override
		public void onCreatePreferences(Bundle bundle, String rootKey) {
			SecretTimetableFetcher.preference(this, this.context, this.node);
		}
	}
}
