package com.example.petitesannonceslocales.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
        adAdapter = new AdAdapter(adList);
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