package com.example.spotifywrapper;

public class AlbumContainer {
    private final String albumName;
    private final String albumImageURL;
    AlbumContainer(String albumName, String albumImageURL) {
        this.albumName = albumName;
        this.albumImageURL = albumImageURL;
    }
    public final String getAlbumName() {
        return albumName;
    }
    public final String getAlbumImageURL() {
        return albumImageURL;
    }
}
