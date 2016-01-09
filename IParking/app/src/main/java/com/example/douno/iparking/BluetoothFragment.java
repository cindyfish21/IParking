package com.example.douno.iparking;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class BluetoothFragment extends Fragment {

    private static View view;
    private Button Devices_Button;
    private TextView textView_bounded;
    private TextView textView_unbounded;
    private ListView devicelist;
    private ListView deviceList_unbounded;

    private BluetoothAdapter myBluetooth = null;
    Intent turnBTon;
    private Set<BluetoothDevice> pairedDevices;
    public static String EXTRA_ADDRESS = "device_address";
    ArrayList list = new ArrayList();
    ArrayList list_unBounded = new ArrayList();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(container==null)
            return null;
        view = (RelativeLayout)inflater.inflate(R.layout.fragment_bluetooth, container, false);

        textView_bounded = (TextView) view.findViewById(R.id.textView);
        textView_unbounded = (TextView) view.findViewById(R.id.textView_unBounded);
        devicelist = (ListView)view.findViewById(R.id.listView);
        deviceList_unbounded = (ListView) view.findViewById(R.id.listView_unBounded);
        Devices_Button = (Button)view.findViewById(R.id.Devices_Button);


        myBluetooth = BluetoothAdapter.getDefaultAdapter();

        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        //註冊廣播接收器
        getActivity().registerReceiver(mReceiver, intentFilter);

        if(myBluetooth == null)
        {
            //Show a mensag. that the device has no bluetooth adapter
            Toast.makeText(view.getContext(), "Bluetooth Device Not Available", Toast.LENGTH_LONG).show();
        }
        else if(!myBluetooth.isEnabled())
        {
            //Ask to the user turn the bluetooth on
            turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mReceiver.onReceive(getContext(), turnBTon);
            startActivityForResult(turnBTon,1);
        }

        Devices_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                textView_bounded.setVisibility(View.INVISIBLE);
                textView_unbounded.setVisibility(View.INVISIBLE);
                list.clear();
                list_unBounded.clear();
                new Thread(runnable).start();
                //for (BluetoothDevice bt : myBluetooth.getName())

                pairedDevicesList();
            }
        });
        return view;
    }
    private void pairedDevicesList()
    {
        pairedDevices = myBluetooth.getBondedDevices();


        if (pairedDevices.size()>0)
        {
            for(BluetoothDevice bt : pairedDevices)
            {
                list.add(bt.getName() + "\n" + bt.getAddress()); //Get the device's name and the address
                textView_bounded.setVisibility(View.VISIBLE);
            }
        }
        else
        {
            Toast.makeText(view.getContext(), "No Paired Bluetooth Devices Found.", Toast.LENGTH_LONG).show();
        }

        ArrayAdapter adapter = new ArrayAdapter(view.getContext(),android.R.layout.simple_list_item_1, list);
        devicelist.setAdapter(adapter);
        devicelist.setOnItemClickListener(myListClickListener); //Method called when the device from the list is clicked

    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(mReceiver);
        super.onDestroy();

    }

    Runnable runnable = new Runnable(){
        @Override
        public void run() {

            myBluetooth.startDiscovery();


        }
    };



    private AdapterView.OnItemClickListener myListClickListener = new AdapterView.OnItemClickListener()
    {
        public void onItemClick (AdapterView<?> av, View v, int arg2, long arg3)
        {
            // Get the device MAC address, the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);

            // Make an intent to start next activity.
            Intent intent = new Intent(getActivity(), BluetoothService.class);
            Bundle bundle = new Bundle();
            bundle.putString("EXTRA_ADDRESS", address);  //藍芽address
            intent.putExtras(bundle);
            getActivity().startService(intent);
        }
    };
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //獲得掃描到的遠端藍牙設備
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //System.out.println(device.getAddress());
                Log.i("1111","1");
                textView_unbounded.setVisibility(View.VISIBLE);
                list_unBounded.add(device.getName() + "\n" + device.getAddress());
                ArrayAdapter adapter = new ArrayAdapter(view.getContext(),android.R.layout.simple_list_item_1, list_unBounded);
                deviceList_unbounded.setAdapter(adapter);
                deviceList_unbounded.setOnItemClickListener(myListClickListener); //Method called when the device from the list is clicked
            }
        }
    };
}
