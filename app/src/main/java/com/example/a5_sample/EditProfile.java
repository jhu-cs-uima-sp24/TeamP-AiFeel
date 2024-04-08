package com.example.a5_sample;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.graphics.Color;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EditProfile extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private Button save;
    private Button back;
    private EditText name;
    private EditText age;
    private Spinner gender;
    private Spinner notification;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        back = findViewById(R.id.quit_btn);
        save = findViewById(R.id.save_btn);
        name = findViewById(R.id.nameDisplay);
        age = findViewById(R.id.ageDisplay);

        // Initialize database reference
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users");

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Finish the current activity to go back to the profile
                finish();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = name.getText().toString().trim();
                int userAge = Integer.parseInt(age.getText().toString().trim());
                String userGender = gender.getSelectedItem().toString();
                String userNotification = notification.getSelectedItem().toString();

                Map<String, Object> updates = new HashMap<>();
                updates.put("name", userName);
                updates.put("age", userAge);
                updates.put("gender", userGender);
                updates.put("notification", userNotification);

                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

                userRef.updateChildren(updates)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(EditProfile.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(EditProfile.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                        });

                finish();
            }
        });


        gender = findViewById(R.id.genderSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.gender_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender.setAdapter(adapter);

        notification = findViewById(R.id.notificationSpinner);
        ArrayAdapter<CharSequence> ntfAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.notification_array,
                android.R.layout.simple_spinner_item
        );
        ntfAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        notification.setAdapter(ntfAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        databaseReference.child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String nameVal = dataSnapshot.child("name").getValue(String.class);
                            if (nameVal != null) {
                                name.setText(nameVal);
                            }
                            Integer ageNum = dataSnapshot.child("age").getValue(Integer.class);
                            if (ageNum != null) {
                                age.setText(String.valueOf(ageNum));
                            }

                            if (dataSnapshot.hasChild("gender")) {
                                String genderVal = dataSnapshot.child("gender").getValue(String.class);
                                if (genderVal != null) {
                                    ArrayAdapter<CharSequence> genderAdapter = (ArrayAdapter<CharSequence>) gender.getAdapter();
                                    int genderIndex = genderAdapter.getPosition(genderVal);
                                    if (genderIndex != -1) {
                                        gender.setSelection(genderIndex);
                                    }
                                }
                            }

                            if (dataSnapshot.hasChild("notification")) {
                                String notificationVal = dataSnapshot.child("notification").getValue(String.class);
                                if (notificationVal != null) {
                                    ArrayAdapter<CharSequence> notificationAdapter = (ArrayAdapter<CharSequence>) notification.getAdapter();
                                    int notificationIndex = notificationAdapter.getPosition(notificationVal);
                                    if (notificationIndex != -1) {
                                        notification.setSelection(notificationIndex);
                                    }
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

