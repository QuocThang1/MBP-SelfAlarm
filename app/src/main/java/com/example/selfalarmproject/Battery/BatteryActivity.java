package com.example.selfalarmproject.Battery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.selfalarmproject.R;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.slider.Slider;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class BatteryActivity extends AppCompatActivity {

    private TextView batteryLevelText;
    private TextView batteryStatusText;
    private TextView batteryTemperatureText;
    private TextView batteryVoltageText;
    private TextView criticalBatteryText;
    private CircularProgressIndicator batteryLevelIndicator;
    private RadioGroup powerModeRadioGroup;
    private SwitchMaterial autoScreenBrightnessSwitch;
    private SwitchMaterial wifiToggleSwitch;
    private SwitchMaterial backgroundSyncSwitch;
    private Slider criticalBatterySlider;

    private BatteryReceiver batteryReceiver;
    private BatteryPreferences batteryPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battery);

        // Initialize preferences
        batteryPreferences = new BatteryPreferences(this);

        // Initialize UI components
        initializeViews();
        setupToolbar();
        loadSavedPreferences();
        setupListeners();

        // Register battery receiver
        batteryReceiver = new BatteryReceiver();
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryReceiver, intentFilter);

        // Start battery optimization service
        Intent serviceIntent = new Intent(this, BatteryOptimizationService.class);
        startService(serviceIntent);
    }

    private void initializeViews() {
        batteryLevelText = findViewById(R.id.batteryLevelText);
        batteryStatusText = findViewById(R.id.batteryStatusText);
        batteryTemperatureText = findViewById(R.id.batteryTemperatureText);
        batteryVoltageText = findViewById(R.id.batteryVoltageText);
        batteryLevelIndicator = findViewById(R.id.batteryLevelIndicator);
        powerModeRadioGroup = findViewById(R.id.powerModeRadioGroup);
        autoScreenBrightnessSwitch = findViewById(R.id.autoScreenBrightnessSwitch);
        wifiToggleSwitch = findViewById(R.id.wifiToggleSwitch);
        backgroundSyncSwitch = findViewById(R.id.backgroundSyncSwitch);
        criticalBatterySlider = findViewById(R.id.criticalBatterySlider);
        criticalBatteryText = findViewById(R.id.criticalBatteryText);
    }

    private void setupToolbar() {
        setSupportActionBar(findViewById(R.id.batteryToolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void loadSavedPreferences() {
        // Set power mode
        int powerMode = batteryPreferences.getPowerMode();
        RadioButton radioButton;
        switch (powerMode) {
            case BatteryPreferences.POWER_MODE_NORMAL:
                radioButton = findViewById(R.id.radioNormalMode);
                break;
            case BatteryPreferences.POWER_MODE_BALANCED:
                radioButton = findViewById(R.id.radioBalancedMode);
                break;
            case BatteryPreferences.POWER_MODE_SAVER:
                radioButton = findViewById(R.id.radioPowerSaverMode);
                break;
            default:
                radioButton = findViewById(R.id.radioBalancedMode);
                break;
        }
        radioButton.setChecked(true);

        // Set switches
        autoScreenBrightnessSwitch.setChecked(batteryPreferences.isAutoScreenBrightness());
        wifiToggleSwitch.setChecked(batteryPreferences.isAutoWifiToggle());
        backgroundSyncSwitch.setChecked(batteryPreferences.isBackgroundSync());

        // Set critical battery level
        float criticalLevel = batteryPreferences.getCriticalBatteryLevel();
        criticalBatterySlider.setValue(criticalLevel);
        criticalBatteryText.setText(String.format("%d%%", (int)criticalLevel));
    }

    private void setupListeners() {
        // Slider listener
        criticalBatterySlider.addOnChangeListener((slider, value, fromUser) -> {
            criticalBatteryText.setText(String.format("%d%%", (int)value));
        });

        // Apply button listener
        findViewById(R.id.applySettingsButton).setOnClickListener(v -> {
            savePreferences();
            updateBatteryService();
            Snackbar.make(v, "Settings applied successfully", Snackbar.LENGTH_SHORT).show();
        });
    }

    private void savePreferences() {
        // Save power mode
        int selectedId = powerModeRadioGroup.getCheckedRadioButtonId();
        int powerMode;
        if (selectedId == R.id.radioNormalMode) {
            powerMode = BatteryPreferences.POWER_MODE_NORMAL;
        } else if (selectedId == R.id.radioBalancedMode) {
            powerMode = BatteryPreferences.POWER_MODE_BALANCED;
        } else if (selectedId == R.id.radioPowerSaverMode) {
            powerMode = BatteryPreferences.POWER_MODE_SAVER;
        } else {
            powerMode = BatteryPreferences.POWER_MODE_BALANCED;
        }
        batteryPreferences.setPowerMode(powerMode);

        // Save switch states
        batteryPreferences.setAutoScreenBrightness(autoScreenBrightnessSwitch.isChecked());
        batteryPreferences.setAutoWifiToggle(wifiToggleSwitch.isChecked());
        batteryPreferences.setBackgroundSync(backgroundSyncSwitch.isChecked());

        // Save critical battery level
        batteryPreferences.setCriticalBatteryLevel(criticalBatterySlider.getValue());
    }

    private void updateBatteryService() {
        // Update the service with new settings
        Intent serviceIntent = new Intent(this, BatteryOptimizationService.class);
        serviceIntent.setAction(BatteryOptimizationService.ACTION_UPDATE_SETTINGS);
        startService(serviceIntent);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (batteryReceiver != null) {
            unregisterReceiver(batteryReceiver);
        }
    }

    // Inner class for receiving battery status updates
    private class BatteryReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                int temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
                int voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);

                if (level != -1 && scale != -1) {
                    int batteryPct = (int) ((level / (float) scale) * 100);

                    // Update UI
                    batteryLevelText.setText(batteryPct + "%");
                    batteryLevelIndicator.setProgress(batteryPct);

                    // Status text
                    String statusText = "Status: ";
                    switch (status) {
                        case BatteryManager.BATTERY_STATUS_CHARGING:
                            statusText += "Charging";
                            break;
                        case BatteryManager.BATTERY_STATUS_DISCHARGING:
                            statusText += "Discharging";
                            break;
                        case BatteryManager.BATTERY_STATUS_FULL:
                            statusText += "Full";
                            break;
                        case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                            statusText += "Not Charging";
                            break;
                        default:
                            statusText += "Unknown";
                            break;
                    }
                    batteryStatusText.setText(statusText);

                    // Temperature (convert from tenths of a degree Celsius)
                    float temp = temperature / 10.0f;
                    batteryTemperatureText.setText(String.format("Temperature: %.1f°C", temp));

                    // Voltage (convert from millivolts to volts)
                    float volts = voltage / 1000.0f;
                    batteryVoltageText.setText(String.format("Voltage: %.1fV", volts));
                }
            }
        }
    }
}