package com.example.techjini.wifipoc;

import android.app.Activity;
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
import android.net.wifi.WifiManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;

import com.example.techjini.wifipoc.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements WifiClickListener {

    private WifiManager mWifiManager;
    private ActivityMainBinding mBinding;
    private final String StateChange = "android.net.wifi.STATE_CHANGE";
    private final String connectivityChange = "android.net.conn.CONNECTIVITY_CHANGE";
    private int mNetworkId=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mBinding.wifiRecycler.setLayoutManager(new LinearLayoutManager(this));
        mBinding.wifiRecycler.setAdapter(new WifiAdapter(this, getWifiList(), this));
        Log.d("test", "test");

    }

    public List<WifiModel> getWifiList() {
        mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        List<WifiModel> list = new ArrayList<>();
        for (ScanResult scanResult : mWifiManager.getScanResults()) {
            WifiModel wifiModel = new WifiModel(scanResult);
            list.add(wifiModel);
        }
        return list;


    }

    @Override
    public void onWifiSelected(WifiModel wifiModel) {
        String networkPass = "12345678";
        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"" + wifiModel.getScanResult().SSID + "\"";
        conf.preSharedKey = "\"" + networkPass + "\"";
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

    private BroadcastReceiver wifiListener = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            SupplicantState supState=mWifiManager.getConnectionInfo().getSupplicantState();

            if (mNetworkId>0&&intent.getAction().equals(StateChange) && supState.equals(SupplicantState.COMPLETED)) {
                mWifiManager.removeNetwork(mNetworkId);
                mWifiManager.saveConfiguration();
            }

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter iff = new IntentFilter();
        iff.addAction(StateChange);
        iff.addAction(connectivityChange);
       // registerReceiver(wifiListener, iff);
    }
}
