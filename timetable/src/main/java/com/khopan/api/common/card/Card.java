package com.khopan.api.common.card;

import android.graphics.drawable.Drawable;
import android.view.View;

public class Card {
	private final CardView view;
	private final CardBuilder builder;

	Card(CardView view, CardBuilder builder) {
		this.view = view;
		this.builder = builder;
	}

	public Card action(View.OnClickListener listener) {
		this.view.setOnClickListener(listener);
		return this;
	}

	public Card title(String title) {
		if(title != null && !title.isEmpty()) {
			this.view.setTitle(title);
		}

		return this;
	}

	public Card summary(String summary) {
		if(summary != null && !summary.isEmpty()) {
			this.view.setSummary(summary);
		}

		return this;
	}

	public Card icon(Drawable image) {
		this.view.setImage(image);
		return this;
	}

	public Card dividerVisible(boolean visible) {
		this.view.setDividerVisible(visible);
		return this;
	}

	public CardView cardView() {
		return this.view;
	}

	public CardBuilder finish() {
		return this.builder;
	}
}
