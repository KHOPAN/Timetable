package com.khopan.timetable.fragment;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.preference.PreferenceManager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.khopan.api.common.card.CardView;
import com.khopan.timetable.BaseFragment;
import com.khopan.timetable.FragmentInfo;
import com.khopan.timetable.data.SubjectData;
import com.khopan.timetable.data.SubjectDataList;
import com.khopan.timetable.settings.FragmentSettingsActivity;
import com.khopan.timetable.settings.FragmentTitle;
import com.khopan.timetable.utils.SeparatedStringBuilder;
import com.sec.sesl.khopan.timetable.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import dev.oneuiproject.oneui.widget.RoundLinearLayout;

public class EditSubjectFragment extends BaseFragment implements FragmentInfo {
	private final List<ListEntry> list;

	private RoundLinearLayout allSubjectLayout;
	private AppCompatCheckBox allSubjectCheckbox;
	private View allSubjectSeparator;
	private RoundLinearLayout subjectLayout;
	private View addSubjectSeparator;
	private RoundLinearLayout addSubjectLayout;
	private BottomNavigationView actionView;

	private DisplayMetrics displayMetrics;

	private ActivityResultLauncher<Intent> launcher;
	private boolean expanded;
	private float actionViewSize;
	private boolean addable;
	private SharedPreferences preferences;

	public EditSubjectFragment() {
		this.list = new ArrayList<>();
	}

	@Override
	public void onAttach(@NonNull Context context) {
		super.onAttach(context);
		this.displayMetrics = this.resources.getDisplayMetrics();
		this.launcher = this.registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
			if(result.getResultCode() == Activity.RESULT_OK) {
				Intent data = result.getData();

				if(data != null) {
					Serializable hopefullySubjectData = data.getSerializableExtra("subjectData");

					if(hopefullySubjectData instanceof SubjectData) {
						this.addSubject((SubjectData) hopefullySubjectData);
					}
				}
			}
		});
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle bundle) {
		this.preferences = PreferenceManager.getDefaultSharedPreferences(this.context);
		LinearLayout linearLayout = view.findViewById(R.id.linearLayout);
		LayoutTransition linearLayoutTransition = new LayoutTransition();
		linearLayoutTransition.enableTransitionType(LayoutTransition.CHANGING);
		linearLayout.setLayoutTransition(linearLayoutTransition);
		this.allSubjectLayout = new RoundLinearLayout(this.context);
		this.allSubjectLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		this.allSubjectLayout.setOrientation(RoundLinearLayout.VERTICAL);
		this.allSubjectLayout.setBackgroundColor(this.context.getColor(R.color.oui_background_color));
		this.allSubjectLayout.setVisibility(View.GONE);
		linearLayout.addView(this.allSubjectLayout);
		CardView allSubjectCardView = new CardView(this.context);
		allSubjectCardView.containerView.removeAllViews();
		this.allSubjectCheckbox = new AppCompatCheckBox(this.context);
		this.allSubjectCheckbox.setText(this.getString(R.string.allSubject));
		TypedValue value = new TypedValue();
		this.context.getTheme().resolveAttribute(R.attr.textAppearanceListItem, value, true);
		this.allSubjectCheckbox.setTextAppearance(value.data);
		this.allSubjectCheckbox.setOnClickListener(instance -> this.allSubjectCheckbox());
		allSubjectCardView.setOnClickListener(instance -> this.allSubjectCheckbox.performClick());
		allSubjectCardView.containerView.addView(this.allSubjectCheckbox);
		this.allSubjectLayout.addView(allSubjectCardView);
		this.allSubjectSeparator = new View(this.context);
		this.allSubjectSeparator.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, this.context.getResources().getDimensionPixelSize(dev.oneuiproject.oneui.design.R.dimen.sesl_list_subheader_min_height)));
		this.allSubjectSeparator.setVisibility(View.GONE);
		linearLayout.addView(this.allSubjectSeparator);
		this.subjectLayout = new RoundLinearLayout(this.context);
		this.subjectLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		this.subjectLayout.setOrientation(RoundLinearLayout.VERTICAL);
		this.subjectLayout.setBackgroundColor(this.context.getColor(R.color.oui_background_color));
		linearLayout.addView(this.subjectLayout);
		this.addSubjectSeparator = new View(this.context);
		this.addSubjectSeparator.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, this.context.getResources().getDimensionPixelSize(dev.oneuiproject.oneui.design.R.dimen.sesl_list_subheader_min_height)));
		linearLayout.addView(this.addSubjectSeparator);
		this.addSubjectLayout = new RoundLinearLayout(this.context);
		this.addSubjectLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		this.addSubjectLayout.setOrientation(RoundLinearLayout.VERTICAL);
		this.addSubjectLayout.setBackgroundColor(this.context.getColor(R.color.oui_background_color));
		linearLayout.addView(this.addSubjectLayout);
		CardView addSubjectView = new CardView(this.context);
		addSubjectView.setTitle("Add Subject");
		addSubjectView.setImage(this.context.getDrawable(R.drawable.add_icon));
		FrameLayout.LayoutParams addSubjectViewImageViewParams = (FrameLayout.LayoutParams) addSubjectView.imageView.getLayoutParams();
		addSubjectViewImageViewParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
		addSubjectViewImageViewParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
		addSubjectView.setOnClickListener(instance -> this.addSubjectView());
		this.addSubjectLayout.addView(addSubjectView);
		this.actionView = view.findViewById(R.id.actionView);
		this.actionView.getMenu().getItem(0).setOnMenuItemClickListener(item -> this.deleteMenuItem());
		this.actionViewSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 62.4f, this.displayMetrics);
	}

	private void allSubjectCheckbox() {
		boolean state = this.allSubjectCheckbox.isChecked();

		for(ListEntry entry : this.list) {
			entry.subjectCheckbox.setChecked(state);
		}
	}

	private void addSubjectView() {
		if(this.expanded) {
			return;
		}

		FragmentSettingsActivity.launch(this, FragmentTitle.title(this.resources.getString(R.string.editSubject), this.resources.getString(R.string.addSubject)), this.launcher, AddSubjectFragment.class, null);
	}

	private boolean deleteMenuItem() {
		List<ListEntry> removeList = new ArrayList<>();

		for(ListEntry entry : this.list) {
			if(entry.subjectCheckbox.isChecked()) {
				removeList.add(entry);
			}
		}

		this.collapse();

		for(ListEntry entry : removeList) {
			this.subjectLayout.removeView(entry.subjectView);
		}

		this.list.removeAll(removeList);
		int size = this.list.size();

		for(int i = 0; i < size; i++) {
			this.list.get(i).subjectView.setDividerVisible(i != 0);
		}

		if(size == 0) {
			this.addSubjectSeparator.setVisibility(View.GONE);
		}

		this.addData();
		return true;
	}


	private void expand() {
		this.addSubjectSeparator.setVisibility(View.GONE);
		this.addSubjectLayout.animate().alpha(0.0f);

		if(this.list.size() > 1) {
			this.allSubjectLayout.setVisibility(View.VISIBLE);
			this.allSubjectSeparator.setVisibility(View.VISIBLE);
		}

		for(ListEntry entry : this.list) {
			entry.subjectCheckbox.setVisibility(View.VISIBLE);
		}

		this.actionView.animate().translationY(-this.actionViewSize);
		this.expanded = true;
	}

	private void collapse() {
		this.allSubjectCheckbox.setChecked(false);
		this.addSubjectSeparator.setVisibility(View.VISIBLE);
		this.addSubjectLayout.animate().alpha(1.0f);
		this.allSubjectLayout.setVisibility(View.GONE);
		this.allSubjectSeparator.setVisibility(View.GONE);

		for(ListEntry entry : this.list) {
			entry.subjectCheckbox.setChecked(false);
			entry.subjectCheckbox.setVisibility(View.GONE);
		}

		this.actionView.animate().translationY(0.0f);
		this.expanded = false;
	}

	private void addSubject(SubjectData data) {
		SeparatedStringBuilder summary = new SeparatedStringBuilder();
		summary.setSeparateText("\n");
		summary.setAddBrace(false);

		if(data.subjectIdentifier != null && !data.subjectIdentifier.isEmpty()) {
			summary.append(data.subjectIdentifier);
		}

		if(data.teacherList != null) {
			int length = data.teacherList.length;

			if(length > 0) {
				if(length == 1) {
					summary.append(data.teacherList[0]);
				} else {
					SeparatedStringBuilder teacher = new SeparatedStringBuilder();

					for(int i = 0; i < length; i++) {
						String teacherName = data.teacherList[i];
						teacher.append(teacherName);
					}

					summary.append(teacher);
				}
			}
		}

		CardView subjectView = new CardView(this.context);
		subjectView.setTitle(data.subjectName);
		subjectView.setSummary(summary.toString());
		AppCompatCheckBox subjectCheckbox = new AppCompatCheckBox(this.context);
		subjectView.setOnLongClickListener(view -> {
			this.expand();

			if(!subjectCheckbox.isChecked()) {
				subjectCheckbox.performClick();
			}

			return true;
		});

		subjectView.setOnClickListener(view -> {
			if(this.expanded) {
				subjectCheckbox.performClick();
			}
		});

		LinearLayout.LayoutParams subjectCheckboxParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		subjectCheckboxParams.rightMargin = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15.0f, this.displayMetrics));
		subjectCheckbox.setLayoutParams(subjectCheckboxParams);
		subjectCheckbox.setOnClickListener(view -> {
			if(this.list.size() > 1) {
				boolean state = true;

				for(ListEntry entry : this.list) {
					if(!entry.subjectCheckbox.isChecked()) {
						state = false;
					}
				}

				this.allSubjectCheckbox.setChecked(state);
			}
		});

		subjectCheckbox.setVisibility(View.GONE);
		subjectView.containerView.addView(subjectCheckbox, 0);
		subjectView.containerView.setLayoutTransition(new LayoutTransition());
		this.subjectLayout.addView(subjectView);
		subjectView.setDividerVisible(this.subjectLayout.indexOfChild(subjectView) != 0);
		ListEntry entry = new ListEntry();
		entry.subjectView = subjectView;
		entry.subjectCheckbox = subjectCheckbox;
		entry.data = data;
		this.list.add(entry);
		this.addSubjectSeparator.setVisibility(View.VISIBLE);
		this.addData();
	}

	private void addData() {
		if(this.addable) {
			try {
				ObjectMapper mapper = new ObjectMapper();
				SubjectDataList list = new SubjectDataList();
				list.dataList = new SubjectData[this.list.size()];

				for(int i = 0; i < list.dataList.length; i++) {
					list.dataList[i] = this.list.get(i).data;
				}

				SharedPreferences.Editor editor = this.preferences.edit();
				editor.putString("subjectList", mapper.writeValueAsString(list));
				editor.apply();
			} catch(Throwable ignored) {

			}
		}
	}

	private void refresh() {
		this.addable = false;
		this.list.clear();
		this.subjectLayout.removeAllViews();

		if(this.preferences.contains("subjectList")) {
			String subject = this.preferences.getString("subjectList", "");

			if(subject != null && !subject.isEmpty()) {
				try {
					ObjectMapper mapper = new ObjectMapper();
					SubjectDataList list = mapper.readValue(subject, SubjectDataList.class);

					if(list.dataList != null) {
						for(int i = 0; i < list.dataList.length; i++) {
							this.addSubject(list.dataList[i]);
						}
					}
				} catch(Throwable ignored) {

				}
			}
		}

		this.addable = true;
	}

	@Override
	public boolean onBackPressed() {
		if(!this.expanded) {
			return false;
		}

		this.collapse();
		return true;
	}

	@Override
	public void onEntered() {
		this.refresh();
	}

	@Override
	public int getLayoutResourceIdentifier() {
		return R.layout.edit_subject_fragment;
	}

	@Override
	public int getIconResourceIdentifier() {
		return R.drawable.ic_oui_compose_edit;
	}

	@Override
	public String getTitle() {
		return this.resources.getString(R.string.editSubject);
	}

	private static class ListEntry {
		private CardView subjectView;
		private AppCompatCheckBox subjectCheckbox;
		private SubjectData data;
	}
}
