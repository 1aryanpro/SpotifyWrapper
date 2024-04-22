package com.example.spotifywrapper;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    int[] pages;
    int currentPage = -1;

    List<String> wrappedIDs;
    RecyclerView recyclerView;
    WrappedAdapter adapter;
    SpotifyAuthManager sm;
    FirebaseManager fire;

    boolean everyone = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      
        sm = new SpotifyAuthManager(getApplicationContext(), this);
        fire = new FirebaseManager();
      
        Button launchButton = findViewById(R.id.public_filter);
        Button createButton = findViewById(R.id.create_wrapped);
        launchButton.setOnClickListener((v) -> launchWrapped());
        createButton.setOnClickListener((v) -> createWrapped());

        getData();

        recyclerView = findViewById(R.id.wrapped_list);
    }

    private void getData() {
        ClickListener listener = new ClickListener() {
            @Override
            public void click(int index) {
                WrappedDataContainer curr = adapter.getItem(index);
                launchWrapped(curr.userID, curr.epoch);
            }
        };

        FirebaseManager fire = new FirebaseManager();
        SpotifyAuthManager sm = new SpotifyAuthManager(getApplicationContext(), this);
        fire.getWrappedIDs(everyone, sm.userEmail, data -> {
            Collections.sort(data, Comparator.comparing((a) -> -a.epoch));
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new WrappedAdapter(data, getApplication(), listener);
            recyclerView.setAdapter(adapter);
        });
    }

    private void createWrapped() {
        Intent wrapped = new Intent(this, WrappedActivity.class);
        wrapped.putExtra("createNew", 1);
        startActivity(wrapped);
    }

    private void launchWrapped() {
        everyone = !everyone;
        Button everyoneBTN = findViewById(R.id.public_filter);
        everyoneBTN.setText(everyone ? "PUBLIC" : "PRIVATE");
        getData();
    }

    private void launchWrapped(String userID, long epoch) {
        Intent wrapped = new Intent(this, WrappedActivity.class);
        wrapped.putExtra("userID", userID);
        wrapped.putExtra("wrappedID", epoch);
        startActivity(wrapped);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Class act = LoginDebugActivity.class;
        int id = item.getItemId();
        if (id == R.id.launch_firebaseDebug) {
            act = FirebaseDebugActivity.class;
        } else if (id == R.id.launch_updateAccount) {
            act = UpdateAccountActivity.class;
        }
        startActivity(new Intent(this, act));
        return true;
    }
}