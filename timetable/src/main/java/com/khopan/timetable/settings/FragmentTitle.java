package com.khopan.timetable.settings;

public class FragmentTitle {
	final String expandedTitle;
	final String collapsedTitle;
	final String expandedSubTitle;

	private FragmentTitle(String expandedTitle, String collapsedTitle, String expandedSubTitle) {
		this.expandedTitle = expandedTitle;
		this.collapsedTitle = collapsedTitle;
		this.expandedSubTitle = expandedSubTitle;
	}

	public static FragmentTitle empty() {
		return new FragmentTitle("", "", "");
	}

	public static FragmentTitle title(String expandedTitle, String collapsedTitle, String expandedSubTitle) {
		return new FragmentTitle(expandedTitle, collapsedTitle, expandedSubTitle);
	}

	public static FragmentTitle title(String title, String subTitle) {
		return new FragmentTitle(title, subTitle, subTitle);
	}
}
