package com.example.douno.iparking;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsFragment extends Fragment {

    private static View view;
    private static GoogleMap mMap;
    private static Double latitude_target, longitude_target;
    public static String latitude_string = null, longitude_string = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLocation();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(container==null)
            return null;
        view = (RelativeLayout)inflater.inflate(R.layout.fragment_maps, container, false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(mMap != null) {
            //mMap.clear();  //第一次成功開啟地圖之外，每次開啟fragment都先初始化mMap
            mMap = null;
        }
        setUpMapIfNeeded();
    }

    //取得位置
    private void setLocation() {
        latitude_string = getArguments().getString("latitude_string");
        longitude_string = getArguments().getString("longitude_string");
        latitude_target = Double.parseDouble(latitude_string);
        longitude_target = Double.parseDouble(longitude_string);
    }

    //如果可以產生地圖 就畫出地圖
    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.location_map)).getMap();
            if (mMap != null)  //可以產生
                setUpMap();
        }
    }


    //設置地圖
    private static void setUpMap() {
        mMap.setMyLocationEnabled(true);  //顯示自己的位置
        MarkerOptions makerOptions = new MarkerOptions();
        makerOptions.position(new LatLng(latitude_target, longitude_target));
        makerOptions.title("你的車子");
        makerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.parking));
        mMap.addMarker(makerOptions);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude_target, longitude_target), 15f));  //設定初始縮放
    }
}
