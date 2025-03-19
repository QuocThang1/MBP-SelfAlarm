package com.example.selfalarmproject.SMSAndPhoneCall;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class SMSReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.provider.Telephony.SMS_RECEIVED".equals(intent.getAction())) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");
                String format = bundle.getString("format"); // Lấy định dạng PDU

                if (pdus != null) {
                    for (Object pdu : pdus) {
                        SmsMessage message = SmsMessage.createFromPdu((byte[]) pdu, format);
                        String sender = message.getOriginatingAddress();
                        String content = message.getMessageBody();
                        Toast.makeText(context, "Tin nhắn từ: " + sender + "\n" + content, Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }
}
