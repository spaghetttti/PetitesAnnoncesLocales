package com.example.petitesannonceslocales.ui.home;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petitesannonceslocales.R;
import com.example.petitesannonceslocales.databinding.FragmentHomeBinding;
import com.example.petitesannonceslocales.utils.Ad;
import com.example.petitesannonceslocales.utils.AdAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;
    private RecyclerView recyclerViewAds;
    private AdAdapter adAdapter;
    private List<Ad> allAds = new ArrayList<>();
    private List<Ad> filteredAds = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerViewAds = binding.recyclerViewAds; // Ensure you have a RecyclerView in your layout with this ID
        recyclerViewAds.setLayoutManager(new LinearLayoutManager(requireContext()));

        allAds = new ArrayList<>();
        adAdapter = new AdAdapter(requireContext(), filteredAds, new AdAdapter.AdActionListener() {
            @Override
            public void onAdSelected(Ad ad) {
                // Handle ad selection
            }

            @Override
            public void onAdLike(Ad ad) {
                Toast.makeText(getContext(), "Ad Liked!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdContact(Ad ad) {
                if (ad.getContactMethod().equals("Email")) {
                    // Open mail app with ad.getContactInfo() email string
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                    emailIntent.setData(Uri.parse("mailto:" + ad.getContactInfo())); // This ensures only email apps handle this intent
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Inquiry about your Ad");
                    emailIntent.putExtra(Intent.EXTRA_TEXT, "Hello, I am interested in your ad.");

                    try {
                        startActivity(emailIntent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(getContext(), "No email app found!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Open phone app with ad.getContactInfo() phone number string
                    Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
                    phoneIntent.setData(Uri.parse("tel:" + ad.getContactInfo())); // This ensures the phone app handles this intent

                    try {
                        startActivity(phoneIntent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(getContext(), "No phone app found!", Toast.LENGTH_SHORT).show();
                    }
                }

                // Show a confirmation toast
                Toast.makeText(getContext(), "Contact initiated!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdEdit(Ad ad) {
                // Not applicable for Home View
            }

            @Override
            public void onAdDelete(Ad ad) {
                // Not applicable for Home View
            }
        }, AdAdapter.Mode.HOME_VIEW);

        recyclerViewAds.setAdapter(adAdapter);

        db = FirebaseFirestore.getInstance();

        loadAds();
        setupSearchView();
        setupCategorySpinner();
        return root;
    }

    private void loadAds() {
        db.collection("ads").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    allAds.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Ad ad = document.toObject(Ad.class);
                        ad.setId(document.getId());
                        allAds.add(ad);
                    }
                    filteredAds.clear();
                    filteredAds.addAll(allAds);
                    adAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Failed to load ads", Toast.LENGTH_SHORT).show();
                });
    }

    private void setupSearchView() {
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterAds(query, getSelectedCategory());
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterAds(newText, getSelectedCategory());
                return true;
            }
        });
    }

    private void setupCategorySpinner() {
//        String[] categories = {"All", "Electronics", "Furniture", "Vehicles", "Jobs"};
        // Get the string array from resources
        String[] allCategories = getResources().getStringArray(R.array.categories_array);

        // Create a new array with "All" as the first element
        String[] categories = new String[allCategories.length + 1];
        categories[0] = "All";

        // Copy the elements from the resource array to the new array
        System.arraycopy(allCategories, 0, categories, 1, allCategories.length);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item, categories);
        binding.spinnerCategory.setAdapter(spinnerAdapter);

        binding.spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterAds(binding.searchView.getQuery().toString(), categories[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private String getSelectedCategory() {
        return binding.spinnerCategory.getSelectedItem().toString();
    }

    private void filterAds(String query, String category) {
        filteredAds.clear();

        for (Ad ad : allAds) {
            boolean matchesQuery = query.isEmpty() || ad.getTitle().toLowerCase().contains(query.toLowerCase());
            boolean matchesCategory = category.equals("All") || ad.getCategory().equals(category);

            if (matchesQuery && matchesCategory) {
                filteredAds.add(ad);
            }
        }

        adAdapter.notifyDataSetChanged();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}