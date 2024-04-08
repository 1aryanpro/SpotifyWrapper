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
import java.util.function.Consumer;

import okhttp3.Call;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
import okhttp3.Response;

public class APIRequests {
    //private static final String BASE_URL = "https://api.spotify.com";
    //private static final String TAG = "";

    public List<String> artistsForGenre;


    //list of Web API endpoints to get all info on artists
    public List<String> artistsEndpointForGenre;

    public List<String> listOfGenres = new ArrayList<>();

    //private void getResponseForTopArtists() {return "";}

    //Serves as the onResponse method inside the .enqueue and Callback()
    private void parseTopArtistsResponse(Call call, Response jsonResponse) throws IOException{
        if (jsonResponse.isSuccessful()) {
            try {
                assert jsonResponse.body() != null;
                JSONObject jsonData = new JSONObject(jsonResponse.body().string());
                JSONArray items = jsonData.getJSONArray("items");
                for (int i = 0; i < items.length(); i++) {
                    JSONObject artist = (JSONObject) items.get(i);
                    String artistName = artist.getString("name"); //gets the name of this current artist
                    String artistId = artist.getString("id"); //gets the artist id for this current artist
                }
            } catch (JSONException e) {
                Log.e("HTTP", "Failed to parse JSON response", e);
            }
        } else {
            Log.e("HTTP", "Failed to fetch data: " + jsonResponse.code() + " - " + jsonResponse.message());
        }
    }

    //private void getResponseForTopTracks() {return;}

    private void parseTopTracksResponse(Call call, Response jsonResponse) throws IOException {
        if (jsonResponse.isSuccessful()) {
            try {
                assert jsonResponse.body() != null;
                JSONObject jsonData = new JSONObject(jsonResponse.body().string());
                JSONArray items = jsonData.getJSONArray("items");
                for (int i = 0; i < items.length(); i++) {
                    JSONObject track = (JSONObject) items.get(i);
                    String trackName = track.getString("name"); //gets the name of the track
                    JSONArray artistList = track.getJSONArray("artists"); //gets the array of artist(s) for track --> USED for GENRES method
                    JSONObject album = track.getJSONObject("album");
                    JSONArray images = album.getJSONArray("images");
                    for (int j = 0; j < images.length(); j++) {
                        JSONObject image = images.getJSONObject(j);
                        String imageURL = image.getString("url"); //gets imageURL
                        //gets dimensions of image for XML use
                        int height = image.getInt("height");
                        int width = image.getInt("width");

                        //perform desired actions
                    }
                    for (int j = 0; j < artistList.length(); j++) {
                        JSONObject artist = artistList.getJSONObject(j);
                        String artistName = artist.getString("name");
                        artistsForGenre.add(artistName); //add to list of existed artists for Top Tracks
                                                            //will be used to identify top genres

                        //href --> link to the Web API endpoint providing full details of the artist
                        String hrefArtist = artist.getString("href");
                        artistsEndpointForGenre.add(hrefArtist); //used for future access to artists INFO within API call
                    }
                    String trackPopularity = track.getString("popularity"); // gets the pop of the track
                }
            } catch (JSONException e){
                Log.e("HTTP", "Failed to parse JSON response", e);
            }
        } else {
            Log.e("HTTP", "Failed to fetch data: " + jsonResponse.code() + " - " + jsonResponse.message());
        }
    }

    //top 5 genres of a listener

    /**
     * Goes through artistsEndpointForGenre list (contains all endpoints), and makes an API call
     * --> to requestAPIForArtistInfo
     * -----------------------------------------------------------------------------------------
     * Could also create method for requestAPIForArtistInfo where additional parameter takes in
     * artistsEndpointForGenre if possible?????
     */

    private void callAPIForArtistsInfo() {
        /*
        for (int i = 0; i < artistsEndpointForGenre.size(); i++) {
            //make a request to callAPI with the endpoint for each being each element of
                //artistsEndpointForGenre list
        }
         */
    }

    /**
     * Makes the request to the API to retrieve data
     * @param endpoint
     * @param onSuccess
     */

    private void requestAPIForArtistInfo(String endpoint, Consumer<JSONObject> onSuccess) {
        //after making the call to this method to make a request...

        //the retrieveTop5Genres acts as "onResponse" for this "callAPI" method--> change as needed to fit everything else
    }

    /**
     * This method will be called for upon each request, and will construct a list of genres.
     *
     * When actually going through the response, it will return a list of genres the artists does
     * --> go through each element and add individually to listOfGenres
     * @param call
     * @param jsonResponse
     * @throws IOException
     */
    private void retrieveListOfGenres(Call call, Response jsonResponse) throws IOException{
        if (jsonResponse.isSuccessful()) {
            assert jsonResponse.body() != null;
            //catch (JSONException e){
               // Log.e("HTTP", "Failed to parse JSON response", e);
            //}
        } else {
            Log.e("HTTP", "Failed to fetch data: " + jsonResponse.code() + " - " + jsonResponse.message());
        }
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
            listOfGenres.add(toAdd);
            countMap.replace(toAdd, null);
            countMap.remove(toAdd);
        }
        if (!countMap.isEmpty()) {
            countMap.replaceAll((k, v) -> null);
            countMap.clear();
        }
        return listOfGenres;
    }
}
