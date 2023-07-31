package com.khopan.timetable.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.khopan.timetable.widgets.CardView;
import com.sec.sesl.khopan.timetable.BuildConfig;
import com.sec.sesl.khopan.timetable.R;

import dev.oneuiproject.oneui.layout.ToolbarLayout;

public class AboutActivity extends AppCompatActivity {
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		this.setContentView(R.layout.fragment_settings_activity);
		ToolbarLayout toolbar = this.findViewById(R.id.drawerLayout);
		toolbar.setTitle(this.getString(R.string.app_name), this.getString(R.string.aboutTimetable));
		toolbar.setExpandedSubtitle(this.getString(R.string.aboutTimetable));
		toolbar.setNavigationButtonAsBack();
		toolbar.setExpanded(false, false);
		this.getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.main_content, new AboutFragment(this))
				.commit();
	}

	public static class AboutFragment extends Fragment {
		private final AboutActivity activity;

		private Context context;
		private Resources resources;

		public AboutFragment(AboutActivity activity) {
			this.activity = activity;
		}

		@Override
		public void onAttach(@NonNull Context context) {
			super.onAttach(context);
			this.context = context;
			this.resources = this.context.getResources();
		}

		@Nullable
		@Override
		public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup group, @Nullable Bundle bundle) {
			return inflater.inflate(R.layout.about_fragment, group, false);
		}

		@Override
		public void onViewCreated(@NonNull View view, @Nullable Bundle bundle) {
			ImageView timetableIcon = view.findViewById(R.id.timetableIcon);
			timetableIcon.setImageDrawable(this.context.getDrawable(R.mipmap.launcher_icon));
			TextView timetableVersion = view.findViewById(R.id.timetableVersion);
			timetableVersion.setText(this.resources.getString(R.string.version, BuildConfig.VERSION_NAME, BuildConfig.BUILD_NUMBER));
			CardView whoOne = view.findViewById(R.id.whoOne);
			whoOne.setTitleText("???");
			whoOne.setSummaryText("???????? ????????");
			CardView whoTwo = view.findViewById(R.id.whoTwo);
			whoTwo.setTitleText("?????");
			whoTwo.setSummaryText("??????? ????????");
			CardView timetableLicense = view.findViewById(R.id.timetableLicense);
			timetableLicense.setOnClickListener(cardView -> {
				Intent intent = new Intent(this.activity, LicenseActivity.class);
				Bundle extras = new Bundle();
				extras.putString("licenseOf", this.getString(R.string.app_name));
				extras.putString("licenseName", "Apache License 2.0");
				extras.putString("license", "https://raw.githubusercontent.com/KHOPAN/Timetable/main/LICENSE");
				extras.putString("licenseUrl", "https://github.com/KHOPAN/Timetable/blob/main/LICENSE");
				intent.putExtras(extras);
				this.activity.startActivity(intent);
			});

			CardView oneuiprojectLicense = view.findViewById(R.id.oneuiprojectLicense);
			oneuiprojectLicense.setOnClickListener(cardView -> {
				Intent intent = new Intent(this.activity, LicenseActivity.class);
				Bundle extras = new Bundle();
				extras.putString("licenseOf", "OneUI Project");
				extras.putString("licenseName", "MIT License");
				extras.putString("license", "https://raw.githubusercontent.com/KHOPAN/Timetable/main/LICENSE_ONEUI_PROJECT");
				extras.putString("licenseUrl", "https://github.com/KHOPAN/Timetable/blob/main/LICENSE_ONEUI_PROJECT");
				intent.putExtras(extras);
				this.activity.startActivity(intent);
			});
		}
	}
}
