package com.example.a5_sample.ui.journalEntry;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class JournalEntryViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public JournalEntryViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("");
    }

    public LiveData<String> getText() {
        return mText;
    }
}