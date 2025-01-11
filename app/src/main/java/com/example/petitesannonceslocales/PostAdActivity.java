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

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class PostAdActivity extends AppCompatActivity {

    private Spinner spinnerCategory;
    private EditText editTextTitle, editTextDescription, editTextContactInfo;
    private Button buttonUploadImage, buttonSubmitAd;
    private ImageView imageViewPreview;
    private RadioGroup radioGroupContact;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_ad);

        // Initialize UI components
        spinnerCategory = findViewById(R.id.spinnerCategory);
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDescription = findViewById(R.id.editTextDescription);
        buttonUploadImage = findViewById(R.id.buttonUploadImage);
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

        // Image Upload
        buttonUploadImage.setOnClickListener(v -> openImagePicker());

        // Submit Button
        buttonSubmitAd.setOnClickListener(v -> submitAd());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            imageViewPreview.setImageURI(selectedImageUri);
            imageViewPreview.setVisibility(View.VISIBLE);
        }
    }

    private void submitAd() {
        String category = spinnerCategory.getSelectedItem().toString();
        String title = editTextTitle.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String contactInfo = editTextContactInfo.getText().toString().trim();

        int selectedContactMethodId = radioGroupContact.getCheckedRadioButtonId();
        String contactMethod = selectedContactMethodId == R.id.radioEmail ? "Email" : "Phone";

        if (title.isEmpty() || description.isEmpty() || contactInfo.isEmpty()) {
            Log.w("SubmitAd", "Validation failed: One or more fields are empty.");
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save to Firebase Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> ad = new HashMap<>();
        ad.put("category", category);
        ad.put("title", title);
        ad.put("description", description);
        ad.put("contactMethod", contactMethod);
        ad.put("imageUri", selectedImageUri != null ? selectedImageUri.toString() : "No Image");
        ad.put("contactInfo", contactInfo);


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

