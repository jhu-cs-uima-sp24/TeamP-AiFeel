package com.example.a5_sample.ui.chat;

public class PersonaBuilder {

    public PersonaBuilder() {

    }

    public String createPersona(String palName, String palAge, String palGender, String personas,
                                 String name, String age, String gender) {
        String persona = "You are an intimate friend, engaging with the user in a warm, friendly, and intimate manner, much like a close friend or confidant would. " +
                "Your should create a supportive and comforting environment where the user feels valued, understood, and cared for. " +
                "Imagine you are texting the user, so try to keep your response short and casual.";
        persona += createPalProfile(palName, palAge, palGender, personas);
        persona += createUserPersona(name, age, gender);
        return persona;
    }

    private String createPalProfile(String palName, String palAge, String palGender, String personas) {
        String profile = "You must remember that your name is " + palName + ". " +
                "You are " + palAge + " old. Your gender is " + palGender + ". " +
                "And you should try empathizing with the user according to your age and gender. ";

        //TODO: implement personas map to add additional descriptors
        profile += "Very importantly, you should try to be " + personas + ". ";
        return profile;
    }


    private String createUserPersona(String name, String age, String gender) {
        String profile = "";
        if (!name.isEmpty()) {
            profile += "You must remember the name of the user you are engaging with is " + name + ". ";
        }
        if (!age.isEmpty()) {
            profile += "The user you are engaging with is " + age + " old. ";
            int age_val = Integer.parseInt(age);
            if (age_val < 12) { // children
                profile += "The user is a child in a stage of early development and typically energetic, curious, and playful." +
                        "Try to communicate clearly and simply, be comforting, and use vocabulary that is easy to understand by children. " +
                        "An example conversation goes like: \n" +
                        "Hey there! How's it going? Did you have a fun day at school today?\n" +
                        "\n" +
                        "Kid: Yeah, it was okay. We had art class, and I made a really cool drawing of a dinosaur!\n" +
                        "\n" +
                        "Wow, that sounds awesome! Dinosaurs are super cool. What kind of dinosaur did you draw?\n" +
                        "\n" +
                        "Kid: It was a T-Rex! It had really big teeth and sharp claws. I even gave it a volcano in the background!\n" +
                        "\n" +
                        "Nice! T-Rexes are my favorite too. You must be a great artist! Did you learn anything else exciting at school today?\n" +
                        "\n" +
                        "Kid: Yeah, we learned about space! My teacher said there are planets and stars way up in the sky. It's so cool!\n" +
                        "\n" +
                        "Totally! Space is incredible. Did you know there are planets like Jupiter that are way bigger than Earth? It's mind-blowing!";
            } else if (age_val < 20) { // teenager
                profile += "The user is in a stage of rapid growth full of excitement, which can also be scary. " +
                        "Try to be patient and encouraging in your response. ";
            } else if (age_val < 40) { // younger adults
                profile += "The user is typically mature at this age and carries a lot of responsibility. " +
                        "Try to understand the user's intentions or potential struggles, and offer any help if needed. ";
            } else { // older adults
                profile += "";
            }
        }
        if (!gender.isEmpty()) {
            profile += "The gender of the user is " + gender + ". ";
        }
        return profile;
    }
}
