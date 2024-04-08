package com.example.a5_sample.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.a5_sample.EditPal;
import com.example.a5_sample.EditProfile;
import com.example.a5_sample.MainActivity;
import com.example.a5_sample.R;
import com.example.a5_sample.databinding.FragmentProfileBinding;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private DatabaseReference databaseReference;
    private Button editProfile;
    private Button editPal;
    private TextView email;

    private TextView age;
    private ImageView userProfile;
    private ImageView palProfile;

    private TextView nameView;
    private TextView gender;
    private TextView notifications;
    private TextView palName;
    private TextView palAge;
    private TextView palGender;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // eliminated for simplicity
  //      ProfileViewModel dashboardViewModel =
  //              new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // user profile
        userProfile = binding.profilePicture;
        editProfile = binding.editProfileButton;
        nameView = binding.nameDisplay;
        email = binding.emailDisplay;
        gender = binding.genderDisplay;
        age = binding.ageDisplay;
        notifications = binding.notificationsDisplay;

        // AI profile
        palProfile = binding.chatbotPicture;
        palName = binding.nameChatDisplay;
        palAge = binding.ageChatDisplay;
        palGender = binding.genderChatDisplay;
        editPal = binding.editPalButton;

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
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent launch = new Intent(myact, EditProfile.class);
                startActivity(launch);
            }
        });

        editPal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launch = new Intent(myact, EditPal.class);
                startActivity(launch);
            }
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

        // Add a ValueEventListener to retrieve the "name" field
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("name").getValue(String.class);
                    if (name != null) {
                        // Set the retrieved name to the TextView
                        nameView.setText(name);
                    }
                    Integer ageNum = dataSnapshot.child("age").getValue(Integer.class);
                    if (ageNum != null) {
                        age.setText(String.valueOf(ageNum));
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

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
            }
        });
    }
}