
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
        setContentView(R.layout.activity_login);

        Context context = getApplicationContext();
        myPrefs = context.getSharedPreferences(getString(R.string.storage), Context.MODE_PRIVATE);

        EditText student_name = findViewById(R.id.new_name);
        student_name.setText(myPrefs.getString("MY_name", null));

        ImageButton login_button = (ImageButton) findViewById(R.id.login_btn);
        login_button.setOnClickListener(new View.OnClickListener() {
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

        Button signup_button = (Button) findViewById(R.id.sign_up);
        signup_button.setOnClickListener(new View.OnClickListener() {
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