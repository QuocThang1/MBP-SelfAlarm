package com.example.selfalarmproject;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.selfalarmproject.Music.MusicActivity;
import com.google.android.material.card.MaterialCardView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        MaterialCardView cardMusic = findViewById(R.id.card_music);
        MaterialCardView cardCall = findViewById(R.id.card_calls_sms);
        MaterialCardView cardSchedule = findViewById(R.id.card_schedule);
        MaterialCardView cardBattery = findViewById(R.id.card_battery);

        cardMusic.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, MusicActivity.class)));
        cardCall.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, CallsActivity.class)));
        cardSchedule.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ScheduleActivity.class)));
        cardBattery.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, BatteryActivity.class)));

    }
}