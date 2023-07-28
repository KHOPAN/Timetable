package com.khopan.timetable;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public abstract class BaseFragment extends Fragment implements FragmentInfo {
	protected Context context;
	protected Resources resources;

	@Override
	public void onAttach(@NonNull Context context) {
		super.onAttach(context);
		this.context = context;
		this.resources = this.getResources();
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup group, @Nullable Bundle bundle) {
		return inflater.inflate(this.getLayoutResourceIdentifier(), group, false);
	}
}
