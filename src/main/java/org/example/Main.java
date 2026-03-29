package org.example;

import org.example.globalsettings.GlobalSettings;
import org.example.model.FreeUser;
import org.example.model.User;
import org.example.service.FreeUserServices;


public class Main {
    static void main() {
        GlobalSettings.setFreeUserTrialUpdate(3);
        FreeUserServices services = new FreeUserServices();

        User user1 = services.getOrCreateNewUser("FREE", "majie", 20,001);


        if (user1 instanceof FreeUser freeUser){
            freeUser.setServices(services);
        }
        user1.accessContent();
        user1.accessContent();
        user1.accessContent();
        user1.accessContent();







    }
}
