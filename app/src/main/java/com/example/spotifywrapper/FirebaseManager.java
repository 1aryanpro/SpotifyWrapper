package com.example.spotifywrapper;

import android.util.Log;
import android.util.Pair;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class FirebaseManager {
    FirebaseDatabase db;
    FirebaseManager() {
        db = FirebaseDatabase.getInstance();
    }

    public void testWorking(String testStr) {
        db.getReference("testingString/otherString").setValue(testStr);
    }

    public void setRef(String ref, Object val) {
        db.getReference(ref).setValue(val);
    }

    public void getWrappedIDs(Consumer<List<Pair<String,String>>> onSuccess) {
        db.getReference().get().addOnCompleteListener(curr -> {
            List<Pair<String, String>> wrappedIDs = new ArrayList<>();
            for (DataSnapshot snap: curr.getResult().getChildren()) {
                String userID = snap.getKey();

                for(DataSnapshot wrap : snap.child("wraps").getChildren()) {
                    wrappedIDs.add(new Pair<>(userID, wrap.getKey()));
                }

            }
            onSuccess.accept(wrappedIDs);
        });
    }

    public void getUsername(String userMail, Consumer<String> onSuccess) {
        db.getReference(userMail).child("username").get().addOnCompleteListener(dataSnap -> {
            onSuccess.accept(dataSnap.getResult().getValue(String.class));
        });
    }

    public void setUsername(String userMail, String username) {
        setRef(userMail + "/username", username);
    }

    public void deleteUser(String userMail) {
        db.getReference(userMail).removeValue();
    }

    public void createUser(String userMail) {
        Log.d("USER REGIS", userMail);
        db.getReference(userMail).get().addOnCompleteListener(dataSnapshot -> {
            if (dataSnapshot.getResult().getValue() == null) {
                db.getReference(userMail + "/username").setValue(userMail);
            } else {
                Log.d("USER REGIS", "OLD USER :(");
            }
        });
    }
}
