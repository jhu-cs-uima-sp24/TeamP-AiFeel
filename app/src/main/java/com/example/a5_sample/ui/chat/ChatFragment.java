package com.example.a5_sample.ui.chat;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a5_sample.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatFragment extends Fragment {
    private final String API_KEY = "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";
    private RecyclerView recyclerView;
    private EditText messageEditText;
    private ImageButton sendButton;
    private List<Message> messageList;
    private MessageAdapter messageAdapter;
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private final OkHttpClient client = new OkHttpClient();
    private String persona;
    private DatabaseReference databaseReference;
    private List<String> pastJournal;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);

        // Initialize views
        recyclerView = rootView.findViewById(R.id.recycler_view);
        messageEditText = rootView.findViewById(R.id.message_edit_text);
        sendButton = rootView.findViewById(R.id.send_btn);

        // Initialize message list and adapter
        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList);

        // initialize firebase
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            // Now you have the UID of the current user
            databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
            fetchProfilePictures();
            pastJournal = loadPastJournals();
        }

        recyclerView.setAdapter(messageAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(requireContext());
        llm.setStackFromEnd(true);
        recyclerView.setLayoutManager(llm);

        // Set click listener for send button
        sendButton.setOnClickListener(v -> {
            String question = messageEditText.getText().toString().trim();
            if (question.isEmpty()) return;
            addToChat(question, Message.SENT_BY_ME);
            messageEditText.setText("");
            try {
                callAPI(question);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });

        return rootView;
    }

    private void fetchProfilePictures() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("pal_picture")) {
                    Uri palPicUri = Uri.parse(dataSnapshot.child("pal_picture").getValue(String.class));
                    if (palPicUri != null) {
                        Log.e("hi", "hi");
                        messageAdapter.setPalImage(palPicUri);
                        messageAdapter.notifyDataSetChanged();
                    }
                }
                if (dataSnapshot.hasChild("profile_picture")) {
                    Uri picUri = Uri.parse(dataSnapshot.child("profile_picture").getValue(String.class));
                    if (picUri != null) {
                        messageAdapter.setImage(picUri);
                        messageAdapter.notifyDataSetChanged();

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
                Log.e("ChatFragment", "Failed to fetch profile pictures: " + databaseError.getMessage());
            }
        });
    }
    private List<String> loadPastJournals() {
        List<String> pastJournals = new ArrayList<>();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Iterate through the dataSnapshot to retrieve past journals
                for (DataSnapshot dateSnapshot : dataSnapshot.getChildren()) {
                    // Get the journal entry text for each date and add it to the list
                    if (dateSnapshot.hasChild("journalEntryText")) {
                        String date = dateSnapshot.getKey();
                        String journalEntryText = dateSnapshot.child("journalEntryText").getValue(String.class);
                        pastJournals.add(date + ": " + journalEntryText);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that occur during the data retrieval
                Log.e("loadPastJournals", "Failed to load past journals: " + databaseError.getMessage());
            }
        });
        return pastJournals;
    }


    // Method to add a message to the chat
    void addToChat(String message, String sentBy) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Add message to list
                Message msg = new Message(message, sentBy);
                messageList.add(msg);

                // Notify adapter of data change
                messageAdapter.notifyDataSetChanged();

                // Scroll to the last item in the list
                recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());

                // Generate a unique key for the message
                String messageId = databaseReference.child("messages").push().getKey();

                // Create a Map containing the message details
                Map<String, Object> messageValues = new HashMap<>();
                messageValues.put("content", msg.getMessage());
                messageValues.put("sentBy", msg.getSentBy());

                // Create a Map to hold the updates
                Map<String, Object> updates = new HashMap<>();
                updates.put("/messages/" + messageId, messageValues);

                // Update the database with the new message
                databaseReference.updateChildren(updates)
                        .addOnSuccessListener(aVoid -> {
                            // Message added successfully
                        })
                        .addOnFailureListener(e -> {
                            // Failed to add message
                            Log.e("ChatFragment", "Failed to add message: " + e.getMessage());
                        });
            }
        });
    }

    // Method to handle the response from the API
    private void addResponse(String response) {
        addToChat(response, Message.SENT_BY_BOT);
    }


    // Method to make the API call
    private void callAPI(String question) throws JSONException {
        // Create JSON request body
        JSONArray messagesArray = new JSONArray();
        JSONObject userMessage = new JSONObject();
        JSONObject systemMessage = new JSONObject();

        // Add conversation history to the request body
        for (Message message : messageList) {
            JSONObject messageObject = new JSONObject();
            try {
                String role = "user";
                if (!message.getSentBy().equals("me")){
                    role = "assistant";
                }
                messageObject.put("role", role);
                messageObject.put("content", message.getMessage());
                messagesArray.put(messageObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        try {
            JSONObject journalReader = new JSONObject();
            journalReader.put("role", "system");
            journalReader.put("content", "You are reading through the user's past journals. " +
                    "If he asks you about a date that doesn't exist in the journal, answer that you don't know.");
            messagesArray.put(journalReader);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (String journal : pastJournal) {
            JSONObject journalObject = new JSONObject();
            try {
                String role = "user";
                journalObject.put("role", role);
                journalObject.put("content", "past journal "+journal);
                messagesArray.put(journalObject);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            systemMessage.put("role", "system");
            systemMessage.put("content",  persona);
            userMessage.put("role", "user");
            userMessage.put("content", question);
            messagesArray.put(systemMessage);
            messagesArray.put(userMessage);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("messages", messagesArray);
            jsonBody.put("model", "gpt-3.5-turbo");
            jsonBody.put("max_tokens", 4000);
            jsonBody.put("temperature", 1);
            jsonBody.put("stop", "\n");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Create the API request
        RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .header("Authorization", "Bearer " + API_KEY)
                .post(body)
                .build();

        // Make the API call
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                // Handle failure
                addResponse("Failed to load response due to " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        // Parse the response and add messages to the chat
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        JSONArray jsonArray = jsonObject.getJSONArray("choices");
                        StringBuilder allResponsesBuilder = new StringBuilder();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject json = jsonArray.getJSONObject(i);
                            JSONObject messageObject = json.getJSONObject("message");
                            String content = messageObject.getString("content");
                            allResponsesBuilder.append(content);
                            if (i != jsonArray.length() - 1) allResponsesBuilder.append("\n"); // Append each response
                        }
                        String allResponses = allResponsesBuilder.toString();
                        addResponse(allResponses); // Add all responses as a single message
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    // Handle unsuccessful response
                    addResponse("Failed to load response due to " + response.body().string());
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        DatabaseReference messagesRef = databaseReference.child("messages");
        messagesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messageList.clear();
                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    // Retrieve each message from the snapshot
                    String content = messageSnapshot.child("content").getValue(String.class);
                    String sentBy = messageSnapshot.child("sentBy").getValue(String.class);
                    Message message = new Message(content, sentBy);
                    messageList.add(message);
                }
                // Update the UI with the loaded messages
                messageAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
                Log.e("ChatFragment", "Failed to read messages: " + databaseError.getMessage());
            }
        });

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // default characteristics
                    String name = "", age = "", gender = "", personas = "";
                    String user_name = "", user_age = "", user_gender = "";
                    if (dataSnapshot.hasChild("palName")) {
                        String nameVal = dataSnapshot.child("palName").getValue(String.class);
                        if (nameVal != null) {
                            name = nameVal;
                        }
                    }
                    if (dataSnapshot.hasChild("palAge")) {
                        String palAgeNum = dataSnapshot.child("palAge").getValue(String.class);
                        if (palAgeNum != null) {
                            age = palAgeNum;
                        }
                    }
                    if (dataSnapshot.hasChild("palGender")) {
                        String genderVal = dataSnapshot.child("palGender").getValue(String.class);
                        if (genderVal != null) {
                            gender = genderVal;
                        }
                    }
                    if (dataSnapshot.hasChild("personas")) {
                        String personasString = dataSnapshot.child("personas").getValue(String.class);
                        if (personasString != null) {
                            personas = personasString;
                        }
                    }
                    if (dataSnapshot.hasChild("name")) {
                        String nameVal = dataSnapshot.child("name").getValue(String.class);
                        if (nameVal != null) {
                            user_name = nameVal;
                        }
                    }
                    if (dataSnapshot.hasChild("age")) {
                        String palAgeNum = dataSnapshot.child("age").getValue(String.class);
                        if (palAgeNum != null) {
                            user_age = palAgeNum;
                        }
                    }
                    if (dataSnapshot.hasChild("profile_picture")) {
                        Uri pic = Uri.parse(dataSnapshot.child("profile_picture").getValue(String.class));
                        if (pic != null) {
                            messageAdapter.setImage(pic);
                        }
                    }
                    if (dataSnapshot.hasChild("pal_picture")) {
                        Uri pic = Uri.parse(dataSnapshot.child("pal_picture").getValue(String.class));
                        if (pic != null) {
                            messageAdapter.setPalImage(pic);
                        }
                    }
                    if (dataSnapshot.hasChild("gender")) {
                        String genderVal = dataSnapshot.child("gender").getValue(String.class);
                        if (genderVal != null) {
                            user_gender = genderVal;
                        }
                    }
                    PersonaBuilder pb = new PersonaBuilder();
                    persona = pb.createPersona(name, age, gender, personas, user_name, user_age, user_gender);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
            }
        });
    }
}

