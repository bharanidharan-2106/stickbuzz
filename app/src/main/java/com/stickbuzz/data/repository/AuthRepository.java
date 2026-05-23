package com.stickbuzz.data.repository;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.*;

public class AuthRepository {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    public AuthRepository() {
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
    }

    public void login(String email, String password,
                      MutableLiveData<String> roleLiveData,
                      MutableLiveData<Boolean> loadingLiveData,
                      MutableLiveData<String> errorLiveData) {

        loadingLiveData.setValue(true);

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {

                    String uid = firebaseAuth.getCurrentUser().getUid();

                    databaseReference.child(uid)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot snapshot) {
                                    String role = snapshot.child("role").getValue(String.class);
                                    roleLiveData.setValue(role);
                                    loadingLiveData.setValue(false);
                                }

                                @Override
                                public void onCancelled(DatabaseError error) {
                                    loadingLiveData.setValue(false);
                                }
                            });
                })
                .addOnFailureListener(e -> {

                    loadingLiveData.setValue(false);

                    if (e instanceof FirebaseAuthException) {
                        String code = ((FirebaseAuthException) e).getErrorCode();

                        if (code.equals("ERROR_WRONG_PASSWORD"))
                            errorLiveData.setValue("INVALID_PASSWORD");

                        else if (code.equals("ERROR_USER_NOT_FOUND"))
                            errorLiveData.setValue("USER_NOT_FOUND");

                        else if (code.equals("ERROR_INVALID_EMAIL"))
                            errorLiveData.setValue("INVALID_EMAIL_FORMAT");
                    }
                });
    }
    public void register(String name, String email, String password,
                         MutableLiveData<String> roleLiveData,
                         MutableLiveData<Boolean> loadingLiveData) {

        loadingLiveData.setValue(true);

        FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    loadingLiveData.setValue(false);

                    if (task.isSuccessful()) {

                        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                        DatabaseReference ref = FirebaseDatabase.getInstance()
                                .getReference("users")
                                .child(uid);

                        ref.child("name").setValue(name);
                        ref.child("email").setValue(email);
                        ref.child("role").setValue("user");

                        roleLiveData.setValue("user");

                    } else {
                        roleLiveData.setValue(null);
                    }
                });
    }

}
