package com.example.spotifywrapper;

import java.util.List;

public class TrackContainer {

    private final String trackName;
    private final String albumName;
    private final List<String> artists;
    TrackContainer(String trackName, String albumName, List<String> artists) {
        this.trackName = trackName;
        this.albumName = albumName;
        this.artists = artists;
    }
    public String getTrackName() {
        return trackName;
    }
    public String getAlbumName() {
        return albumName;
    }
    public List<String> getArtists() {
        return artists;
    }
}
