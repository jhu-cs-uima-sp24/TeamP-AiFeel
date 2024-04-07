package com.example.a5_sample;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.graphics.Color;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

public class EditProfile extends AppCompatActivity {
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
                // Perform save operation here if needed
                // For now, just finish the current activity to go back to the profile
                finish();
            }
        });


        gender = (Spinner) findViewById(R.id.genderSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.gender_array,
                android.R.layout.simple_spinner_item
        );
        // Specify the layout to use when the list of choices appears.
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner.
        gender.setAdapter(adapter);

        notification = (Spinner) findViewById(R.id.notificationSpinner);
        ArrayAdapter<CharSequence> ntfAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.notification_array,
                android.R.layout.simple_spinner_item
        );
        // Specify the layout to use when the list of choices appears.
        ntfAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner.
        notification.setAdapter(ntfAdapter);



    }
}
