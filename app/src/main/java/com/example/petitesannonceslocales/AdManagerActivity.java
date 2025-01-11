package com.example.petitesannonceslocales;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petitesannonceslocales.utils.Ad;
import com.example.petitesannonceslocales.utils.AdAdapter;
import com.example.petitesannonceslocales.utils.SessionManager;
import com.google.firebase.firestore.*;
import java.util.ArrayList;
import java.util.List;

public class AdManagerActivity extends AppCompatActivity {

    private RecyclerView recyclerViewAds;
    private AdAdapter adAdapter;
    private FirebaseFirestore db;
    private List<Ad> adList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_list);

        recyclerViewAds = findViewById(R.id.recyclerViewAds);
        recyclerViewAds.setLayoutManager(new LinearLayoutManager(this));

        adList = new ArrayList<Ad>();
        adAdapter = new AdAdapter(adList);
        recyclerViewAds.setAdapter(adAdapter);

        db = FirebaseFirestore.getInstance();

        // Get the current user's ID from the SessionManager
        SessionManager.getInstance().getCurrentUserLiveData().observe(this, user -> {
            if (user != null) {
                fetchUserAds(user.getEmail());
            } else {
                Toast.makeText(this, "No active user session", Toast.LENGTH_SHORT).show();
                finish(); // Exit the activity if no user is logged in
            }
        });
    }

    private void fetchUserAds(String userEmail) {
        db.collection("ads")
                .whereEqualTo("userEmail", userEmail)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        adList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Ad ad = document.toObject(Ad.class);
                            ad.setId(document.getId()); // Set the document ID as the Ad ID
                            adList.add(ad);
                        }
                        adAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Failed to load ads", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
