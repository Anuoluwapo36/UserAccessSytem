package org.example.model;



public abstract class  User {
    private String name;
    private int age;
    private long userID;
    public enum SubscriptionStatus {TRIAL, ACTIVE, EXPIRED, NONE, LOCKED}
    private SubscriptionStatus status;
    private boolean isLocked  = false;

    public User(String name, int age, long userID) {
        this.name = name;
        this.age = age;
        this.userID = userID;
        this.status = SubscriptionStatus.NONE;
        this.isLocked = false;
    }

    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name=name;
    }

    public int getAge(){
        return age;
    }
    public void  setAge(int age){
        this.age=age;
    }

    public  void setUserID(long userID){
        this.userID=userID;
    }
    public  long getUserID(){
        return userID;
    }

    public SubscriptionStatus getStatus() {
        return status;
    }

    public void setStatus(SubscriptionStatus status) {
        this.status = status;

    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setIsLocked(boolean isLocked) {
        this.isLocked = isLocked;
    }

    public abstract void accessContent();







    public  void  checkAccess(){
        if(isLocked()) {
            System.out.println("account locked unable to access content");
            return;
        }

        switch (status) {
            case EXPIRED, NONE -> System.out.println("Account expired: please upgrade");
            default -> System.out.println("Accessing content");
        }
    }



}
