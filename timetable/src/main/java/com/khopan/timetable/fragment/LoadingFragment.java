package com.khopan.timetable.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sec.sesl.khopan.timetable.R;

public class LoadingFragment extends Fragment {
	private final String text;

	public LoadingFragment(String text) {
		this.text = text == null ? "" : text;
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup group, @Nullable Bundle bundle) {
		return inflater.inflate(R.layout.loading_fragment, group, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle bundle) {
		TextView fetchView = view.findViewById(R.id.fetchView);
		fetchView.setText(this.text);
	}
}
