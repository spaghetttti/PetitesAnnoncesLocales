package com.example.petitesannonceslocales.ui.liked;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.petitesannonceslocales.utils.LikedAdsManager;

import java.util.List;

public class LikedListViewModel extends AndroidViewModel {

    private final MutableLiveData<List<String>> likedAdIds;

    public LikedListViewModel(Application application) {
        super(application);
        LikedAdsManager likedAdsManager = new LikedAdsManager(application);
        likedAdIds = new MutableLiveData<>(likedAdsManager.getLikedAds());
    }

    public LiveData<List<String>> getLikedAdIds() {
        return likedAdIds;
    }
}
