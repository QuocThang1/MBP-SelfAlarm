package com.example.selfalarmproject.Battery;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

public class BatteryOptimizationService extends Service {
    private static final String TAG = "BatteryOptimizationService";
    public static final String ACTION_UPDATE_SETTINGS = "com.example.selfalarmproject.Battery.ACTION_UPDATE_SETTINGS";


    // Action constants
    public static final String ACTION_BATTERY_UPDATE = "ACTION_BATTERY_UPDATE";
    public static final String ACTION_POWER_CONNECTED = "ACTION_POWER_CONNECTED";
    public static final String ACTION_POWER_DISCONNECTED = "ACTION_POWER_DISCONNECTED";
    public static final String ACTION_SCREEN_ON = "ACTION_SCREEN_ON";
    public static final String ACTION_SCREEN_OFF = "ACTION_SCREEN_OFF";

    private final IBinder binder = new LocalBinder();
    private BatteryPreferences batteryPreferences;

    public class LocalBinder extends Binder {
        public BatteryOptimizationService getService() {
            return BatteryOptimizationService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        batteryPreferences = new BatteryPreferences(this);
        Log.d(TAG, "BatteryOptimizationService started");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && ACTION_UPDATE_SETTINGS.equals(intent.getAction())) {
            // Gọi phương thức cập nhật cài đặt ở đây
            updateSettings();
        }
        return START_STICKY;
    }

    private void updateSettings() {
        // Cập nhật các thiết lập tối ưu pin từ BatteryPreferences
        BatteryPreferences batteryPreferences = new BatteryPreferences(this);

        boolean autoBrightness = batteryPreferences.isAutoScreenBrightness();
        boolean autoWifi = batteryPreferences.isAutoWifiToggle();
        boolean backgroundSync = batteryPreferences.isBackgroundSync();
        float criticalLevel = batteryPreferences.getCriticalBatteryLevel();

        // Xử lý logic tương ứng với các thiết lập
        applyPowerSettings(autoBrightness, autoWifi, backgroundSync, criticalLevel);
    }

    private void applyPowerSettings(boolean autoBrightness, boolean autoWifi, boolean backgroundSync, float criticalLevel) {
        // Viết logic điều chỉnh hệ thống tại đây
    }

    private void handleAction(Intent intent) {
        String action = intent.getAction();

        if (ACTION_BATTERY_UPDATE.equals(action)) {
            float batteryLevel = intent.getFloatExtra("level", -1);
            boolean isCharging = intent.getBooleanExtra("isCharging", false);
            handleBatteryUpdate(batteryLevel, isCharging);
        } else if (ACTION_POWER_CONNECTED.equals(action)) {
            handlePowerConnected();
        } else if (ACTION_POWER_DISCONNECTED.equals(action)) {
            handlePowerDisconnected();
        } else if (ACTION_SCREEN_ON.equals(action)) {
            handleScreenOn();
        } else if (ACTION_SCREEN_OFF.equals(action)) {
            handleScreenOff();
        }
    }

    private void handleBatteryUpdate(float batteryLevel, boolean isCharging) {
        Log.d(TAG, "Handling battery update. Level: " + batteryLevel + "%, Charging: " + isCharging);

        if (!isCharging && batteryLevel <= batteryPreferences.getCriticalBatteryLevel()) {
            Log.d(TAG, "Critical battery level reached! Activating power-saving mode.");
            applyPowerSavingSettings();
        } else if (isCharging) {
            Log.d(TAG, "Device is charging. Restoring normal settings.");
            restoreNormalSettings();
        }
    }

    private void handlePowerConnected() {
        Log.d(TAG, "Power connected: Restoring normal settings");
        restoreNormalSettings();
    }

    private void handlePowerDisconnected() {
        Log.d(TAG, "Power disconnected: Checking battery level");
        float batteryLevel = batteryPreferences.getCriticalBatteryLevel();
        if (batteryLevel <= batteryPreferences.getCriticalBatteryLevel()) {
            applyPowerSavingSettings();
        }
    }

    private void handleScreenOn() {
        Log.d(TAG, "Screen turned ON: Restoring brightness settings");
        restoreBrightnessSettings();
    }

    private void handleScreenOff() {
        Log.d(TAG, "Screen turned OFF: Lowering brightness to save power");
        applyLowBrightness();
    }

    private void applyPowerSavingSettings() {
        Log.d(TAG, "Applying power-saving settings...");
        if (batteryPreferences.isAutoWifiToggle()) {
            toggleWifi(false);
        }
        if (batteryPreferences.isBackgroundSync()) {
            toggleSync(false);
        }
        applyLowBrightness();
    }

    private void restoreNormalSettings() {
        Log.d(TAG, "Restoring normal settings...");
        if (batteryPreferences.isAutoWifiToggle()) {
            toggleWifi(true);
        }
        if (batteryPreferences.isBackgroundSync()) {
            toggleSync(true);
        }
        restoreBrightnessSettings();
    }

    private void toggleWifi(boolean enabled) {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            wifiManager.setWifiEnabled(enabled);
            Log.d(TAG, "WiFi " + (enabled ? "enabled" : "disabled"));
        }
    }

    private void toggleSync(boolean enabled) {
        Log.d(TAG, "Background sync " + (enabled ? "enabled" : "disabled"));
        // Android 10 trở lên không cho phép bật/tắt sync từ code, chỉ có thể hướng dẫn user
    }

    private void applyLowBrightness() {
        try {
            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 30);
            Log.d(TAG, "Screen brightness set to low");
        } catch (Exception e) {
            Log.e(TAG, "Failed to lower brightness", e);
        }
    }

    private void restoreBrightnessSettings() {
        if (batteryPreferences.isAutoScreenBrightness()) {
            try {
                Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 150);
                Log.d(TAG, "Screen brightness restored");
            } catch (Exception e) {
                Log.e(TAG, "Failed to restore brightness", e);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "BatteryOptimizationService destroyed");
    }
}
