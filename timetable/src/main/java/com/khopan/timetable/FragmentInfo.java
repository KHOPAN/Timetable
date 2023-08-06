package com.khopan.timetable;

public interface FragmentInfo {
	int getLayoutResourceIdentifier();
	int getIconResourceIdentifier();
	String getTitle();

	default void onEntered() {}
	default void onExited() {}

	default boolean onBackPressed() {
		return false;
	}
}
