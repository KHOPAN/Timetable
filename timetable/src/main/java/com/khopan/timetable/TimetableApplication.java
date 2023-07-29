package com.khopan.timetable;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

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
		this.binding.drawerListView.setLayoutManager(new LinearLayoutManager(this));
		this.binding.drawerListView.setAdapter(new DrawerListAdapter(this, this.fragmentDrawerList, this));
		this.binding.drawerListView.setItemAnimator(null);
		this.binding.drawerListView.setHasFixedSize(true);
		this.binding.drawerListView.seslSetLastRoundedCorner(false);
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
		Fragment fragment = this.fragmentList.get(fragmentIndex);
		FragmentTransaction transaction = this.fragmentManager.beginTransaction();
		List<Fragment> fragments = this.fragmentManager.getFragments();

		for(int i = 0; i < fragments.size(); i++) {
			Fragment loopFragment = fragments.get(i);
			transaction.hide(loopFragment);

			if(loopFragment instanceof FragmentInfo) {
				if(loopFragment == fragment) {
					((FragmentInfo) loopFragment).onEntered();
				} else {
					((FragmentInfo) loopFragment).onExited();
				}
			}
		}

		transaction.show(fragment);
		transaction.commit();

		if(fragment instanceof FragmentInfo) {
			FragmentInfo info = (FragmentInfo) fragment;
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
}
