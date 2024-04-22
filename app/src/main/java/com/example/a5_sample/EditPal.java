package com.example.a5_sample;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class EditPal extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private PersonalityAdapter adapter;
    private SelectedPersonalityAdapter selectedAdapter; // Adapter for selected personas
    private List<String> allPersonalities;
    private List<String> personas; // List to store selected personalities
    private Spinner gender;
    private EditText name;
    private EditText age;
    private boolean hasPopulated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_pal);

        hasPopulated = false;
        Button quit = findViewById(R.id.quit_btn);
        Button save = findViewById(R.id.save_btn);
        EditText search = findViewById(R.id.searchPersonas);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        // RecyclerView for selected personas
        RecyclerView selectedRecyclerView = findViewById(R.id.selectedRecyclerView);
        name = findViewById(R.id.nameDisplay);
        age = findViewById(R.id.ageDisplay);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("users");

        quit.setOnClickListener(view -> finish());

        save.setOnClickListener(view -> {
            String palName = name.getText().toString().trim();

            String palGender = gender.getSelectedItem().toString();
            String palAge = age.getText().toString().trim();

            // Create a map to store profile information and personas
            Map<String, Object> updates = new HashMap<>();

            updates.put("palAge", palAge);
            updates.put("palName", palName);
            updates.put("palGender", palGender);

            // Convert list of personas to a comma-separated string
            String personasString = TextUtils.join(",", personas);
            updates.put("personas", personasString);

            // Get user ID
            String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

            // Update profile information in Firebase
            userRef.updateChildren(updates)
                    .addOnSuccessListener(aVoid -> Toast.makeText(EditPal.this, "Profile updated successfully", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(EditPal.this, "Failed to update profile", Toast.LENGTH_SHORT).show());

            finish();
        });

        gender = findViewById(R.id.genderSpinner);
        ArrayAdapter<CharSequence> genAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.gender_array,
                android.R.layout.simple_spinner_item
        );
        genAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender.setAdapter(genAdapter);

        allPersonalities = loadAllPersonalities();

        personas = new ArrayList<>(); // Initialize personas list
        // Set up RecyclerView with all personalities
        adapter = new PersonalityAdapter(allPersonalities, personality -> {
            if (!personas.contains(personality) && personas.size() < 3) { // Check for duplicates and maximum limit
                // Add the clicked personality to personas
                personas.add(personality);
                // Update the selected RecyclerView
                selectedAdapter.setPersonalities(personas);
            } else {
                // Display a Toast message indicating the reason for failure
                if (personas.contains(personality)) {
                    Toast.makeText(EditPal.this, "This personality is already selected", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(EditPal.this, "You can select up to 3 personalities", Toast.LENGTH_SHORT).show();
                }
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.setPersonalities(new ArrayList<>());

        // Initialize and set up the RecyclerView for selected personas
        selectedAdapter = new SelectedPersonalityAdapter(new ArrayList<>());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL); // Set orientation to horizontal
        selectedRecyclerView.setLayoutManager(layoutManager);
        selectedRecyclerView.setAdapter(selectedAdapter);

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String query = charSequence.toString().toLowerCase();
                if (query.isEmpty()) {
                    adapter.setPersonalities(new ArrayList<>()); // Clear the RecyclerView
                } else {
                    List<String> filteredPersonalities = filter(allPersonalities, query);
                    adapter.setPersonalities(filteredPersonalities);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }


    private List<String> filter(List<String> personalities, String query) {
        if (query.isEmpty()) {
            return new ArrayList<>(); // Return an empty list if the query is empty
        }
        List<String> filteredList = new ArrayList<>();
        for (String personality : personalities) {
            if (personality.toLowerCase().contains(query)) {
                filteredList.add(personality);
            }
        }
        return filteredList;
    }



    private List<String> loadAllPersonalities() {
        List<String> personalities = new ArrayList<>();

        // Manually add personalities
        personalities.add("Adventurous");
        personalities.add("Analytical");
        personalities.add("Artistic");
        personalities.add("Assertive");
        personalities.add("Compassionate");
        personalities.add("Confident");
        personalities.add("Creative");
        personalities.add("Curious");
        personalities.add("Determined");
        personalities.add("Empathetic");
        personalities.add("Enthusiastic");
        personalities.add("Friendly");
        personalities.add("Funny");
        personalities.add("Helpful");
        personalities.add("Honest");
        personalities.add("Independent");
        personalities.add("Innovative");
        personalities.add("Intelligent");
        personalities.add("Optimistic");
        personalities.add("Patient");
        personalities.add("Persistent");
        personalities.add("Practical");
        personalities.add("Reliable");
        personalities.add("Sociable");
        personalities.add("Serious");
        personalities.add("Supportive");
        personalities.add("Thoughtful");
        personalities.add("Tolerant");

        return personalities;
    }

    @Override
    public void onStart() {
        super.onStart();
        databaseReference.child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild("palName")) {
                        String nameVal = dataSnapshot.child("palName").getValue(String.class);
                        if (nameVal != null) {
                            name.setText(nameVal);
                        }
                    }
                    if (dataSnapshot.hasChild("palAge")) {
                        String ageNum = dataSnapshot.child("palAge").getValue(String.class);
                        if (ageNum != null) {
                            age.setText(ageNum);
                        }
                    }

                    if (dataSnapshot.hasChild("palGender")) {
                        String genderVal = dataSnapshot.child("palGender").getValue(String.class);
                        if (genderVal != null) {
                            ArrayAdapter<CharSequence> genderAdapter = (ArrayAdapter<CharSequence>) gender.getAdapter();
                            int genderIndex = genderAdapter.getPosition(genderVal);
                            if (genderIndex != -1) {
                                gender.setSelection(genderIndex);
                            }
                        }
                    }

                    if (dataSnapshot.exists() && dataSnapshot.hasChild("personas")) {
                        String personasString = dataSnapshot.child("personas").getValue(String.class);
                        if (!hasPopulated && personasString != null && !personasString.isEmpty()) {
                            personas.addAll(Arrays.asList(personasString.split(",")));
                            selectedAdapter.setPersonalities(personas); // Prepopulate the adapter with user's personas
                            hasPopulated = true;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
            }
        });
    }
}
