package com.example.a5_sample.ui.oldJournalEntry;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a5_sample.MainActivity;
import com.example.a5_sample.PersonaAdapter;
import com.example.a5_sample.R;
import com.example.a5_sample.databinding.OldFragmentJournalEntryBinding;
import com.example.a5_sample.ui.home.HomeFragment;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class OldJournalEntryFragment extends Fragment {

    private OldFragmentJournalEntryBinding binding;
    private DatabaseReference databaseReference;
    private TextView journalEntry;
    private String AIResponse;
    private TextView date;
    private ImageButton send;
    private ImageButton mailbox;
    private ImageButton back;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = OldFragmentJournalEntryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //assign journal entry, disabled send button, mail button, back arrow, date
        journalEntry = binding.journalEntryText;
        send = binding.disabledSendButton;
        mailbox = binding.mailButton;
        back = binding.backArrow;
        date = binding.dateText;

        //write date to journal
        Bundle bundle = this.getArguments();
        String dateText = bundle.getString("date", "");
        date.setText(dateText);

        //initialize firebase and get user ID
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

        //read journal entry for the given date from database and write to screen
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild(""+dateText+"")) {
                        String temp1 = dataSnapshot.child(""+dateText+"").child("journalEntryText").getValue(String.class);
                        String temp2 = dataSnapshot.child(""+dateText+"").child("AIResponse").getValue(String.class);
                        journalEntry.setText(temp1);
                        AIResponse = temp2;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
            }
        });

        //when the user opens the mailbox, show AI message
        mailbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(AIResponse).setTitle(R.string.ai_response_title);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        //return to calendar view
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new HomeFragment();
                FragmentManager fm = getParentFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                transaction.replace(R.id.fragment_home, fragment);
                transaction.commit();
            }
        });

        //disable sending for old entries
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),"Cannot send old journal entries",Toast.LENGTH_SHORT).show();            }
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