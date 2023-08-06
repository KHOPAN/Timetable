package com.khopan.api.common.card;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.sec.sesl.khopan.timetable.R;

public class CardView extends LinearLayout {
	public final TextView titleTextView;
	public final TextView summaryTextView;
	public final View dividerView;
	public final FrameLayout parentView;
	public final LinearLayout containerView;
	public final RelativeLayout relativeLayout;

	public FrameLayout imageLayout;
	public ImageView imageView;

	private final Context context;

	private String titleText;
	private String summaryText;
	private boolean isDividerViewVisible;
	private Drawable image;

	public CardView(@NonNull Context context) {
		this(context, null);
	}

	public CardView(@NonNull Context context, Drawable image) {
		super(context);
		this.context = context;
		this.image = image;
		this.dividerView = new View(this.context);
		this.parentView = new FrameLayout(this.context);
		this.containerView = new LinearLayout(this.context);
		this.titleTextView = new TextView(this.context);
		this.summaryTextView = new TextView(this.context);
		this.relativeLayout = new RelativeLayout(this.context);
		this.refresh();
		this.setTitle("");
		this.setSummary("");
		this.setDividerVisible(false);
		this.setEnabled(true);
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		this.setFocusable(enabled);
		this.setClickable(enabled);
		this.parentView.setEnabled(enabled);
		this.containerView.setAlpha(enabled ? 1.0f : 0.4f);
	}

	public String getTitle() {
		return this.titleText;
	}

	public String getSummary() {
		return this.summaryText;
	}

	public Drawable getImage() {
		return this.image;
	}

	public boolean isDividerVisible() {
		return this.isDividerViewVisible;
	}

	public void setTitle(String title) {
		if(title == null) {
			title = "";
		}

		this.titleText = title;
		this.titleTextView.setText(this.titleText);

		if(this.titleText.isEmpty()) {
			this.titleTextView.setVisibility(View.GONE);
		} else {
			this.titleTextView.setVisibility(View.VISIBLE);
		}
	}

	public void setSummary(String summary) {
		if(summary == null) {
			summary = "";
		}

		this.summaryText = summary;
		this.summaryTextView.setText(this.summaryText);

		if(this.summaryText.isEmpty()) {
			this.summaryTextView.setVisibility(View.GONE);
		} else {
			this.summaryTextView.setVisibility(View.VISIBLE);
		}
	}

	public void setImage(Drawable image) {
		this.image = image;
		this.refresh();
	}

	public void setDividerVisible(boolean visible) {
		this.isDividerViewVisible = visible;
		this.dividerView.setVisibility(this.isDividerViewVisible ? View.VISIBLE : View.GONE);
	}

	public void refresh() {
		boolean isIconView = this.image != null;
		this.containerView.removeAllViews();
		this.parentView.removeAllViews();
		this.relativeLayout.removeAllViews();
		this.removeAllViews();
		this.addView(this.parentView);
		ViewGroup.LayoutParams parentParams = this.parentView.getLayoutParams();
		parentParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
		parentParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
		this.parentView.setLayoutParams(parentParams);
		Resources resources = this.context.getResources();
		this.parentView.addView(this.dividerView);
		FrameLayout.LayoutParams dividerViewParams = (FrameLayout.LayoutParams) this.dividerView.getLayoutParams();
		dividerViewParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
		dividerViewParams.height = Math.round(resources.getDimension(R.dimen.sesl_list_divider_height));
		TypedValue value = new TypedValue();
		Resources.Theme theme = this.context.getTheme();
		theme.resolveAttribute(R.attr.listDividerColor, value, true);
		this.dividerView.setBackgroundColor(value.data);
		DisplayMetrics metrics = this.context.getResources().getDisplayMetrics();
		int marginEnd = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24.0f, metrics));
		dividerViewParams.setMarginStart(isIconView ? marginEnd + Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 38.0f, metrics)) + Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16.0f, metrics)) - Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8.0f, metrics)) : marginEnd);
		dividerViewParams.setMarginEnd(marginEnd);
		this.containerView.setMinimumHeight(Math.round(resources.getDimension(R.dimen.sesl_list_preferred_item_height_small)));
		int paddingStart;

		if(isIconView) {
			paddingStart = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20.0f, metrics));
		} else {
			value = new TypedValue();
			theme.resolveAttribute(R.attr.listPreferredItemPaddingStart, value, true);
			paddingStart = Math.round(this.getResources().getDimension(value.resourceId));
		}

		value = new TypedValue();
		theme.resolveAttribute(R.attr.listPreferredItemPaddingEnd, value, true);
		int paddingEnd = Math.round(this.getResources().getDimension(value.resourceId));
		this.containerView.setPadding(paddingStart, 0, paddingEnd, 0);
		this.parentView.addView(this.containerView);
		FrameLayout.LayoutParams containerViewParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		containerViewParams.gravity = Gravity.CENTER;
		this.containerView.setLayoutParams(containerViewParams);
		this.containerView.setGravity(Gravity.CENTER_VERTICAL);
		value = new TypedValue();
		theme.resolveAttribute(R.attr.listChoiceBackgroundIndicator, value, true);
		this.containerView.setBackground(this.context.getDrawable(value.resourceId));
		this.containerView.setBaselineAligned(false);

		if(isIconView) {
			this.imageLayout = new FrameLayout(this.context);
			LayoutParams frameLayoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			frameLayoutParams.gravity = Gravity.CENTER | Gravity.START;
			this.imageLayout.setLayoutParams(frameLayoutParams);
			this.imageLayout.setPadding(0, 0, Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16.0f, metrics)), 0);
			this.containerView.addView(this.imageLayout);
			this.imageView = new ImageView(this.context);
			int size = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 38.0f, metrics));
			FrameLayout.LayoutParams iconViewParams = new FrameLayout.LayoutParams(size, size);
			int margin = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8.0f, metrics));
			iconViewParams.topMargin = margin;
			iconViewParams.bottomMargin = margin;
			iconViewParams.gravity = Gravity.CENTER;
			this.imageView.setLayoutParams(iconViewParams);
			this.imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
			this.imageView.setImageDrawable(this.image);
			this.imageLayout.addView(this.imageView);
		}

		LayoutParams relativeLayoutParams = new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
		relativeLayoutParams.weight = 1;
		this.relativeLayout.setLayoutParams(relativeLayoutParams);
		int padding = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14.0f, metrics));
		this.relativeLayout.setPadding(0, padding, 0, padding);
		this.containerView.addView(this.relativeLayout);
		this.titleTextView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		value = new TypedValue();
		theme.resolveAttribute(R.attr.textAppearanceListItem, value, true);
		this.titleTextView.setTextAppearance(value.data);
		int titleTextViewIdentifier = View.generateViewId();
		this.titleTextView.setId(titleTextViewIdentifier);

		if(isIconView) {
			paddingEnd = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12.0f, metrics));
			this.titleTextView.setPadding(0, 0, paddingEnd, 0);
		}

		this.relativeLayout.addView(this.titleTextView);
		RelativeLayout.LayoutParams summaryTextViewParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		summaryTextViewParams.addRule(RelativeLayout.BELOW, titleTextViewIdentifier);
		summaryTextViewParams.addRule(RelativeLayout.ALIGN_START, titleTextViewIdentifier);
		this.summaryTextView.setLayoutParams(summaryTextViewParams);
		value = new TypedValue();
		theme.resolveAttribute(android.R.attr.textAppearanceSmall, value, true);
		this.summaryTextView.setTextAppearance(value.data);

		if(isIconView) {
			this.summaryTextView.setPadding(0, 0, paddingEnd, 0);
		}

		this.relativeLayout.addView(this.summaryTextView);
	}
}
