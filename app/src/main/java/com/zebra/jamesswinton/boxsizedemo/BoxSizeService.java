package com.zebra.jamesswinton.boxsizedemo;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class BoxSizeService extends Service {

    // Debugging
    private static final String TAG = "HUDInterfaceService";

    // Constants
    private static final int BACKGROUND_SERVICE_NOTIFICATION = 1;

    //
    public static final String LAUNCH_SERVICE = "LAUNCH-SERVICE";
    public static final String GET_MEASUREMENT = "GET-MEASUREMENT";
    public static final String GET_VOLUME = "GET-VOLUME";

    // Intent Actions
    private static final String ACTION_GET_VOLUME = "com.zebra.boxsizeinterface.GET_VOLUME";
    private static final String ACTION_GET_MEASUREMENT = "com.zebra.boxsizeinterface.GET_MEASUREMENT";
    private static final String ACTION_STOP_SERVICE = "com.zebra.boxsizeinterface.STOP_SERVICE";
    private static final String ACTION_LAUNCH_HOME_ACTIVITY = "com.zebra.boxsizeinterface.LAUNCH_HOME";

    @Override
    public void onCreate() {
        super.onCreate();
        // Register Screen on/off receiver
        IntentFilter hudActionFilter = new IntentFilter();
        hudActionFilter.addAction(ACTION_GET_MEASUREMENT);
        hudActionFilter.addAction(ACTION_GET_VOLUME);
        hudActionFilter.addAction(ACTION_STOP_SERVICE);
        registerReceiver(measurementReceiver, hudActionFilter);

        // Start Service
        startForeground(BACKGROUND_SERVICE_NOTIFICATION, createServiceNotification());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Service Started");
        if (intent != null && intent.getAction() != null) {
            switch (intent.getAction()) {
                case ACTION_STOP_SERVICE:
                    stopSelf();
                    return START_NOT_STICKY;
                case ACTION_LAUNCH_HOME_ACTIVITY:
                    startMeasurementActivity();
                    break;
                case ACTION_GET_MEASUREMENT:
                    startMeasurementActivity(Measurement.MEASURE_TYPE.SINGLE);
                    break;
                case ACTION_GET_VOLUME:
                    startMeasurementActivity(Measurement.MEASURE_TYPE.VOLUME);
                    break;
            }
        }
        return START_STICKY;
    }

    private Notification createServiceNotification() {
        // Create Variables
        String channelId = "com.zebra.boxsizeinterface";
        String channelName = "Custom Background Notification Channel";

        // Create Channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_NONE);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            // Set Channel
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null) {
                manager.createNotificationChannel(notificationChannel);
            }
        }

        // Build Notification
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,
                channelId);

        // Build Content action
        Intent contentIntent = new Intent(this, BoxSizeService.class);
        contentIntent.setAction(ACTION_LAUNCH_HOME_ACTIVITY);
        PendingIntent contentPendingIntent = PendingIntent.getService(this,
                0, contentIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        // Build Get Measurement action
        Intent measureIntent = new Intent(this, BoxSizeService.class);
        measureIntent.setAction(ACTION_GET_MEASUREMENT);
        PendingIntent measurePendingIntent = PendingIntent.getService(this,
                0, measureIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Action getMeasureAction = new NotificationCompat.Action(
                R.drawable.ic_volume,
                "Get Measurement",
                measurePendingIntent
        );

        // Build Get Volume action
        Intent volumeIntent = new Intent(this, BoxSizeService.class);
        volumeIntent.setAction(ACTION_GET_VOLUME);
        PendingIntent volumePendingIntent = PendingIntent.getService(this,
                0, volumeIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Action getVolumeAction = new NotificationCompat.Action(
                R.drawable.ic_volume,
                "Get Volume",
                volumePendingIntent
        );

        // Build StopService action
        Intent stopIntent = new Intent(this, BoxSizeService.class);
        stopIntent.setAction(ACTION_STOP_SERVICE);
        PendingIntent stopPendingIntent = PendingIntent.getService(this,
                0, stopIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Action stopServiceAction = new NotificationCompat.Action(
                R.drawable.ic_delete,
                "Stop Service",
                stopPendingIntent
        );

        // Return Build Notification object
        return notificationBuilder
                .setContentTitle("Boz Size Interface Active")
                .setContentIntent(contentPendingIntent)
                .setSmallIcon(R.drawable.ic_volume)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setOngoing(true)
                .addAction(getMeasureAction)
                .addAction(getVolumeAction)
                .addAction(stopServiceAction)
                .build();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Un-register Receiver
        unregisterReceiver(measurementReceiver);
    }

    /**
     * Broadcast Receiver
     */

    // Broadcast Receiver for HUD Interface intents
    BroadcastReceiver measurementReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get Action
            String intentAction = intent.getAction();
            Log.i(TAG, "Intent Received: " + intentAction);

            // Handle Actions
            if (intentAction != null) {
                // Handle Data
                switch (intentAction) {
                    case ACTION_GET_MEASUREMENT: {
                        startMeasurementActivity(Measurement.MEASURE_TYPE.SINGLE);
                        break;
                    }
                    case ACTION_GET_VOLUME: {
                        startMeasurementActivity(Measurement.MEASURE_TYPE.VOLUME);
                        break;
                    }
                }
            } else {
                Log.e(TAG, "Intent Action was Null");
            }
        }
    };

    private void startMeasurementActivity() {
        Intent measureIntent = new Intent(BoxSizeService.this, MeasureActivity.class);
        measureIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(measureIntent);
    }

    private void startMeasurementActivity(Measurement.MEASURE_TYPE measureType) {
        Intent measureIntent = new Intent(BoxSizeService.this, MeasureActivity.class);
        measureIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        switch (measureType) {
            case SINGLE:
                measureIntent.putExtra(GET_MEASUREMENT, "");
                startActivity(measureIntent);
                break;
            case VOLUME:
                measureIntent.putExtra(GET_VOLUME, "");
                startActivity(measureIntent);
                break;
        }
    }
}
