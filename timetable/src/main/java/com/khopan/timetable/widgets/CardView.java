package com.khopan.timetable.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.sec.sesl.khopan.timetable.R;

public class CardView extends LinearLayout {
	public final TextView titleTextView;
	public final TextView summaryTextView;

	private final Context context;

	boolean isIconView;
	boolean isDividerViewVisible;

	private final FrameLayout parentView;
	private final LinearLayout containerView;
	private final View dividerView;
	private int iconColor;
	private Drawable iconDrawable;
	private String titleText;
	private String summaryText;

	public CardView(@NonNull Context context, AttributeSet attribute) {
		super(context, attribute);
		this.context = context;
		this.setStyleable(attribute);
		this.removeAllViews();

		if(this.isIconView) {
			View.inflate(this.context, R.layout.cardview_widget_icon, this);
			ImageView iconImageView = this.findViewById(R.id.cardview_icon);
			iconImageView.setImageDrawable(this.iconDrawable);

			if(this.iconColor != -1) {
				iconImageView.getDrawable().setTint(this.iconColor);
			}
		} else {
			View.inflate(this.context, R.layout.cardview_widget, this);
		}

		this.parentView = this.findViewById(R.id.cardview_main_container);
		this.containerView = this.findViewById(R.id.cardview_container);
		this.titleTextView = this.findViewById(R.id.cardview_title);
		this.titleTextView.setText(this.titleText);
		this.summaryTextView = this.findViewById(R.id.cardview_summary);

		if(this.summaryText != null && !this.summaryText.isEmpty()) {
			this.summaryTextView.setText(this.summaryText);
			this.summaryTextView.setVisibility(View.VISIBLE);
		}

		this.dividerView = findViewById(R.id.cardview_divider);
		MarginLayoutParams parameters = (MarginLayoutParams) this.dividerView.getLayoutParams();
		int marginEnd = this.getResources().getDimensionPixelSize(R.dimen.cardview_icon_divider_margin_end);
		parameters.setMarginStart(this.isIconView ? marginEnd + getResources().getDimensionPixelSize(R.dimen.cardview_icon_size) + getResources().getDimensionPixelSize(R.dimen.cardview_icon_margin_end) - getResources().getDimensionPixelSize(R.dimen.cardview_icon_margin_vertical) : marginEnd);
		parameters.setMarginEnd(marginEnd);
		this.dividerView.setVisibility(this.isDividerViewVisible ? View.VISIBLE : View.GONE);
		this.setFocusable(true);
		this.setClickable(true);
	}

	public void setStyleable(AttributeSet attribute) {
		TypedArray array = this.context.obtainStyledAttributes(attribute, R.styleable.CardView); // close() = Crash, I don't know why.
		this.iconDrawable = array.getDrawable(R.styleable.CardView_IconDrawable);
		this.iconColor = array.getColor(R.styleable.CardView_IconColor, -1);
		this.titleText = array.getString(R.styleable.CardView_TitleText);
		this.summaryText = array.getString(R.styleable.CardView_SummaryText);
		this.isIconView = this.iconDrawable != null;
		this.isDividerViewVisible = array.getBoolean(R.styleable.CardView_isDividerViewVisible, false);
		array.recycle();
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		this.setFocusable(enabled);
		this.parentView.setEnabled(enabled);
		this.containerView.setAlpha(enabled ? 1.0f : 0.4f);
	}

	public String getTitleText() {
		return this.titleText;
	}

	public void setTitleText(String title) {
		this.titleText = title;
		this.titleTextView.setText(this.titleText);
	}

	public String getSummaryText() {
		return this.summaryText;
	}

	public void setSummaryText(String text) {
		if(text == null) {
			text = "";
		}

		this.summaryText = text;
		this.summaryTextView.setText(this.summaryText);

		if(this.summaryText.isEmpty()) {
			this.summaryTextView.setVisibility(View.GONE);
		} else {
			this.summaryTextView.setVisibility(View.VISIBLE);
		}
	}

	public void setDividerVisible(boolean visible) {
		this.dividerView.setVisibility(visible ? View.VISIBLE : View.GONE);
	}
}
