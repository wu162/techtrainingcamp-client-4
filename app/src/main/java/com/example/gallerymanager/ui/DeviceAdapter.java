package com.example.gallerymanager.ui;

import android.net.wifi.p2p.WifiP2pDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gallerymanager.R;
import com.example.gallerymanager.utils.DeviceUtils;

import java.util.List;

/**
 * 作者：leavesC
 * 时间：2019/11/23 11:56
 * 描述：
 */
public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.ViewHolder> {

    private List<WifiP2pDevice> wifiP2pDeviceList;

    private OnClickListener clickListener;

    public interface OnClickListener {

        void onSendFile(int position);

    }

    public DeviceAdapter(List<WifiP2pDevice> wifiP2pDeviceList) {
        this.wifiP2pDeviceList = wifiP2pDeviceList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device, parent, false);
//        view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (clickListener != null) {
//                    clickListener.onItemClick((Integer) v.getTag());
//                }
//            }
//        });
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.tv_deviceName.setText(wifiP2pDeviceList.get(position).deviceName);
        holder.tv_deviceAddress.setText(wifiP2pDeviceList.get(position).deviceAddress);
        holder.tv_deviceDetails.setText(DeviceUtils.getDeviceStatus(wifiP2pDeviceList.get(position).status));
        holder.sendFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onSendFile((Integer) v.getTag());
                }
            }
        });
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return wifiP2pDeviceList.size();
    }

    public void setClickListener(OnClickListener clickListener) {
        this.clickListener = clickListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_deviceName;

        private TextView tv_deviceAddress;

        private TextView tv_deviceDetails;

        private Button sendFileBtn;

        ViewHolder(View itemView) {
            super(itemView);
            tv_deviceName = itemView.findViewById(R.id.tv_deviceName);
            tv_deviceAddress = itemView.findViewById(R.id.tv_deviceAddress);
            tv_deviceDetails = itemView.findViewById(R.id.tv_deviceDetails);
            sendFileBtn = itemView.findViewById(R.id.btn_send_file);;
        }

    }

}
