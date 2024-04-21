package com.example.a5_sample;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MoodClassifier {

    private OkHttpClient client;
    private final String API_KEY = "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";

    private String prompt = "Classify this person's mood based on the input text, your answer should only be a integer between 1 and 5. " +
            "1 is associated the mood similar to anger, " +
            "2 is associated the mood similar to sadness and fear, " +
            "3 is associated with the mood similar to frustration, " +
            "4 is associated with the mood similar to relaxation and content, " +
            "5 is associated with the mood similar to joy and happiness. Here is the input text: ";
    public MoodClassifier() {
        client = new OkHttpClient();
    }

    public interface MoodClassificationCallback {
        void onClassificationSuccess(int mood);
        void onClassificationFailure(Exception e);
    }

    public void classifyMood(String journalEntry, MoodClassificationCallback callback) {
        // define the JSON body for the request
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("prompt", prompt + journalEntry);
            jsonBody.put("max_tokens", 4000);
            jsonBody.put("model", "gpt-3.5-turbo");
            jsonBody.put("temperature", 0.5);
        } catch (JSONException e) {
            callback.onClassificationFailure(e);
            return;
        }

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(jsonBody.toString(), JSON);

        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .header("Authorization", "Bearer " + API_KEY)
                .post(body)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                callback.onClassificationFailure(e);
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String responseData = response.body().string();
                        JSONObject jsonObject = new JSONObject(responseData);
                        String moodText = jsonObject.getJSONArray("choices").getJSONObject(0).getString("text").trim();
                        int mood = Integer.parseInt(moodText);
                        callback.onClassificationSuccess(mood);
                    } catch (Exception e) {
                        callback.onClassificationFailure(e);
                    }
                } else {
                    callback.onClassificationFailure(new Exception("Unsuccessful request: " + response));
                }
            }
        });
    }
}
