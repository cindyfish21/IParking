package com.example.douno.iparking;

import android.app.ProgressDialog;
import android.app.Service;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.nearby.bootstrap.Device;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.Vector;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class BluetoothService extends Service {
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    private ProgressDialog progress;
    Intent intent;

    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static String EXTRA_ADDRESS  = "device_address";

    public BluetoothService() {
    }

    @Override
    public void onCreate() {
        Log.d("PrinterService", "Service started");
        super.onCreate();
        IntentFilter filter1 = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
        IntentFilter filter2 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        IntentFilter filter3 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        this.registerReceiver(mReceiver, filter1);
        this.registerReceiver(mReceiver, filter2);
        this.registerReceiver(mReceiver, filter3);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        EXTRA_ADDRESS = intent.getStringExtra("EXTRA_ADDRESS");
        Log.d("value", EXTRA_ADDRESS);
        new ConnectBT().execute();
        new Thread(runnable).start();
        mReceiver.onReceive(getApplicationContext(), intent);
        return super.onStartCommand(intent, flags, startId);
    }

     Runnable runnable = new Runnable(){
        @Override
        public void run() {
            Log.d("test", "go");
           while(true) {
                try {
                    if(isBtConnected){

                        String hex = Integer.toHexString(btSocket.getInputStream().read());
                        Log.d("Get", hex);
                        if(hex.equals("2")){
                            Log.d("GPS","Success");
                            LocationM.testLocationProvider(getApplicationContext());
                        }

                    }
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                   // e.printStackTrace();
                }
           }
        }
    };


    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {

            }
            else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                   Log.d("state","CONNECTED");
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {

            }
            else if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action)) {

            }
            else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                Log.d("state","DISCONNECTED");
                btSocket = null;
                isBtConnected=false;
            }
        }
    };

    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

//        @Override
//        protected void onPreExecute()
//        {
//            progress = ProgressDialog.show(getApplicationContext(), "Connecting...", "Please wait!!!");  //show a progress dialog
//        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try
            {
                if (btSocket == null || !isBtConnected)
                {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(EXTRA_ADDRESS);//connects to the device's address and checks if it's available
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection

                }
            }
            catch (IOException e)
            {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess) {
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                //finish();
            }
            else
            {
                msg("Connected.");
                isBtConnected = true;
            }
           // progress.dismiss();
        }
    }
    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
