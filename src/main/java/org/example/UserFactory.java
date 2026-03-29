package org.example;

import org.example.model.FreeUser;
import org.example.model.PremiumUser;
import org.example.model.User;
import org.jetbrains.annotations.NotNull;

public class UserFactory {
    public static User createUser(@NotNull String type, String name , int age , long userID){

        if(type.equalsIgnoreCase("FREE")){
            return new FreeUser(name, age, userID);
        } else if (type.equalsIgnoreCase("PREMIUM")) {
            return new PremiumUser(name, age, userID);

        }else {
            throw new IllegalArgumentException("Invalid user type");
        }
    }


}
