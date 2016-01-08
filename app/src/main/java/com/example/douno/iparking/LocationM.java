package com.example.douno.iparking;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2015/10/24.
 */
public class LocationM implements LocationListener {
    //public boolean getService = false;        //是否已開啟定位服務
    static Double [] l=new Double[2];
    public static void testLocationProvider(Context mcontext) {
        //取得系統定位服務

        LocationManager status = (LocationManager) (mcontext.getSystemService(Context.LOCATION_SERVICE));
        if (status.isProviderEnabled(LocationManager.GPS_PROVIDER) || status.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            //如果GPS或網路定位開啟，呼叫locationServiceInitial()更新位置
            //Toast.makeText(mcontext, "取得位置", Toast.LENGTH_LONG).show();
            LocationManager lms;
            String bestProvider = LocationManager.GPS_PROVIDER;	//最佳資訊提供者
            lms = (LocationManager) mcontext.getSystemService(mcontext.LOCATION_SERVICE);	//取得系統定位服務
            Criteria criteria = new Criteria();	//資訊提供者選取標準
            bestProvider = lms.getBestProvider(criteria, true);	//選擇精準度最高的提供者
            Location location=null;
            location = lms.getLastKnownLocation(bestProvider);
            l[0] = location.getLongitude();	//取得經度
            l[1] = location.getLatitude();	//取得緯度
            try{
                FileWriter fw = new FileWriter("/sdcard/parking.txt");
                BufferedWriter bw = new BufferedWriter(fw); //將BufferedWeiter與FileWrite物件做連結
                bw.write(l[0] + "," + l[1]);
                bw.close();
                new Thread(runnable).start();//啟動執行序runnable
            }catch(IOException e){
                e.printStackTrace();
            }
        } else {
            Toast.makeText(mcontext, "請開啟定位服務", Toast.LENGTH_LONG).show();
            //startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));	//開啟設定頁面
        }
    }
    //連接資料庫
    static Runnable runnable = new Runnable(){
        @Override
        public void run() {
            HttpPost httpRequest =new HttpPost("http://140.120.13.246:8080/AndroidServer/GetDataServlet");
            List<NameValuePair> params =new ArrayList<NameValuePair>();
            String location= String.valueOf(l[0])+","+String.valueOf(l[1]);
            //String b="9997";
            params.add(new BasicNameValuePair("location", location));
            try{
                httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse response = httpclient.execute(httpRequest);
                //HttpEntity entity = response.getEntity();
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }

        }
    };


    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
