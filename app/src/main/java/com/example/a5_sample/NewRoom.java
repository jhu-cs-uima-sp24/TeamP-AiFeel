package com.example.a5_sample;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class NewRoom extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_room);

        Button sbtn = (Button) findViewById(R.id.save_btn);
        sbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText location = (EditText) findViewById(R.id.new_room);
                String name = location.getText().toString();
                EditText size = (EditText) findViewById(R.id.new_cap);
                String sizeText = size.getText().toString();

                if (!name.isEmpty() && !sizeText.isEmpty()) {
                    int cap = Integer.parseInt(size.getText().toString());
                    StudyRoom newRoom = new StudyRoom(name, cap);

                    MainActivity.rooms.add(newRoom);
                    MainActivity.roomsAdapter.notifyDataSetChanged();

                    finish();

                } else {
                    CharSequence text = "Text Field cannot be empty to save room";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(NewRoom.this, text, duration);
                    toast.show();
                }

            }
        });

        Button qbtn = (Button) findViewById(R.id.quit_btn);
        qbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}