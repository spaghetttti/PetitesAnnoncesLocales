package com.example.petitesannonceslocales.utils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SessionManager {
    private static SessionManager instance;
    private final FirebaseAuth auth;
    private final FirebaseFirestore db;
    private MutableLiveData<User> currentUserLiveData;

    private SessionManager() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUserLiveData = new MutableLiveData<>();
        loadCurrentUser();
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    private void loadCurrentUser() {
        FirebaseUser firebaseUser = auth.getCurrentUser();
        if (firebaseUser != null) {
            db.collection("users").document(firebaseUser.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                            currentUserLiveData.setValue(user);
                        }
                    })
                    .addOnFailureListener(e -> currentUserLiveData.setValue(null));
        } else {
            currentUserLiveData.setValue(null);
        }
    }

    public LiveData<User> getCurrentUserLiveData() {
        return currentUserLiveData;
    }

    public void logout() {
        auth.signOut();
        currentUserLiveData.setValue(null);
    }
}

