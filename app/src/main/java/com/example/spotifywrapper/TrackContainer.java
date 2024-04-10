package com.example.spotifywrapper;

import java.util.List;

public class TrackContainer {

    private final String trackName;
    private final String albumName;
    private final List<String> artists;
    private final int minutesListened;
    TrackContainer(String trackName, String albumName, List<String> artists, int minutesListened) {
        this.trackName = trackName;
        this.albumName = albumName;
        this.artists = artists;
        this.minutesListened = minutesListened;
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
    public int getMinutesListened() {
        return minutesListened;
    }
}
