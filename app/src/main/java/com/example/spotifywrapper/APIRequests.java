package com.example.spotifywrapper;

//import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;
 */

import java.io.IOException;
import java.util.Objects;
import java.util.function.Consumer;

import okhttp3.Call;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
import okhttp3.Response;

public class APIRequests {

    public List<ArtistContainer> topArtists = new ArrayList<>(); //add ArtistContainer objects (Name, Image URL)
    public List<TrackContainer> topTracks = new ArrayList<>(); //add TrackContainer objects (Name, Album, Artist)
    public List<String> topGenres = new ArrayList<>(); //add genres to this list FOR EACH artist
    public List<String> artistsForGenre; //MAY NOT BE NEEDED
    public List<String> artistsEndpointForGenre = new ArrayList<>();
    public List<String> listOfGenres = new ArrayList<>();

    public List<String> orderedGenres = new ArrayList<>();

    private void parseTopArtistsResponse(SpotifyAuthManager sm) throws JSONException{
        sm.callAPI("/v1/me/top/artists", data -> {
               JSONArray items = data.getJSONArray("items");
               for (int i = 0; i < 3; i++) { //get ONLY top 3 artists
                   JSONObject artist = items.getJSONObject(i);
                   String artistName = artist.getString("name");
                   JSONArray imagesArray = artist.getJSONArray("images");
                   String artistImageURL = null;
                   if (imagesArray.length() > 0) {
                       JSONObject image = imagesArray.getJSONObject(0);
                       artistImageURL = image.getString("url");
                   } else {
                       //error
                   }
                   topArtists.add(new ArtistContainer(artistName, artistImageURL)); //MIGHT HAVE TO DO A NULL CHECK WHEN USING THIS LIST
               }
        });
    }

    private void parseTopTracksResponse(SpotifyAuthManager sm) {
        sm.callAPI("/v1/me/top/tracks", data -> {
            JSONArray items = data.getJSONArray("items");
            for (int i = 0; i < 5; i++) { //to get top 5
                JSONObject track = items.getJSONObject(i);
                String trackName = track.getString("name"); //name of track
                JSONObject album = track.getJSONObject("album");
                String albumName = album.getString("name");
                JSONArray artistList = track.getJSONArray("artists"); //array of artist(s) --> USED for GENRES method too !!!!!!
                List<String> artistOfTrack = new ArrayList<>(); //artist(s) of track
                for (int j = 0; j < artistList.length(); j++) {
                    JSONObject artist = artistList.getJSONObject(j);
                    String artistName = artist.getString("name");
                    artistOfTrack.add(artistName); //add artist(s) of track to a list

                    String artistID = artist.getString("id"); //USED AS PART OF ENDPOINT

                    artistsForGenre.add(artistName); //add to list of existed artists for Top Tracks
                    //will be used to identify top genres

                    artistsEndpointForGenre.add(artistID); //used as PART of endpoint
                }
                topTracks.add(new TrackContainer(trackName, albumName, artistOfTrack));
            }
        });
    }

    private void parseTopGenresResponse(SpotifyAuthManager sm) {
        for (int i = 0; i < artistsEndpointForGenre.size(); i++) {
            sm.callAPI("/v1/artists/" + artistsEndpointForGenre.get(i), data -> {
                JSONArray genresList = data.getJSONArray("genres");
                for (int genreIndex = 0; genreIndex < genresList.length(); genreIndex++) {
                    String genre = genresList.get(genreIndex);
                    listOfGenres.add(genre);
                }
            });
        }
        genreRanking(listOfGenres);
    }

    private List<String> genreRanking(List<String> genresList) {
        //creates a mapping of <Genre, NumberOfOccurrencesOfGenre>
        Map<String, Integer> countMap = new HashMap<>();
        for (String genre : genresList) {
            if (!countMap.containsKey(genre)) {
                countMap.put(genre, 1);
            } else {
                Integer oldCount = countMap.get(genre);
                countMap.replace(genre, countMap.get(genre), oldCount + 1);
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
            orderedGenres.add(toAdd);
            countMap.replace(toAdd, null);
            countMap.remove(toAdd);
        }
        if (!countMap.isEmpty()) {
            countMap.replaceAll((k, v) -> null);
            countMap.clear();
        }
        return orderedGenres;
    }
}