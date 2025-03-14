package com.example.selfalarmproject.Music;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.selfalarmproject.R;

public class MusicActivity extends AppCompatActivity {
    ImageButton btnplay, btnnext, btnprevious,  btnSelectSong, btnrepeat;
    TextView tvSongTitle, tvArtist;
    Boolean flag=true;
    ImageView songImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.music_main);
        btnplay =findViewById(R.id.buttonPlay);
        btnnext = findViewById(R.id.buttonNext);
        btnprevious = findViewById(R.id.buttonPrevious);
        btnSelectSong = findViewById(R.id.buttonSelectSong);
        btnrepeat = findViewById(R.id.buttonRepeat);
        tvSongTitle = findViewById(R.id.songTitle);
        tvArtist = findViewById(R.id.artistName);
        songImageView = findViewById(R.id.imageView);

        SongInfoReceiver.setTextViews(tvSongTitle, tvArtist);
        SongInfoReceiver.setImageView(songImageView);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(MusicActivity.this, MusicService.class );
                intent1.putExtra("action", "PLAY_PAUSE");
                startService(intent1);
                if (flag == true)
                {
                    btnplay.setImageResource(R.drawable.pause);
                    flag = false;
                }
                else{
                    btnplay.setImageResource(R.drawable.play);
                    flag = true;
                }
            }
        });

        btnnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MusicActivity.this, MusicService.class);
                intent.putExtra("action", "NEXT");
                startService(intent);
                if (flag == false)
                {
                    btnplay.setImageResource(R.drawable.play);
                    flag = true;
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        updateSongInfoFromPreferences();
                    }
                }, 10);
            }
        });

        btnprevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MusicActivity.this, MusicService.class);
                intent.putExtra("action", "PREVIOUS");
                startService(intent);
                if (flag == false)
                {
                    btnplay.setImageResource(R.drawable.play);
                    flag = true;
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        updateSongInfoFromPreferences();
                    }
                }, 10);
            }
        });

        btnrepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MusicActivity.this, MusicService.class);
                intent.putExtra("action", "REPEAT");
                startService(intent);
                if (flag == true)
                {
                    btnplay.setImageResource(R.drawable.pause);
                    flag = false;
                }
                else{
                    btnplay.setImageResource(R.drawable.play);
                    flag = true;
                }
            }
        });

        btnSelectSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MusicActivity.this, SongListActivity.class);
                startActivity(intent);
                if (flag == false)
                {
                    btnplay.setImageResource(R.drawable.play);
                    flag = true;
                }
                updateSongInfoFromPreferences();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateSongInfoFromPreferences();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void updateSongInfoFromPreferences() {
        android.content.SharedPreferences prefs = getSharedPreferences("MusicPrefs", MODE_PRIVATE);
        String title = prefs.getString("selectedSongTitle", "Chưa chọn bài hát");
        String artist = prefs.getString("selectedSongArtist", "Chưa có nghệ sĩ");
        int backgroundId = prefs.getInt("selectedBackgroundId", R.drawable.motbaihatkhongvuimay);

        tvSongTitle.setText(title);
        tvArtist.setText(artist);
        songImageView.setImageResource(backgroundId);
    }
}