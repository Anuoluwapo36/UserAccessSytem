package org.example.globalsettings;

public class GlobalSettings {

    //constructing global rule for free user on trail
    private static  final int TRIAL_COUNT_FREE_USER = 3;
    private static  int FREE_USER_TRIAL_UPDATE = TRIAL_COUNT_FREE_USER;

    private static final boolean PREMIUM_HAS_Access = true;
    private static boolean hasAccess_Flag = PREMIUM_HAS_Access;






    public static int getFreeUserTrialUpdate() {
        return FREE_USER_TRIAL_UPDATE;
    }

    public static void setFreeUserTrialUpdate(int freeUserTrialUpdate) {
        FREE_USER_TRIAL_UPDATE = freeUserTrialUpdate;
    }

    public static boolean isHasAccess_Flag() {
        return hasAccess_Flag;
    }

    public static void setHasAccess_Flag(boolean hasAccess_Flag) {
        GlobalSettings.hasAccess_Flag = hasAccess_Flag;
    }



}
