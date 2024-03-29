package com.example.a5_sample;
import java.lang.String;
import java.util.Random;


public class StudyRoom {
    private static Random randy = new Random();
    private String location;
    private int occupants;
    private int capacity;
    private boolean checked_in;

    public StudyRoom(String name, int capacity) {
        this.location = name;
        this.capacity = capacity;
        this.occupants = randy.nextInt(capacity + 1);
        this.checked_in = false;
    }

    public String getname() { return location; }

    public int getoccupants() { return occupants; }

    public int getcap() { return capacity; }

    public boolean getchecked_in() { return checked_in; }

    public int getavailable() { return capacity - occupants; }

    public void setchecked_in(boolean a) {
        if (a != checked_in) {
            checked_in = a;
            if (a) occupants++;
            else occupants--;
        }
    }
}
