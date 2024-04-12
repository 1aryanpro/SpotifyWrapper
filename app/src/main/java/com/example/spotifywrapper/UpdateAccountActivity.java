package com.example.spotifywrapper;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.spotifywrapper.FirebaseManager;
import com.example.spotifywrapper.R;

public class UpdateAccountActivity extends AppCompatActivity {
    TextView usernameView;
    FirebaseManager fire;
    SpotifyAuthManager sm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_account);

        sm = new SpotifyAuthManager(getApplicationContext(), this);
        fire = new FirebaseManager();

        usernameView = findViewById(R.id.username_textView);
        fire.getUsername(sm.userEmail, username -> {
            usernameView.setText(username);
        });

        Button changeUsernameBTN = findViewById(R.id.change_username);
        changeUsernameBTN.setOnClickListener((v) -> editUsername());

        Button backHome = findViewById(R.id.back_home);
        backHome.setOnClickListener((v) -> finish());

        Button deleteUserBTN = findViewById(R.id.delete_account);
        deleteUserBTN.setOnClickListener((v) -> deleteUser());
    }

    private void deleteUser() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure?");

        // Set positive button and its click listener
        builder.setPositiveButton("Yes", (dialog, which) -> {
            fire.deleteUser(sm.userEmail);
            sm.clearUserToken();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });


        // Set negative button and its click listener
        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void editUsername() {
        EditText input = new EditText(this);

        // Create an AlertDialog Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New Username:");
        builder.setView(input);

        // Set positive button and its click listener
        builder.setPositiveButton("DONE", null);


        // Set negative button and its click listener
        builder.setNegativeButton("Cancel", null);

        // Create and show the dialog
        AlertDialog dialog = builder.create();


        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String userInput = input.getText().toString();
                        if (userInput.contains(" ") || userInput.contains("\n")) return;

                        usernameView.setText(userInput);
                        fire.setUsername(sm.userEmail, userInput);
                        dialog.dismiss();
                    }
                });
            }
        });

        dialog.show();
    }
}