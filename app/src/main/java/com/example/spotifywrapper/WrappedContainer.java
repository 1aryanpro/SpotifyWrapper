package com.example.spotifywrapper;

import android.app.Activity;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class WrappedContainer {

    public List<ArtistContainer> topArtists = new ArrayList<>();
    public List<TrackContainer> topTracks = new ArrayList<>();
    private List<AlbumContainer> topAlbumsFromTracks = new ArrayList<>();
    public GenreContainer topGenre;
    private List<String> artistsEndpointForGenre = new ArrayList<>();
    private List<String> listOfGenres = new ArrayList<>();
    private final SpotifyAuthManager sm;
    private FirebaseManager fire;
    private String wrappedPath;

    WrappedContainer(Context appContext, Activity activity) {
        sm = new SpotifyAuthManager(appContext, activity);
        fire = new FirebaseManager();
    }

    public void createFromScratch() {
        wrappedPath = sm.userEmail + "/wraps/" + Instant.now().getEpochSecond();

        topTracksAndAlbumsResponse();
        topArtistsResponse();
        topGenresResponse();
    }

    public void createFromExisting(String userID, int wrappedID) {

    }

    public void topArtistsResponse() {
        sm.callAPI("/v1/me/top/artists", data -> {
            try {
                JSONArray items = data.getJSONArray("items");
                for (int i = 0; i < 3; i++) {
                    JSONObject artist = items.getJSONObject(i);
                    String artistName = artist.getString("name");
                    JSONArray imagesArray = artist.getJSONArray("images");
                    String artistImageURL = null;
                    if (imagesArray.length() > 0) {
                        JSONObject image = imagesArray.getJSONObject(0);
                        artistImageURL = image.getString("url");
                    }
                    topArtists.add(new ArtistContainer(artistName, artistImageURL));
                }
                fire.setRef(wrappedPath, this);

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void topTracksAndAlbumsResponse() {
        sm.callAPI("/v1/me/top/tracks", data -> {
            try {
                JSONArray items = data.getJSONArray("items");
                for (int i = 0; i < 5; i++) { //to get top 5
                    JSONObject track = items.getJSONObject(i);
                    String trackName = track.getString("name"); //name of track

                    JSONObject album = track.getJSONObject("album");
                    String albumName = album.getString("name");

                    JSONArray albumImagesArray = album.getJSONArray("images");
                    String imageAlbumURL = null;

                    if (albumImagesArray.length() > 0) {
                        JSONObject image = albumImagesArray.getJSONObject(0);
                        imageAlbumURL = image.getString("url");
                    }

                    JSONArray artistList = track.getJSONArray("artists"); //array of artist(s) --> USED for GENRES method too !!!!!!
                    List<String> artistOfTrack = new ArrayList<>(); //artist(s) of track

                    for (int j = 0; j < artistList.length(); j++) {
                        JSONObject artist = artistList.getJSONObject(j);
                        String artistName = artist.getString("name");
                        artistOfTrack.add(artistName); //add artist(s) of track to a list

                        String artistID = artist.getString("id"); //USED AS PART OF ENDPOINT

                        artistsEndpointForGenre.add(artistID); //used as PART of endpoint
                    }
                    topTracks.add(new TrackContainer(trackName, albumName, imageAlbumURL, artistOfTrack));
                    topAlbumsFromTracks.add(new AlbumContainer(albumName, imageAlbumURL));
                    fire.setRef(wrappedPath, this);
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void topGenresResponse() {
        for (int i = 0; i < artistsEndpointForGenre.size(); i++) {
            sm.callAPI("/v1/artists/" + artistsEndpointForGenre.get(i), data -> {
                try {
                    JSONArray genresList = data.getJSONArray("genres");
                    Map<String, Integer> countMap = new HashMap<>();

                    Integer max = 0;
                    String toAdd = "";

                    for (int genreIndex = 0; genreIndex < genresList.length(); genreIndex++) {
                        String genre = (String) genresList.get(genreIndex);
                        if (!countMap.containsKey(genre)) {
                            countMap.put(genre, 1);
                        } else {
                            Integer oldCount = countMap.get(genre);
                            if (oldCount != null) countMap.replace(genre, oldCount, oldCount + 1);
                        }
                        if (countMap.get(genre) > max) {
                            max = countMap.get(genre);
                            toAdd = genre;
                        }
                    }

                    topGenre = new GenreContainer(toAdd);

                    fire.setRef(wrappedPath, this);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}