package com.example.petitesannonceslocales;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petitesannonceslocales.utils.Ad;
import com.example.petitesannonceslocales.utils.AdAdapter;
import com.example.petitesannonceslocales.utils.LikedAdsManager;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class LikedAdsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdAdapter adAdapter;
    private FirebaseFirestore db;
    private List<Ad> likedAdList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liked_ads);

        recyclerView = findViewById(R.id.recyclerViewLikedAds);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        likedAdList = new ArrayList<>();
        adAdapter = new AdAdapter(this, likedAdList, new AdAdapter.AdActionListener() {
            @Override
            public void onAdSelected(Ad ad) {
                // Handle ad click
            }

            @Override
            public void onAdLike(Ad ad) {
                // Not applicable for Liked Ads
            }

            @Override
            public void onAdEdit(Ad ad) {
                // Not applicable for Liked Ads
            }

            @Override
            public void onAdDelete(Ad ad) {
                // Not applicable for Liked Ads
            }

            @Override
            public void onAdContact(Ad ad) {
                // Contact logic
            }
        }, AdAdapter.Mode.HOME_VIEW);

        recyclerView.setAdapter(adAdapter);
        db = FirebaseFirestore.getInstance();

        loadLikedAds();
    }

    private void loadLikedAds() {
        LikedAdsManager likedAdsManager = new LikedAdsManager(this);
        List<String> likedAdIds = likedAdsManager.getLikedAds();

        if (!likedAdIds.isEmpty()) {
            db.collection("ads")
                    .whereIn("id", likedAdIds)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        likedAdList.clear();
                        likedAdList.addAll(queryDocumentSnapshots.toObjects(Ad.class));
                        adAdapter.notifyDataSetChanged();
                    })
                    .addOnFailureListener(e -> {
                        // Handle error
                    });
        }
    }
}
