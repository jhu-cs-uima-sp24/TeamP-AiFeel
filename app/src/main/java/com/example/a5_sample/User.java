package com.example.a5_sample;
import java.lang.String;

public class User {
    private String userId; // Unique ID from Firebase Authentication
    private String email;
    private String profile_picture;
    private String name;
    private String age;
    private String gender;
    // Constructors, getters, and setters
    private String uid;
    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }
    public User(String name, String email) {
        this.name= name;
        this.email = email;
    }

    //getters
    public String getAge() { return age; }

    public String getUserId(){ return userId; }

    public String getEmail(){ return email; }

    public String getName(){ return name; }

    public String getGender() { return gender; }

    //setters

    public void setAge(String age) { this.age = age; }

    public void setEmail(String email){ this.email = email; }

    public void setName(String name){ this.name = name; }


    public void setUid(String UID) {
        uid = UID;
    }

    public void setImageURI(String string) {
        profile_picture = string;
    }
}
