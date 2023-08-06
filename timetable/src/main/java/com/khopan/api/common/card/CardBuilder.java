package com.khopan.api.common.card;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.sec.sesl.khopan.timetable.R;

import dev.oneuiproject.oneui.widget.RoundLinearLayout;
import dev.oneuiproject.oneui.widget.Separator;

public class CardBuilder {
	private final LinearLayout view;
	private final Context context;

	private RoundLinearLayout layout;
	private boolean added;
	private Separator separator;

	public CardBuilder(LinearLayout view, Context context) {
		if(view == null) {
			throw new NullPointerException("View cannot be null");
		}

		if(context == null) {
			throw new NullPointerException("Context cannot be null");
		}

		this.view = view;
		this.context = context;
	}

	public RoundLinearLayout layout() {
		this.layout = new RoundLinearLayout(this.context);
		this.layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		this.layout.setBackgroundColor(this.context.getColor(R.color.oui_background_color));
		this.layout.setOrientation(RoundLinearLayout.VERTICAL);
		this.view.addView(this.layout);
		return this.layout;
	}

	public Card card() {
		if(this.layout == null) {
			this.layout();
		}

		CardView view = new CardView(this.context);
		view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		view.setDividerVisible(this.added);
		this.layout.addView(view);
		this.added = true;
		return new Card(view, this);
	}

	public CardBuilder separate() {
		return this.separate("");
	}

	public CardBuilder separate(String text) {
		this.separator = new Separator(this.context);
		this.separator.setText(text == null ? "" : text);
		this.view.addView(this.separator);
		this.layout = null;
		this.added = false;
		return this;
	}

	public Separator separator() {
		return this.separator;
	}

	public LinearLayout finishView() {
		return this.view;
	}

	public Context finishContext() {
		return this.context;
	}
}
