package com.example.a5_sample;

import static android.content.ContentValues.TAG;

import android.content.Context;
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

import java.util.List;

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
                    LayoutInflater inflater = getLayoutInflater();
                    View customToastLayout = inflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.root_layout));
                    Toast mToast = new Toast(getApplicationContext());

                    TextView txtMessage = customToastLayout.findViewById(R.id.txt_message);
                    txtMessage.setText("Please enter your email");

                    mToast.setDuration(Toast.LENGTH_SHORT);
                    mToast.setView(customToastLayout);
                    mToast.setGravity(Gravity.BOTTOM, 0, 50);
                    mToast.show();
                    return;
                }
                if (password == null || password.isEmpty()) {
                    LayoutInflater inflater = getLayoutInflater();
                    View customToastLayout = inflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.root_layout));
                    Toast mToast = new Toast(getApplicationContext());

                    TextView txtMessage = customToastLayout.findViewById(R.id.txt_message);
                    txtMessage.setText("Please enter your password");

                    mToast.setDuration(Toast.LENGTH_SHORT);
                    mToast.setView(customToastLayout);
                    mToast.setGravity(Gravity.BOTTOM, 0, 50);
                    mToast.show();
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
                                    LayoutInflater inflater = getLayoutInflater();
                                    View customToastLayout = inflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.root_layout));
                                    Toast mToast = new Toast(getApplicationContext());

                                    TextView txtMessage = customToastLayout.findViewById(R.id.txt_message);
                                    txtMessage.setText("Wrong email/password, or no account associated with the email");

                                    mToast.setDuration(Toast.LENGTH_LONG);
                                    mToast.setView(customToastLayout);
                                    mToast.setGravity(Gravity.BOTTOM, 0, 50);
                                    mToast.show();
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

        Button forgot_button = (Button) findViewById(R.id.forgot_password);
        forgot_button.setOnClickListener(new View.OnClickListener() {
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
                                txtMessage.setText("Reset password link is sent.");

                                mToast.setDuration(Toast.LENGTH_SHORT);
                                mToast.setView(customToastLayout);
                                mToast.setGravity(Gravity.BOTTOM, 0, 50);
                                mToast.show();
                            } else {
                                Log.d(TAG, "no email");
                                LayoutInflater inflater = getLayoutInflater();
                                View customToastLayout = inflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.root_layout));
                                Toast mToast = new Toast(getApplicationContext());

                                TextView txtMessage = customToastLayout.findViewById(R.id.txt_message);
                                txtMessage.setText("This email is not registered with us.");

                                mToast.setDuration(Toast.LENGTH_SHORT);
                                mToast.setView(customToastLayout);
                                mToast.setGravity(Gravity.BOTTOM, 0, 50);
                                mToast.show();
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

