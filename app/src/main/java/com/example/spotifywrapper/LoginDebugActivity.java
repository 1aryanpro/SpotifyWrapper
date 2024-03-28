package com.example.spotifywrapper;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import org.json.JSONException;

public class LoginDebugActivity extends AppCompatActivity {

    private TextView tokenTextView;
    private TextView responseTextView;
    private SpotifyAuthManager sm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_debug);

        tokenTextView = findViewById(R.id.token_text_view);
        responseTextView = findViewById(R.id.response_text_view);

        Button tokenButton = findViewById(R.id.token_btn);
        Button logoutButton = findViewById(R.id.logout_btn);
        Button profileButton = findViewById(R.id.profile_btn);

        tokenButton.setOnClickListener((v) -> getToken());
        logoutButton.setOnClickListener((v) -> logoutToken());
        profileButton.setOnClickListener((v) -> getUserData());

        sm = new SpotifyAuthManager(getApplicationContext());
        sm.getUserToken();
        setTextAsync(sm.getUserToken(), tokenTextView);
    }

    private void getToken() {
        if (sm.getUserToken() != null) return;
        sm.requestUserToken(this);
    }

    private void logoutToken() {
        sm.clearUserToken();
        setTextAsync(sm.getStoredUserToken(), tokenTextView);
    }

    private void getUserData() {
        sm.callAPI("/v1/artists/0TnOYISbd1XYRBk9myaseg", data -> {
            try {
                setTextAsync(data.toString(4), responseTextView);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);

        String userToken = response.getAccessToken();
        sm.setUserToken(userToken);
        setTextAsync(userToken, tokenTextView);
    }

    private void setTextAsync(final String text, TextView textView) {
        runOnUiThread(() -> textView.setText(text));
    }
}