package com.example.petitesannonceslocales.ui.home;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petitesannonceslocales.databinding.FragmentHomeBinding;
import com.example.petitesannonceslocales.utils.Ad;
import com.example.petitesannonceslocales.utils.AdAdapter;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;
    private RecyclerView recyclerViewAds;
    private AdAdapter adAdapter;
    private List<Ad> adList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerViewAds = binding.recyclerViewAds; // Ensure you have a RecyclerView in your layout with this ID
        recyclerViewAds.setLayoutManager(new LinearLayoutManager(requireContext()));

        adList = new ArrayList<>();
        adAdapter = new AdAdapter(requireContext(), adList, new AdAdapter.AdActionListener() {
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

        // Fetch all ads from Firestore and observe data changes
        homeViewModel.getAllAds().observe(getViewLifecycleOwner(), ads -> {
            Log.d("ads from model:", ads.toString());
            adList.clear();
            adList.addAll(ads);
            adAdapter.notifyDataSetChanged();
        });
        Log.d("ads in fragment:", adList.toString());
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}