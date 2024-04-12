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
    public static final String USER_TOKEN_KEY = "spotify-client-token";
    public static final String USER_EMAIL_KEY = "spotify-user-email";
    private final Activity activity;
    private String userToken = null;
    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;
    public String userEmail;

    SpotifyAuthManager(Context context, Activity conActivity) {
        activity = conActivity;
        pref = context.getSharedPreferences(USER_TOKEN_KEY, Context.MODE_PRIVATE);
        editor = pref.edit();

        getStoredUserToken();
        getStoredUserEmail();
    }

    public String getUserToken() {
        return userToken;
    }

    public String getStoredUserToken() {
        userToken = pref.getString(USER_TOKEN_KEY, null);
        Log.d("getStoredUserToken", userToken == null ? "NULL" : userToken);
        return userToken;
    }

    public String getStoredUserEmail() {
        userEmail = pref.getString(USER_EMAIL_KEY, null);
        if (userEmail != null) new FirebaseManager().createUser(userEmail);
        return userEmail;
    }

    public void setUserEmail(String em) {
        userEmail = em.replace("@", "-").replace(".", "-");
        editor.putString(USER_EMAIL_KEY, userEmail);
        editor.apply();

        new FirebaseManager().createUser(userEmail);
    }

    public void setUserToken(String t) {
        userToken = t;
        editor.putString(USER_TOKEN_KEY, userToken);
        editor.apply();

        callAPI("/v1/me/", data -> {
            try {
                setUserEmail(data.getString("email"));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void clearUserToken() {
        userToken = null;
        editor.remove(USER_TOKEN_KEY);
        editor.apply();
    }

    public void requestUserToken() {
        final AuthorizationRequest request =
                new AuthorizationRequest.Builder(CLIENT_ID, AuthorizationResponse.Type.TOKEN, REDIRECT_URI)
                .setShowDialog(false)
                .setScopes(new String[] { "user-read-email", "user-top-read" })
                .build();
        AuthorizationClient.openLoginActivity(activity, 0, request);
    }

    public void callAPI(String endpoint, Consumer<JSONObject> onSuccess) {
        if (userToken == null) {
            return;
        }

        Log.d("CallAPI Called", "endpoint: " + endpoint);

        final Request request = new Request.Builder()
                .url("https://api.spotify.com" + endpoint)
                .addHeader("Authorization", "Bearer " + userToken)
                .build();

        Call call = new OkHttpClient().newCall(request);

        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("getUserData", "Failed to make the request", e);
                userToken = null;
                requestUserToken();
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
