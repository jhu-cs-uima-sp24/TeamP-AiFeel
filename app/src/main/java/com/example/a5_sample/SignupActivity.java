
package com.example.a5_sample;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class SignupActivity extends AppCompatActivity {
    private SharedPreferences myPrefs;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
//        Context context = getApplicationContext();
//        myPrefs = context.getSharedPreferences(getString(R.string.storage), Context.MODE_PRIVATE);
//
        String receivedEmail = getIntent().getStringExtra("email");
        EditText email_text = findViewById(R.id.email_text);
        email_text.setText(receivedEmail);


        ImageButton searchButton = findViewById(R.id.email_lookup);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = email_text.getText().toString();

                // Check if the email field is not empty
                if (!email.isEmpty()) {
                    Intent intent = new Intent(SignupActivity.this, CreateProfileActivity.class); // Or go to a signup page
                    intent.putExtra("email", email);
                    startActivity(intent);
//                    mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(task -> {
//                        if (task.isSuccessful()) {
//                            List<String> signInMethods = task.getResult().getSignInMethods();
//                            if (signInMethods != null && signInMethods.contains("password")) {
//                                Toast.makeText(SignupActivity.this, "Reset password link is send to this email.", Toast.LENGTH_LONG).show();
//                            } else {
//                                Log.d(TAG, "Navigate to create profile");
//                                Intent intent = new Intent(SignupActivity.this, CreateProfileActivity.class); // Or go to a signup page
//                                intent.putExtra("email", email);
//                                startActivity(intent);
//                            }
//                        } else {
//                            // Handle error
//                            Log.e("EmailCheck", "Failed to check email", task.getException());
//                            Toast.makeText(SignupActivity.this, "Failed to check email. Please try again.", Toast.LENGTH_SHORT).show();
//                        }
//                    });
                } else {
                    // Email field is empty, prompt the user to fill it
                    Toast.makeText(SignupActivity.this, "Please enter your email.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        Button signin_button = (Button) findViewById(R.id.sign_in);
        signin_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //go back to sign in page
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        // do stuff here
        super.onDestroy();
    }

}