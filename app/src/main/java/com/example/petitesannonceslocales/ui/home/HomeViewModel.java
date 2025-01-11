package com.example.petitesannonceslocales.ui.home;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.petitesannonceslocales.utils.Ad;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<List<Ad>> adsLiveData;
    private final FirebaseFirestore db;

    public HomeViewModel() {
        adsLiveData = new MutableLiveData<>();
        db = FirebaseFirestore.getInstance();
        fetchAllAds();
    }

    public LiveData<List<Ad>> getAllAds() {
        return adsLiveData;
    }

    private void fetchAllAds() {
        db.collection("ads")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Ad> ads = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Ad ad = document.toObject(Ad.class);
                            ad.setId(document.getId());
                            ads.add(ad);
                        }
                        Log.d("ads from db:", ads.toString());
                        adsLiveData.setValue(ads);
                    } else {
                        adsLiveData.setValue(new ArrayList<>()); // Return empty list on failure
                    }
                })
                .addOnFailureListener(e -> adsLiveData.setValue(new ArrayList<>())); // Handle errors
    }
}