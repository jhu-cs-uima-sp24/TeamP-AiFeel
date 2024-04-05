
package com.example.a5_sample;

        import android.content.Context;
        import android.content.SharedPreferences;
        import android.os.Bundle;
        import android.view.View;
        import android.widget.Button;
        import android.content.Intent;
        import android.widget.EditText;
        import android.widget.ImageButton;
        import android.widget.TextView;


        import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {
    private SharedPreferences myPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

//        Context context = getApplicationContext();
//        myPrefs = context.getSharedPreferences(getString(R.string.storage), Context.MODE_PRIVATE);
//
        String receivedEmail = getIntent().getStringExtra("email");
        EditText email_text = findViewById(R.id.email_text);
        email_text.setText(receivedEmail);

        ImageButton search_button = (ImageButton) findViewById(R.id.email_lookup);
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){

                //check if email + password combination makesense
//                SharedPreferences.Editor editor = myPrefs.edit();
//                String myName = myPrefs.getString("MY_name", null);
//                if (myName == null || myName.isEmpty()) { // add student name to profile, the first time
//                    editor.putString("MY_name",student_name.getText().toString());
//                    editor.apply();
//                }
                Intent intent = new Intent(SignupActivity.this, MainActivity.class); //login successful, go to main page
                startActivity(intent);
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