package com.example.a5_sample.ui.chat;

public class PersonaBuilder {

    public PersonaBuilder() {

    }

    public String createPersona(String palName, String palAge, String palGender, String personas,
                                 String name, String age, String gender) {
        String persona = createPalProfile(palName, palAge, palGender, personas);
        persona += createUserPersona(name, age, gender);
        return persona;
    }

    private String createPalProfile(String palName, String palAge, String palGender, String personas) {
        String profile = "";
        if (!palName.isEmpty()) profile += "You must remember that your name is " + palName + ". ";
        if (!palAge.isEmpty()) profile += "You must pretend that you are " + palAge + " old. ";
        if (!palGender.isEmpty()) profile += "Your gender is " + palGender + ". " +
                "And you should try empathizing with the user according to your age and gender. ";
        if (!personas.isEmpty()) profile += "Very importantly, you must be " + personas + " no matter what, according to the user's request. For example, if you are funny, then tell jokes from time to time. ";
        return profile;
    }


    private String createUserPersona(String name, String age, String gender) {
        String profile = "";
        if (!name.isEmpty()) {
            profile += "You must remember the name of the user you are engaging with is " + name + ". ";
        }
        if (!gender.isEmpty()) {
            profile += "The gender of the user is " + gender + ". ";
        }
        if (!age.isEmpty()) {
            profile += "The user you are engaging with is " + age + " old. ";
            int age_val = Integer.parseInt(age);
            if (age_val <= 12) { // children
                profile += "You must no matter what use very simple and childish words as if you are talking to a young child";
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

        return profile;
    }
}
