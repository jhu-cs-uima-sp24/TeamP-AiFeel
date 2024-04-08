package com.example.a5_sample;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class EditPal extends AppCompatActivity {
    private Button save;
    private Button quit;
    private EditText search;
    private RecyclerView recyclerView;
    private RecyclerView selectedRecyclerView; // RecyclerView for selected personas
    private PersonalityAdapter adapter;
    private SelectedPersonalityAdapter selectedAdapter; // Adapter for selected personas
    private List<String> allPersonalities;
    private List<String> personas; // List to store selected personalities
    private Spinner gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_pal);

        quit = findViewById(R.id.quit_btn);
        save = findViewById(R.id.save_btn);
        search = findViewById(R.id.searchPersonas);
        recyclerView = findViewById(R.id.recyclerView);
        selectedRecyclerView = findViewById(R.id.selectedRecyclerView);

        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        gender = (Spinner) findViewById(R.id.genderSpinner);
        ArrayAdapter<CharSequence> genAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.gender_array,
                android.R.layout.simple_spinner_item
        );
        genAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender.setAdapter(genAdapter);

        allPersonalities = loadAllPersonalities();

        personas = new ArrayList<>(); // Initialize personas list
        // Set up RecyclerView with all personalities
        adapter = new PersonalityAdapter(allPersonalities, new PersonalityAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String personality) {
                if (!personas.contains(personality) && personas.size() < 3) { // Check for duplicates and maximum limit
                    // Add the clicked personality to personas
                    personas.add(personality);
                    // Update the selected RecyclerView
                    selectedAdapter.setPersonalities(personas);
                }
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.setPersonalities(new ArrayList<>());

        // Initialize and set up the RecyclerView for selected personas
        selectedAdapter = new SelectedPersonalityAdapter(new ArrayList<>());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL); // Set orientation to horizontal
        selectedRecyclerView.setLayoutManager(layoutManager);
        selectedRecyclerView.setAdapter(selectedAdapter);

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String query = charSequence.toString().toLowerCase();
                if (query.isEmpty()) {
                    adapter.setPersonalities(new ArrayList<>()); // Clear the RecyclerView
                } else {
                    List<String> filteredPersonalities = filter(allPersonalities, query);
                    adapter.setPersonalities(filteredPersonalities);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }


    private List<String> filter(List<String> personalities, String query) {
        if (query.isEmpty()) {
            return new ArrayList<>(); // Return an empty list if the query is empty
        }
        List<String> filteredList = new ArrayList<>();
        int count = 0;
        for (String personality : personalities) {
            if (personality.toLowerCase().contains(query)) {
                filteredList.add(personality);
                count++;
            }
            if (count == 30) { // Limit to 3 personalities
                break;
            }
        }
        return filteredList;
    }



    private List<String> loadAllPersonalities() {
        List<String> personalities = new ArrayList<>();

        // Manually add personalities
        personalities.add("Adventurous");
        personalities.add("Analytical");
        personalities.add("Artistic");
        personalities.add("Assertive");
        personalities.add("Compassionate");
        personalities.add("Confident");
        personalities.add("Creative");
        personalities.add("Curious");
        personalities.add("Determined");
        personalities.add("Empathetic");
        personalities.add("Enthusiastic");
        personalities.add("Friendly");
        personalities.add("Helpful");
        personalities.add("Honest");
        personalities.add("Independent");
        personalities.add("Innovative");
        personalities.add("Intelligent");
        personalities.add("Optimistic");
        personalities.add("Patient");
        personalities.add("Persistent");
        personalities.add("Practical");
        personalities.add("Reliable");
        personalities.add("Resilient");
        personalities.add("Resourceful");
        personalities.add("Sociable");
        personalities.add("Supportive");
        personalities.add("Thoughtful");
        personalities.add("Tolerant");
        personalities.add("Versatile");
        personalities.add("Visionary");

        return personalities;
    }
}
