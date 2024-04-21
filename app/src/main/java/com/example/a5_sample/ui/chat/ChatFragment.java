package com.example.a5_sample.ui.chat;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
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
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatFragment extends Fragment {
    private final String API_KEY = "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";
    // Declare variables
    private RecyclerView recyclerView;
    private TextView welcomeTextView;
    private EditText messageEditText;
    private ImageButton sendButton;
    private List<Message> messageList;
    private MessageAdapter messageAdapter;
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private final OkHttpClient client = new OkHttpClient();
    private String persona;
    private DatabaseReference databaseReference;

    private String journalExample;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);

        // Initialize views
        recyclerView = rootView.findViewById(R.id.recycler_view);
        welcomeTextView = rootView.findViewById(R.id.welcome_text);
        messageEditText = rootView.findViewById(R.id.message_edit_text);
        sendButton = rootView.findViewById(R.id.send_btn);

        // Initialize message list and adapter
        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(messageAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(requireContext());
        llm.setStackFromEnd(true);
        recyclerView.setLayoutManager(llm);


        // initialize firebase
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            // Now you have the UID of the current user
            databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

        }

        // Set click listener for send button
        sendButton.setOnClickListener(v -> {
            String question = messageEditText.getText().toString().trim();
            addToChat(question, Message.SENT_BY_ME);
            messageEditText.setText("");
            callAPI(question);
            welcomeTextView.setVisibility(View.GONE);
        });

        return rootView;
    }

    // Method to add a message to the chat
    void addToChat(String message, String sentBy) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Add message to list
                messageList.add(new Message(message, sentBy));
                // Notify adapter of data change
                messageAdapter.notifyDataSetChanged();
                // Scroll to the last item in the list
                recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
            }
        });
    }

    // Method to handle the response from the API
    private void addResponse(String response) {
        // Remove the "Typing..." message from the list
        messageList.remove(messageList.size() - 1);
        // Add the response to the chat
        addToChat(response, Message.SENT_BY_BOT);
    }


    // Method to make the API call
    private void callAPI(String question) {
        // Add a "Typing..." message to the chat
        messageList.add(new Message("Typing... ", Message.SENT_BY_BOT));

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
                    role = "system";
                }
                messageObject.put("role", role);
                messageObject.put("content", message.getMessage());
                messagesArray.put(messageObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         *         try {
         *             JSONObject journal = new JSONObject();
         *             journal.put("role", "user");
         *             journal.put("content", journalExample);
         *             messagesArray.put(journal);
         *         } catch (JSONException e) {
         *             e.printStackTrace();
         *         }
         **/

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
            jsonBody.put("temperature", 0);
            jsonBody.put("stop", "\n");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("ERROR", jsonBody.toString());

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
                        Log.d("tag", jsonObject.toString());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject json = jsonArray.getJSONObject(i);
                            JSONObject messageObject = json.getJSONObject("message");
                            String content = messageObject.getString("content");
                            addResponse(content);
                        }
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
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // default characteristics
                    String name = "Melissa", age = "20", gender = "Female", personas = "Kind, Careful, Cheerful";
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

