
package com.example.a5_sample;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
        String receivedEmail = getIntent().getStringExtra("email");
        EditText email_text = findViewById(R.id.email_text);
        email_text.setText(receivedEmail);


        Button searchButton =  (Button) findViewById(R.id.sign_up);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = email_text.getText().toString();

                // Check if the email field is not empty
                if (!email.isEmpty()) {
                    mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            List<String> signInMethods = task.getResult().getSignInMethods();
                            if (signInMethods != null && signInMethods.contains("password")) {
                                Log.d(TAG, "reset password");

                                LayoutInflater inflater = getLayoutInflater();
                                View customToastLayout = inflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.root_layout));
                                Toast mToast = new Toast(getApplicationContext());

                                TextView txtMessage = customToastLayout.findViewById(R.id.txt_message);
                                txtMessage.setText("This email already has an account with us. A reset password link is sent.");

                                mToast.setDuration(Toast.LENGTH_SHORT);
                                mToast.setView(customToastLayout);
                                mToast.setGravity(Gravity.BOTTOM, 0, 50);
                                mToast.show();
                            } else {
                                Log.d(TAG, "Navigate to create profile");
                                Intent intent = new Intent(SignupActivity.this, CreateProfileActivity.class); // Or go to a signup page
                                intent.putExtra("email", email);
                                startActivity(intent);
                            }
                        } else {
                            Log.e("EmailCheck", "Failed to check email", task.getException());
                            LayoutInflater inflater = getLayoutInflater();
                            View customToastLayout = inflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.root_layout));
                            Toast mToast = new Toast(getApplicationContext());

                            TextView txtMessage = customToastLayout.findViewById(R.id.txt_message);
                            txtMessage.setText("Failed to check email. Please try again.");

                            mToast.setDuration(Toast.LENGTH_SHORT);
                            mToast.setView(customToastLayout);
                            mToast.setGravity(Gravity.BOTTOM, 0, 50);
                            mToast.show();
                        }
                    });
                } else {
                    // Email field is empty, prompt the user to fill it
                    LayoutInflater inflater = getLayoutInflater();
                    View customToastLayout = inflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.root_layout));
                    Toast mToast = new Toast(getApplicationContext());

                    TextView txtMessage = customToastLayout.findViewById(R.id.txt_message);
                    txtMessage.setText("Please enter your email.");

                    mToast.setDuration(Toast.LENGTH_SHORT);
                    mToast.setView(customToastLayout);
                    mToast.setGravity(Gravity.BOTTOM, 0, 50);
                    mToast.show();
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