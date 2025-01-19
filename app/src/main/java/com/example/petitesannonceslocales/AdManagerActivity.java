package com.example.petitesannonceslocales;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
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

        adList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        adAdapter = new AdAdapter(this, adList, new AdAdapter.AdActionListener() {
            @Override
            public void onAdSelected(Ad ad) {
                // Handle ad selection if needed
            }

            @Override
            public void onAdLike(Ad ad) {
                // Handle ad selection if needed

            }

            @Override
            public void onAdContact(Ad ad) {
                // Handle ad selection if needed
            }

            @Override
            public void onAdEdit(Ad ad) {
                Intent intent = new Intent(AdManagerActivity.this, EditAdActivity.class);
                intent.putExtra("ad_id", ad.getId());
                intent.putExtra("title", ad.getTitle());
                intent.putExtra("description", ad.getDescription());
                intent.putExtra("category", ad.getCategory());
                intent.putExtra("contactMethod", ad.getContactMethod());
                startActivity(intent);
            }

            @Override
            public void onAdDelete(Ad ad) {
                new AlertDialog.Builder(AdManagerActivity.this)
                        .setTitle("Delete Ad")
                        .setMessage("Are you sure you want to delete this ad?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            db.collection("ads").document(ad.getId())
                                    .delete()
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(AdManagerActivity.this, "Ad deleted successfully.", Toast.LENGTH_SHORT).show();
                                        adList.remove(ad);
                                        adAdapter.notifyDataSetChanged();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(AdManagerActivity.this, "Failed to delete ad.", Toast.LENGTH_SHORT).show();
                                    });
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        }, AdAdapter.Mode.MANAGE_VIEW);

        recyclerViewAds.setAdapter(adAdapter);

        SessionManager.getInstance().getCurrentUserLiveData().observe(this, user -> {
            if (user != null) {
                fetchUserAds(user.getEmail());
            } else {
                Toast.makeText(this, "No active user session", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        Button refreshButton = findViewById(R.id.buttonRefresh);

        refreshButton.setOnClickListener(v -> {
            // Re-fetch the data manually
            SessionManager.getInstance().getCurrentUserLiveData().observe(this, user -> {
                if (user != null) {
                    fetchUserAds(user.getEmail());
                } else {
                    Toast.makeText(this, "No active user session", Toast.LENGTH_SHORT).show();
                }
            });
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
                            ad.setId(document.getId());
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
