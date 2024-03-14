package com.example.spotifywrapper;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.function.Consumer;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SpotifyAuthManager {
    private static final String CLIENT_ID = BuildConfig.SPOTIFY_CLIENT_ID;
    private static final String REDIRECT_URI = Uri.parse("spotifywrapper://auth").toString();
    private static String userToken = null;
    public static final String USER_TOKEN_KEY = "spotify-client-token";

    private SpotifyAuthManager() {
        throw new AssertionError("This class should not be instantiated.");
    }

    public static String getUserToken(Activity contextActivity) {
        if (userToken != null) return userToken;
        return getStoredUserToken(contextActivity);
    }

    public static String getStoredUserToken(Activity contextActivity) {
        SharedPreferences sp = contextActivity.getPreferences(Context.MODE_PRIVATE);
        userToken = sp.getString(USER_TOKEN_KEY, null);
        return userToken;
    }

    public static void setUserToken(Activity contextActivity, String t) {
        userToken = t;

        SharedPreferences sharedPreferences = contextActivity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SpotifyAuthManager.USER_TOKEN_KEY, userToken);
        editor.apply();
    }

    public static void requestUserToken(Activity contextActivity) {
        final AuthorizationRequest request =
                new AuthorizationRequest.Builder(CLIENT_ID, AuthorizationResponse.Type.TOKEN, REDIRECT_URI)
                .setShowDialog(false)
                .setScopes(new String[] { "user-read-email" }) // <--- Change the scope of your requested token here
                .setCampaign("your-campaign-token")
                .build();
        AuthorizationClient.openLoginActivity(contextActivity, 0, request);
    }

    public static void callAPI(String endpoint, Consumer<JSONObject> onSuccess) {
        if (userToken == null) {
            return;
        }

        final Request request = new Request.Builder()
                .url("https://api.spotify.com" + endpoint)
                .addHeader("Authorization", "Bearer " + userToken)
                .build();

        Call call = new OkHttpClient().newCall(request);

        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("getUserData", "Failed to make the request", e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e("HTTP", "Failed to fetch data: " + response.code() + " - " + response.message());
                    return;
                }
                try {
                    assert response.body() != null;
                    JSONObject jsonData = new JSONObject(response.body().string());
                    onSuccess.accept(jsonData);
                } catch (JSONException e) {
                    Log.e("HTTP", "Failed to parse JSON response", e);
                }
            }
        });
    }
}
