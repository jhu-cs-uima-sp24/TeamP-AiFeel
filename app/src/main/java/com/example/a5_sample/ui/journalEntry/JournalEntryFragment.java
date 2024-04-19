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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class JournalEntryFragment extends Fragment {

    private FragmentJournalEntryBinding binding;
    private DatabaseReference databaseReference;
    private EditText journalEntry;
    private String journalEntryText;
    private String AIResponse;
    private LocalDate getDate;
    private TextView date;
    private ImageButton send;
    private ImageButton mailbox;
    private boolean emptyMailbox;
    private SharedPreferences myPrefs;

    public JournalEntryFragment(String journalEntryText, String AIResponse) {
        this.journalEntryText = journalEntryText;
        this.AIResponse = AIResponse;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentJournalEntryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //assign journal entry, send button, mail button, date
        journalEntry = binding.journalEntryText;
        send = binding.sendButton;
        mailbox = binding.mailButton;
        date = binding.dateText;
        getDate = LocalDate.now();
        String dateText = getDate.toString();
        date.setText(dateText);

        //retrieve previous journal entry and mailbox status and set
        Context context = getActivity().getApplicationContext();
        myPrefs = context.getSharedPreferences(getString(R.string.storage), Context.MODE_PRIVATE);
        String temp1 = myPrefs.getString("journalEntry", "");
        String temp2 = myPrefs.getString("mailboxStatus", "");
        journalEntry.setText(temp1);
        if (temp2 == "true") {
            mailbox.setImageResource(R.drawable.mail_icon);
        } else if (temp2 == "false"){
            mailbox.setImageResource(R.drawable.new_mail_icon);
        }

        //initialize firebase and get user ID
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        }

        //write journal entry and AI response to database
        Map<String, Object> userJournal = new HashMap<>();
        userJournal.put(""+dateText+"", new JournalEntryFragment(journalEntry.toString(), AIResponse));
        databaseReference.setValue(userJournal);

        //when user sends journal entry, trigger new AI message
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mailbox.setImageResource(R.drawable.new_mail_icon);
                emptyMailbox = false;
            }
        });

        //when the user opens the mailbox, empty it
        mailbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mailbox.setImageResource(R.drawable.mail_icon);
                emptyMailbox = true;
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(R.string.ai_response).setTitle(R.string.ai_response_title);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        return root;
    }

    //retrieve previous journal entry and mailbox status and set
    @Override
    public void onResume() {
        super.onResume();
        Context context = getActivity().getApplicationContext();
        myPrefs = context.getSharedPreferences(getString(R.string.storage), Context.MODE_PRIVATE);
        String temp1 = myPrefs.getString("journalEntry", "");
        String temp2 = myPrefs.getString("mailboxStatus", "");
        journalEntry.setText(temp1);
        if (temp2 == "true") {
            mailbox.setImageResource(R.drawable.mail_icon);
        } else if (temp2 == "false"){
            mailbox.setImageResource(R.drawable.new_mail_icon);
        }
    }

    //save journal entry and mailbox status to memory before closing
    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences.Editor myEdit = myPrefs.edit();
        myEdit.putString("journalEntry", journalEntry.getText().toString());
        myEdit.putString("mailboxStatus", String.valueOf(emptyMailbox));
        myEdit.apply();
    }

    //save journal entry and mailbox status to memory before closing
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        SharedPreferences.Editor myEdit = myPrefs.edit();
        myEdit.putString("journalEntry", journalEntry.getText().toString());
        myEdit.putString("mailboxStatus", String.valueOf(emptyMailbox));
        myEdit.apply();
    }
}