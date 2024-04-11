package com.example.spotifywrapper;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class WrappedActivity extends AppCompatActivity {
    int[] pages;
    int currPage;
    APIRequests apiRequest;
    FirebaseManager fire;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        apiRequest = new APIRequests(getApplicationContext(), this);
//        fire = new FirebaseManager();

        currPage = -1;
        pages = new int[]{
                R.layout.wrapped_title,
                R.layout.wrapped_top_song,
                R.layout.wrapped_top_5_songs,
                R.layout.wrapped_top_artist,
                R.layout.wrapped_top_3_artists,
//                R.layout.wrapped_top_genre
        };

        nextPage();
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            Log.e("src",src);
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            Log.e("Bitmap","returned");
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Exception",e.getMessage());
            return null;
        }
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
                title.setText("Spotify Wrapped");
                break;
            case 1:
                TrackContainer a = apiRequest.topTracks.get(0);
                TextView name = findViewById(R.id.song_name);
                name.setText(a.trackName);
                break;
            case 2:
                int[] songViewIDs = new int[] {R.id.song_one, R.id.song_two, R.id.song_three, R.id.song_four, R.id.song_five};

                for (int i = 0; i < songViewIDs.length; i++) {
                    TextView songView = findViewById(songViewIDs[i]);
                    String songName = apiRequest.topTracks.get(i).trackName;
                    if (songName != null) songView.setText(songName);
                }

                break;
            case 3:
                TextView topArtistName = findViewById(R.id.top_artist_name);
                TextView songName = findViewById(R.id.artist_song_name);
                TextView artistTime = findViewById(R.id.artist_listening_time);
                TextView artistMonth = findViewById(R.id.artist_peak_month);

                ArtistContainer topArtist = apiRequest.topArtists.get(0);

                topArtistName.setText(topArtist.artistName);
                break;
            case 4:

                int[] artistViewIDs = new int[] {R.id.artist_name_one, R.id.artist_name_two, R.id.artist_name_three};

                for (int i = 0; i < artistViewIDs.length; i++) {
                    TextView artistNameView = findViewById(artistViewIDs[i]);
                    String artistName = apiRequest.topArtists.get(i).artistName;
                    if (artistName != null) artistNameView.setText(artistName);
                }
                break;
        }
    }
}