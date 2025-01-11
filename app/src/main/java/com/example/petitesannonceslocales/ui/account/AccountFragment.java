package com.example.petitesannonceslocales.ui.account;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


import com.example.petitesannonceslocales.PostAdActivity;
import com.example.petitesannonceslocales.R;
import com.example.petitesannonceslocales.RegisterActivity;
import com.example.petitesannonceslocales.SignInActivity;

import java.util.Objects;

public class AccountFragment extends Fragment {
    private AccountViewModel viewModel;
    private TextView emailTextView;
    private TextView userTypeTextView;
    private Button actionButton;
    private Button signinButton;
    private Button postAdButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        emailTextView = view.findViewById(R.id.emailTextView);
        userTypeTextView = view.findViewById(R.id.userTypeTextView);
        actionButton = view.findViewById(R.id.actionButton);
        signinButton = view.findViewById(R.id.signinButton);
        postAdButton = view.findViewById(R.id.postAd);

        viewModel = new ViewModelProvider(this).get(AccountViewModel.class);

        observeViewModel();
        actionButton.setOnClickListener(v -> {
            if (viewModel.getCurrentUser().getValue() != null) {
                viewModel.logout();
            } else {
                Intent intent = new Intent(getActivity(), RegisterActivity.class);
                startActivity(intent);
            }
        });

        signinButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SignInActivity.class);
            startActivity(intent);
        });

        postAdButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PostAdActivity.class);
            startActivity(intent);
        });
    }

    private void observeViewModel() {
        viewModel.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            postAdButton.setVisibility(View.GONE);
            if (user != null) {
                Log.wtf("user:", user.getEmail());
                emailTextView.setText(user.getEmail());
                userTypeTextView.setText(user.getUserType());
                actionButton.setText("Logout");
                signinButton.setVisibility(View.GONE);
                if (Objects.equals(user.getUserType(), "Professional")) {
                    postAdButton.setVisibility(View.VISIBLE);
                }
            } else {
                emailTextView.setText("");
                userTypeTextView.setText("");
                actionButton.setText("Register");
                signinButton.setVisibility(View.VISIBLE);
            }
        });
    }
}
