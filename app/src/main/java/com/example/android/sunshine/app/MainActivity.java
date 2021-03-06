package com.example.android.sunshine.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    String mLocation;
    private final String FORECAST_TAG = "FORECAST_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLocation=Utility.getPreferredLocation(this);


        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.main_container, new ForecastFragment(),FORECAST_TAG)
                    .commit();
        }
    }

    @Override
    public void onResume(){
        super.onResume();

        String savedLocation = Utility.getPreferredLocation(this);
        if(savedLocation!=null&&!(mLocation.equals(savedLocation))){

            ForecastFragment forecastFragment = (ForecastFragment)getSupportFragmentManager()
                    .findFragmentByTag(FORECAST_TAG);

            if(forecastFragment!=null){
                forecastFragment.onLocationChanged();
            }

            mLocation=savedLocation;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this,SettingsActivity.class));
            return true;
        }
        if(id==R.id.action_map){
            openPreferedLocationInMap();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    private void openPreferedLocationInMap(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String location = prefs.getString(getString(R.string.pref_location_key),
                getString(R.string.pref_location_default));

        //create geo location uri from prefred locaiton
        Uri geoLocation = Uri.parse("geo:0,0?").buildUpon()
                .appendQueryParameter("q",location).build();

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);

        //start activity  is succesful
        if(intent.resolveActivity(getPackageManager())!=null){
            startActivity(intent);
        }else{
            //TODO log error message
        }

    }
}





