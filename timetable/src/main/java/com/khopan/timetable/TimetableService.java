package com.khopan.timetable;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.sec.sesl.khopan.timetable.R;

import java.util.Timer;
import java.util.TimerTask;

public class TimetableService extends Service {
	public static String LastCurrentSubjectText;

	private Timer timer;

	@Override
	public void onCreate() {
		String channelName = "timetableBackgroundService";
		Resources resources = this.getResources();
		Context context = this.getBaseContext();
		NotificationChannel channel = new NotificationChannel(channelName, resources.getString(R.string.serviceNotification), NotificationManager.IMPORTANCE_LOW);
		channel.setShowBadge(false);
		channel.enableLights(false);
		channel.enableVibration(false);
		this.getSystemService(NotificationManager.class).createNotificationChannel(channel);
		this.startForeground(192331, new NotificationCompat.Builder(this.getApplicationContext(), channelName)
				.setContentTitle(resources.getString(R.string.app_name))
				.setContentText(resources.getString(R.string.runningInTheBackground))
				.setContentIntent(PendingIntent.getActivity(context, 0, new Intent(context, TimetableApplication.class), PendingIntent.FLAG_IMMUTABLE))
				.setSmallIcon(R.drawable.application_icon)
				.setPriority(NotificationCompat.PRIORITY_LOW)
				.setOngoing(true)
				.build());
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		this.timer = new Timer();
		this.timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				TickRegistry.tick();
			}
		}, 0, 10);

		return Service.START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if(this.timer != null) {
			this.timer.cancel();
		}
	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
