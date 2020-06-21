package com.example.gallerymanager.ui.send;

import android.content.BroadcastReceiver;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gallerymanager.R;
import com.example.gallerymanager.ui.DeviceAdapter;
import com.example.gallerymanager.utils.IOUtils;
import com.example.gallerymanager.view.LoadingDialog;
import com.example.gallerymanager.wifi.DirectActionListener;
import com.example.gallerymanager.wifi.DirectBroadcastReceiver;

import net.gallery.library.sample.client.TCPClient;
import net.gallery.library.sample.client.bean.ServerInfo;
import net.gallery.library.sample.foo.constants.TCPConstants;
import net.gallery.library.clink.box.FileSendPacket;
import net.gallery.library.clink.core.IoContext;
import net.gallery.library.clink.impl.IoSelectorProvider;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class SendFileActivity extends AppCompatActivity {

    private static final String TAG = "SendFileActivity";

    private WifiP2pManager wifiP2pManager;

    private WifiP2pManager.Channel channel;

    private WifiP2pInfo wifiP2pInfo;

    private boolean wifiP2pEnabled = false;

    private TextView tv_myDeviceName;

    private TextView tv_myDeviceAddress;

//    private TextView tv_myDeviceStatus;

//    private TextView tv_status;

    private List<WifiP2pDevice> wifiP2pDeviceList;

    private DeviceAdapter deviceAdapter;

    private LoadingDialog loadingDialog;

    private BroadcastReceiver broadcastReceiver;

    private WifiP2pDevice mWifiP2pDevice;

    private ArrayList<String> filesToSend;

    private HashMap<String,TCPClient> tcpClientList=new HashMap<>();

    private DirectActionListener directActionListener = new DirectActionListener() {

        @Override
        public void wifiP2pEnabled(boolean enabled) {
            wifiP2pEnabled = enabled;
        }

        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
            dismissLoadingDialog();
            wifiP2pDeviceList.clear();
            deviceAdapter.notifyDataSetChanged();
            Log.e(TAG, "onConnectionInfoAvailable");
            Log.e(TAG, "onConnectionInfoAvailable groupFormed: " + wifiP2pInfo.groupFormed);
            Log.e(TAG, "onConnectionInfoAvailable isGroupOwner: " + wifiP2pInfo.isGroupOwner);
            Log.e(TAG, "onConnectionInfoAvailable getHostAddress: " + wifiP2pInfo.groupOwnerAddress.getHostAddress());
            StringBuilder stringBuilder = new StringBuilder();
            if (mWifiP2pDevice != null) {
                stringBuilder.append("连接的设备名：");
                stringBuilder.append(mWifiP2pDevice.deviceName);
                stringBuilder.append("\n");
                stringBuilder.append("连接的设备的地址：");
                stringBuilder.append(mWifiP2pDevice.deviceAddress);
            }
            stringBuilder.append("\n");
            stringBuilder.append("是否群主：");
            stringBuilder.append(wifiP2pInfo.isGroupOwner ? "是群主" : "非群主");
            stringBuilder.append("\n");
            stringBuilder.append("群主IP地址：");
            stringBuilder.append(wifiP2pInfo.groupOwnerAddress.getHostAddress());
//            tv_status.setText(stringBuilder);
            if (wifiP2pInfo.groupFormed && !wifiP2pInfo.isGroupOwner) {
                SendFileActivity.this.wifiP2pInfo = wifiP2pInfo;
            }
        }

        @Override
        public void onDisconnection() {
            Log.e(TAG, "onDisconnection");
            showToast("wifi未打开");
            wifiP2pDeviceList.clear();
            deviceAdapter.notifyDataSetChanged();
//            tv_status.setText(null);
            SendFileActivity.this.wifiP2pInfo = null;
        }

        @Override
        public void onSelfDeviceAvailable(WifiP2pDevice wifiP2pDevice) {
            Log.e(TAG, "onSelfDeviceAvailable");
            Log.e(TAG, "DeviceName: " + wifiP2pDevice.deviceName);
            Log.e(TAG, "DeviceAddress: " + wifiP2pDevice.deviceAddress);
            Log.e(TAG, "Status: " + wifiP2pDevice.status);
            tv_myDeviceName.setText(wifiP2pDevice.deviceName);
            tv_myDeviceAddress.setText(wifiP2pDevice.deviceAddress);
//            tv_myDeviceStatus.setText(DeviceUtils.getDeviceStatus(wifiP2pDevice.status));
        }

        @Override
        public void onPeersAvailable(Collection<WifiP2pDevice> wifiP2pDeviceList) {
            Log.e(TAG, "onPeersAvailable :" + wifiP2pDeviceList.size());
            SendFileActivity.this.wifiP2pDeviceList.clear();
            SendFileActivity.this.wifiP2pDeviceList.addAll(wifiP2pDeviceList);
            deviceAdapter.notifyDataSetChanged();
            loadingDialog.cancel();
        }

        @Override
        public void onChannelDisconnected() {
            Log.e(TAG, "onChannelDisconnected");
        }

    };
    private Toolbar toolbar;

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void dismissLoadingDialog() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }

    private void connect() {
        WifiP2pConfig config = new WifiP2pConfig();
        if (config.deviceAddress != null && mWifiP2pDevice != null) {
            config.deviceAddress = mWifiP2pDevice.deviceAddress;
            config.wps.setup = WpsInfo.PBC;
            showLoadingDialog("正在连接 " + mWifiP2pDevice.deviceName);
            wifiP2pManager.connect(channel, config, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    Log.e(TAG, "connect onSuccess");
                }

                @Override
                public void onFailure(int reason) {
                    showToast("连接失败");
                    dismissLoadingDialog();
                }
            });
        }
    }

    private void showLoadingDialog(String message) {
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialog(this);
        }
        loadingDialog.show(message, true, false);
    }

    private void disconnect() {
        wifiP2pManager.removeGroup(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onFailure(int reasonCode) {
                Log.e(TAG, "disconnect onFailure:" + reasonCode);
            }

            @Override
            public void onSuccess() {
                Log.e(TAG, "disconnect onSuccess");
//                tv_status.setText(null);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_file);
        toolbar = findViewById(R.id.send_file_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        this.filesToSend=getIntent().getStringArrayListExtra("files_send");
        initView();
        initEvent();
        searchDevice();
    }

    private TCPClient startClient() {
        TCPClient tcpClient=null;
        try {
            IoContext.setup()
                    .ioProvider(new IoSelectorProvider())
                    .start();
            tcpClient = TCPClient.startWith(
                    new ServerInfo(TCPConstants.PORT_SERVER,wifiP2pInfo.groupOwnerAddress.getHostAddress(),""),
                    IOUtils.getFileDir());
        } catch (IOException e) {
            showToast("网络初始化失败");
        }finally {
            return tcpClient;
        }
    }

    private void initEvent() {
        wifiP2pManager = (WifiP2pManager) getSystemService(WIFI_P2P_SERVICE);
        if (wifiP2pManager == null) {
            finish();
            return;
        }
        channel = wifiP2pManager.initialize(this, getMainLooper(), directActionListener);
        broadcastReceiver = new DirectBroadcastReceiver(wifiP2pManager, channel, directActionListener);
        registerReceiver(broadcastReceiver, DirectBroadcastReceiver.getIntentFilter());
    }

    private void initView() {
        setTitle("发送文件");
        tv_myDeviceName = findViewById(R.id.tv_myDeviceName);
        tv_myDeviceAddress = findViewById(R.id.tv_myDeviceAddress);
//        tv_myDeviceStatus = findViewById(R.id.tv_myDeviceStatus);
//        tv_status = findViewById(R.id.tv_status);
        loadingDialog = new LoadingDialog(this);
        RecyclerView rv_deviceList = findViewById(R.id.rv_deviceList);
        wifiP2pDeviceList = new ArrayList<>();
        deviceAdapter = new DeviceAdapter(wifiP2pDeviceList);
        deviceAdapter.setClickListener(new DeviceAdapter.OnClickListener() {
            @Override
            public void onSendFile(int position) {
                mWifiP2pDevice = wifiP2pDeviceList.get(position);
//                showToast(mWifiP2pDevice.deviceName);
                sendFile();
            }
        });
        rv_deviceList.setAdapter(deviceAdapter);
        rv_deviceList.setLayoutManager(new LinearLayoutManager(this));
    }

    private void sendFile() {
        connect();  //连接对应的设备
        TCPClient tcpClient=tcpClientList.get(mWifiP2pDevice.deviceAddress);
        if(tcpClient==null){
            tcpClient = startClient();
            tcpClientList.put(mWifiP2pDevice.deviceAddress,tcpClient);
        }
        if (wifiP2pInfo != null) {
            for(String filePath:filesToSend){
                File file = new File(filePath);
                FileSendPacket packet = new FileSendPacket(file);
                tcpClient.send(packet);
            }
//            FileTransfer fileTransfer = new FileTransfer(file.getPath(), file.length());
//            new WifiClientTask(this, fileTransfer).execute(wifiP2pInfo.groupOwnerAddress.getHostAddress());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        for(TCPClient tcpClient:tcpClientList.values()){
            tcpClient.exit();
        }

        try {
            IoContext.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.send_file_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toolbar_search_device:
                searchDevice();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void searchDevice() {
        if (!wifiP2pEnabled) {
            showToast("需要先打开Wifi");
        }
        loadingDialog.show("正在搜索附近设备", true, false);
        wifiP2pDeviceList.clear();
        deviceAdapter.notifyDataSetChanged();
        //搜寻附近带有 Wi-Fi P2P 的设备
        wifiP2pManager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int reasonCode) {
                showToast("搜索失败");
                loadingDialog.cancel();
            }
        });
    }
}