package com.khopan.timetable;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.khopan.timetable.activity.AboutActivity;
import com.khopan.timetable.fragment.EditSubjectFragment;
import com.khopan.timetable.fragment.EditTimetableFragment;
import com.khopan.timetable.fragment.SettingsFragment;
import com.khopan.timetable.fragment.TimetableFragment;
import com.khopan.timetable.list.DrawerListAdapter;
import com.sec.sesl.khopan.timetable.R;
import com.sec.sesl.khopan.timetable.databinding.TimetableLayoutBinding;

import java.util.ArrayList;
import java.util.List;

public class TimetableApplication extends AppCompatActivity implements DrawerListAdapter.DrawerListener {
	private final List<Fragment> fragmentList;
	private final List<Fragment> fragmentDrawerList;

	private TimetableLayoutBinding binding;
	private Resources resources;
	private FragmentManager fragmentManager;
	private Fragment fragment;

	public TimetableApplication() {
		this.fragmentList = new ArrayList<>();
		this.fragmentDrawerList = new ArrayList<>();
	}

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		this.startService(new Intent(this, TimetableService.class));
		this.binding = TimetableLayoutBinding.inflate(this.getLayoutInflater());
		this.setContentView(this.binding.getRoot());
		this.resources = this.getResources();
		this.fragmentList.add(new TimetableFragment());
		this.fragmentList.add(new SettingsFragment());
		this.fragmentList.add(new EditTimetableFragment());
		this.fragmentList.add(new EditSubjectFragment());
		this.fragmentDrawerList.add(this.fragmentList.get(0));
		this.fragmentDrawerList.add(null);
		this.fragmentDrawerList.add(this.fragmentList.get(1));
		this.fragmentDrawerList.add(null);
		this.fragmentDrawerList.add(this.fragmentList.get(2));
		this.fragmentDrawerList.add(this.fragmentList.get(3));
		this.binding.drawerLayout.setDrawerButtonIcon(this.getDrawable(R.drawable.ic_oui_info_outline));
		this.binding.drawerLayout.setDrawerButtonOnClickListener(view -> this.startActivity(new Intent(this, AboutActivity.class)));
		this.binding.drawerListView.setLayoutManager(new LinearLayoutManager(this));
		this.binding.drawerListView.setAdapter(new DrawerListAdapter(this, this.fragmentDrawerList, this));
		this.binding.drawerListView.setHasFixedSize(true);
		this.fragmentManager = this.getSupportFragmentManager();
		FragmentTransaction transaction = this.fragmentManager.beginTransaction();

		for(int i = 0; i < this.fragmentList.size(); i++) {
			transaction.add(R.id.main_content, this.fragmentList.get(i));
		}

		transaction.commit();
		this.fragmentManager.executePendingTransactions();
		this.onDrawerItemSelected(0);
	}

	public void setFragment(int fragmentIndex) {
		this.fragment = this.fragmentList.get(fragmentIndex);
		FragmentTransaction transaction = this.fragmentManager.beginTransaction();
		List<Fragment> fragments = this.fragmentManager.getFragments();

		for(int i = 0; i < fragments.size(); i++) {
			Fragment loopFragment = fragments.get(i);
			transaction.hide(loopFragment);

			if(loopFragment instanceof FragmentInfo) {
				if(loopFragment == this.fragment) {
					((FragmentInfo) loopFragment).onEntered();
				} else {
					((FragmentInfo) loopFragment).onExited();
				}
			}
		}

		transaction.show(this.fragment);
		transaction.commit();

		if(this.fragment instanceof FragmentInfo) {
			FragmentInfo info = (FragmentInfo) this.fragment;
			String title = info.getTitle();
			this.binding.drawerLayout.setTitle(this.resources.getString(R.string.app_name), title);
			this.binding.drawerLayout.setExpandedSubtitle(title);
		}

		this.binding.drawerLayout.setDrawerOpen(false, true);
	}

	@Override
	public boolean onDrawerItemSelected(int position) {
		this.setFragment(this.fragmentList.indexOf(this.fragmentDrawerList.get(position)));
		return true;
	}

	@Override
	public void onBackPressed() {
		if(this.fragment == null || (this.fragment instanceof FragmentInfo && !(((FragmentInfo) this.fragment).onBackPressed()))) {
			super.onBackPressed();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		this.binding.drawerLayout.setDrawerOpen(true, false);
		this.binding.drawerLayout.setDrawerOpen(false, false);
	}

	@Override
	protected void onPause() {
		super.onPause();
		this.binding.drawerLayout.setDrawerOpen(true, false);
		this.binding.drawerLayout.setDrawerOpen(false, false);
	}

	@Override
	protected void onStop() {
		super.onStop();
		this.binding.drawerLayout.setDrawerOpen(true, false);
		this.binding.drawerLayout.setDrawerOpen(false, false);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.binding.drawerLayout.setDrawerOpen(true, false);
		this.binding.drawerLayout.setDrawerOpen(false, false);
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		this.binding.drawerLayout.setDrawerOpen(true, false);
		this.binding.drawerLayout.setDrawerOpen(false, false);
	}

	@Override
	protected void onPostResume() {
		super.onPostResume();
		this.binding.drawerLayout.setDrawerOpen(true, false);
		this.binding.drawerLayout.setDrawerOpen(false, false);
	}
}
