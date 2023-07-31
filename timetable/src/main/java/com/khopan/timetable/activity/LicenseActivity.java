package com.khopan.timetable.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.khopan.timetable.fragment.LoadingFragment;
import com.khopan.timetable.secret.GitHubRequest;
import com.khopan.timetable.utils.SeparatedStringBuilder;
import com.khopan.timetable.widgets.CardView;
import com.sec.sesl.khopan.timetable.R;

import java.nio.charset.StandardCharsets;

import dev.oneuiproject.oneui.layout.ToolbarLayout;
import dev.oneuiproject.oneui.widget.Toast;

public class LicenseActivity extends AppCompatActivity {
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		Intent intent = this.getIntent();

		if(intent == null) {
			throw new NullPointerException("Intent cannot be null");
		}

		Bundle extras = intent.getExtras();
		String licenseOf = extras.getString("licenseOf");
		String licenseName = extras.getString("licenseName");
		String license = extras.getString("license");
		String licenseUrl = extras.getString("licenseUrl");
		this.setContentView(R.layout.fragment_settings_activity);
		ToolbarLayout toolbar = this.findViewById(R.id.drawerLayout);
		toolbar.setTitle(licenseName, licenseOf);
		toolbar.setExpandedSubtitle(licenseOf);
		toolbar.setNavigationButtonAsBack();
		toolbar.setExpanded(false, false);
		this.getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.main_content, new LoadingFragment(this.getString(R.string.loadingLicense)))
				.commit();

		new Thread(() -> {
			byte[] response = GitHubRequest.request(license);

			if(response == null) {
				this.runOnUiThread(() -> Toast.makeText(this, this.getString(R.string.nullResponseGitHub), Toast.LENGTH_LONG));
			}

			String text = new String(response, StandardCharsets.UTF_8);
			SeparatedStringBuilder licenseText = new SeparatedStringBuilder();
			licenseText.setSeparateText("\n");

			for(String line : text.split("\n")) {
				licenseText.append(line.trim());
			}

			this.getSupportFragmentManager()
					.beginTransaction()
					.replace(R.id.main_content, new LicenseFragment(licenseText.toString(), licenseUrl))
					.commit();
		}).start();
	}

	public static class LicenseFragment extends Fragment {
		private final String license;
		private final String licenseUrl;

		private Context context;

		public LicenseFragment(String license, String licenseUrl) {
			this.license = license;
			this.licenseUrl = licenseUrl;
		}

		@Override
		public void onAttach(@NonNull Context context) {
			super.onAttach(context);
			this.context = context;
		}

		@Nullable
		@Override
		public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup group, @Nullable Bundle bundle) {
			return inflater.inflate(R.layout.episode_fragment, group, false);
		}

		@Override
		public void onViewCreated(@NonNull View view, @Nullable Bundle bundle) {
			super.onViewCreated(view, bundle);
			TextView contentView = view.findViewById(R.id.contentView);
			contentView.setText(this.license);
			contentView.setGravity(Gravity.CENTER);
			contentView.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10.5f, view.getResources().getDisplayMetrics()));
			CardView additional = view.findViewById(R.id.additional);
			additional.setTitleText(this.getString(R.string.viewOnGitHub));
			additional.setSummaryText(this.licenseUrl);
			additional.setOnClickListener(additionalView -> {
				try {
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setData(Uri.parse(this.licenseUrl));
					this.requireActivity().startActivity(intent);
				} catch(Throwable ignored) {
					Toast.makeText(this.context, this.getString(R.string.errorUrl, this.licenseUrl), Toast.LENGTH_LONG).show();
				}
			});
		}
	}
}
