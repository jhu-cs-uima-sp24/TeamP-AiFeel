
package com.example.a5_sample;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class CreateProfileActivity extends AppCompatActivity {
    private SharedPreferences myPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_2);

//        Context context = getApplicationContext();
//        myPrefs = context.getSharedPreferences(getString(R.string.storage), Context.MODE_PRIVATE);
//
        String receivedEmail = getIntent().getStringExtra("email");
        EditText email_text = findViewById(R.id.email_text);
        email_text.setText(receivedEmail);
        EditText name_text = findViewById(R.id.name_text);
        EditText password_text = findViewById(R.id.password_text);
        EditText retype_password_text = findViewById(R.id.password_text2);


        Button signup_Button = (Button) findViewById(R.id.sign_up);
        signup_Button.setOnClickListener(new View.OnClickListener() {
            String email = email_text.getText().toString();
            String password = password_text.getText().toString();
            @Override
            public void onClick(View v) {
//                FirebaseAuth mAuth = FirebaseAuth.getInstance();
//                mAuth.createUserWithEmailAndPassword(email, password)
//                        .addOnCompleteListener(CreateProfileActivity.this, new OnCompleteListener<AuthResult>() {
//                            @Override
//                            public void onComplete(@NonNull Task<AuthResult> task) {
//                                if (task.isSuccessful()) {
//                                    // Sign in success, update UI with the signed-in user's information
//                                    Log.d(TAG, "createUserWithEmail:success");
//                                    FirebaseUser user = mAuth.getCurrentUser();
//                                    updateUI(user);
//                                } else {
//                                    // If sign in fails, display a message to the user.
//                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
//                                    Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
//                                            Toast.LENGTH_SHORT).show();
//                                    updateUI(null);
//                                }
//                            }
//                        });
                Intent intent = new Intent(CreateProfileActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });


        Button signin_button = (Button) findViewById(R.id.sign_in);
        signin_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //go back to sign in page
                Intent intent = new Intent(CreateProfileActivity.this, LoginActivity.class);
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