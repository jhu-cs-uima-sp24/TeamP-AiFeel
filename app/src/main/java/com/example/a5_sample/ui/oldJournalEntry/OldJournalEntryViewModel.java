package com.example.a5_sample.ui.oldJournalEntry;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class OldJournalEntryViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public OldJournalEntryViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}