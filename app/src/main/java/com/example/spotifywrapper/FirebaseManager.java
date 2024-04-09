package com.example.spotifywrapper;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseManager {
    FirebaseDatabase db;
    FirebaseManager() {
        db = FirebaseDatabase.getInstance();
    }

    public void testWorking(String testStr) {
        DatabaseReference ref = db.getReference("testingString");
        ref.setValue(testStr);
    }
}
