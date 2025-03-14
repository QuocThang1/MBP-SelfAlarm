package com.example.selfalarmproject.Music;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.selfalarmproject.R;

import java.util.ArrayList;
import java.util.List;

public class SongListActivity extends AppCompatActivity {

    private ListView listViewSongs;
    private List<Song> songs;
    private List<String> displayNames;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.songlist_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        listViewSongs = findViewById(R.id.listViewSongs);

        songs = new ArrayList<>();
        songs.add(new Song("Một bài hát không vui mấy", "Trí", R.raw.motbaihatkhongvuimay, R.drawable.motbaihatkhongvuimay));
        songs.add(new Song("Mất kết nối", "Dương Domic", R.raw.matketnoi, R.drawable.matketnoi));
        songs.add(new Song("Giờ thì", "Bùi Trường Linh", R.raw.giothi, R.drawable.giothi));
        songs.add(new Song("Hư không", "Khánh", R.raw.hukhong, R.drawable.hukhong));
        songs.add(new Song("Wrong Times", "dangrangto ft puppy", R.raw.wrongtime, R.drawable.wrongtimes));

        displayNames = new ArrayList<>();
        for (Song song : songs) {
            displayNames.add(song.getTitle());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                displayNames
        );

        listViewSongs.setAdapter(adapter);

        listViewSongs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Song selectedSong = songs.get(position);

                // Save song to SharedPreferences
                SharedPreferences prefs = getSharedPreferences("MusicPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("selectedSongId", selectedSong.getResourceId());
                editor.putString("selectedSongTitle", selectedSong.getTitle());
                editor.putString("selectedSongArtist", selectedSong.getArtist());
                editor.putInt("selectedBackgroundId", selectedSong.getBackgroundResourceId());
                editor.apply();

                Intent musicIntent = new Intent(SongListActivity.this, MusicService.class);
                musicIntent.putExtra("action", "CHANGE_SONG");
                startService(musicIntent);

                Toast.makeText(SongListActivity.this,
                        "Song: " + selectedSong.getTitle() ,
                        Toast.LENGTH_SHORT).show();

                finish();
            }
        });
    }
}