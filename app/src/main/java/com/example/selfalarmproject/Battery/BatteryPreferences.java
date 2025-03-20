package com.example.selfalarmproject.Battery;

import android.content.Context;
import android.content.SharedPreferences;

public class BatteryPreferences {
    // Constants
    public static final String PREFS_NAME = "battery_prefs";
    public static final String KEY_POWER_MODE = "power_mode";
    public static final String KEY_AUTO_SCREEN_BRIGHTNESS = "auto_screen_brightness";
    public static final String KEY_AUTO_WIFI_TOGGLE = "auto_wifi_toggle";
    public static final String KEY_BACKGROUND_SYNC = "background_sync";
    public static final String KEY_CRITICAL_BATTERY_LEVEL = "critical_battery_level";

    // Power mode constants
    public static final int POWER_MODE_NORMAL = 0;
    public static final int POWER_MODE_BALANCED = 1;
    public static final int POWER_MODE_SAVER = 2;

    // Default values
    private static final int DEFAULT_POWER_MODE = POWER_MODE_BALANCED;
    private static final boolean DEFAULT_AUTO_SCREEN_BRIGHTNESS = true;
    private static final boolean DEFAULT_AUTO_WIFI_TOGGLE = true;
    private static final boolean DEFAULT_BACKGROUND_SYNC = true;
    private static final float DEFAULT_CRITICAL_BATTERY_LEVEL = 15.0f;

    private final SharedPreferences preferences;

    public BatteryPreferences(Context context) {
        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // Power Mode
    public int getPowerMode() {
        return preferences.getInt(KEY_POWER_MODE, DEFAULT_POWER_MODE);
    }

    public void setPowerMode(int mode) {
        preferences.edit().putInt(KEY_POWER_MODE, mode).apply();
    }

    // Auto Screen Brightness
    public boolean isAutoScreenBrightness() {
        return preferences.getBoolean(KEY_AUTO_SCREEN_BRIGHTNESS, DEFAULT_AUTO_SCREEN_BRIGHTNESS);
    }

    public void setAutoScreenBrightness(boolean enabled) {
        preferences.edit().putBoolean(KEY_AUTO_SCREEN_BRIGHTNESS, enabled).apply();
    }

    // Auto WiFi Toggle
    public boolean isAutoWifiToggle() {
        return preferences.getBoolean(KEY_AUTO_WIFI_TOGGLE, DEFAULT_AUTO_WIFI_TOGGLE);
    }

    public void setAutoWifiToggle(boolean enabled) {
        preferences.edit().putBoolean(KEY_AUTO_WIFI_TOGGLE, enabled).apply();
    }

    // Background Sync
    public boolean isBackgroundSync() {
        return preferences.getBoolean(KEY_BACKGROUND_SYNC, DEFAULT_BACKGROUND_SYNC);
    }

    public void setBackgroundSync(boolean enabled) {
        preferences.edit().putBoolean(KEY_BACKGROUND_SYNC, enabled).apply();
    }

    // Critical Battery Level
    public float getCriticalBatteryLevel() {
        return preferences.getFloat(KEY_CRITICAL_BATTERY_LEVEL, DEFAULT_CRITICAL_BATTERY_LEVEL);
    }

    public void setCriticalBatteryLevel(float level) {
        preferences.edit().putFloat(KEY_CRITICAL_BATTERY_LEVEL, level).apply();
    }
}