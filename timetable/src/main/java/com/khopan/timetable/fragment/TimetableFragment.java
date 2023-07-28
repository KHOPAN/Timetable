package com.khopan.timetable.fragment;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.VibrationAttributes;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;
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
	private long lastTime;
	private String lastCurrentSubjectText;
	private Notification notification;

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
		this.activity = this.getActivity();
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
		this.initializeNotification();

		NotificationManagerCompat manager = NotificationManagerCompat.from(this.context);
		manager.notify(1523, this.notification);

		TickRegistry.attach(this :: update);
	}

	private void initializeNotification() {
		//Vibrator vibrator = this.getActivity().getSystemService(Vibrator.class);
		//vibrator.vibrate(VibrationEffect.createWaveform(new long[] {0, 100, 100, 100, 100, 100, 0}, -1));
		String channelName = "subjectNotification";
		NotificationManager manager = this.getActivity().getSystemService(NotificationManager.class);
		NotificationChannel channel = new NotificationChannel(channelName, "Subject Notification", NotificationManager.IMPORTANCE_HIGH);
		channel.setShowBadge(false);
		channel.enableLights(true);
		channel.enableVibration(true);
		channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
		manager.createNotificationChannel(channel);
		NotificationCompat.Builder builder = new NotificationCompat.Builder(this.context, channelName)
				.setContentTitle("Thai Language")
				.setContentText("Current Subject\nThai Language\nIdentifier: T12345")
				.setSmallIcon(R.drawable.ic_oui_education)
				.setPriority(NotificationCompat.PRIORITY_MAX)
				.setOngoing(true);

		this.notification = builder.build();
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
				String key = new String[] {"", "sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturdy"}[day];
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
		currentSubjectIdentifierBuilder.appendRaw("Identifier: ");
		currentSubjectTeacherBuilder.appendRaw("Teacher: ");
		currentSubjectTimezoneBuilder.appendRaw("Timezone: ");

		if(size > 0) {
			for(Subject subjectEntry : subjectList) {
				int index = subjectEntry.subjectDataIndex;
				SubjectData data = list.dataList[index];

				if(data.subjectName != null && !data.subjectName.isEmpty()) {
					currentSubjectBuilder.append(data.subjectName);
				} else {
					currentSubjectBuilder.append("None");
				}

				if(data.subjectIdentifier != null && !data.subjectIdentifier.isEmpty()) {
					currentSubjectIdentifierBuilder.append(data.subjectIdentifier);
				} else {
					currentSubjectIdentifierBuilder.append("None");
				}

				if(data.teacherList != null && data.teacherList.length != 0) {
					SeparatedStringBuilder teacherBuilder = new SeparatedStringBuilder();

					for(String teacher : data.teacherList) {
						teacherBuilder.append(teacher);
					}

					currentSubjectTeacherBuilder.append(teacherBuilder);
				} else {
					currentSubjectTeacherBuilder.append("None");
				}

				currentSubjectTimezoneBuilder.append(this.time(subjectEntry));
			}
		} else {
			this.none(currentSubjectBuilder, currentSubjectIdentifierBuilder, currentSubjectTeacherBuilder, currentSubjectTimezoneBuilder);
		}

		String currentSubjectText = currentSubjectBuilder.toString();
		String currentSubjectIdentifierText = currentSubjectIdentifierBuilder.toString();
		String currentSubjectTeacherText = currentSubjectTeacherBuilder.toString();
		String currentSubjectTimezoneText = currentSubjectTimezoneBuilder.toString();

		if(currentSubjectText.equals(this.lastCurrentSubjectText)) {
			this.lastCurrentSubjectText = currentSubjectText;
		}

		this.activity.runOnUiThread(() -> {
			this.currentSubjectView.setText(currentSubjectText);
			this.currentSubjectIdentifierView.setText(currentSubjectIdentifierText);
			this.currentSubjectTeacherView.setText(currentSubjectTeacherText);
			this.currentSubjectTimezoneView.setText(currentSubjectTimezoneText);
		});
	}

	private void processNextPreviousSubject(List<Subject> subjectList, SubjectDataList list, boolean isNext) {
		int size = subjectList.size();
		SeparatedStringBuilder subjectBuilder = new SeparatedStringBuilder();
		SeparatedStringBuilder subjectIdentifierBuilder = new SeparatedStringBuilder();
		SeparatedStringBuilder subjectTeacherBuilder = new SeparatedStringBuilder();
		SeparatedStringBuilder subjectTimezoneBuilder = new SeparatedStringBuilder();
		subjectIdentifierBuilder.appendRaw("Identifier: ");
		subjectTeacherBuilder.appendRaw("Teacher: ");
		subjectTimezoneBuilder.appendRaw("Timezone: ");

		if(size > 0) {
			for(Subject subjectEntry : subjectList) {
				if(subjectEntry != null) {
					int index = subjectEntry.subjectDataIndex;
					SubjectData data = list.dataList[index];

					if(data.subjectName != null && !data.subjectName.isEmpty()) {
						subjectBuilder.append(data.subjectName);
					} else {
						subjectBuilder.append("None");
					}

					if(data.subjectIdentifier != null && !data.subjectIdentifier.isEmpty()) {
						subjectIdentifierBuilder.append(data.subjectIdentifier);
					} else {
						subjectIdentifierBuilder.append("None");
					}

					if(data.teacherList != null && data.teacherList.length != 0) {
						SeparatedStringBuilder teacherBuilder = new SeparatedStringBuilder();

						for(String teacher : data.teacherList) {
							teacherBuilder.append(teacher);
						}

						subjectTeacherBuilder.append(teacherBuilder);
					} else {
						subjectTeacherBuilder.append("None");
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

			builder.append("None");
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
		return this.resources.getString(R.string.app_name);
	}
}
