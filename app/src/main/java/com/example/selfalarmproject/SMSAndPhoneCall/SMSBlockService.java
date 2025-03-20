package com.example.selfalarmproject.SMSAndPhoneCall;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SMSBlockService extends Service {
    private List<String> blacklist;
    private SMSManager smsManager;
    private BroadcastReceiver smsReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        smsManager = new SMSManager(this);
        blacklist = new ArrayList<>();

        // Trong thực tế: blacklist = smsManager.getBlacklist();
        // Hoặc tải từ cơ sở dữ liệu
        blacklist.add("+84901234567");
        blacklist.add("+84987654321");

        registerSMSReceiver();
    }

    private void registerSMSReceiver() {
        smsReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if ("android.provider.Telephony.SMS_RECEIVED".equals(intent.getAction())) {
                    Object[] pdus = (Object[]) intent.getExtras().get("pdus");
                    String format = intent.getExtras().getString("format");

                    for (Object pdu : pdus) {
                        SmsMessage message = SmsMessage.createFromPdu((byte[]) pdu, format);
                        String sender = message.getOriginatingAddress();

                        if (blacklist.contains(sender)) {
                            // Xóa tin nhắn không mong muốn
                            Log.i("SMSBlockService", "Blocking SMS from: " + sender);
                            abortBroadcast(); // Ngăn tin nhắn được gửi đến các ứng dụng khác
                            Toast.makeText(context, "Đã chặn tin nhắn từ: " + sender, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        };

        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        filter.setPriority(999); // Ưu tiên cao nhất
        registerReceiver(smsReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (smsReceiver != null) {
            unregisterReceiver(smsReceiver);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}