package com.example.techjini.wifipoc;

/**
 * Created by techjini on 18/7/16.
 */
public class Util {
    public static String getSecureType(String capabilities) {

        if (capabilities.contains("WPA2")) {
            return "WPA2";
        } else if (capabilities.contains("WPA")) {
            return "WPA";
        } else if (capabilities.contains("WEP")) {
            return "WEP";
        }
        return "open";
    }

}
