package com.example.techjini.wifipoc;

/**
 * Created by techjini on 18/7/16.
 */
public class Util {
    public static String getSecureType(String capabilities) {

        if (capabilities.contains("WPA2")) {
            return WifiModel.SECURE_WPA2;
        } else if (capabilities.contains("WPA")) {
            return WifiModel.SECURE_WPA;
        } else if (capabilities.contains("WEP")) {
            return WifiModel.SECURE_WEP;
        }
        return WifiModel.SECURE_OPEN;
    }

}
