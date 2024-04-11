package com.example.spotifywrapper;
import java.util.List;

public class GenreContainer {
    private final String genres;
    GenreContainer(String genres) {
        this.genres = genres;
    }
    public String getGenres() {
        return genres;
    }
}
