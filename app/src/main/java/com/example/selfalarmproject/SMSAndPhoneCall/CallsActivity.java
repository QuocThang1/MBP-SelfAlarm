package com.example.selfalarmproject.SMSAndPhoneCall;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.selfalarmproject.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class CallsActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 101;
    private CallManager callManager;
    private SMSManager smsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calls);

        // Khởi tạo các manager
        callManager = new CallManager(this);
        smsManager = new SMSManager(this);

        // Khởi động các dịch vụ
        startService(new Intent(this, CallBlockService.class));
        startService(new Intent(this, SMSBlockService.class));

        // Kiểm tra quyền
        checkPermissions();

        // Thiết lập ViewPager2 và TabLayout
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);

        // Thiết lập adapter cho ViewPager2
        // Thêm code cho viewPager.setAdapter() ở đây

        // Thiết lập TabLayoutMediator
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Cuộc gọi");
                    break;
                case 1:
                    tab.setText("Tin nhắn");
                    break;
                case 2:
                    tab.setText("Danh sách đen");
                    break;
            }
        }).attach();

        // Thiết lập FloatingActionButton
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            // Mở dialog để thêm số điện thoại vào danh sách đen
            // Thêm code để hiển thị dialog ở đây
        });
    }

    private void checkPermissions() {
        String[] permissions = {
                Manifest.permission.READ_CALL_LOG,
                Manifest.permission.READ_SMS,
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.CALL_PHONE
        };

        boolean allPermissionsGranted = true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                allPermissionsGranted = false;
                break;
            }
        }

        if (!allPermissionsGranted) {
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
        } else {
            loadData();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }

            if (allGranted) {
                loadData();
            } else {
                Toast.makeText(this, "Ứng dụng cần quyền để quản lý cuộc gọi và tin nhắn", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    private void loadData() {
        // Tải dữ liệu cuộc gọi và tin nhắn
        // Cập nhật UI
    }
}