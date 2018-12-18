package net.vanhara.radek.weatherinfo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.io.*;

public class MainActivity extends AppCompatActivity {

    // Project Created by Ferdousur Rahman Shajib
    // www.androstock.com

    TextView selectCity, cityField, detailsField, currentTemperatureField, humidity_field, pressure_field, weatherIcon, updatedField;
    ProgressBar loader;
    Typeface weatherFont;
    String Lat = "49.32468166813563";
    String Lng = "17.585845068097115";
    /* Please Put your API KEY here */
    String OPEN_WEATHER_MAP_API = "e3a4db20e01d8e050fd123e80a3d80c6";
    public final static String EXTRA_MESSAGE = "net.vanhara.radek.MESSAGE";
    public String myLocationFile = "myLocationFile.dat";
    Context myContext;

    /* Please Put your API KEY here */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        myContext = this;

        loader = (ProgressBar) findViewById(R.id.loader);
        selectCity = (TextView) findViewById(R.id.selectCity);
        cityField = (TextView) findViewById(R.id.city_field);
        updatedField = (TextView) findViewById(R.id.updated_field);
        detailsField = (TextView) findViewById(R.id.details_field);
        currentTemperatureField = (TextView) findViewById(R.id.current_temperature_field);
        humidity_field = (TextView) findViewById(R.id.humidity_field);
        pressure_field = (TextView) findViewById(R.id.pressure_field);
        weatherIcon = (TextView) findViewById(R.id.weather_icon);
        weatherFont = Typeface.createFromAsset(getAssets(), "fonts/weathericons-regular-webfont.ttf");
        weatherIcon.setTypeface(weatherFont);
        loadDataFromBackup();
        taskLoadUp(Lat, Lng);

        selectCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LocationActivity.class);
                intent.putExtra(EXTRA_MESSAGE, Lat.toString()+"," + Lng.toString());
                startActivity(intent);
            }
         } );
    }

    protected void loadDataFromBackup()
    {
        try {
            FileInputStream  fileInputStream = myContext.openFileInput(myLocationFile);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            Log.i("LocationActivity", line);
            line = line.substring(10, line.length() - 1);
            String []locations = line.split(",");
            Lat = locations[0];
            Lng = locations[1];
            fileInputStream.close();
            //Log.i("LocationActivity",myPosition.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onResume(){
        super.onResume();
        loadDataFromBackup();
        taskLoadUp(Lat, Lng);
    }

    public void taskLoadUp(String Lat, String Lng) {
        if (Function.isNetworkAvailable(getApplicationContext())) {
            DownloadWeather task = new DownloadWeather();
            task.execute(Lat, Lng);
        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
        }
    }



    class DownloadWeather extends AsyncTask < String, Void, String > {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loader.setVisibility(View.VISIBLE);

        }
        protected String doInBackground(String...args) {
            String url = "http://api.openweathermap.org/data/2.5/weather?lat=" + args[0] +"&lon="+ args[1] +
                    "&units=metric&appid=" + OPEN_WEATHER_MAP_API;
            String xml = Function.excuteGet(url);
            return xml;
        }
        @Override
        protected void onPostExecute(String xml) {

            try {
                JSONObject json = new JSONObject(xml);
                if (json != null) {
                    JSONObject details = json.getJSONArray("weather").getJSONObject(0);
                    JSONObject main = json.getJSONObject("main");
                    DateFormat df = DateFormat.getDateTimeInstance();

                    cityField.setText(json.getString("name").toUpperCase(Locale.US) + ", " + json.getJSONObject("sys").getString("country"));
                    detailsField.setText(details.getString("description").toUpperCase(Locale.US));
                    currentTemperatureField.setText(String.format("%.2f", main.getDouble("temp")) + "°");
                    humidity_field.setText("Humidity: " + main.getString("humidity") + "%");
                    pressure_field.setText("Pressure: " + main.getString("pressure") + " hPa");
                    updatedField.setText(df.format(new Date(json.getLong("dt") * 1000)));
                    weatherIcon.setText(Html.fromHtml(Function.setWeatherIcon(details.getInt("id"),
                            json.getJSONObject("sys").getLong("sunrise") * 1000,
                            json.getJSONObject("sys").getLong("sunset") * 1000)));

                    loader.setVisibility(View.GONE);

                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Error, Check City", Toast.LENGTH_SHORT).show();
            }


        }



    }



}