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

import com.example.a5_sample.JournalEntry;
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
    private EditText journalEntry;
    private String AIResponse;
    private LocalDate getDate;
    private TextView date;
    private ImageButton save;
    private ImageButton send;
    private ImageButton mailbox;
    private boolean emptyMailbox;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentJournalEntryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //assign journal entry, send button, mail button, date
        journalEntry = binding.journalEntryText;
        save = binding.saveButton;
        send = binding.sendButton;
        mailbox = binding.mailButton;
        date = binding.dateText;
        getDate = LocalDate.now();
        String dateText = getDate.toString();
        date.setText(dateText);

        //initialize firebase
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

        //initialize database with default values
        if (userRef.child(""+dateText+"") == null) {
            Map<String, Object> updates = new HashMap<>();
            updates.put(""+dateText+"", new JournalEntry(null, "No response yet", true));
            userRef.updateChildren(updates);
        }

        //retrieve last journal entry, AI response, mailbox status from database
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild(""+dateText+"")) {
                        String temp1 = dataSnapshot.child(""+dateText+"").child("journalEntryText").getValue(String.class);
                        String temp2 = dataSnapshot.child(""+dateText+"").child("AIResponse").getValue(String.class);
                        boolean temp3 = dataSnapshot.child(""+dateText+"").child("mailboxStatus").getValue(boolean.class);
                        journalEntry.setText(temp1);
                        AIResponse = temp2;
                        emptyMailbox = temp3;
                        if (temp3 == true) {
                            mailbox.setImageResource(R.drawable.mail_icon);
                        } else if (temp3 == false){
                            mailbox.setImageResource(R.drawable.new_mail_icon);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
            }
        });

        //write journal entry and AI response to database upon pressing save
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> updates = new HashMap<>();
                updates.put(""+dateText+"", new JournalEntry(journalEntry.getText().toString(), AIResponse, emptyMailbox));
                userRef.updateChildren(updates);
            }
        });

        //when user sends journal entry, trigger new AI message
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mailbox.setImageResource(R.drawable.new_mail_icon);
                emptyMailbox = false;
                AIResponse = "HI";
                Map<String, Object> updates = new HashMap<>();
                updates.put(""+dateText+"", new JournalEntry(journalEntry.getText().toString(), AIResponse, emptyMailbox));
                userRef.updateChildren(updates);
            }
        });

        //when the user opens the mailbox, empty it and show AI message
        mailbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mailbox.setImageResource(R.drawable.mail_icon);
                emptyMailbox = true;
                Map<String, Object> updates = new HashMap<>();
                updates.put(""+dateText+"", new JournalEntry(journalEntry.getText().toString(), AIResponse, emptyMailbox));
                userRef.updateChildren(updates);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(AIResponse).setTitle(R.string.ai_response_title);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}