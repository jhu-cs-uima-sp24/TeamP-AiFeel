package com.example.a5_sample.ui.profile;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
// import androidx.lifecycle.ViewModelProvider;

import com.example.a5_sample.R;
import com.example.a5_sample.databinding.FragmentProfileBinding;
// import com.example.a5_sample.ui.profile.ProfileViewModel;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private Button btn;
    private EditText student;
    private EditText majors;
    private EditText year;
    private EditText age;
    private SharedPreferences myPrefs;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // eliminated for simplicity
  //      ProfileViewModel dashboardViewModel =
  //              new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        btn = binding.save;
        student = binding.editTextStudent;
        majors = binding.editTextMajors;
        year = binding.editTextYear;
        age = binding.editTextAge;

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