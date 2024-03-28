package com.example.spotifywrapper;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationResponse;

public class LoginActivity extends AppCompatActivity {
    String userToken;
    SpotifyAuthManager sm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button loginButton = findViewById(R.id.login_btn);
        loginButton.setOnClickListener((v) -> logIn());

        sm = new SpotifyAuthManager(getApplicationContext());

        userToken = sm.getUserToken();
        if (userToken != null) finishLogIn();
    }

    private void logIn() {
        sm.requestUserToken(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);

        userToken = response.getAccessToken();
        if (userToken == null) return;

        sm.setUserToken(userToken);

        finishLogIn();
    }

    private void finishLogIn() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(this, LoginDebugActivity.class);
        int id = item.getItemId();
        startActivity(intent);
        finish();
        return true;
    }
}