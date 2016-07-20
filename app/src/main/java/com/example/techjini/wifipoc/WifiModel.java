package com.example.techjini.wifipoc;

import android.net.wifi.ScanResult;

/**
 * Created by techjini on 18/7/16.
 */
public class WifiModel {
    public static final String SECURE_WEP="WEP";
    public static final String SECURE_WPA="WPA";
    public static final String SECURE_WPA2="WPA2";
    public static final String SECURE_OPEN="open";
    private ScanResult scanResult;
    private String secureType;

    public WifiModel(ScanResult scanResult) {
        this.scanResult = scanResult;
        this.secureType = Util.getSecureType(scanResult.capabilities);

    }

    public ScanResult getScanResult() {
        return scanResult;
    }

    public void setScanResult(ScanResult scanResult) {
        this.scanResult = scanResult;
    }

    public String getSecureType() {
        return secureType;
    }

    public void setSecureType(String secureType) {
        this.secureType = secureType;
    }
}
