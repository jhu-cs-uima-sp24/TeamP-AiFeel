package com.example.a5_sample;
import java.lang.String;
import java.util.Random;


public class JournalEntry {

    public String journalEntryText;
    public String AIResponse;
    public boolean mailboxStatus;

    public JournalEntry(String journalEntryText, String AIResponse, boolean mailboxStatus) {
        this.journalEntryText = journalEntryText;
        this.AIResponse = AIResponse;
        this.mailboxStatus = mailboxStatus;
    }
}