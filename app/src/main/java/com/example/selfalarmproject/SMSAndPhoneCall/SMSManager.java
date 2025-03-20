package com.example.selfalarmproject.SMSAndPhoneCall;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class SMSManager {
    private Context context;
    private List<String> blacklistNumbers;

    public SMSManager(Context context) {
        this.context = context;
        this.blacklistNumbers = new ArrayList<>();
        // Khi triển khai thực tế, bạn nên tải danh sách đen từ cơ sở dữ liệu
        // Ví dụ: loadBlacklistFromDatabase();
    }

    public void addToBlacklist(String phoneNumber) {
        if (!blacklistNumbers.contains(phoneNumber)) {
            blacklistNumbers.add(phoneNumber);
            // Khi triển khai thực tế: saveBlacklistToDatabase();
        }
    }

    public void removeFromBlacklist(String phoneNumber) {
        blacklistNumbers.remove(phoneNumber);
        // Khi triển khai thực tế: saveBlacklistToDatabase();
    }

    public boolean isBlacklisted(String phoneNumber) {
        return blacklistNumbers.contains(phoneNumber);
    }

    public List<String> getBlacklist() {
        return blacklistNumbers;
    }

    public List<SMSMessage> getAllSMS() {
        List<SMSMessage> messages = new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(
                Telephony.Sms.CONTENT_URI,
                new String[]{
                        Telephony.Sms._ID,
                        Telephony.Sms.ADDRESS,
                        Telephony.Sms.BODY,
                        Telephony.Sms.DATE,
                        Telephony.Sms.TYPE
                },
                null,
                null,
                Telephony.Sms.DEFAULT_SORT_ORDER
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(Telephony.Sms._ID));
                String address = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS));
                String body = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.BODY));
                long date = cursor.getLong(cursor.getColumnIndexOrThrow(Telephony.Sms.DATE));
                int type = cursor.getInt(cursor.getColumnIndexOrThrow(Telephony.Sms.TYPE));

                SMSMessage message = new SMSMessage(id, address, body, date, type);
                messages.add(message);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return messages;
    }

    public void deleteSMS(long messageId) {
        try {
            Uri uri = Uri.parse("content://sms/" + messageId);
            context.getContentResolver().delete(uri, null, null);
        } catch (Exception e) {
            Log.e("SMSManager", "Error deleting SMS: " + e.getMessage());
        }
    }

    // Lớp đại diện cho một tin nhắn SMS
    public static class SMSMessage {
        private long id;
        private String sender;
        private String content;
        private long timestamp;
        private int type; // 1: Inbox, 2: Sent, etc.

        public SMSMessage(long id, String sender, String content, long timestamp, int type) {
            this.id = id;
            this.sender = sender;
            this.content = content;
            this.timestamp = timestamp;
            this.type = type;
        }

        public long getId() {
            return id;
        }

        public String getSender() {
            return sender;
        }

        public String getContent() {
            return content;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public int getType() {
            return type;
        }
    }
}