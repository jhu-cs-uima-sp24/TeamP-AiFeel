package com.example.a5_sample.ui.journalEntry;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.a5_sample.databinding.FragmentJournalEntryBinding;

public class JournalEntryFragment extends Fragment {

    private FragmentJournalEntryBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        JournalEntryViewModel dashboardViewModel =
                new ViewModelProvider(this).get(JournalEntryViewModel.class);
        binding = FragmentJournalEntryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}