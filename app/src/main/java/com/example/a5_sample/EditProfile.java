package com.example.a5_sample;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.a5_sample.databinding.FragmentProfileBinding;

public class EditProfile extends AppCompatActivity {
    private FragmentProfileBinding binding;
    private Button save;
    private Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

    }
}
