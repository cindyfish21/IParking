package com.example.douno.iparking;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static FragmentManager fragmentManager;
    String []location_array=new String[2];
    static String latitude_string, longitude_string;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //呼叫滑動選單的按鈕在toolbar(最上方那一條)
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //toolbar按鈕呼叫左邊滑動選單
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);  //先找到layout
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);  //再把drawer layout跟 toobar綁在一起
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //左邊滑動選單
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // initialising the object of the FragmentManager. Here I'm passing getSupportFragmentManager(). You can pass getFragmentManager() if you are coding for Android 3.0 or above.
        fragmentManager = getSupportFragmentManager();

        //如果已經有綁定
        if (true) {
            Fragment fragment = null;
            fragment = new BluetoothFragment();
            fragmentManager.beginTransaction().replace(R.id.content, fragment).commit();
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;

        switch(id) {
            case R.id.nav_map:
                //讀位置
                FileReader fr= null;
                try {
                    fr = new FileReader("/sdcard/parking.txt");
                    BufferedReader bw = new BufferedReader(fr);
                    String location =bw.readLine();
                    location_array=location.replaceAll("![.0^9]","").split(",");
                    latitude_string = location_array[1].trim();
                    longitude_string = location_array[0].trim();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //傳值
                Bundle bundle = new Bundle();
                bundle.putString("latitude_string",latitude_string);  //緯度
                bundle.putString("longitude_string", longitude_string);  //經度
                fragment = new MapsFragment();
                fragment.setArguments(bundle);
                break;
            case R.id.nav_bluetooth:
                fragment = new BluetoothFragment();
                break;
            case R.id.nav_manage:
                //還不知道要放什麼
                break;
        }

        fragmentManager.beginTransaction().replace(R.id.content, fragment).commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
