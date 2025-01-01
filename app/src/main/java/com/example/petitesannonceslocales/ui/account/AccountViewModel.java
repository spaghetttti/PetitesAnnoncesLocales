package com.example.petitesannonceslocales.ui.account;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.petitesannonceslocales.utils.SessionManager;
import com.example.petitesannonceslocales.utils.User;

public class AccountViewModel extends ViewModel {
    private final LiveData<User> currentUserLiveData;

    public AccountViewModel() {
        currentUserLiveData = SessionManager.getInstance().getCurrentUserLiveData();
    }

    public LiveData<User> getCurrentUser() {
        return currentUserLiveData;
    }

    public void logout() {
        SessionManager.getInstance().logout();
    }
}