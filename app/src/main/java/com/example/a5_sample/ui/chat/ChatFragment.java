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
import java.util.Arrays;
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

    private String journalExample = "Date: April 10, 2024\n" +
            "\n" +
            "Morning:\n" +
            "\n" +
            "Woke up at 7:00 AM feeling refreshed after a good night's sleep. Checked my phone for any important notifications and then proceeded to get ready for the day. Had a quick breakfast consisting of scrambled eggs and toast while skimming through the news headlines.\n" +
            "\n" +
            "8:30 AM - 10:00 AM: Computer Networking Lecture\n" +
            "\n" +
            "Attended the computer networking lecture by Professor Smith. Took detailed notes on topics such as TCP/IP protocols, subnetting, and network topologies. Engaged in discussions with classmates about the practical applications of networking concepts in real-world scenarios.\n" +
            "\n" +
            "10:00 AM - 12:00 PM: Study Session at the Library\n" +
            "\n" +
            "Headed to the library with a group of friends to work on our group project for the software engineering course. Discussed the project requirements and divided tasks among team members. Spent the majority of the time brainstorming ideas for the project and creating a project timeline.\n" +
            "\n" +
            "Afternoon:\n" +
            "\n" +
            "Grabbed a quick lunch from the campus cafeteria – a turkey sandwich and a side of fruit salad. Took a short break to relax and recharge before diving back into studying.\n" +
            "\n" +
            "1:30 PM - 3:00 PM: Data Structures and Algorithms Lab\n" +
            "\n" +
            "Attended the lab session led by Teaching Assistant Sarah. Worked on implementing various data structures such as stacks, queues, and linked lists in Python. Collaborated with classmates to solve programming challenges related to sorting algorithms and tree traversal.\n" +
            "\n" +
            "3:00 PM - 5:00 PM: Personal Coding Project\n" +
            "\n" +
            "Dedicated some time to work on my personal coding project – a web-based chat application using Node.js and WebSocket technology. Made significant progress on implementing the chat interface and integrating real-time messaging functionality.\n" +
            "\n" +
            "Evening:\n" +
            "\n" +
            "Took a break from studying to relax and unwind. Went for a jog around campus to get some exercise and clear my mind. Enjoyed the fresh air and beautiful scenery as the sun began to set.\n" +
            "\n" +
            "7:00 PM - 9:00 PM: Dinner and Study Group\n" +
            "\n" +
            "Met up with my study group at a local café to review course material and prepare for an upcoming midterm exam in computer architecture. Discussed challenging topics such as pipelining, memory hierarchy, and CPU scheduling algorithms. Helped each other work through practice problems and clarify concepts.\n" +
            "\n" +
            "9:00 PM - 11:00 PM: Review Session and Assignment Completion\n" +
            "\n" +
            "Reviewed lecture notes and textbook chapters to reinforce understanding of key concepts covered throughout the day. Completed a few practice problems and worked on coding assignments for the algorithms course. Made sure to double-check my solutions for accuracy and efficiency.\n" +
            "\n" +
            "Late Night:\n" +
            "\n" +
            "Wrapped up studying around 11:00 PM and headed back to my dorm room. Took a hot shower to relax before getting ready for bed. Set my alarm for the next day and spent some time unwinding with a book before turning in for the night.\n" +
            "\n" +
            "Overall, it was a productive and fulfilling day filled with learning, collaboration, and progress towards my academic and personal goals in computer science.";

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

        try {
            JSONObject journal = new JSONObject();
            journal.put("role", "user");
            journal.put("content", journalExample);
            messagesArray.put(journal);
        } catch (JSONException e) {
            e.printStackTrace();
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
            jsonBody.put("temperature", 0);
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
                    // Retrieve personas from Firebase
                    if (dataSnapshot.hasChild("personas")) {
                        String personasString = dataSnapshot.child("personas").getValue(String.class);
                        if (personasString != null) {
                            personas = personasString;
                        }
                    }
                    persona = "You are an Intimate Friend, engaging with the user in a warm, friendly, and intimate manner, much like a close friend or confidant would. " +
                            "Your purpose is to create a supportive and comforting environment where the user feels valued, understood, and cared for. " +
                            "Using language that fosters intimacy, empathy, and emotional connection, you aim to build rapport with the user and provide a safe space for them to express their thoughts, feelings, and concerns. " +
                            "Whether they need someone to talk to, seek advice, or simply want to share their experiences, you are here to listen, support, and offer genuine companionship. " +
                            "As your Insightful Journal Companion, I'm here to provide personalized support and guidance based on a deep understanding of your thoughts, experiences, and aspirations. With access to your entire journal history, I have insights into your past experiences, challenges, and triumphs, allowing me to offer tailored advice and assistance."+
                            "You should especially respond to them with these extremely important qualities: " + personas +
                            " and you name is " + name + ", and you are a " + age + " year old " + gender;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
            }
        });
    }
}

