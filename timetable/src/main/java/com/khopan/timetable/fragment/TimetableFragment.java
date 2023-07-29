package com.khopan.timetable.fragment;

import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.PreferenceManager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.khopan.timetable.BaseFragment;
import com.khopan.timetable.FragmentInfo;
import com.khopan.timetable.TickRegistry;
import com.khopan.timetable.TimetableApplication;
import com.khopan.timetable.TimetableService;
import com.khopan.timetable.appwidget.TimetableWidgetProvider;
import com.khopan.timetable.data.Subject;
import com.khopan.timetable.data.SubjectData;
import com.khopan.timetable.data.SubjectDataList;
import com.khopan.timetable.data.SubjectList;
import com.khopan.timetable.utils.SeparatedStringBuilder;
import com.sec.sesl.khopan.timetable.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TimetableFragment extends BaseFragment implements FragmentInfo {
	static DateFormat TimeFormat;
	static DateFormat DateFormat;

	private Activity activity;
	private Resources resources;
	private long lastTime;
	private Vibrator vibrator;

	private TextView currentSubjectView;
	private TextView currentSubjectIdentifierView;
	private TextView currentSubjectTeacherView;
	private TextView currentSubjectTimezoneView;
	private TextView timeView;
	private TextView dateView;
	private TextView nextSubjectView;
	private TextView nextSubjectIdentifierView;
	private TextView nextSubjectTeacherView;
	private TextView nextSubjectTimezoneView;
	private TextView previousSubjectView;
	private TextView previousSubjectIdentifierView;
	private TextView previousSubjectTeacherView;
	private TextView previousSubjectTimezoneView;

	@Override
	public void onAttach(@NonNull Context context) {
		super.onAttach(context);
		this.activity = this.requireActivity();
		this.resources = this.activity.getResources();
		this.vibrator = this.activity.getSystemService(Vibrator.class);

		if(!this.vibrator.hasVibrator()) {
			this.vibrator = null;
		}

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.context);
		Locale locale = Locale.getDefault();
		TimetableFragment.TimeFormat = new SimpleDateFormat(preferences.getString("timeFormat", "HH:mm.ss"), locale);
		TimetableFragment.DateFormat = new SimpleDateFormat(preferences.getString("dateFormat", "dd EEEE MMMM yyyy"), locale);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle bundle) {
		this.currentSubjectView = view.findViewById(R.id.currentSubjectView);
		this.currentSubjectIdentifierView = view.findViewById(R.id.currentSubjectIdentifierView);
		this.currentSubjectTeacherView = view.findViewById(R.id.currentSubjectTeacherView);
		this.currentSubjectTimezoneView = view.findViewById(R.id.currentSubjectTimezoneView);
		this.timeView = view.findViewById(R.id.timeView);
		this.dateView = view.findViewById(R.id.dateView);
		this.nextSubjectView = view.findViewById(R.id.nextSubjectView);
		this.nextSubjectIdentifierView = view.findViewById(R.id.nextSubjectIdentifierView);
		this.nextSubjectTeacherView = view.findViewById(R.id.nextSubjectTeacherView);
		this.nextSubjectTimezoneView = view.findViewById(R.id.nextSubjectTimezoneView);
		this.previousSubjectView = view.findViewById(R.id.previousSubjectView);
		this.previousSubjectIdentifierView = view.findViewById(R.id.previousSubjectIdentifierView);
		this.previousSubjectTeacherView = view.findViewById(R.id.previousSubjectTeacherView);
		this.previousSubjectTimezoneView = view.findViewById(R.id.previousSubjectTimezoneView);
		String none = this.resources.getString(R.string.noneText);
		this.currentSubjectView.setText(none);
		this.currentSubjectIdentifierView.setText(String.format("%s: %s", this.resources.getString(R.string.identifier), none));
		this.currentSubjectTeacherView.setText(String.format("%s: %s", this.resources.getString(R.string.teacher), none));
		this.currentSubjectTimezoneView.setText(String.format("%s: %s", this.resources.getString(R.string.timezone), none));
		this.nextSubjectView.setText(none);
		this.nextSubjectIdentifierView.setText(String.format("%s: %s", this.resources.getString(R.string.identifier), none));
		this.nextSubjectTeacherView.setText(String.format("%s: %s", this.resources.getString(R.string.teacher), none));
		this.nextSubjectTimezoneView.setText(String.format("%s: %s", this.resources.getString(R.string.timezone), none));
		this.previousSubjectView.setText(none);
		this.previousSubjectIdentifierView.setText(String.format("%s: %s", this.resources.getString(R.string.identifier), none));
		this.previousSubjectTeacherView.setText(String.format("%s: %s", this.resources.getString(R.string.teacher), none));
		this.previousSubjectTimezoneView.setText(String.format("%s: %s", this.resources.getString(R.string.timezone), none));
		this.initializeNotification();
		TickRegistry.attach(this :: update);
	}

	private void initializeNotification() {
		NotificationManager manager = this.activity.getSystemService(NotificationManager.class);
		NotificationChannel channel = new NotificationChannel("subjectNotification", this.resources.getString(R.string.subjectNotification), NotificationManager.IMPORTANCE_HIGH);
		channel.setShowBadge(false);
		channel.enableLights(true);
		channel.enableVibration(true);
		channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
		manager.createNotificationChannel(channel);
	}

	public void notify(String title, String content) {
		NotificationCompat.Builder builder = new NotificationCompat.Builder(this.context, "subjectNotification")
				.setContentTitle(title)
				.setStyle(new NotificationCompat.BigTextStyle().bigText(content))
				.setContentIntent(PendingIntent.getActivity(context, 0, new Intent(context, TimetableApplication.class), PendingIntent.FLAG_IMMUTABLE))
				.setSmallIcon(R.drawable.ic_oui_education)
				.setPriority(NotificationCompat.PRIORITY_MAX);

		NotificationManagerCompat manager = NotificationManagerCompat.from(this.context);
		manager.notify(1523, builder.build());

		if(this.vibrator != null) {
			this.vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
		}
	}

	private void update() {
		try {
			Calendar calendar = Calendar.getInstance();

			if(this.isVisible()) {
				Date date = calendar.getTime();
				String timeText = TimetableFragment.TimeFormat.format(date);
				String dateText = TimetableFragment.DateFormat.format(date);

				this.activity.runOnUiThread(() -> {
					if(TimetableFragment.TimeFormat != null) {
						this.timeView.setText(timeText);
					}

					if(TimetableFragment.DateFormat != null) {
						this.dateView.setText(dateText);
					}
				});
			}

			long time = System.currentTimeMillis();

			if(time - this.lastTime >= 1000) {
				this.lastTime = time;
				int day = calendar.get(Calendar.DAY_OF_WEEK);
				String key = new String[] {"", "sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday"}[day];
				SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.context);
				String subjectData = preferences.getString("subjectList", "");

				if(subjectData != null && !subjectData.isEmpty()) {
					ObjectMapper mapper = new ObjectMapper();
					SubjectDataList list = mapper.readValue(subjectData, SubjectDataList.class);

					if(list.dataList != null) {
						String dayData = preferences.getString(key, "");

						if(dayData != null && !dayData.isEmpty()) {
							SubjectList subjectList = mapper.readValue(dayData, SubjectList.class);
							int hour = calendar.get(Calendar.HOUR_OF_DAY);
							int minute = calendar.get(Calendar.MINUTE);
							List<Subject> currentSubjectList = new ArrayList<>();
							List<Subject> nextSubjectList = new ArrayList<>();
							List<Subject> previousSubjectList = new ArrayList<>();

							for(int i = 0; i < subjectList.subjectList.length; i++) {
								Subject subject = subjectList.subjectList[i];

								if(this.isTimeWithinIntervals(subject.startTimeHour, subject.startTimeMinute, subject.endTimeHour, subject.endTimeMinute, hour, minute)) {
									currentSubjectList.add(subject);

									if(i != subjectList.subjectList.length - 1) {
										nextSubjectList.add(subjectList.subjectList[i + 1]);
									} else {
										nextSubjectList.add(null);
									}

									if(i > 0) {
										previousSubjectList.add(subjectList.subjectList[i - 1]);
									} else {
										previousSubjectList.add(null);
									}
								}
							}

							this.processCurrentSubject(currentSubjectList, list);
							this.processNextPreviousSubject(nextSubjectList, list, true);
							this.processNextPreviousSubject(previousSubjectList, list, false);
						}
					}
				}
			}
		} catch(Throwable ignored) {

		}
	}

	private void processCurrentSubject(List<Subject> subjectList, SubjectDataList list) {
		int size = subjectList.size();
		SeparatedStringBuilder currentSubjectBuilder = new SeparatedStringBuilder();
		SeparatedStringBuilder currentSubjectIdentifierBuilder = new SeparatedStringBuilder();
		SeparatedStringBuilder currentSubjectTeacherBuilder = new SeparatedStringBuilder();
		SeparatedStringBuilder currentSubjectTimezoneBuilder = new SeparatedStringBuilder();
		boolean none = false;

		if(size > 0) {
			for(Subject subjectEntry : subjectList) {
				int index = subjectEntry.subjectDataIndex;
				SubjectData data = list.dataList[index];

				if(data.subjectName != null && !data.subjectName.isEmpty()) {
					currentSubjectBuilder.append(data.subjectName);
				} else {
					currentSubjectBuilder.append(this.resources.getString(R.string.noneText));
				}

				if(data.subjectIdentifier != null && !data.subjectIdentifier.isEmpty()) {
					currentSubjectIdentifierBuilder.append(data.subjectIdentifier);
				} else {
					currentSubjectIdentifierBuilder.append(this.resources.getString(R.string.noneText));
				}

				if(data.teacherList != null && data.teacherList.length != 0) {
					SeparatedStringBuilder teacherBuilder = new SeparatedStringBuilder();

					for(String teacher : data.teacherList) {
						teacherBuilder.append(teacher);
					}

					currentSubjectTeacherBuilder.append(teacherBuilder);
				} else {
					currentSubjectTeacherBuilder.append(this.resources.getString(R.string.noneText));
				}

				currentSubjectTimezoneBuilder.append(this.time(subjectEntry));
			}
		} else {
			none = true;
			this.none(currentSubjectBuilder, currentSubjectIdentifierBuilder, currentSubjectTeacherBuilder, currentSubjectTimezoneBuilder);
		}

		String currentSubjectText = currentSubjectBuilder.toString();
		String currentSubjectIdentifierText = this.resources.getString(R.string.identifier) + ": " + currentSubjectIdentifierBuilder;
		String currentSubjectTeacherText = this.resources.getString(R.string.teacher) + ": " + currentSubjectTeacherBuilder;
		String currentSubjectTimezoneText = this.resources.getString(R.string.timezone) + ": " + currentSubjectTimezoneBuilder;
		boolean notify;
		String content;

		if(!currentSubjectText.equals(TimetableService.LastCurrentSubjectText)) {
			TimetableService.LastCurrentSubjectText = currentSubjectText;
			SeparatedStringBuilder builder = new SeparatedStringBuilder();
			builder.setSeparateText("\n");
			builder.appendRaw(this.resources.getString(R.string.currentSubject));
			builder.appendRaw(": ");
			builder.append(currentSubjectText);
			builder.append(currentSubjectIdentifierText);
			builder.append(currentSubjectTeacherText);
			builder.append(currentSubjectTimezoneText);
			content = builder.toString();
			notify = !none;
		} else {
			content = "";
			notify = false;
		}

		this.activity.runOnUiThread(() -> {
			this.currentSubjectView.setText(currentSubjectText);
			this.currentSubjectIdentifierView.setText(currentSubjectIdentifierText);
			this.currentSubjectTeacherView.setText(currentSubjectTeacherText);
			this.currentSubjectTimezoneView.setText(currentSubjectTimezoneText);

			if(notify) {
				this.notify(currentSubjectText, content);
			}

			if(!currentSubjectText.equals(TimetableWidgetProvider.CurrentSubject) || !currentSubjectIdentifierBuilder.toString().equals(TimetableWidgetProvider.CurrentSubjectIdentifier) || !currentSubjectTeacherBuilder.toString().equals(TimetableWidgetProvider.CurrentSubjectTeacher) || !currentSubjectTimezoneBuilder.toString().equals(TimetableWidgetProvider.CurrentSubjectTimezone)) {
				TimetableWidgetProvider.CurrentSubject = currentSubjectText;
				TimetableWidgetProvider.CurrentSubjectIdentifier = currentSubjectIdentifierBuilder.toString();
				TimetableWidgetProvider.CurrentSubjectTeacher = currentSubjectTeacherBuilder.toString();
				TimetableWidgetProvider.CurrentSubjectTimezone = currentSubjectTimezoneBuilder.toString();
				Application application = this.activity.getApplication();
				int[] identifiers = AppWidgetManager.getInstance(application).getAppWidgetIds(new ComponentName(application, TimetableWidgetProvider.class));
				TimetableWidgetProvider widget = new TimetableWidgetProvider();
				widget.onUpdate(this.activity, AppWidgetManager.getInstance(this.activity), identifiers);
			}
		});
	}

	private void processNextPreviousSubject(List<Subject> subjectList, SubjectDataList list, boolean isNext) {
		int size = subjectList.size();
		SeparatedStringBuilder subjectBuilder = new SeparatedStringBuilder();
		SeparatedStringBuilder subjectIdentifierBuilder = new SeparatedStringBuilder();
		SeparatedStringBuilder subjectTeacherBuilder = new SeparatedStringBuilder();
		SeparatedStringBuilder subjectTimezoneBuilder = new SeparatedStringBuilder();
		subjectIdentifierBuilder.appendRaw(this.resources.getString(R.string.identifier));
		subjectIdentifierBuilder.appendRaw(": ");
		subjectTeacherBuilder.appendRaw(this.resources.getString(R.string.teacher));
		subjectTeacherBuilder.appendRaw(": ");
		subjectTimezoneBuilder.appendRaw(this.resources.getString(R.string.timezone));
		subjectTimezoneBuilder.appendRaw(": ");

		if(size > 0) {
			for(Subject subjectEntry : subjectList) {
				if(subjectEntry != null) {
					int index = subjectEntry.subjectDataIndex;
					SubjectData data = list.dataList[index];

					if(data.subjectName != null && !data.subjectName.isEmpty()) {
						subjectBuilder.append(data.subjectName);
					} else {
						subjectBuilder.append(this.resources.getString(R.string.noneText));
					}

					if(data.subjectIdentifier != null && !data.subjectIdentifier.isEmpty()) {
						subjectIdentifierBuilder.append(data.subjectIdentifier);
					} else {
						subjectIdentifierBuilder.append(this.resources.getString(R.string.noneText));
					}

					if(data.teacherList != null && data.teacherList.length != 0) {
						SeparatedStringBuilder teacherBuilder = new SeparatedStringBuilder();

						for(String teacher : data.teacherList) {
							teacherBuilder.append(teacher);
						}

						subjectTeacherBuilder.append(teacherBuilder);
					} else {
						subjectTeacherBuilder.append(this.resources.getString(R.string.noneText));
					}

					subjectTimezoneBuilder.append(this.time(subjectEntry));
				} else {
					this.none(subjectBuilder, subjectIdentifierBuilder, subjectTeacherBuilder, subjectTimezoneBuilder);
				}
			}
		} else {
			this.none(subjectBuilder, subjectIdentifierBuilder, subjectTeacherBuilder, subjectTimezoneBuilder);
		}

		String subjectText = subjectBuilder.toString();
		String subjectIdentifierText = subjectIdentifierBuilder.toString();
		String subjectTeacherText = subjectTeacherBuilder.toString();
		String subjectTimezoneText = subjectTimezoneBuilder.toString();

		this.activity.runOnUiThread(() -> {
			if(isNext) {
				this.nextSubjectView.setText(subjectText);
				this.nextSubjectIdentifierView.setText(subjectIdentifierText);
				this.nextSubjectTeacherView.setText(subjectTeacherText);
				this.nextSubjectTimezoneView.setText(subjectTimezoneText);
			} else {
				this.previousSubjectView.setText(subjectText);
				this.previousSubjectIdentifierView.setText(subjectIdentifierText);
				this.previousSubjectTeacherView.setText(subjectTeacherText);
				this.previousSubjectTimezoneView.setText(subjectTimezoneText);
			}
		});
	}

	private String time(Subject subjectEntry) {
		return String.format(Locale.getDefault(), "%02d:%02d - %02d:%02d", subjectEntry.startTimeHour, subjectEntry.startTimeMinute, subjectEntry.endTimeHour, subjectEntry.endTimeMinute);
	}

	private void none(SeparatedStringBuilder... builders) {
		if(builders == null) {
			return;
		}

		for(SeparatedStringBuilder builder : builders) {
			if(builder == null) {
				continue;
			}

			builder.append(this.resources.getString(R.string.noneText));
		}
	}

	private boolean isTimeWithinIntervals(int hour1, int minute1, int hour2, int minute2, int checkHour, int checkMinute) {
		int checkMinutes = checkHour * 60 + checkMinute;
		return checkMinutes >= hour1 * 60 + minute1 && checkMinutes <= hour2 * 60 + minute2;
	}

	@Override
	public int getLayoutResourceIdentifier() {
		return R.layout.timetable_fragment;
	}

	@Override
	public int getIconResourceIdentifier() {
		return R.drawable.application_icon;
	}

	@Override
	public String getTitle() {
		return this.resources.getString(R.string.timetable);
	}
}
