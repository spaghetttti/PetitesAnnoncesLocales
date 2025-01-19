package com.example.petitesannonceslocales;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.petitesannonceslocales.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditAdActivity extends AppCompatActivity {

    private EditText editTextTitle, editTextDescription, editTextContactMethod;
    private Spinner spinnerCategory;
    private Button buttonSaveChanges;
    private String adId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_ad);

        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDescription = findViewById(R.id.editTextDescription);
        editTextContactMethod = findViewById(R.id.editTextContactMethod);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        buttonSaveChanges = findViewById(R.id.buttonSaveChanges);

        // Get ad data from intent
        adId = getIntent().getStringExtra("ad_id");
        String title = getIntent().getStringExtra("title");
        String description = getIntent().getStringExtra("description");
        String category = getIntent().getStringExtra("category");
        String contactMethod = getIntent().getStringExtra("contactMethod");

        // Populate fields
        editTextTitle.setText(title);
        editTextDescription.setText(description);
        editTextContactMethod.setText(contactMethod);
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
                            "contactMethod", editTextContactMethod.getText().toString()
                    )
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(EditAdActivity.this, "Ad updated successfully.", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(EditAdActivity.this, "Failed to update ad.", Toast.LENGTH_SHORT).show());
        });
    }
}
