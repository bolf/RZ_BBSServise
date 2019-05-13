package com.ritm_z.bbsservice;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.IBinder;

import java.util.Set;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;

public class RZ_BarcodeService extends Service {

    BluetoothSPP bt;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        new Thread(() -> {
//            while (true) {
//                try {
//                    Thread.sleep(5000);
//                    emmitBroadcast();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
        bt = new BluetoothSPP(getApplicationContext());
        bt.setOnDataReceivedListener((data, message) -> {

            Intent ntnt = new Intent();
            ntnt.setAction("com.ritm_z.barcode.RECEIVED");
            ntnt.putExtra("text", message); //main text
            ntnt.putExtra("base", "");
            ntnt.putExtra("title", "считан штрих-код"); //for action recognition
            sendBroadcast(ntnt);

            //Toast.makeText(getApplicationContext(),"and:".concat(message),Toast.LENGTH_SHORT).show();
        });

        bt.setupService();
        bt.startService(BluetoothState.DEVICE_OTHER);

        //get all paired devices, find one we need, connect
        BluetoothAdapter mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        // Get a set of currently paired devices
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

        for (BluetoothDevice device : pairedDevices) {
            if (device.getName().contains("R1820")) {
                bt.connect(device.getAddress());
                bt.autoConnect("R1820");
            }
        }

        //Toast.makeText(getApplicationContext(),"Служба подключения сканера включена",Toast.LENGTH_SHORT).show();
        return super.onStartCommand(intent, flags, startId);
    }

//    public void emmitBroadcast(){
//        Intent intent = new Intent();
//        intent.setAction("com.ritm_z.barcode.RECEIVED");
//        intent.putExtra("text", "text"); //Основной текст сообщения
//        intent.putExtra("base", "");
//        intent.putExtra("title", "title"); //Будем использовать для определения действия
//        sendBroadcast(intent);
//    }

    @Override
    public void onDestroy() {
        bt.disconnect();
        //Toast.makeText(getApplicationContext(),"Служба подключения сканера отключена",Toast.LENGTH_SHORT).show();
    }
}
