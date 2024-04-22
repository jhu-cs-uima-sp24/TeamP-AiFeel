package com.example.a5_sample;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.a5_sample.databinding.ActivityMainBinding;
import com.example.a5_sample.ui.chat.ChatFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    public JournalEntry current;
    private DatabaseReference dbref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        current = null;
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            dbref = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        }

        //BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_profile, R.id.navigation_home, R.id.navigation_chat, R.id.navigation_journalEntry)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        Context context = getApplicationContext();
        SharedPreferences myPrefs = context.getSharedPreferences(getString(R.string.storage), Context.MODE_PRIVATE);
        String name = myPrefs.getString("loginName", "Owner");
    }

    private boolean isChatPage() {
        // Check if the NavController is initialized and has a current destination
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        if (navController.getCurrentDestination() != null) {
            // Check if the current destination is the ChatFragment
            return navController.getCurrentDestination().getId() == R.id.navigation_chat;
        }
        // If NavController or current destination is null, return false
        return false;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (isChatPage()) {
            getMenuInflater().inflate(R.menu.chat, menu);
        } else {
            getMenuInflater().inflate(R.menu.main, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.action_clear_chat) { // Handle clear chat option
            if (dbref != null) {
                dbref.child("messages").removeValue()
                        .addOnSuccessListener(aVoid -> {
                            // Messages removed successfully
                            Log.d("MainActivity", "All messages removed successfully");
                        })
                        .addOnFailureListener(e -> {
                            // Failed to remove messages
                            Log.e("MainActivity", "Failed to remove messages: " + e.getMessage());
                        });
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Clear the options menu to remove all menu items
        menu.clear();
        // Re-create the options menu to inflate new menu items based on the current destination
        getMenuInflater().inflate(isChatPage() ? R.menu.chat : R.menu.main, menu);
        return super.onPrepareOptionsMenu(menu);
    }


}