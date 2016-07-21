package com.example.techjini.wifipoc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;

import com.example.techjini.wifipoc.databinding.ActivityMainBinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements WifiClickListener {


    private static final String TAG = MainActivity.class.getSimpleName();
    private WifiManager mWifiManager;
    private ActivityMainBinding mBinding;
    private final String StateChange = "android.net.wifi.STATE_CHANGE";
    private final String connectivityChange = "android.net.conn.CONNECTIVITY_CHANGE";
    private int mNetworkId = -1;
    private WifiModel mWifiModel;
    private boolean isBroadcastRegistered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mBinding.wifiRecycler.setLayoutManager(new LinearLayoutManager(this));
        mBinding.wifiRecycler.setAdapter(new WifiAdapter(this, getWifiList(), this));
        Log.d("test", "test");
        Handler connectHandler = new Handler();
        connectHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
              /*  if(mWifiModel!=null)
                onWifiSelected(mWifiModel);*/
            }

        }, 1000L * 30);
        Handler disconnectHandler = new Handler();
        disconnectHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
               /* mWifiManager.disableNetwork(mNetworkId);
                mWifiManager.disconnect();
                mWifiManager.removeNetwork(mNetworkId);
                mWifiManager.saveConfiguration();*/
            }

        }, 1000L * 60);

    }

    public List<WifiModel> getWifiList() {
        mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        List<WifiModel> list = new ArrayList<>();
        for (ScanResult scanResult : mWifiManager.getScanResults()) {
            WifiModel wifiModel = new WifiModel(scanResult);
            list.add(wifiModel);
            if (scanResult.SSID.equals("Ayaz")) {
                mWifiModel = wifiModel;
            }
        }
        return list;


    }

    @Override
    public void onWifiSelected(WifiModel wifiModel) {
        String networkPass = "TjMobile@123";
        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = Constant.DOUBLE_QUOTE + wifiModel.getScanResult().SSID + Constant.DOUBLE_QUOTE;
        switch (wifiModel.getSecureType()) {
            case WifiModel.SECURE_WPA2:
            case WifiModel.SECURE_WPA:
                /*conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                conf.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);*/
                conf.preSharedKey = Constant.DOUBLE_QUOTE + networkPass + Constant.DOUBLE_QUOTE;
                break;
            case WifiModel.SECURE_WEP:
                if (networkPass.matches(Constant.HEX_PATTERN)) {
                    conf.wepKeys[0] = networkPass;
                } else {
                    conf.wepKeys[0] = Constant.DOUBLE_QUOTE + networkPass + Constant.DOUBLE_QUOTE;
                }
                conf.wepTxKeyIndex = 0;
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                conf.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                conf.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                conf.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);

                break;
            case WifiModel.SECURE_OPEN:
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                break;

        }
        registerReceiver();
        mWifiManager.addNetwork(conf);
        List<WifiConfiguration> list = mWifiManager.getConfiguredNetworks();
        for (final WifiConfiguration i : list) {
            if (i.SSID != null && i.SSID.equals("\"" + wifiModel.getScanResult().SSID + "\"")) {
                Log.v("rht", "WifiConfiguration SSID " + i.SSID);

                boolean isDisconnected = mWifiManager.disconnect();
                Log.v("rht", "isDisconnected : " + isDisconnected);

                boolean isEnabled = mWifiManager.enableNetwork(i.networkId, true);
                Log.v("rht", "isEnabled : " + isEnabled);

                boolean isReconnected = mWifiManager.reconnect();
                Log.v("rht", "isReconnected : " + isReconnected);
                mBinding.wifiName.setText("Click to Disconnect");
                mBinding.wifiName.setVisibility(View.VISIBLE);
                mNetworkId = i.networkId;
                mBinding.wifiName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mWifiManager.disableNetwork(i.networkId);
                        mWifiManager.disconnect();
                        mWifiManager.removeNetwork(mNetworkId);
                        mWifiManager.saveConfiguration();
                    }
                });
                break;
            }
        }

    }

    private void registerReceiver() {
        isBroadcastRegistered=true;
        IntentFilter iff = new IntentFilter();
        iff.addAction(StateChange);
        iff.addAction(connectivityChange);
        registerReceiver(wifiListener, iff);
    }

    private BroadcastReceiver wifiListener = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            /*if(intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
                int networkType = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_TYPE);
                boolean isWiFi = networkType == ConnectivityManager.TYPE_WIFI;
                boolean isMobile = networkType == ConnectivityManager.TYPE_MOBILE;
                NetworkInfo networkInfo = connectivityManager.getNetworkInfo(networkType);

                if(networkInfo.getType() == ConnectivityManager.TYPE_WIFI &&
                        networkInfo.isConnected()) {
                    // Wifi is connected
                    Log.d("Inetify", "Wifi is connected: " + String.valueOf(networkInfo));

                    Log.e("intent action", intent.getAction());
                    if (isNetworkConnected(context)){
                        Log.e("WiFi", "is Connected. Saving...");
                        try {
                            saveFilesToServer("/" + ctx.getString(R.string.app_name).replaceAll(" ", "_") + "/Temp.txt");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

             mContext = context;
*/

            if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {

                ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = cm.getActiveNetworkInfo();

                if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI &&
                        networkInfo.isConnected()) {
                    // Wifi is connected
                    WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    String ssid = wifiInfo.getSSID();

                    Log.e(TAG, " -- Wifi connected --- " + " SSID " + ssid);

                } else if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI &&
                        !networkInfo.isConnected()) {
                    WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    String ssid = wifiInfo.getSSID();
                    Log.e(TAG, " -- Wrong Password --- " + " SSID " + ssid);
                }

            } else if (intent.getAction().equalsIgnoreCase(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
                int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
                if (wifiState == WifiManager.WIFI_STATE_DISABLED) {
                    Log.e(TAG, " ----- Wifi  Disconnected ----- ");
                }

            }

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
       /* IntentFilter iff = new IntentFilter();
        iff.addAction(StateChange);
        iff.addAction(connectivityChange);
         registerReceiver(wifiListener, iff);*/
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(isBroadcastRegistered)
        unregisterReceiver(wifiListener);
        isBroadcastRegistered=false;
    }
}
