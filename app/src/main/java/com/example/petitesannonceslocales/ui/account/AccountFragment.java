package com.example.petitesannonceslocales.ui.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


import com.example.petitesannonceslocales.R;
import com.example.petitesannonceslocales.RegisterActivity;
import com.example.petitesannonceslocales.SignInActivity;

public class AccountFragment extends Fragment {
    private AccountViewModel viewModel;
    private TextView emailTextView;
    private TextView userTypeTextView;
    private Button actionButton;
    private Button signinButton;

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
    }

    private void observeViewModel() {
        viewModel.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                emailTextView.setText(user.getEmail());
                userTypeTextView.setText(user.getUserType());
                actionButton.setText("Logout");
            } else {
                emailTextView.setText("");
                userTypeTextView.setText("");
                actionButton.setText("Register");
            }
        });
    }
}
