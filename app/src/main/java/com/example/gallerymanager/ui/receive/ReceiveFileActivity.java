package com.example.gallerymanager.ui.receive;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gallerymanager.R;
import com.example.gallerymanager.utils.IOUtils;
import com.example.gallerymanager.view.LoadingDialog;
import com.example.gallerymanager.wifi.DirectActionListener;
import com.example.gallerymanager.wifi.DirectBroadcastReceiver;

import net.gallery.library.sample.foo.constants.TCPConstants;
import net.gallery.library.sample.server.TCPServer;
import net.gallery.library.clink.core.IoContext;
import net.gallery.library.clink.impl.IoSelectorProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class ReceiveFileActivity extends AppCompatActivity {

    private WifiP2pManager wifiP2pManager;

    private WifiP2pManager.Channel channel;

    private boolean connectionInfoAvailable;

    private LoadingDialog loadingDialog;

    private DirectActionListener directActionListener = new DirectActionListener() {
        @Override
        public void wifiP2pEnabled(boolean enabled) {
//            log("wifiP2pEnabled: " + enabled);
        }

        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
//            log("onConnectionInfoAvailable");
//            log("isGroupOwner：" + wifiP2pInfo.isGroupOwner);
//            log("groupFormed：" + wifiP2pInfo.groupFormed);
            if (wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner) {
                connectionInfoAvailable = true;
                if (wifiServerService != null) {
                    startService(new Intent(ReceiveFileActivity.this,WifiServerService.class));
                }
            }
        }

        @Override
        public void onDisconnection() {
            connectionInfoAvailable = false;
//            log("onDisconnection");
        }

        @Override
        public void onSelfDeviceAvailable(WifiP2pDevice wifiP2pDevice) {
//            log("onSelfDeviceAvailable");
//            log(wifiP2pDevice.toString());
        }

        @Override
        public void onPeersAvailable(Collection<WifiP2pDevice> wifiP2pDeviceList) {
//            log("onPeersAvailable,size:" + wifiP2pDeviceList.size());
            for (WifiP2pDevice wifiP2pDevice : wifiP2pDeviceList) {
//                log(wifiP2pDevice.toString());
            }
        }

        @Override
        public void onChannelDisconnected() {
//            log("onChannelDisconnected");
        }
    };

    private BroadcastReceiver broadcastReceiver;

    private WifiServerService wifiServerService;

    private ProgressDialog progressDialog;
    private TCPServer tcpServer;
    private ClientDeviceAdapter adapter;

//    private ServiceConnection serviceConnection = new ServiceConnection() {
//
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            WifiServerService.MyBinder binder = (WifiServerService.MyBinder) service;
//            wifiServerService = binder.getService();
//            wifiServerService.setProgressChangListener(progressChangListener);
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            wifiServerService = null;
//            bindService();
//        }
//    };

//    private WifiServerService.OnProgressChangListener progressChangListener = new WifiServerService.OnProgressChangListener() {
//        @Override
//        public void onProgressChanged(final FileTransfer fileTransfer, final int progress) {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    progressDialog.setMessage("文件名： " + new File(fileTransfer.getFilePath()).getName());
//                    progressDialog.setProgress(progress);
//                    progressDialog.show();
//                }
//            });
//        }
//
//        @Override
//        public void onTransferFinished(final File file) {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    progressDialog.cancel();
//                    if (file != null && file.exists()) {
////                        Glide.with(ReceiveFileActivity.this).load(file.getPath()).into(iv_image);
//                    }
//                }
//            });
//        }
//    };

//    private TextView tv_log;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_file);
        initView();
        wifiP2pManager = (WifiP2pManager) getSystemService(WIFI_P2P_SERVICE);
        if (wifiP2pManager == null) {
            finish();
            return;
        }
        channel = wifiP2pManager.initialize(this, getMainLooper(), directActionListener);
        broadcastReceiver = new DirectBroadcastReceiver(wifiP2pManager, channel, directActionListener);
        registerReceiver(broadcastReceiver, DirectBroadcastReceiver.getIntentFilter());
//        bindService();
        startServer();
        createGroup();

        RecyclerView clientDeviceList = findViewById(R.id.rv_client_deviceList);
        adapter = new ClientDeviceAdapter(new ArrayList<String>());
        clientDeviceList.setLayoutManager(new LinearLayoutManager(this));
        clientDeviceList.setAdapter(adapter);
    }

    private void startServer() {
        try {
            IoContext.setup()
                    .ioProvider(new IoSelectorProvider())
                    .start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        tcpServer = new TCPServer(TCPConstants.PORT_SERVER, IOUtils.getFileDir(), new TCPServer.TCPServerListener() {
            @Override
            public void onNewClientMessage(String msg, String ip) {

            }

            @Override
            public void onNewClientArrived(String ip) {
                adapter.addDevice(ip);
            }
        });

        boolean isSucceed = tcpServer.start();
        if (!isSucceed) {
            showToast("网络初始化失败");
            return;
        }
    }

    private void initView() {
//        setTitle("接收文件");
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setTitle("正在接收文件");
        progressDialog.setMax(100);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tcpServer.stop();
        try {
            IoContext.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        if (wifiServerService != null) {
//            wifiServerService.setProgressChangListener(null);
//            unbindService(serviceConnection);
//        }
        unregisterReceiver(broadcastReceiver);
//        stopService(new Intent(this, WifiServerService.class));
        if (connectionInfoAvailable) {
            removeGroup();
        }
    }

    public void createGroup() {
        showLoadingDialog("初始化中");

        wifiP2pManager.createGroup(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
//                log("createGroup onSuccess");
                dismissLoadingDialog();
                showToast("初始化完成");
            }

            @Override
            public void onFailure(int reason) {
//                log("createGroup onFailure: " + reason);
                dismissLoadingDialog();
                showToast("初始化失败");
            }
        });
    }

    private void dismissLoadingDialog() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }

    public void removeGroup(View view) {
        removeGroup();
    }

    private void removeGroup() {
        wifiP2pManager.removeGroup(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
//                log("removeGroup onSuccess");
//                showToast("onSuccess");
            }

            @Override
            public void onFailure(int reason) {
//                log("removeGroup onFailure");
                showToast("退出发生错误");
            }
        });
    }

//    private void log(String log) {
//        tv_log.append(log + "\n");
//        tv_log.append("----------" + "\n");
//    }

//    private void bindService() {
//        Intent intent = new Intent(ReceiveFileActivity.this, WifiServerService.class);
//        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
//    }



    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showLoadingDialog(String message) {
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialog(this);
        }
        loadingDialog.show(message, true, false);
    }

}