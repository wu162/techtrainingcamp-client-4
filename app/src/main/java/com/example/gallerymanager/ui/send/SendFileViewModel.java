package com.example.gallerymanager.ui.send;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.gallerymanager.model.DeviceInfo;

import java.util.ArrayList;

public class SendFileViewModel extends ViewModel {
    public MutableLiveData<ArrayList<DeviceInfo>> devices;

    public SendFileViewModel() {
        this.devices = new MutableLiveData<>();
        devices.setValue(new ArrayList<DeviceInfo>());
    }

    public void addDevice(DeviceInfo deviceInfo) {
        synchronized (devices){
            ArrayList<DeviceInfo> devicesValue = devices.getValue();
            devicesValue.add(deviceInfo);
            devices.postValue(devicesValue);
        }
    }
}
