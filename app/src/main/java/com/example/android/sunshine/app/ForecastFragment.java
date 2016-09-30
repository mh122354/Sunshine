package com.example.android.sunshine.app;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.sunshine.app.data.WeatherContract;
import com.example.android.sunshine.app.data.WeatherDbHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

import static android.R.attr.id;

/**
 * Created by mh122354 on 9/18/2016.
 */
public class ForecastFragment extends Fragment {

    ArrayAdapter<String> arrayAdapter;


    public ForecastFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);


        //Array Adapter binding to List View
         arrayAdapter = new ArrayAdapter<>(getActivity(),
                R.layout.list_item_forecast,R.id.list_item_forecast_textview,new ArrayList<String>());

        ListView forecastListView = (ListView)rootView.findViewById(R.id.listview_forecast);
        forecastListView.setAdapter(arrayAdapter);
        forecastListView.setOnItemClickListener(listViewListener);


        return rootView;

    }

    @Override
    public void onStart() {
        super.onStart();
        updateWeather();
    }

    private void updateWeather(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String zipPreference = prefs.getString(getString(R.string.pref_location_key),getString(R.string.pref_location_default));
        //Refresh weather with value in location settings
        FetchWeatherTask weatherTask = new FetchWeatherTask(getActivity(),arrayAdapter);
        weatherTask.execute(zipPreference);
    }

    private AdapterView.OnItemClickListener listViewListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            String selectedForecast;
            Intent intent = new Intent(getActivity(),DetailActivity.class);
           selectedForecast= arrayAdapter.getItem(position);
            intent.putExtra(Intent.EXTRA_TEXT,selectedForecast);
            startActivity(intent);

        }
    };

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

       if(id==R.id.action_refresh){
           updateWeather();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
         long addLocation(String locationSetting,
                             String cityName,double lat, double lon){

        Context mContext = getActivity();
       long requestId;

        Cursor locationCursor = mContext.getContentResolver().query(
                WeatherContract.LocationEntry.CONTENT_URI,
                new String[]{WeatherContract.LocationEntry._ID},
                WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING+" =?",
                new String[]{locationSetting},null);
        if(locationCursor.moveToFirst()){
            int locationId = locationCursor.getColumnIndex(WeatherContract.LocationEntry._ID);
            requestId = locationCursor.getLong(locationId);

        }
        else {
            ContentValues locationValues = new ContentValues();
            locationValues.put(WeatherContract.LocationEntry.COLUMN_CITY_NAME,cityName);
            locationValues.put(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,locationSetting);
            locationValues.put(WeatherContract.LocationEntry.COLUMN_COORD_LAT,lat);
            locationValues.put(WeatherContract.LocationEntry.COLUMN_COORD_LONG,lon);

            Uri insertedUri = mContext.getContentResolver().insert(
                    WeatherContract.LocationEntry.CONTENT_URI,
                    locationValues
            );

            requestId= ContentUris.parseId(insertedUri);

        }
             locationCursor.close();

        return requestId;



    }

}

