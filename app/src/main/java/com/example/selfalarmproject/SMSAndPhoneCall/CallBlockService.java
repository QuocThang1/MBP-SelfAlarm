package com.example.selfalarmproject.SMSAndPhoneCall;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyCallback;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.util.Arrays;
import java.util.List;

public class CallBlockService extends Service {
    private final List<String> blacklist = Arrays.asList("+84901234567", "+84987654321"); // Đã thêm final
    private TelephonyManager telephonyManager;

    @Override
    public void onCreate() {
        super.onCreate();
        telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Sử dụng TelephonyCallback cho Android 12 trở lên
            registerCallStateCallback();
        } else {
            // Sử dụng PhoneStateListener cho Android 11 trở xuống
            registerPhoneStateListener();
        }
    }

    @SuppressWarnings("deprecation")
    private void registerPhoneStateListener() {
        telephonyManager.listen(new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                handleIncomingCall(state, incomingNumber);
            }
        }, PhoneStateListener.LISTEN_CALL_STATE);
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    private void registerCallStateCallback() {
        CallStateCallback callStateCallback = new CallStateCallback();
        try {
            telephonyManager.registerTelephonyCallback(
                    getMainExecutor(),
                    callStateCallback
            );
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    private class CallStateCallback extends TelephonyCallback implements TelephonyCallback.CallStateListener {
        @Override
        public void onCallStateChanged(int state) {
            // Lưu ý: TelephonyCallback.CallStateListener không cung cấp số điện thoại
            // Do đó, chúng ta cần sử dụng CallScreeningService để lọc cuộc gọi từ Android 12+
            handleIncomingCall(state, null);
        }
    }

    private void handleIncomingCall(int state, String incomingNumber) {
        if (state == TelephonyManager.CALL_STATE_RINGING && incomingNumber != null && blacklist.contains(incomingNumber)) {
            Toast.makeText(getApplicationContext(), "Chặn số: " + incomingNumber, Toast.LENGTH_SHORT).show();
            // Không thể tự động ngắt cuộc gọi trên Android 9+, nhưng có thể tắt chuông hoặc chuyển hướng.
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            telephonyManager.listen(null, 0);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}