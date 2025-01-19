package com.example.petitesannonceslocales;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.petitesannonceslocales.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditAdActivity extends AppCompatActivity {

    private EditText editTextTitle, editTextDescription, editTextContactMethod, editTextContactInfo, editTextImageUrl;
    private Spinner spinnerCategory;
    private Button buttonSaveChanges;
    private String adId;
    private ImageView imageViewPreview;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_ad);

        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDescription = findViewById(R.id.editTextDescription);
        editTextContactMethod = findViewById(R.id.editTextContactMethod);
        editTextContactInfo = findViewById(R.id.editTextContactInfo);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        buttonSaveChanges = findViewById(R.id.buttonSaveChanges);
        editTextImageUrl = findViewById(R.id.editTextImageUrl); // New input for image URL
        imageViewPreview = findViewById(R.id.imageViewPreview);


        // Get ad data from intent
        adId = getIntent().getStringExtra("ad_id");
        String title = getIntent().getStringExtra("title");
        String description = getIntent().getStringExtra("description");
        String category = getIntent().getStringExtra("category");
        String contactMethod = getIntent().getStringExtra("contactMethod");
        String contactInfo = getIntent().getStringExtra("contactInfo");

        // Populate fields
        editTextTitle.setText(title);
        editTextDescription.setText(description);
        editTextContactMethod.setText(contactMethod);
        editTextContactInfo.setText(contactInfo);
        // Add a listener to the image URL field to update the preview
        editTextImageUrl.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) { // When the user finishes editing
                updateImagePreview(editTextImageUrl.getText().toString().trim());
            }
        });
        // TODO: Set spinnerCategory to the correct value
        // Populate Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.categories_array, // Define this array in res/values/strings.xml
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
        int spinnerPosition = adapter.getPosition(category);
        spinnerCategory.setSelection(spinnerPosition);


        buttonSaveChanges.setOnClickListener(v -> {
            // Update ad in Firestore
            FirebaseFirestore.getInstance().collection("ads")
                    .document(adId)
                    .update(
                            "title", editTextTitle.getText().toString(),
                            "description", editTextDescription.getText().toString(),
                            "category", spinnerCategory.getSelectedItem().toString(),
                            "contactMethod", editTextContactMethod.getText().toString(),
                            "contactInfo", editTextContactInfo.getText().toString(),
                            "imageUri", editTextImageUrl.getText().toString(),
                            "price", editTextImageUrl.getText().toString()

                    )
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(EditAdActivity.this, "Ad updated successfully.", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(EditAdActivity.this, "Failed to update ad.", Toast.LENGTH_SHORT).show());
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
}
