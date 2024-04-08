package com.example.a5_sample;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class ErrorPrevention {
    public static void makeToast(Context context, String message) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewGroup rootLayout = ((Activity) context).findViewById(R.id.root_layout);
        View customToastLayout = inflater.inflate(R.layout.custom_toast,  (ViewGroup) rootLayout, false);

//        View customToastLayout = inflater.inflate(R.layout.custom_toast, null, false);

        TextView txtMessage = customToastLayout.findViewById(R.id.txt_message);
        txtMessage.setText(message);

        Toast mToast = new Toast(context.getApplicationContext());
        mToast.setDuration(Toast.LENGTH_LONG);
        mToast.setView(customToastLayout);
        mToast.setGravity(Gravity.BOTTOM, 0, 50);
        mToast.show();
    }
}


