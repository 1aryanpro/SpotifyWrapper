package com.example.spotifywrapper;

//import android.os.AsyncTask;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class APIRequests {

    public List<ArtistContainer> topArtists = new ArrayList<>(); //add ArtistContainer objects (Name, Image URL)
    public List<TrackContainer> topTracks = new ArrayList<>(); //add TrackContainer objects (Name, Album, Artist)
    public List<AlbumContainer> topAlbumsFromTracks = new ArrayList<>();
    public List<GenreContainer> topOrderedGenres = new ArrayList<>();
    public List<String> artistsEndpointForGenre = new ArrayList<>(); //used to retrieve info of artist to ID Top Genres
    public List<String> listOfGenres = new ArrayList<>();
    private final SpotifyAuthManager sm;
    public String userEmail;

    APIRequests(Context appContext, Activity activity) {
        sm = new SpotifyAuthManager(appContext, activity);

        getUserEmail();
        topArtistsResponse();
        topTracksAndAlbumsResponse();
        topGenresResponse();

    }

    public void getUserEmail() {
        Log.d("API REQUESTS", "here");
        sm.callAPI("/v1/me/", data -> {
            try {
                userEmail = data.getString("email");
                Log.d("called V1/ME", userEmail);
            } catch (Exception e) {
                Log.e("called V1/ME", "call error");
                throw new RuntimeException(e);
            }
        });
    }

    public void topArtistsResponse() {
        sm.callAPI("/v1/me/top/artists", data -> {
            try {
                JSONArray items = data.getJSONArray("items");
                for (int i = 0; i < 3; i++) { //get ONLY top 3 artists
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
                    topTracks.add(new TrackContainer(trackName, albumName, artistOfTrack));
                    topAlbumsFromTracks.add(new AlbumContainer(albumName, imageAlbumURL));
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
                    for (int genreIndex = 0; genreIndex < genresList.length(); genreIndex++) {
                        String genre = (String) genresList.get(genreIndex);
                        listOfGenres.add(genre);
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            });

        }
        genreRanking(listOfGenres);
    }

    private void genreRanking(List<String> genresList) {
        //creates a mapping of <Genre, NumberOfOccurrencesOfGenre>
        Map<String, Integer> countMap = new HashMap<>();
        for (String genre : genresList) {
            if (!countMap.containsKey(genre)) {
                countMap.put(genre, 1);
            } else {
                Integer oldCount = countMap.get(genre);
                if (oldCount != null) countMap.replace(genre, oldCount, oldCount + 1);
            }
        }
        Integer max = 0;
        String toAdd = "";
        int traversal = 0;
        //finds the Top5Genres of user
        while (traversal < 5) {
            for (Map.Entry<String, Integer> entry : countMap.entrySet()) {
                if (entry.getValue().compareTo(max) > 0) {
                    max = entry.getValue();
                    toAdd = entry.getKey();
                }
            }
            traversal++;
            topOrderedGenres.add(new GenreContainer(toAdd));
            countMap.replace(toAdd, null);
            countMap.remove(toAdd);
        }
        if (!countMap.isEmpty()) {
            countMap.replaceAll((k, v) -> null);
            countMap.clear();
        }
    }
}