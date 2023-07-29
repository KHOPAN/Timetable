package com.khopan.timetable.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.khopan.story.Episode;
import com.khopan.timetable.utils.SeparatedStringBuilder;
import com.sec.sesl.khopan.timetable.R;

import java.util.List;

import dev.oneuiproject.oneui.widget.Toast;

public class EpisodeFragment extends Fragment {
	private Context context;

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
		bundle = this.getArguments();

		if(bundle == null) {
			throw new NullPointerException("Arguments cannot be null");
		}

		JsonNode node = null;

		try {
			ObjectMapper mapper = new ObjectMapper();
			node = mapper.readTree(bundle.getString("episode"));
		} catch(Throwable ignored) {
			Toast.makeText(this.context, "Error while parsing JSON", Toast.LENGTH_SHORT).show();
		}

		Episode episode = Episode.deserialize(node);
		TextView contentView = view.findViewById(R.id.contentView);
		TextView additional = view.findViewById(R.id.additional);
		contentView.setText(episode.content().get());
		List<String> additionalInformation = episode.additionalInformationList();
		SeparatedStringBuilder builder = new SeparatedStringBuilder();
		builder.setSeparateText("\n");

		for(int i = 0; i < additionalInformation.size(); i++) {
			builder.append(additionalInformation.get(i));
		}

		additional.setText(builder.toString());
	}
}
