package com.example.spotifywrapper;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    int[] pages;
    int currentPage = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button launchButton = findViewById(R.id.launch_wrapped);
        launchButton.setOnClickListener((v) -> launchWrapped());

        pages = new int[]{R.id.wrapper1, R.id.wrapper2, R.id.wrapper3, R.id.wrapper4, R.id.wrapper5, R.id.wrapper6};
    }

    private void launchWrapped() {
        Intent wrapped = new Intent(this, WrappedActivity.class);
        wrapped.putExtra("id", 0);
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