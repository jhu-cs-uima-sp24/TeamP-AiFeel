package com.example.a5_sample;

import android.graphics.PorterDuff;
import android.widget.ArrayAdapter;

import java.util.List;
import android.graphics.Color;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

public class StudyRoomAdapter extends ArrayAdapter<StudyRoom> {
    int resource;
    MainActivity myact;

    String out = getContext().getString(R.string.leave);
    String in = getContext().getString(R.string.enter);
    public StudyRoomAdapter(Context ctx, int res, List<StudyRoom> studyRoomList)
    {
        super(ctx, res, studyRoomList);
        resource = res;
        myact = (MainActivity) ctx;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout itemView;
        StudyRoom rm = getItem(position);

        if (convertView == null) {
            itemView = new LinearLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(inflater);
            vi.inflate(resource, itemView, true);
        } else {
            itemView = (LinearLayout) convertView;
        }

        TextView name = (TextView) itemView.findViewById(R.id.room_location);
        name.setText(rm.getname());
        TextView size = (TextView) itemView.findViewById(R.id.room_size);
        size.setText(rm.getoccupants() + " / " + rm.getcap());
        Button checkin = (Button) itemView.findViewById(R.id.checkin);
        String availText = rm.getchecked_in() ? out : in;
        checkin.setText(availText);

        checkin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(myact.current != null && checkin.getText().equals(in)){
                    CharSequence text = "ERROR: MUST CHECKOUT FIRST";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(myact, text, duration);
                    toast.show();

                } else {
                    if (checkin.getText().equals(out)) { // leaving room
                        checkin.setText(in);
                        rm.setchecked_in(false);
                        size.setText(rm.getoccupants() + " / " + rm.getcap());
                        myact.current = null;
                    } else {
                        if (rm.getavailable() == 0) {
                            CharSequence text = "ERROR: ROOM IS FULL";
                            int duration = Toast.LENGTH_SHORT;

                            Toast toast = Toast.makeText(myact, text, duration);
                            toast.show();
                        } else {
                            rm.setchecked_in(true);
                            checkin.setText(out);
                            size.setText(rm.getoccupants() + " / " + rm.getcap());
                            myact.current = rm;
                        }
                    }
                }

            }
        });

        return itemView;
    }
}


