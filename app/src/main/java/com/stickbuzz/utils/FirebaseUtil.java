package com.stickbuzz.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.atomic.AtomicMarkableReference;

public class FirebaseUtil {

    private static FirebaseAuth auth;
    private static FirebaseDatabase database;

    public static FirebaseAuth getAuth() {
        if (auth == null)
            auth = FirebaseAuth.getInstance();
        return auth;
    }

    public static DatabaseReference getUsersRef() {
        if (database == null)
            database = FirebaseDatabase.getInstance();
        return database.getReference("users");
    }

    public static DatabaseReference getMatchesRef() {
        if (database == null)
            database = FirebaseDatabase.getInstance();
        return database.getReference("matches");
    }

    public static FirebaseDatabase getDatabase() {

        if (database == null) {
            database = FirebaseDatabase.getInstance();
        }

        return database;
    }
    public static DatabaseReference getTournamentsRef() {
        return getDatabase().getReference("tournaments");
    }
    public static DatabaseReference getTeamsRef() {
        return getDatabase().getReference("teams");
    }
}
