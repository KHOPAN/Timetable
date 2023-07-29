package com.khopan.timetable.appwidget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

import com.sec.sesl.khopan.timetable.R;

public class TimetableWidgetProvider extends AppWidgetProvider {
	public static String CurrentSubject;
	public static String CurrentSubjectIdentifier;
	public static String CurrentSubjectTeacher;
	public static String CurrentSubjectTimezone;

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		if(TimetableWidgetProvider.CurrentSubject == null) {
			String none = context.getString(R.string.noneText);
			TimetableWidgetProvider.CurrentSubject = none;
			TimetableWidgetProvider.CurrentSubjectIdentifier = none;
			TimetableWidgetProvider.CurrentSubjectTeacher = none;
			TimetableWidgetProvider.CurrentSubjectTimezone = none;
		}

		for(int identifier : appWidgetIds) {
			RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.timetable_widget);
			views.setTextViewText(R.id.subjectView, context.getString(R.string.currentSubject) + ": " + TimetableWidgetProvider.CurrentSubject);
			views.setTextViewText(R.id.subjectIdentifierView, context.getString(R.string.identifier) + ": " + TimetableWidgetProvider.CurrentSubjectIdentifier);
			views.setTextViewText(R.id.subjectTeacherView, context.getString(R.string.teacher) + ": " + TimetableWidgetProvider.CurrentSubjectTeacher);
			views.setTextViewText(R.id.subjectTimezoneView, context.getString(R.string.timezone) + ": " + TimetableWidgetProvider.CurrentSubjectTimezone);
			appWidgetManager.updateAppWidget(identifier, views);
		}
	}
}
