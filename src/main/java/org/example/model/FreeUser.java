package org.example.model;

import org.example.globalsettings.GlobalSettings;
import org.example.UserFactory;
import org.example.service.FreeUserServices;

import java.util.Scanner;

public class FreeUser extends User{


    private int remainingTrial = GlobalSettings.getFreeUserTrialUpdate();
    private  FreeUserServices services;


    public FreeUser(String name, int age, long userID ){
        super(name, age, userID);
    }

    public FreeUserServices getServices() {
        return services;
    }

    public void setServices(FreeUserServices services) {
        this.services = services;
    }

    public int getRemainingTrial() {
        return remainingTrial;
    }

    public void setRemainingTrial(int remainingTrial) {
        this.remainingTrial = remainingTrial;
    }


    public void updateStatus(){
        if(remainingTrial > 0){
            setStatus(SubscriptionStatus.TRIAL);

        } else{
            setStatus(SubscriptionStatus.ACTIVE);
        }
    }

    @Override
    public void accessContent() {
        updateStatus();
        if (getStatus() == SubscriptionStatus.TRIAL) {
            super.accessContent();
            remainingTrial--;
        } else if (getStatus() == SubscriptionStatus.ACTIVE) {
            System.out.println("Free trial finished: please upgrade to premium to continue");
            User upgrade = upgradePremium();

           if(upgrade != null){
               upgrade.accessContent();
           }
        } else {
            super.accessContent();
        }


    }

    public User upgradePremium() {

        System.out.println("\n");
        System.out.println("=======upgrade to Premium user==========(yes/No)");
        Scanner sc = new Scanner(System.in);
        System.out.println("Upgrade to premium (yes/no)");
        String input = sc.nextLine();

        if (input.equalsIgnoreCase("yes")) {
            User premiumUser = UserFactory.createUser("PREMIUM", this.getName(), this.getAge(), this.getUserID());
            services.replaceUser(getUserID(), premiumUser);
            premiumUser.setStatus(SubscriptionStatus.ACTIVE);


            return premiumUser;


        } else {
            System.out.println("Access blocked until you upgrade");
            return null;

        }

    }


}
