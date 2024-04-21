package com.example.a5_sample.ui.journalEntry;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.a5_sample.JournalEntry;
import com.example.a5_sample.R;
import com.example.a5_sample.databinding.FragmentJournalEntryBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class JournalEntryFragment extends Fragment {
    private final String API_KEY = "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";
    private final OkHttpClient client = new OkHttpClient();
    private FragmentJournalEntryBinding binding;
    private EditText journalEntry;
    private String AIResponse;
    private LocalDate getDate;
    private TextView date;
    private ImageButton save;
    private ImageButton send;
    private ImageButton mailbox;
    private boolean emptyMailbox;
    private int mood = 3; //assume neutral moods
    private String dateText;
    private DatabaseReference userRef;
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
        dateText = getDate.toString();
        date.setText(dateText);

        //initialize firebase
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

        //initialize database with default values
        if (userRef.child(""+dateText+"") == null) {
            Map<String, Object> updates = new HashMap<>();
            updates.put(""+dateText+"", new JournalEntry("", "No response yet", true, mood));
            userRef.updateChildren(updates);
        }

        //retrieve last journal entry, AI response, mailbox status from database
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild(""+dateText+"")) {
                        String new_journalentry = dataSnapshot.child(""+dateText+"").child("journalEntryText").getValue(String.class);
                        String new_AIResponse = dataSnapshot.child(""+dateText+"").child("AIResponse").getValue(String.class);
                        boolean is_empty = dataSnapshot.child(""+dateText+"").child("mailboxStatus").getValue(boolean.class);
                        int user_mood = dataSnapshot.child(""+dateText+"").child("mood").getValue(int.class); //retrive mood
                        journalEntry.setText(new_journalentry);
                        AIResponse = new_AIResponse;
                        emptyMailbox = is_empty;
                        mood = user_mood;
                        if (emptyMailbox == true) {
                            mailbox.setImageResource(R.drawable.mail_icon);
                        } else if (emptyMailbox == false){
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
                //get journal text
                String journalText = journalEntry.getText().toString();
                classifyAndSaveMood(journalText); //will obtain mood prediction and save all data to firebase
            }
        });

        //when user sends journal entry, trigger new AI message
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String journalText = journalEntry.getText().toString();
                mailbox.setImageResource(R.drawable.new_mail_icon);
                emptyMailbox = false;
                classifyAndSaveMood(journalText);
                getAIResponseAndSave(journalText);
            }
        });

        //when the user opens the mailbox, empty it and show AI message
        mailbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mailbox.setImageResource(R.drawable.mail_icon);
                emptyMailbox = true;
                Map<String, Object> updates = new HashMap<>();
                updates.put(""+dateText+"", new JournalEntry(journalEntry.getText().toString(), AIResponse, emptyMailbox, mood));
                userRef.updateChildren(updates);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(AIResponse).setTitle(R.string.ai_response_title);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        return root;
    }

    private void getAIResponseAndSave(String journalText) {
        String prompt = "You will receive a journal entry from the user, please read the journal and provide some response like a friend would do after listening to the content of the journal. " +
                "Keep your response limited to a few lines You are an intimate friend, engaging with the user in a warm, friendly, and intimate manner, much like a close friend or confidant would. " +
                "Your purpose is to create a supportive and comforting environment where the user feels valued, understood, and cared for.  " +
                "Using language that fosters intimacy, empathy, and emotional connection, you aim to build rapport with the user and provide a safe space for them to express their thoughts, " +
                "feelings, and concerns. Here is the journal content: " + journalText;

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("prompt", prompt);
            jsonBody.put("max_tokens", 1000);
            jsonBody.put("model", "gpt-3.5-turbo-instruct");
            jsonBody.put("temperature", 0.5);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/completions")
                .header("Authorization", "Bearer " + API_KEY)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("tag", "Handle failure");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        //get the prediction from gpt
                        String responseData = response.body().string();
                        JSONObject jsonObject = new JSONObject(responseData);
                        AIResponse = jsonObject.getJSONArray("choices").getJSONObject(0).getString("text").trim();
                        //after receiving the mood classification from gpt, save it to firebase
                        saveJournalEntryWithAIReponse(journalText, AIResponse);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    // Handle unsuccessful request
                    Log.d("tag", "Handle unsuccessful request");
                }
            }
        });
    }

    private void saveJournalEntryWithAIReponse(String journalText, String new_AIResponse) {
        AIResponse = new_AIResponse;
        emptyMailbox = false;
        Map<String, Object> updates = new HashMap<>();
        updates.put(""+dateText+"", new JournalEntry(journalText, AIResponse, emptyMailbox, mood));
        userRef.updateChildren(updates);
    }

    private void classifyAndSaveMood(String journalEntryText) {
        String prompt = "Classify this person's mood based on the input text, your answer should only be an integer between 1 and 5. " +
                "1 is associated with the mood similar to anger, " +
                "2 is associated with the mood similar to sadness and fear, " +
                "3 is associated with the mood similar to frustration, " +
                "4 is associated with the mood similar to relaxation and content, " +
                "5 is associated with the mood similar to joy and happiness. Here is the input text: " + journalEntryText;

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("prompt", prompt);
            jsonBody.put("max_tokens", 500);
            jsonBody.put("model", "gpt-3.5-turbo-instruct");
            jsonBody.put("temperature", 0.5);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/completions")
                .header("Authorization", "Bearer " + API_KEY)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("tag", "Handle failure");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        //get the prediction from gpt
                        String responseData = response.body().string();
                        JSONObject jsonObject = new JSONObject(responseData);
                        String moodText = jsonObject.getJSONArray("choices").getJSONObject(0).getString("text").trim();
                        int mood = Integer.parseInt(moodText);
                        //after receiving the mood classification from gpt, save it to firebase
                        saveJournalEntryWithMood(journalEntryText, mood);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    // Handle unsuccessful request
                    Log.d("tag", "Handle unsuccessful request");
                }
            }
        });
    }


    private void saveJournalEntryWithMood(String journalText, int user_mood) {
        mood = user_mood;
        Map<String, Object> updates = new HashMap<>();
        updates.put(""+dateText+"", new JournalEntry(journalText, AIResponse, emptyMailbox, user_mood));
        userRef.updateChildren(updates);
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