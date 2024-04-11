package com.example.spotifywrapper;

public class ArtistContainer {
    private final String artistName;
    private final String artistImageURL;
    ArtistContainer(String artistName, String artistImageURL) {
        this.artistName = artistName;
        this.artistImageURL = artistImageURL;
    }
    public String getArtistName() {
        return artistName;
    }
    public String getArtistImageURL() {
        return artistImageURL;
    }
}
