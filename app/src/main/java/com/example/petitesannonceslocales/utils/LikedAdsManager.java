package com.example.petitesannonceslocales.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class LikedAdsManager {
    private static final String PREF_NAME = "LikedAdsPrefs";
    private static final String LIKED_ADS_KEY = "LikedAds";
    private SharedPreferences sharedPreferences;
    private Gson gson;

    public LikedAdsManager(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
    }

    public void likeAd(String adId) {
        List<String> likedAds = getLikedAds();
        if (!likedAds.contains(adId)) {
            likedAds.add(adId);
            saveLikedAds(likedAds);
        }
    }

    public void unlikeAd(String adId) {
        List<String> likedAds = getLikedAds();
        if (likedAds.contains(adId)) {
            likedAds.remove(adId);
            saveLikedAds(likedAds);
        }
    }

    public boolean isAdLiked(String adId) {
        return getLikedAds().contains(adId);
    }

    public List<String> getLikedAds() {
        String json = sharedPreferences.getString(LIKED_ADS_KEY, "[]");
        Type listType = new TypeToken<List<String>>() {}.getType();
        return gson.fromJson(json, listType);
    }

    private void saveLikedAds(List<String> likedAds) {
        String json = gson.toJson(likedAds);
        sharedPreferences.edit().putString(LIKED_ADS_KEY, json).apply();
    }
}
