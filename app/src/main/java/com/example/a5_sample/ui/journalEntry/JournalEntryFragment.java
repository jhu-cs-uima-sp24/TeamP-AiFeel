package com.example.a5_sample.ui.journalEntry;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.a5_sample.MainActivity;
import com.example.a5_sample.R;
import com.example.a5_sample.databinding.FragmentJournalEntryBinding;

public class JournalEntryFragment extends Fragment {

    private FragmentJournalEntryBinding binding;
    private EditText journalEntry;
    private TextView AIResponse;
    private TextView date;
    private TextView mood;
    private boolean sendButtonDisabled;
    private ImageButton send;
    private ImageButton mailbox;
    private SharedPreferences myPrefs;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentJournalEntryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //assign journal entry, send button, mail button
        journalEntry = binding.journalEntryText;
        send = binding.sendButton;
        mailbox = binding.mailButton;

        //when user sends entry, trigger new AI message
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mailbox.setImageResource(R.drawable.new_mail_icon);
            }
        });

        //when the user opens the mailbox, empty it
        mailbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mailbox.setImageResource(R.drawable.mail_icon);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(R.string.ai_response).setTitle(R.string.ai_response_title);
                AlertDialog dialog = builder.create();
                dialog.show();
            }

        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        Context context = getActivity().getApplicationContext();
        myPrefs = context.getSharedPreferences(getString(R.string.storage), Context.MODE_PRIVATE);
        String temp = myPrefs.getString("journalEntry", "");
        journalEntry.setText(temp);
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences.Editor myEdit = myPrefs.edit();
        myEdit.putString("journalEntry", journalEntry.getText().toString());
        myEdit.apply();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}