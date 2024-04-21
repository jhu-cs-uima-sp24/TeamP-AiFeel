package com.example.a5_sample;

public class JournalEntry {

    public String journalEntryText;
    public String AIResponse;
    public boolean mailboxStatus;

    public int mood; //scale of 1-5, 1 is the lowest

    public JournalEntry(String journalEntryText, String AIResponse, boolean mailboxStatus, int mood) {
        this.journalEntryText = journalEntryText;
        this.AIResponse = AIResponse;
        this.mailboxStatus = mailboxStatus;
        this.mood = mood;
    }
}