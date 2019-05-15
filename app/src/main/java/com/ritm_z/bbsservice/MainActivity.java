package com.ritm_z.bbsservice;

import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.Set;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Toast.makeText(getApplicationContext(),"onCreate MainActivity",Toast.LENGTH_LONG).show();

        stopPreviouslyStartedService();

        BluetoothSPP bt = new BluetoothSPP(getApplicationContext());

        if(bt.isBluetoothEnabled()) {
            bt.setupService();
            bt.startService(BluetoothState.DEVICE_OTHER);

            //get all paired devices, find one we need, connect
            BluetoothAdapter mBtAdapter = BluetoothAdapter.getDefaultAdapter();

            // Get a set of currently paired devices
            Set<BluetoothDevice>  pairedDevices = mBtAdapter.getBondedDevices();

            // If there are paired devices, add each one to the ArrayAdapter
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    if(device.getName().contains("R1820") || device.getName().toLowerCase().contains("scanner")){
                        //start service only if appropriate dev is found
                        startService(new Intent(this, RZ_BarcodeService.class));
                        finish();
                        return;
                    }
                }
                Toast.makeText(getApplicationContext(),"Не найдено ранее ожидаемых спаренных устройств",Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(getApplicationContext(),"Не найдено ранее спаренных устройств",Toast.LENGTH_LONG).show();
                finish();
            }

        }else{
            Toast.makeText(getApplicationContext(),"Включите bluetooth и повторите попытку",Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void stopPreviouslyStartedService(){
        boolean serviceIsRunning = false;
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (RZ_BarcodeService.class.getName().equals(service.service.getClassName())) {
                serviceIsRunning = true;
            }
        }
        if(serviceIsRunning) {
            stopService(new Intent(MainActivity.this, RZ_BarcodeService.class));
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}