package com.example.selfalarmproject.SMSAndPhoneCall;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyCallback;
import android.telephony.TelephonyManager;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
public class CallReceiver {
    private final Context context;
    private TelephonyManager telephonyManager;
    private CallStateCallback callStateCallback;

    public CallReceiver(Context context) {
        this.context = context;
        this.telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

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
        LegacyCallStateListener listener = new LegacyCallStateListener();
        telephonyManager.listen(listener, android.telephony.PhoneStateListener.LISTEN_CALL_STATE);
    }

    private void registerCallStateCallback() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            callStateCallback = new CallStateCallback();
            try {
                telephonyManager.registerTelephonyCallback(
                        context.getMainExecutor(),
                        callStateCallback
                );
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    public void unregister() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (callStateCallback != null) {
                telephonyManager.unregisterTelephonyCallback(callStateCallback);
            }
        } else {
            telephonyManager.listen(null, android.telephony.PhoneStateListener.LISTEN_NONE);
        }
    }

    @SuppressWarnings("deprecation")
    private class LegacyCallStateListener extends android.telephony.PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            handleCallState(state, incomingNumber);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    private class CallStateCallback extends TelephonyCallback implements TelephonyCallback.CallStateListener {
        private String lastPhoneNumber = "";

        @Override
        public void onCallStateChanged(int state) {
            handleCallState(state, lastPhoneNumber);
        }
    }

    private void handleCallState(int state, String incomingNumber) {
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                Toast.makeText(context, "Cuộc gọi đến từ: " + incomingNumber, Toast.LENGTH_SHORT).show();
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                Toast.makeText(context, "Cuộc gọi đang diễn ra", Toast.LENGTH_SHORT).show();
                break;
            case TelephonyManager.CALL_STATE_IDLE:
                Toast.makeText(context, "Cuộc gọi kết thúc", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}