package com.khopan.timetable.utils;

import androidx.annotation.NonNull;

public class SeparatedStringBuilder {
	private final StringBuilder builder;

	private String separateText;
	private boolean appended;
	private boolean addBrace;

	public SeparatedStringBuilder() {
		this.builder = new StringBuilder();
		this.separateText = ", ";
		this.appended = false;
		this.addBrace = true;
	}

	public String getSeparateText() {
		return this.separateText;
	}

	public void setSeparateText(String text) {
		this.separateText = text;
	}

	public boolean isAddBrace() {
		return this.addBrace;
	}

	public void setAddBrace(boolean addBrace) {
		this.addBrace = addBrace;
	}

	public void appendRaw(String text) {
		this.builder.append(text);
	}

	public void append(String text) {
		if(this.appended) {
			this.builder.append(this.separateText);
		} else {
			this.appended = true;
		}

		this.builder.append(text);
	}

	public void appendRaw(SeparatedStringBuilder builder) {
		if(this.addBrace) {
			this.builder.append('[');
		}

		this.builder.append(builder.toString());

		if(this.addBrace) {
			this.builder.append(']');
		}
	}

	public void append(SeparatedStringBuilder builder) {
		if(builder != null) {
			if(this.appended) {
				this.builder.append(this.separateText);
			} else {
				this.appended = true;
			}

			if(this.addBrace) {
				this.builder.append('[');

			}
			this.builder.append(builder);

			if(this.addBrace) {
				this.builder.append(']');
			}
		}
	}

	@NonNull
	@Override
	public String toString() {
		return this.builder.toString();
	}
}
