
package com.example.a5_sample;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class CreateProfileActivity extends AppCompatActivity {
    private SharedPreferences myPrefs;
    private DatabaseReference dbref;
    private static final int PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 100;
    private static final int PICK_IMAGE_REQUEST = 101;
    FirebaseAuth mAuth;

    ImageButton btnPickImage;
    ActivityResultLauncher<Intent> resultLauncher;
    ImageView profileImg;
    Uri imageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_2);

        dbref = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        String receivedEmail = getIntent().getStringExtra("email");
        EditText email_text = findViewById(R.id.email_text);
        email_text.setText(receivedEmail);
        EditText name_text = findViewById(R.id.name_text);
        EditText password_text = findViewById(R.id.password_text);
        password_text.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        EditText retype_password_text = findViewById(R.id.password_text2);
        retype_password_text.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        btnPickImage = findViewById(R.id.edit_profile_picture_button);
        profileImg = findViewById(R.id.profile_picture);

        btnPickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(CreateProfileActivity.this)
                        .crop(1f, 1f)	    			//crop image
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }
        });

        Button signup_Button = (Button) findViewById(R.id.sign_up);
        signup_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = email_text.getText().toString();
                String name =  name_text.getText().toString();
                String password = password_text.getText().toString();
                String re_password = retype_password_text.getText().toString();

                if(TextUtils.isEmpty(email) || TextUtils.isEmpty(name) ||TextUtils.isEmpty(password) || TextUtils.isEmpty(re_password)){
                    LayoutInflater inflater = getLayoutInflater();
                    View customToastLayout = inflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.root_layout));
                    Toast mToast = new Toast(getApplicationContext());

                    TextView txtMessage = customToastLayout.findViewById(R.id.txt_message);
                    txtMessage.setText("Please fill out all fields");

                    mToast.setDuration(Toast.LENGTH_SHORT);
                    mToast.setView(customToastLayout);
                    mToast.setGravity(Gravity.BOTTOM, 0, 50);
                    mToast.show();
                    return;
                } else {
                    if(password.equals(re_password)){
                        mAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(CreateProfileActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // sign in success,
                                            Log.d(TAG, "createUserWithEmail:success");
                                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                            String userId = firebaseUser.getUid();
                                            Map<String, Object> userMap = new HashMap<>();
                                            userMap.put("name", name);
                                            userMap.put("email", email);
                                            if (imageUri != null) {
                                                userMap.put("profile_picture", imageUri.toString());
                                            }
                                            DatabaseReference usersRef = dbref.child("users").child(userId);
                                            usersRef.setValue(userMap)
                                                    .addOnSuccessListener(aVoid -> {
                                                        Log.d(TAG, "User data saved successfully.");
                                                        Intent intent = new Intent(CreateProfileActivity.this, MainActivity.class);
                                                        startActivity(intent);
                                                        finish();
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        Log.w(TAG, "User data save failed.", e);
                                                        Toast.makeText(CreateProfileActivity.this, "Failed to save user data.", Toast.LENGTH_SHORT).show();
                                                    });
                                            //User user = new User(name, email);
//                                            if(imageUri != null){
//                                                user.setImageURI(imageUri.toString());
//                                            }
//                                            DatabaseReference usersRef = dbref.child("users");
//                                            if (firebaseUser != null) {
//                                                user.setUid(firebaseUser.getUid()); //set user field
//                                                usersRef.child(firebaseUser.getUid()).setValue(user)
//                                                        .addOnSuccessListener(aVoid -> {
//                                                            Log.d(TAG, "User data saved successfully.");
//                                                            Intent intent = new Intent(CreateProfileActivity.this, MainActivity.class);
//                                                            startActivity(intent);
//                                                            finish();
//                                                        })
//                                                        .addOnFailureListener(e -> {
//                                                            Log.w(TAG, "User data save failed.", e);
//                                                            Toast.makeText(CreateProfileActivity.this, "Failed to save user data.", Toast.LENGTH_SHORT).show();
//                                                        });
//
//
//                                            }
                                        } else {
                                            if(password.length() < 6){
                                                LayoutInflater inflater = getLayoutInflater();
                                                View customToastLayout = inflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.root_layout));
                                                Toast mToast = new Toast(getApplicationContext());

                                                TextView txtMessage = customToastLayout.findViewById(R.id.txt_message);
                                                txtMessage.setText("Password is too short. Please create a password with length > 5");

                                                mToast.setDuration(Toast.LENGTH_SHORT);
                                                mToast.setView(customToastLayout);
                                                mToast.setGravity(Gravity.BOTTOM, 0, 50);
                                                mToast.show();
                                            } else {
                                                // If sign in fails, display a message to the user.
                                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                                LayoutInflater inflater = getLayoutInflater();
                                                View customToastLayout = inflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.root_layout));
                                                Toast mToast = new Toast(getApplicationContext());

                                                TextView txtMessage = customToastLayout.findViewById(R.id.txt_message);
                                                txtMessage.setText("Error with connecting to app authentication system, please try again later");

                                                mToast.setDuration(Toast.LENGTH_SHORT);
                                                mToast.setView(customToastLayout);
                                                mToast.setGravity(Gravity.BOTTOM, 0, 50);
                                                mToast.show();
                                            }
                                        }
                                    }
                                });
                    } else {

                        LayoutInflater inflater = getLayoutInflater();
                        View customToastLayout = inflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.root_layout));
                        Toast mToast = new Toast(getApplicationContext());

                        TextView txtMessage = customToastLayout.findViewById(R.id.txt_message);
                        txtMessage.setText("Passwords do not match");

                        mToast.setDuration(Toast.LENGTH_SHORT);
                        mToast.setView(customToastLayout);
                        mToast.setGravity(Gravity.BOTTOM, 0, 50);
                        mToast.show();
                    }

                }
            }
        });


        Button signin_button = (Button) findViewById(R.id.sign_in);
        signin_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //go back to sign in page
                Intent intent = new Intent(CreateProfileActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imageUri = data.getData();
        profileImg.setImageURI(imageUri);
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