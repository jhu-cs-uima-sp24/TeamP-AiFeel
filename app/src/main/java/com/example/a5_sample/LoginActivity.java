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


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private SharedPreferences myPrefs;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Context context = getApplicationContext();
        myPrefs = context.getSharedPreferences(getString(R.string.storage), Context.MODE_PRIVATE);

        EditText email_text= findViewById(R.id.new_email);
        EditText password_text = findViewById(R.id.password_text);

        ImageButton login_button = (ImageButton) findViewById(R.id.login_btn);
        login_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v){
                String email = email_text.getText().toString();
                String password = password_text.getText().toString();
                //error prevention for empty email or password
                if (email == null || email.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password == null || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                    return;
                }
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() { // Fixed context
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    //correct password and email combo, navigate to main activity
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                    //TO-DO, show customized toaster
                                }
                            }
                        });
            }
        });

        Button signup_button = (Button) findViewById(R.id.sign_up);
        signup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //navigates to signup page
                Log.d(TAG, "Navigate to email search");
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                String email = email_text.getText().toString();
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            //user already signed-in, directly go to to main activity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class); //login successful, go to main page
            startActivity(intent);
        } else {
            // No user is signed in
            // stay in the login screen
        }
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

