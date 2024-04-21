package com.example.spotifywrapper;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

    public void getWrappedIDs(boolean everyone, String currUser, Consumer<List<WrappedDataContainer>> onSuccess) {
        db.getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<WrappedDataContainer> list = new ArrayList<>();
                for (DataSnapshot snap: dataSnapshot.getChildren()) {
                    String userID = snap.getKey();
                    if (!everyone && !currUser.equals(userID)) continue;

                    String username = (String) snap.child("username").getValue();

                    for(DataSnapshot wrap : snap.child("wraps").getChildren()) {
                        list.add(new WrappedDataContainer(userID, username, Integer.parseInt(wrap.getKey())));
                    }

                }
                onSuccess.accept(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DatabaseError", error.getCode() + " - " + error.getMessage());
            }
        });
    }

    public void getArtists(String userID, long wrappedID, Consumer<List<ArtistContainer>> onSuccess) {
        String path = userID + "/wraps/" + wrappedID + "/topArtists";
        db.getReference(path).get().addOnCompleteListener(dataSnapshot -> {
            List<ArtistContainer> list = new ArrayList<>();
            for (DataSnapshot snap: dataSnapshot.getResult().getChildren()) {
                String name = (String) snap.child("artistName").getValue();
                String image = (String) snap.child("artistImageURL").getValue();
                list.add(new ArtistContainer(name, image));
            }
            onSuccess.accept(list);
        });
    }

    public void getTracks(String userID, long wrappedID, Consumer<List<TrackContainer>> onSuccess) {
        String path = userID + "/wraps/" + wrappedID + "/topTracks";
        db.getReference(path).get().addOnCompleteListener(dataSnapshot -> {
            List<TrackContainer> list = new ArrayList<>();
            for (DataSnapshot snap: dataSnapshot.getResult().getChildren()) {
                String name = (String) snap.child("trackName").getValue();
                String albumName = (String) snap.child("albumName").getValue();
                String albumURL = (String) snap.child("albumURL").getValue();
                List<String> artists = (List<String>) snap.child("artists").getValue();
                list.add(new TrackContainer(name, albumName, albumURL, artists));
            }
            onSuccess.accept(list);
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
