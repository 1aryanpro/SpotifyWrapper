package com.example.spotifywrapper;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class WrappedActivity extends AppCompatActivity {
    int[] pages;
    int currPage;
    WrappedContainer wrappedContainer;
    FirebaseManager fire;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fire = new FirebaseManager();

        wrappedContainer = new WrappedContainer(getApplicationContext(), this);
        loadWrappedData();

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

    public void loadWrappedData() {
        Intent intent = getIntent();
        int createNew = intent.getIntExtra("createNew", 0);

        if (createNew == 1) {
            wrappedContainer.createFromScratch();
            return;
        }

        String userID = intent.getStringExtra("userID");
        long wrappedID = intent.getLongExtra("wrappedID", 0);

        wrappedContainer.createFromExisting(userID, wrappedID);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        if (event.getActionMasked() != MotionEvent.ACTION_DOWN) return true;

        nextPage();
        return false;
    }

    private void glideLoadImg(ImageView view, String url) {
        Glide.with(this)
                .load(url)
                .apply(new RequestOptions().placeholder(R.drawable.placeholder_avatar)).into(view);
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
                TrackContainer a = wrappedContainer.topTracks.get(0);
                TextView name = findViewById(R.id.song_name);
                name.setText(a.trackName);

                ImageView topTrackImg = findViewById(R.id.top_track_pfp);
                glideLoadImg(topTrackImg, a.albumImageUrl);
                break;
            case 2:
                int[] songViewIDs = new int[] {R.id.song_one, R.id.song_two, R.id.song_three, R.id.song_four, R.id.song_five};
                int[] songViewImgIDs = new int[] {R.id.song_one_covers, R.id.song_two_covers, R.id.song_three_covers, R.id.song_three_covers, R.id.song_four_covers, R.id.song_five_covers};

                for (int i = 0; i < songViewIDs.length; i++) {
                    TextView songView = findViewById(songViewIDs[i]);
                    TrackContainer songName = wrappedContainer.topTracks.get(i);
                    if (songName == null) continue;

                    songView.setText(songName.trackName);
                    glideLoadImg(findViewById(songViewImgIDs[i]), songName.albumImageUrl);
                }

                break;
            case 3:
                TextView topArtistName = findViewById(R.id.top_artist_name);
                ArtistContainer topArtist = wrappedContainer.topArtists.get(0);
                topArtistName.setText(topArtist.artistName);

                glideLoadImg(findViewById(R.id.artist_pfp), topArtist.artistImageURL);
                break;
            case 4:

                int[] artistViewIDs = new int[] {R.id.artist_name_one, R.id.artist_name_two, R.id.artist_name_three};
                int[] artistImgViewIDs = new int[] {R.id.artist_one, R.id.artist_two, R.id.artist_three};

                for (int i = 0; i < artistViewIDs.length; i++) {
                    TextView artistNameView = findViewById(artistViewIDs[i]);
                    ArtistContainer artistName = wrappedContainer.topArtists.get(i);
                    if (artistName == null) continue;

                    artistNameView.setText(artistName.artistName);
                    glideLoadImg(findViewById(artistImgViewIDs[i]), artistName.artistImageURL);
                }
                break;
        }
    }
}