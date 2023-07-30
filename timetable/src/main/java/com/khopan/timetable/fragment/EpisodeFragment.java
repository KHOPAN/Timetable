package com.khopan.timetable.fragment;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
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
import com.khopan.timetable.widgets.CardView;
import com.sec.sesl.khopan.timetable.R;

import java.util.List;

import dev.oneuiproject.oneui.widget.Toast;

public class EpisodeFragment extends Fragment {
	private Context context;
	private Resources resources;

	@Override
	public void onAttach(@NonNull Context context) {
		super.onAttach(context);
		this.context = context;
		this.resources = this.context.getResources();
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
			Toast.makeText(this.context, this.resources.getString(R.string.parsingJSONError), Toast.LENGTH_SHORT).show();
		}

		Episode episode = Episode.deserialize(node);
		TextView contentView = view.findViewById(R.id.contentView);
		CardView additional = view.findViewById(R.id.additional);
		contentView.setText(episode.content().get());
		List<String> additionalInformation = episode.additionalInformationList();
		SeparatedStringBuilder builder = new SeparatedStringBuilder();
		builder.setSeparateText("\n");

		for(int i = 0; i < additionalInformation.size(); i++) {
			builder.append(additionalInformation.get(i));
		}

		additional.setTitleText(builder.toString());
		float tenScalablePixel = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10.0f, this.resources.getDisplayMetrics());
		additional.titleTextView.setLineSpacing(tenScalablePixel, 1.0f);
		ViewGroup.MarginLayoutParams margin = (ViewGroup.MarginLayoutParams) additional.titleTextView.getLayoutParams();
		int intPixel = Math.round(tenScalablePixel);
		margin.topMargin = intPixel;
		margin.bottomMargin = intPixel;
	}
}
