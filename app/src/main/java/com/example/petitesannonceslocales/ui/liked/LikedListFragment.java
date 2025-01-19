package com.example.petitesannonceslocales.ui.liked;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.petitesannonceslocales.databinding.FragmentLikedListBinding;
import com.example.petitesannonceslocales.utils.Ad;
import com.example.petitesannonceslocales.utils.AdAdapter;
import com.example.petitesannonceslocales.utils.LikedAdsManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class LikedListFragment extends Fragment {

    private FragmentLikedListBinding binding;
    private LikedListViewModel likedListViewModel;
    private AdAdapter adAdapter;
    private List<Ad> likedAdList;
    private FirebaseFirestore db;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        likedListViewModel =
                new ViewModelProvider(this).get(LikedListViewModel.class);

        binding = FragmentLikedListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.recyclerViewLikedAds.setLayoutManager(new LinearLayoutManager(getContext()));

        likedAdList = new ArrayList<>();
        adAdapter = new AdAdapter(getContext(), likedAdList, new AdAdapter.AdActionListener() {
            @Override
            public void onAdSelected(Ad ad) {
                // Handle ad click
            }

            @Override
            public void onAdEdit(Ad ad) {
                // Not applicable
            }

            @Override
            public void onAdDelete(Ad ad) {
                // Not applicable
            }

            @Override
            public void onAdLike(Ad ad) {
                // Not applicable
            }

            @Override
            public void onAdContact(Ad ad) {
                // Contact logic
            }
        }, AdAdapter.Mode.HOME_VIEW);

        binding.recyclerViewLikedAds.setAdapter(adAdapter);
        db = FirebaseFirestore.getInstance();

        // Observe the liked ads list
        likedListViewModel.getLikedAdIds().observe(getViewLifecycleOwner(), likedAdIds -> {
            if (!likedAdIds.isEmpty()) {
                loadLikedAdsFromFirestore(likedAdIds);
            } else {
                likedAdList.clear();
                adAdapter.notifyDataSetChanged();
            }
        });

        return root;
    }

    private void loadLikedAdsFromFirestore(List<String> likedAdIds) {
        Log.d("likedAdsManager", likedAdIds.toString()); // Confirm liked IDs are being passed correctly

        db.collection("ads")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    likedAdList.clear();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        if (likedAdIds.contains(document.getId())) {
                            Ad ad = document.toObject(Ad.class);
                            ad.setId(document.getId()); // Explicitly set the Firestore document ID
                            likedAdList.add(ad);
                        }
                    }

                    adAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    // Handle error
                    Log.e("FirestoreError", "Error fetching liked ads: ", e);
                });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}