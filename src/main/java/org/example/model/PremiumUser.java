package org.example.model;

import org.example.globalsettings.GlobalSettings;

public class PremiumUser extends User{


    public PremiumUser(String name, int age, long userID) {
        super(name, age, userID );
    }

    boolean premiumAccess = GlobalSettings.isHasAccess_Flag();
    @Override
    public void accessContent(){
        if(premiumAccess){
            System.out.println("Account upgrade to premium");
            System.out.println("userId: " + getUserID() + " name: " + getName() + " age: " + getAge());
            super.accessContent();
        }else {
            System.out.println("limited access: upgrade or renew sub to get premium access");

        }




    }
}
