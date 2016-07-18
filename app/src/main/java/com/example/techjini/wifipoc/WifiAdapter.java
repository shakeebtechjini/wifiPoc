package com.example.techjini.wifipoc;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.net.wifi.ScanResult;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.techjini.wifipoc.databinding.AdapeterWifiListBinding;

import java.util.List;

/**
 * Created by techjini on 15/7/16.
 */
public class WifiAdapter extends RecyclerView.Adapter<WifiAdapter.WifiAdapterViewHolder> {
    private final Context mContext;
    private final List<WifiModel> mList;
    private WifiClickListener wifiClickListener;


    public WifiAdapter(Context context, List<WifiModel> wifiList, WifiClickListener wifiClickListener) {
        mContext = context;
        mList = wifiList;
        this.wifiClickListener = wifiClickListener;
    }

    @Override
    public WifiAdapter.WifiAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        AdapeterWifiListBinding mBinding;
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.adapeter_wifi_list, parent, false);
        return new WifiAdapterViewHolder(mBinding);
    }

    @Override
    public void onBindViewHolder(WifiAdapter.WifiAdapterViewHolder holder, int position) {
        holder.mBinding.setData(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class WifiAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private AdapeterWifiListBinding mBinding;

        public WifiAdapterViewHolder(AdapeterWifiListBinding binding) {

            super(binding.getRoot());
            mBinding = binding;
            mBinding.setHandler(this);
        }

        @Override
        public void onClick(View v) {
            wifiClickListener.onWifiSelected(mList.get(getAdapterPosition()));
        }
    }
}
