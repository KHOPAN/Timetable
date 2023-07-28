package com.khopan.timetable.settings;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.sec.sesl.khopan.timetable.R;
import com.sec.sesl.khopan.timetable.databinding.FragmentSettingsActivityBinding;

public class FragmentSettingsActivity extends AppCompatActivity {
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		Intent intent = this.getIntent();

		if(intent != null) {
			bundle = intent.getExtras();

			if(bundle != null && bundle.containsKey("className")) {
				Object serialized = bundle.getSerializable("className");

				if(serialized instanceof Class<?>) {
					Class<?> fragmentClass = (Class<?>) serialized;

					try {
						Object hopefullyFragment = fragmentClass.getConstructor().newInstance();

						if(hopefullyFragment instanceof Fragment) {
							Fragment fragment = (Fragment) hopefullyFragment;
							Bundle arguments = intent.getExtras();

							if(arguments != null) {
								fragment.setArguments(arguments);
							}

							String expandedTitle = "";
							String collapsedTitle = "";
							String expandedSubTitle = "";
							boolean setBackground = true;

							if(bundle.containsKey("expandedTitle")) {
								expandedTitle = bundle.getString("expandedTitle");
							}

							if(bundle.containsKey("collapsedTitle")) {
								collapsedTitle = bundle.getString("collapsedTitle");
							}

							if(bundle.containsKey("expandedSubTitle")) {
								expandedSubTitle = bundle.getString("expandedSubTitle");
							}

							if(bundle.containsKey("setBackground")) {
								setBackground = bundle.getBoolean("setBackground");
							}

							FragmentSettingsActivityBinding binding = FragmentSettingsActivityBinding.inflate(this.getLayoutInflater());
							this.setContentView(binding.getRoot());
							binding.drawerLayout.setNavigationButtonAsBack();
							binding.drawerLayout.setTitle(expandedTitle, collapsedTitle);
							binding.drawerLayout.setExpandedSubtitle(expandedSubTitle);

							if(setBackground) {
								binding.drawerLayout.setBackgroundColor(this.getColor(R.color.oui_background_color));
							}

							this.getSupportFragmentManager()
									.beginTransaction()
									.add(R.id.main_content, fragment)
									.show(fragment)
									.commit();
						}
					} catch(Throwable ignored) {

					}
				}
			}
		}
	}

	public static boolean start(Fragment fragment, @Nullable FragmentTitle title, Class<? extends Fragment> fragmentClass, @Nullable Bundle fragmentExtras) {
		return FragmentSettingsActivity.start(fragment, title, fragmentClass, fragmentExtras, true);
	}

	public static boolean start(Fragment fragment, @Nullable FragmentTitle title, Class<? extends Fragment> fragmentClass, @Nullable Bundle fragmentExtras, boolean setBackground) {
		Intent intent = FragmentSettingsActivity.createIntent(fragment, title, fragmentClass, fragmentExtras, setBackground);

		if(intent != null) {
			FragmentActivity activity = fragment.getActivity();

			if(activity != null) {
				activity.startActivity(intent);
			}
		}

		return false;
	}

	public static boolean result(Fragment fragment, @Nullable FragmentTitle title, int requestCode, Class<? extends Fragment> fragmentClass, @Nullable Bundle fragmentExtras) {
		return FragmentSettingsActivity.result(fragment, title, requestCode, fragmentClass, fragmentExtras, true);
	}

	public static boolean result(Fragment fragment, @Nullable FragmentTitle title, int requestCode, Class<? extends Fragment> fragmentClass, @Nullable Bundle fragmentExtras, boolean setBackground) {
		Intent intent = FragmentSettingsActivity.createIntent(fragment, title, fragmentClass, fragmentExtras, setBackground);

		if(intent != null) {
			FragmentActivity activity = fragment.getActivity();

			if(activity != null) {
				activity.startActivityForResult(intent, requestCode);
			}
		}

		return false;
	}

	public static boolean finish(Fragment fragment) {
		if(fragment != null) {
			FragmentActivity activity = fragment.getActivity();

			if(activity != null) {
				activity.finish();
			}
		}

		return false;
	}

	public static boolean finish(Fragment fragment, int resultCode, Intent data) {
		if(fragment != null) {
			FragmentActivity activity = fragment.getActivity();

			if(activity != null) {
				activity.setResult(resultCode, data);
				activity.finish();
			}
		}

		return false;
	}

	public static boolean launch(Fragment fragment, @Nullable FragmentTitle title, ActivityResultLauncher<Intent> launcher, Class<? extends Fragment> fragmentClass, @Nullable Bundle fragmentExtras) {
		return FragmentSettingsActivity.launch(fragment, title, launcher, fragmentClass, fragmentExtras, true);
	}

	public static boolean launch(Fragment fragment, @Nullable FragmentTitle title, ActivityResultLauncher<Intent> launcher, Class<? extends Fragment> fragmentClass, @Nullable Bundle fragmentExtras, boolean setBackground) {
		if(launcher != null) {
			Intent intent = FragmentSettingsActivity.createIntent(fragment, title, fragmentClass, fragmentExtras, setBackground);

			if(intent != null) {
				launcher.launch(intent);
			}
		}

		return false;
	}

	private static Intent createIntent(Fragment fragment, @Nullable FragmentTitle title, Class<? extends Fragment> fragmentClass, @Nullable Bundle fragmentExtras, boolean setBackground) {
		if(fragment != null && fragmentClass != null) {
			FragmentActivity activity = fragment.getActivity();

			if(activity != null) {
				Intent intent = new Intent(fragment.getContext(), FragmentSettingsActivity.class);
				Bundle extras = new Bundle();
				extras.putSerializable("className", fragmentClass);

				if(title == null) {
					title = FragmentTitle.empty();
				}

				extras.putString("expandedTitle", title.expandedTitle == null ? "" : title.expandedTitle);
				extras.putString("collapsedTitle", title.collapsedTitle == null ? "" : title.collapsedTitle);
				extras.putString("expandedSubTitle", title.expandedSubTitle == null ? "" : title.expandedSubTitle);
				extras.putBoolean("setBackground", setBackground);
				intent.putExtras(extras);

				if(fragmentExtras != null) {
					intent.putExtras(fragmentExtras);
				}

				return intent;
			}
		}

		return null;
	}
}
