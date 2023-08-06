package com.khopan.api.common.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.TypedArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.core.widget.NestedScrollView;

import java.lang.reflect.Method;

import dev.oneuiproject.oneui.layout.ToolbarLayout;

public class ActivityUtils {
	private ActivityUtils() {}

	public static LinearLayout initializeView(Activity activity, CharSequence expandedTitle, CharSequence collapsedTitle, CharSequence expandedSubtitle) {
		FrameLayout frameLayout = ActivityUtils.initializeForFragment(activity, expandedTitle, collapsedTitle, expandedSubtitle);
		NestedScrollView scrollView = new NestedScrollView(activity);
		scrollView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		scrollView.setFillViewport(true);
		scrollView.setOverScrollMode(NestedScrollView.OVER_SCROLL_ALWAYS);

		try {
			String methodName = "initializeScrollbars";
			@SuppressLint("DiscouragedPrivateApi")
			Method method = View.class.getDeclaredMethod(methodName, TypedArray.class);
			method.setAccessible(true);
			method.invoke(scrollView, activity.obtainStyledAttributes(null, new int[] {}));
		} catch(Throwable ignored) {

		}

		scrollView.setVerticalScrollBarEnabled(true);
		frameLayout.addView(scrollView);
		LinearLayout linearLayout = new LinearLayout(activity);
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		scrollView.addView(linearLayout);
		return linearLayout;
	}

	public static FrameLayout initializeForFragment(Activity activity, CharSequence expandedTitle, CharSequence collapsedTitle, CharSequence expandedSubtitle) {
		ToolbarLayout toolbarLayout = new ToolbarLayout(activity, null);
		activity.setContentView(toolbarLayout);
		toolbarLayout.setTitle(expandedTitle, collapsedTitle);
		toolbarLayout.setExpandedSubtitle(expandedSubtitle);
		toolbarLayout.setExpanded(false, false);
		toolbarLayout.setNavigationButtonAsBack();
		FrameLayout frameLayout = new FrameLayout(activity);
		frameLayout.setLayoutParams(new ToolbarLayout.ToolbarLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		toolbarLayout.addView(frameLayout);
		return frameLayout;
	}
}
