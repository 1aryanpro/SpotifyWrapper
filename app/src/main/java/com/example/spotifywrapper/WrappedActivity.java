package com.example.spotifywrapper;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.spotifywrapper.databinding.ActivityWrappedBinding;

public class WrappedActivity extends AppCompatActivity {
    int[] pages;
    int currPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currPage = -1;
        pages = new int[]{
                R.layout.wrapped_first_page,
                R.layout.wrapped_second_page,
                R.layout.wrapped_third_page,
                R.layout.wrapped_fourth_page,
                R.layout.wrapped_fifth_page,
                R.layout.wrapped_sixth_page
        };

        nextPage();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        if (event.getActionMasked() != MotionEvent.ACTION_DOWN) return true;

        nextPage();
        return false;
    }

    private void nextPage() {
        currPage++;
        if (currPage >= pages.length) {
            startActivity(new Intent(this, MainActivity.class));
            return;
        }

        setContentView(pages[currPage]);
        switch (currPage) {
            case 0:
                TextView title = findViewById(R.id.textView2);
                title.setText("2024 Spotify Wrapped");
                break;
            case 1:
                TextView name = findViewById(R.id.song_name);
                TextView minutes = findViewById(R.id.total_minutes);
                TextView month = findViewById(R.id.peak_listening_month);
                name.setText("Baby Shark");
                minutes.setText("5,000 minutes");
                month.setText("November");
                break;
            case 2:
                TextView song1 = findViewById(R.id.song_one);
                TextView song2 = findViewById(R.id.song_two);
                TextView song3 = findViewById(R.id.song_three);
                TextView song4 = findViewById(R.id.song_four);
                TextView song5 = findViewById(R.id.song_five);

                song1.setText("Baby Shark");
                song2.setText("Believer");
                song3.setText("Enemy");
                song4.setText("Radioactive");
                song5.setText("Immortals");
                break;
            case 3:
                TextView artistName = findViewById(R.id.top_artist_name);
                TextView songName = findViewById(R.id.artist_song_name);
                TextView artistTime = findViewById(R.id.artist_listening_time);
                TextView artistMonth = findViewById(R.id.artist_peak_month);

                artistName.setText("Imagine Dragons");
                songName.setText("Believer");
                artistTime.setText("2,000 Minutes");
                artistMonth.setText("June");
                break;
            case 4:
                TextView artist1 = findViewById(R.id.artist_name_one);
                TextView artist2 = findViewById(R.id.artist_name_two);
                TextView artist3 = findViewById(R.id.artist_name_three);

                artist1.setText("Imagine Dragons");
                artist2.setText("Panic At the Disco");
                artist3.setText("Taylor Swift");
        }
    }
}