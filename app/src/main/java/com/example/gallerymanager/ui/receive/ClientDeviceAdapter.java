package com.example.gallerymanager.ui.receive;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gallerymanager.R;

import java.util.ArrayList;

public class ClientDeviceAdapter extends RecyclerView.Adapter<ClientDeviceAdapter.ViewHolder> {

    private ArrayList<String> devices;

    public ClientDeviceAdapter(ArrayList<String> devices) {
        this.devices = devices;
    }

    public void addDevice(String ip){
        devices.add(ip);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ClientDeviceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_client_device_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ClientDeviceAdapter.ViewHolder holder, int position) {
        TextView tv_clientIp = holder.itemView.findViewById(R.id.tv_client_device_ip);
        tv_clientIp.setText(devices.get(position));
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
