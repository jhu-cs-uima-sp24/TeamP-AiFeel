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
// import androidx.lifecycle.ViewModelProvider;

import com.example.a5_sample.EditProfile;
import com.example.a5_sample.MainActivity;
import com.example.a5_sample.R;
import com.example.a5_sample.databinding.FragmentProfileBinding;
// import com.example.a5_sample.ui.profile.ProfileViewModel;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private Button editProfile;
    private Button editPal;
    private TextView email;

    private TextView age;
    private ImageView userProfile;
    private ImageView palProfile;

    private TextView name;
    private TextView gender;
    private TextView notifications;
    private TextView palName;
    private TextView palAge;
    private TextView palGender;



    private SharedPreferences myPrefs;

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
        name = binding.nameDisplay;
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

        Context context = getActivity().getApplicationContext();
        myPrefs = context.getSharedPreferences(getString(R.string.storage), Context.MODE_PRIVATE);

        // TODO: implement text initialization using firebase


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

            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}