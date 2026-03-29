package org.example.service;

import org.example.UserFactory;
import org.example.model.User;

import java.util.LinkedHashMap;
import java.util.Map;

public class FreeUserServices {

    private Map<Long, User> userMap = new LinkedHashMap<>();


    public User getOrCreateNewUser(String type, String name, int age , long userID) {
        if (userMap.containsKey(userID)) {
            return userMap.get(userID);
        } else {
            User user = UserFactory.createUser(type, name, age, userID);
            userMap.put(userID, user);
            return user;
        }

    }


    public User replaceUser(long userID , User newUser){

        if (userMap.containsKey(userID)){
            return userMap.put(userID,newUser);


        }else{
            System.out.println("unable to upgrade to premium");

            return newUser;

        }
    }


}









