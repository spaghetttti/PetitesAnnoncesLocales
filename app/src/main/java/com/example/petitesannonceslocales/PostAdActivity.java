package com.example.petitesannonceslocales;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.petitesannonceslocales.utils.SessionManager;
import com.example.petitesannonceslocales.utils.User;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
public class PostAdActivity extends AppCompatActivity {

    private Spinner spinnerCategory;
    private EditText editTextTitle, editTextDescription, editTextContactInfo, editTextImageUrl, editTextPrice;
    private Button buttonSubmitAd;
    private ImageView imageViewPreview;
    private RadioGroup radioGroupContact;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_ad);

        // Initialize UI components
        spinnerCategory = findViewById(R.id.spinnerCategory);
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDescription = findViewById(R.id.editTextDescription);
        editTextPrice = findViewById(R.id.editTextPrice);
        editTextImageUrl = findViewById(R.id.editTextImageUrl); // New input for image URL
        imageViewPreview = findViewById(R.id.imageViewPreview);
        radioGroupContact = findViewById(R.id.radioGroupContact);
        editTextContactInfo = findViewById(R.id.contactValue);
        buttonSubmitAd = findViewById(R.id.buttonSubmitAd);

        // Populate Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.categories_array, // Define this array in res/values/strings.xml
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        // Add a listener to the image URL field to update the preview
        editTextImageUrl.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) { // When the user finishes editing
                updateImagePreview(editTextImageUrl.getText().toString().trim());
            }
        });

        // Submit Button
        buttonSubmitAd.setOnClickListener(v -> submitAd());

        SessionManager.getInstance().getCurrentUserLiveData().observe(this, user -> {
            if (user != null) {
                currentUser = user;
            } else {
                Toast.makeText(this, "No active user session", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateImagePreview(String imageUrl) {
        if (imageUrl.isEmpty()) {
            imageViewPreview.setVisibility(View.GONE);
        } else {
            imageViewPreview.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(imageUrl) // Use Glide to load the image from the URL
                    .placeholder(R.drawable.placeholder_image) // Optional placeholder
                    .error(R.drawable.placeholder_image) // Optional error image
                    .into(imageViewPreview);
        }
    }

    private void submitAd() {
        String category = spinnerCategory.getSelectedItem().toString();
        String title = editTextTitle.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String contactInfo = editTextContactInfo.getText().toString().trim();
        String imageUrl = editTextImageUrl.getText().toString().trim();
        String price = editTextPrice.getText().toString().trim();

        int selectedContactMethodId = radioGroupContact.getCheckedRadioButtonId();
        String contactMethod = selectedContactMethodId == R.id.radioEmail ? "Email" : "Phone";

        if (title.isEmpty() || price.isEmpty() || description.isEmpty() || contactInfo.isEmpty() || imageUrl.isEmpty()) {
            Log.w("SubmitAd", "Validation failed: One or more fields are empty.");
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save to Firebase Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> ad = new HashMap<>();
        ad.put("userEmail", currentUser.getEmail());
        ad.put("category", category);
        ad.put("title", title);
        ad.put("description", description);
        ad.put("contactMethod", contactMethod);
        ad.put("imageUri", imageUrl); // Save the public URL
        ad.put("contactInfo", contactInfo);
        ad.put("price", price);

        db.collection("ads")
                .add(ad)
                .addOnSuccessListener(documentReference -> {
                    Log.d("SubmitAd", "Ad posted successfully with ID: " + documentReference.getId());
                    Toast.makeText(this, "Ad posted successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("SubmitAd", "Error posting ad: " + e.getMessage(), e);
                    Toast.makeText(this, "Error posting ad", Toast.LENGTH_SHORT).show();
                });
    }
}


