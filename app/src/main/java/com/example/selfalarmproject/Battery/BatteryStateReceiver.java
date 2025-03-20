package com.example.selfalarmproject.Battery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.util.Log;

public class BatteryStateReceiver extends BroadcastReceiver {
    private static final String TAG = "BatteryStateReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
            // Get battery information
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
            int health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);
            int temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);

            // Calculate battery percentage
            float batteryPct = -1;
            if (level != -1 && scale != -1) {
                batteryPct = (level / (float) scale) * 100;
            }

            // Get charging state
            boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL;

            // Log battery information
            Log.d(TAG, "Battery level: " + batteryPct + "%");
            Log.d(TAG, "Charging: " + isCharging);
            Log.d(TAG, "Temperature: " + (temperature / 10.0f) + "°C");

            // Pass the information to the service
            Intent serviceIntent = new Intent(context, BatteryOptimizationService.class);
            serviceIntent.setAction(BatteryOptimizationService.ACTION_BATTERY_UPDATE);
            serviceIntent.putExtra("level", batteryPct);
            serviceIntent.putExtra("isCharging", isCharging);
            serviceIntent.putExtra("temperature", temperature / 10.0f);
            context.startService(serviceIntent);

        } else if (Intent.ACTION_POWER_CONNECTED.equals(action)) {
            Log.d(TAG, "Power connected");
            // Handle power connected event
            Intent serviceIntent = new Intent(context, BatteryOptimizationService.class);
            serviceIntent.setAction(BatteryOptimizationService.ACTION_POWER_CONNECTED);
            context.startService(serviceIntent);

        } else if (Intent.ACTION_POWER_DISCONNECTED.equals(action)) {
            Log.d(TAG, "Power disconnected");
            // Handle power disconnected event
            Intent serviceIntent = new Intent(context, BatteryOptimizationService.class);
            serviceIntent.setAction(BatteryOptimizationService.ACTION_POWER_DISCONNECTED);
            context.startService(serviceIntent);

        } else if (Intent.ACTION_SCREEN_ON.equals(action)) {
            Log.d(TAG, "Screen turned on");
            // Handle screen on event
            Intent serviceIntent = new Intent(context, BatteryOptimizationService.class);
            serviceIntent.setAction(BatteryOptimizationService.ACTION_SCREEN_ON);
            context.startService(serviceIntent);

        } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
            Log.d(TAG, "Screen turned off");
            // Handle screen off event
            Intent serviceIntent = new Intent(context, BatteryOptimizationService.class);
            serviceIntent.setAction(BatteryOptimizationService.ACTION_SCREEN_OFF);
            context.startService(serviceIntent);
        }
    }
}