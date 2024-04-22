package com.example.a5_sample.ui.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a5_sample.EditPal;
import com.example.a5_sample.EditProfile;
import com.example.a5_sample.MainActivity;
import com.example.a5_sample.PersonaAdapter;
import com.example.a5_sample.databinding.FragmentProfileBinding;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private DatabaseReference databaseReference;
    private PersonaAdapter personasAdapter;
    private TextView age;
    private ImageView userProfile;
    private ImageView palProfile;
    private TextView nameView;
    private TextView gender;
    private TextView notifications;
    private TextView palName;
    private TextView palAge;
    private TextView palGender;
    private List<String> personasList;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // user profile
        userProfile = binding.profilePicture;
        Button editProfile = binding.editProfileButton;
        nameView = binding.nameDisplay;
        TextView email = binding.emailDisplay;
        gender = binding.genderDisplay;
        age = binding.ageDisplay;
        notifications = binding.notificationsDisplay;

        // AI profile
        palProfile = binding.chatbotPicture;
        palName = binding.nameChatDisplay;
        palAge = binding.ageChatDisplay;
        palGender = binding.genderChatDisplay;
        Button editPal = binding.editPalButton;


        RecyclerView personasRecyclerView = binding.personasRecyclerView;
        personasRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        personasList = new ArrayList<>();
        personasAdapter = new PersonaAdapter(personasList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL); // Set orientation to horizontal
        personasRecyclerView.setLayoutManager(layoutManager);
        personasRecyclerView.setAdapter(personasAdapter);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        // set email
        if (currentUser != null) {
            email.setText(currentUser.getEmail());
            String userId = currentUser.getUid();
            // Now you have the UID of the current user
            databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

        }

        MainActivity myact = (MainActivity) getActivity();
        editProfile.setOnClickListener(view -> {
            Intent launch = new Intent(myact, EditProfile.class);
            startActivity(launch);
        });

        editPal.setOnClickListener(v -> {
            Intent launch = new Intent(myact, EditPal.class);
            startActivity(launch);
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild("name")) {
                        String name = dataSnapshot.child("name").getValue(String.class);
                        if (name != null) {
                            // Set the retrieved name to the TextView
                            nameView.setText(name);
                        }
                    }
                    if (dataSnapshot.hasChild("age")) {
                        String ageNum = dataSnapshot.child("age").getValue(String.class);
                        if (ageNum != null) {
                            age.setText(ageNum);
                        }
                    }
                    if (dataSnapshot.hasChild("profile_picture")) {
                        Uri pic = Uri.parse(dataSnapshot.child("profile_picture").getValue(String.class));
                        if (pic != null) {
                            userProfile.setImageURI(pic);
                        }
                    }
                    if (dataSnapshot.hasChild("pal_picture")) {
                        Uri pic = Uri.parse(dataSnapshot.child("pal_picture").getValue(String.class));
                        if (pic != null) {
                            palProfile.setImageURI(pic);
                        }
                    }
                    if (dataSnapshot.hasChild("gender")) {
                        String genderVal = dataSnapshot.child("gender").getValue(String.class);
                        if (genderVal != null) {
                            gender.setText(genderVal);
                        }
                    }
                    if (dataSnapshot.hasChild("notification")) {
                        String notificationVal = dataSnapshot.child("notification").getValue(String.class);
                        if (notificationVal != null) {
                            notifications.setText(notificationVal);
                        }
                    }
                    if (dataSnapshot.hasChild("palName")) {
                        String nameVal = dataSnapshot.child("palName").getValue(String.class);
                        if (nameVal != null) {
                            palName.setText(nameVal);
                        }
                    }
                    if (dataSnapshot.hasChild("palAge")) {
                        String palAgeNum = dataSnapshot.child("palAge").getValue(String.class);
                        if (palAgeNum != null) {
                            palAge.setText(palAgeNum);
                        }
                    }

                    if (dataSnapshot.hasChild("palGender")) {
                        String genderVal = dataSnapshot.child("palGender").getValue(String.class);
                        if (genderVal != null) {
                            palGender.setText(genderVal);
                        }
                    }
                    // Retrieve personas from Firebase and update RecyclerView
                    if (dataSnapshot.hasChild("personas")) {
                        String personasString = dataSnapshot.child("personas").getValue(String.class);
                        if (personasString != null && !personasString.isEmpty()) {
                            personasList.clear();
                            String[] personasArray = personasString.split(",");
                            personasList.addAll(Arrays.asList(personasArray));
                            personasAdapter.notifyDataSetChanged();
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