package com.example.a5_sample.ui.profile;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
// import androidx.lifecycle.ViewModelProvider;

import com.example.a5_sample.R;
import com.example.a5_sample.databinding.FragmentProfileBinding;
// import com.example.a5_sample.ui.profile.ProfileViewModel;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private Button btn;
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
        btn = binding.save;
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

        Context context = getActivity().getApplicationContext();
        myPrefs = context.getSharedPreferences(getString(R.string.storage), Context.MODE_PRIVATE);
        student.setText(myPrefs.getString("MY_name", null));
        majors.setText(myPrefs.getString("MY_majors", null));
        year.setText(myPrefs.getString("MY_year", null));
        age.setText(myPrefs.getString("MY_age", null));

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor peditor = myPrefs.edit();
                peditor.putString("MY_name", String.valueOf(student.getText()));
                peditor.putString("MY_majors", String.valueOf(majors.getText()));
                peditor.putString("MY_year", String.valueOf(year.getText()));
                peditor.putString("MY_age", String.valueOf(age.getText()));
                peditor.apply();
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