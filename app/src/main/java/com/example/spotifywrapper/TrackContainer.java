package com.example.spotifywrapper;

import java.util.List;

public class TrackContainer {

    public final String trackName;
    public final String albumName;
    public final List<String> artists;
    public final String albumImageUrl;
    TrackContainer(String trackName, String albumName, String albumImageUrl, List<String> artists) {
        this.trackName = trackName;
        this.albumName = albumName;
        this.artists = artists;
        this.albumImageUrl = albumImageUrl;
    }
}
