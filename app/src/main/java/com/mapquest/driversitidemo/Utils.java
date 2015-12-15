package com.mapquest.driversitidemo;

import com.apiosystems.apiosdk.driversiti.Driversiti;
import com.apiosystems.apiosdk.driversiti.data.User;

public class Utils {
    public static boolean isUserRegistered(){
        User activeUser = Driversiti.getSDK().getUserManager().getActiveUser();
        if(activeUser != null) {
            return true;
        }
        return false;
    }

    public static String htmlRed(String text){
        return "<font color='red'>" + text + "</font>";
    }

    public static String htmlGreen(String text){
        return "<font color='green'>" + text + "</font>";
    }

    public static String htmlBlue(String text){
        return "<font color='blue'>" + text + "</font>";
    }

    public static String htmlYellow(String text){
        return "<font color='yellow'>" + text + "</font>";
    }
}
