package com.example.selfalarmproject.SMSAndPhoneCall;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.telecom.TelecomManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CallManager {
    private Context context;
    private List<String> blacklistNumbers;

    public CallManager(Context context) {
        this.context = context;
        this.blacklistNumbers = new ArrayList<>();
        // Khi triển khai thực tế, bạn nên tải danh sách đen từ cơ sở dữ liệu
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

    public List<CallEntry> getCallHistory() {
        List<CallEntry> callHistory = new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(
                CallLog.Calls.CONTENT_URI,
                new String[]{
                        CallLog.Calls._ID,
                        CallLog.Calls.NUMBER,
                        CallLog.Calls.DATE,
                        CallLog.Calls.DURATION,
                        CallLog.Calls.TYPE
                },
                null,
                null,
                CallLog.Calls.DEFAULT_SORT_ORDER
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(CallLog.Calls._ID));
                String number = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.NUMBER));
                long date = cursor.getLong(cursor.getColumnIndexOrThrow(CallLog.Calls.DATE));
                int duration = cursor.getInt(cursor.getColumnIndexOrThrow(CallLog.Calls.DURATION));
                int type = cursor.getInt(cursor.getColumnIndexOrThrow(CallLog.Calls.TYPE));

                CallEntry call = new CallEntry(id, number, date, duration, type);
                callHistory.add(call);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return callHistory;
    }

    // Lớp đại diện cho một mục trong nhật ký cuộc gọi
    public static class CallEntry {
        private long id;
        private String phoneNumber;
        private long timestamp;
        private int duration;
        private int type; // 1: Incoming, 2: Outgoing, 3: Missed, etc.

        public CallEntry(long id, String phoneNumber, long timestamp, int duration, int type) {
            this.id = id;
            this.phoneNumber = phoneNumber;
            this.timestamp = timestamp;
            this.duration = duration;
            this.type = type;
        }

        public long getId() {
            return id;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public int getDuration() {
            return duration;
        }

        public int getType() {
            return type;
        }

        public String getCallTypeString() {
            switch (type) {
                case CallLog.Calls.INCOMING_TYPE:
                    return "Cuộc gọi đến";
                case CallLog.Calls.OUTGOING_TYPE:
                    return "Cuộc gọi đi";
                case CallLog.Calls.MISSED_TYPE:
                    return "Cuộc gọi nhỡ";
                case CallLog.Calls.REJECTED_TYPE:
                    return "Cuộc gọi bị từ chối";
                default:
                    return "Không xác định";
            }
        }

        public String getFormattedDate() {
            return new Date(timestamp).toString();
        }
    }
}