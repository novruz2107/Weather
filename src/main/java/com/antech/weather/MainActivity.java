package com.antech.weather;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import pl.droidsonroids.gif.GifImageView;

import static java.lang.String.valueOf;

public class MainActivity extends AppCompatActivity {

    final long MIN_TIME = 5000;
    final int REQUEST_CODE = 123; // Request Code for permission request callback
    final float MIN_DISTANCE = 1000;

    String WEATHER_URL, WEATHER_URL2, WEATHER_URL3;
    final String APP_ID = "0eb169c9d6c02335f14c9579df3493a2";
    final String LOCATION_PROVIDER = LocationManager.NETWORK_PROVIDER;
    LocationManager mLocationManager;
    LocationListener mLocationListener;

    ImageLoader mImageLoader;
    GifImageView mGifImageView;
    TextView city, temp, mainTitle, desc2, sunrise, sunset, wspeed, wdirection, humidity, pressure;
    ImageView back;
    View view;
    String cityName;
    Boolean isFirst;

    TextView time1, time2, time3, time4, time5, time6, time7, time8, time9, time10, time11,
    time12, time13, time14, time15, time16, time17, time18, time19, time20, time21, time22;
    ImageView image1, image2, image3, image4, image5, image6, image7, image8, image9, image10, image11;

    AutocompleteFilter typeFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isFirst = true;
        handleInits();
        typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                .build();
        getWeatherForCurrentLocation();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }



    private void getWeatherForSelectedLocation(String lat, String longitude){
        WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather?lat="+lat+"&lon="+longitude+"&units=metric&APPID=0eb169c9d6c02335f14c9579df3493a2";
        WEATHER_URL2 = "http://api.openweathermap.org/data/2.5/forecast?lat="+lat+"&lon="+longitude+"&units=metric&APPID=0eb169c9d6c02335f14c9579df3493a2";
        new handleCurrentWeather().execute(WEATHER_URL);
        new handleTimeWeather().execute(WEATHER_URL2);
    }

    private void getWeatherForCurrentLocation() {

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //This is primary
                String longitude = valueOf(location.getLongitude());
                String latitude = valueOf(location.getLatitude());
                Log.d("Novruz", longitude);
                Log.d("Novruz", latitude);
                view.animate().scaleX(1.05f).setDuration(500);
                view.animate().scaleY(1.05f).setDuration(500);

                WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather?lat="+latitude+"&lon="+longitude+"&units=metric&APPID=0eb169c9d6c02335f14c9579df3493a2";
                WEATHER_URL2 = "http://api.openweathermap.org/data/2.5/forecast?lat="+latitude+"&lon="+longitude+"&units=metric&APPID=0eb169c9d6c02335f14c9579df3493a2";
                new handleCurrentWeather().execute(WEATHER_URL);
                new handleTimeWeather().execute(WEATHER_URL2);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d("Novruz", "status changed");
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.d("Novruz", "provider enabled");
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.d("Novruz", "provider disabled");
                Toast.makeText(getApplicationContext(), "You need to activate \"Use wireless networks\" in Location Settings", Toast.LENGTH_LONG);
            }
        };


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE);
            return;
        }
        mLocationManager.requestLocationUpdates(LOCATION_PROVIDER, MIN_TIME, MIN_DISTANCE, mLocationListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getWeatherForCurrentLocation();
            }else{
                Toast.makeText(getApplicationContext(), "Access denied", Toast.LENGTH_SHORT);
            }
        }
    }


    public class handleCurrentWeather extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... params) {
            String result = "";

            URL url;
            HttpURLConnection urlConnection = null;


            try {
                url = new URL(params[0]);
                urlConnection = (HttpURLConnection) url.openConnection();

                InputStreamReader reader = new InputStreamReader(urlConnection.getInputStream());
                int data = reader.read();

                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }

                return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "Internet Connection problem", Toast.LENGTH_SHORT);
                e.printStackTrace();
            }

            return null;

        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONObject details = jsonObject.getJSONArray("weather").getJSONObject(0);
                JSONObject main = jsonObject.getJSONObject("main");
                JSONObject sys = jsonObject.getJSONObject("sys");
                JSONObject wind = jsonObject.getJSONObject("wind");
                Date sunriseDate = new Date((long) sys.getDouble("sunrise") * 1000);
                Date sunsetDate = new Date((long) sys.getDouble("sunset") * 1000);
                sunrise.setText(String.valueOf(sunriseDate).split(" ")[3].split(":")[0] + ":" + String.valueOf(sunriseDate).split(" ")[3].split(":")[1]);
                sunset.setText(String.valueOf(sunsetDate).split(" ")[3].split(":")[0] + ":" + String.valueOf(sunsetDate).split(" ")[3].split(":")[1]);
                wspeed.setText(String.valueOf(wind.getDouble("speed")) + " k/h");
                if(wind.getDouble("deg") <= 20 || wind.getDouble("deg")>=340)
                    wdirection.setText("from North");
                if(wind.getDouble("deg") > 20 && wind.getDouble("deg") < 70)
                    wdirection.setText("from NE");
                if(wind.getDouble("deg") >= 70 && wind.getDouble("deg") <= 110)
                    wdirection.setText("from East");
                if(wind.getDouble("deg") > 110 && wind.getDouble("deg") < 160)
                    wdirection.setText("from SE");
                if(wind.getDouble("deg") >= 160 && wind.getDouble("deg") <= 200)
                    wdirection.setText("from South");
                if(wind.getDouble("deg") > 200 && wind.getDouble("deg") < 250)
                    wdirection.setText("from SW");
                if(wind.getDouble("deg") >= 250 && wind.getDouble("deg") <= 290)
                    wdirection.setText("from West");
                if(wind.getDouble("deg") > 290 && wind.getDouble("deg") < 340)
                    wdirection.setText("from NW");
                humidity.setText(String.valueOf(main.getDouble("humidity")) + " %");
                pressure.setText(String.valueOf(main.getDouble("pressure")) + " hPa");

                view.animate().scaleX(1f).setDuration(500);
                view.animate().scaleY(1f).setDuration(500);
                findViewById(R.id.additionalParameters).animate().alpha(1f);

                mGifImageView.animate().rotation(360f).setDuration(600);
                if(isFirst)
                    city.setText(jsonObject.getString("name").toUpperCase());
                else
                    city.setText(cityName);
                mainTitle.setText(details.getString("main").toUpperCase());
                temp.setText(String.valueOf(main.getDouble("temp")) + " °C");
                desc2.setText("Now it is " + String.valueOf(details.getString("description")) + " with " + classifyWeather(wind.getDouble("speed")));

                if(Calendar.getInstance().get(Calendar.HOUR_OF_DAY) > 8 && Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 20) {
                    switch (String.valueOf(mainTitle.getText()).toLowerCase()){
                        case "clear":
                            mGifImageView.setImageResource(R.drawable.clear_day);
                            back.setImageResource(R.drawable.clear_day_back);
                            break;
                        case "clouds":
                            Random r = new Random();
                            int rand = r.nextInt(2);
                            if(rand == 0) {
                                mGifImageView.setImageResource(R.drawable.few_clouds_day);
                                back.setImageResource(R.drawable.few_clouds_day_back);
                            }
                              else if(rand == 1) {
                                mGifImageView.setImageResource(R.drawable.scattered_clouds);
                                back.setImageResource(R.drawable.scattered_clouds_back);
                            }else{
                                mGifImageView.setImageResource(R.drawable.broken_clouds);
                                back.setImageResource(R.drawable.broken_clouds_back);
                            }
                            break;
                        case "rain":
                            mGifImageView.setImageResource(R.drawable.shower_rain);
                            back.setImageResource(R.drawable.shower_rain_back);
                            break;
                        case "drizzle":
                            mGifImageView.setImageResource(R.drawable.rain_day);
                            back.setImageResource(R.drawable.rain_back);
                            break;
                        case "thunderstorm":
                            mGifImageView.setImageResource(R.drawable.thunderstorm);
                            back.setImageResource(R.drawable.thunderstorm_back);
                            break;
                        case "snow":
                            mGifImageView.setImageResource(R.drawable.snow);
                            back.setImageResource(R.drawable.snow_back);
                            break;
                        case "atmosphere":
                            mGifImageView.setImageResource(R.drawable.mist);
                            back.setImageResource(R.drawable.mist_back);
                            break;
                    }
                }
                else{
                    switch (String.valueOf(mainTitle.getText()).toLowerCase()){
                        case "clear":
                            mGifImageView.setImageResource(R.drawable.clear_night);
                            back.setImageResource(R.drawable.clear_night_back);
                            break;
                        case "clouds":
                                mGifImageView.setImageResource(R.drawable.scattered_clouds);
                                back.setImageResource(R.drawable.scattered_clouds_back);
                            break;
                        case "rain":
                            mGifImageView.setImageResource(R.drawable.shower_rain);
                            back.setImageResource(R.drawable.shower_rain_back);
                            break;
                        case "drizzle":
                            mGifImageView.setImageResource(R.drawable.rain_night);
                            back.setImageResource(R.drawable.rain_back);
                            break;
                        case "thunderstorm":
                            mGifImageView.setImageResource(R.drawable.thunderstorm);
                            back.setImageResource(R.drawable.thunderstorm_back);
                            break;
                        case "snow":
                            mGifImageView.setImageResource(R.drawable.snow);
                            back.setImageResource(R.drawable.snow_back);
                            break;
                        case "atmosphere":
                            mGifImageView.setImageResource(R.drawable.mist);
                            back.setImageResource(R.drawable.mist_back);
                            break;
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class handleTimeWeather extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String result = "";

            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(params[0]);
                urlConnection = (HttpURLConnection) url.openConnection();

                InputStreamReader reader = new InputStreamReader(urlConnection.getInputStream());
                int data = reader.read();

                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }

                return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                findViewById(R.id.view3).animate().alpha(1f);
                findViewById(R.id.horizontalScroll).animate().alpha(1f);
                ArrayList<String> hours = new ArrayList<>();
                ArrayList<Double> temps = new ArrayList<>();
                ArrayList<String> iconCodes = new ArrayList<>();
                JSONObject jsonObject = new JSONObject(result);
                for(int i=0; i<11; i++) {
                    JSONObject jsonObject0 = jsonObject.getJSONArray("list").getJSONObject(i);
                    Date date = new Date((long) jsonObject0.getDouble("dt") *1000);
                    hours.add(String.valueOf(date).split(" ")[3].split(":")[0]);
                    JSONObject main = jsonObject0.getJSONObject("main");
                    temps.add(main.getDouble("temp"));
                    JSONObject weather = jsonObject0.getJSONArray("weather").getJSONObject(0);
                    iconCodes.add(weather.getString("icon"));
                }
                time1.setText(hours.get(0));time2.setText(valueOf(temps.get(0)) + "°");
                time3.setText(hours.get(1));time4.setText(valueOf(temps.get(1)) + "°");
                time5.setText(hours.get(2));time6.setText(valueOf(temps.get(2)) + "°");
                time7.setText(hours.get(3));time8.setText(valueOf(temps.get(3)) + "°");
                time9.setText(hours.get(4));time10.setText(valueOf(temps.get(4)) + "°");
                time11.setText(hours.get(5));time12.setText(valueOf(temps.get(5)) + "°");
                time13.setText(hours.get(6));time14.setText(valueOf(temps.get(6)) + "°");
                time15.setText(hours.get(7));time16.setText(valueOf(temps.get(7)) + "°");
                time17.setText(hours.get(8));time18.setText(valueOf(temps.get(8)) + "°");
                time19.setText(hours.get(9));time20.setText(valueOf(temps.get(9)) + "°");
                time21.setText(hours.get(10));time22.setText(valueOf(temps.get(10)) + "°");
                String url = "http://openweathermap.org/img/w/";
                mImageLoader.displayImage((url + iconCodes.get(0) + ".png"), image1);
                mImageLoader.displayImage((url + iconCodes.get(1) + ".png"), image2);
                mImageLoader.displayImage((url + iconCodes.get(2) + ".png"), image3);
                mImageLoader.displayImage((url + iconCodes.get(3) + ".png"), image4);
                mImageLoader.displayImage((url + iconCodes.get(4) + ".png"), image5);
                mImageLoader.displayImage((url + iconCodes.get(5) + ".png"), image6);
                mImageLoader.displayImage((url + iconCodes.get(6) + ".png"), image7);
                mImageLoader.displayImage((url + iconCodes.get(7) + ".png"), image8);
                mImageLoader.displayImage((url + iconCodes.get(8) + ".png"), image9);
                mImageLoader.displayImage((url + iconCodes.get(9) + ".png"), image10);
                mImageLoader.displayImage((url + iconCodes.get(10) + ".png"), image11);


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    ///////////////////////////////////////////////////////

    void handleInits(){
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);
        mImageLoader = ImageLoader.getInstance();
        view = (View) findViewById(R.id.frameLayout);
        city = (TextView) findViewById(R.id.city);
        mainTitle = (TextView) findViewById(R.id.description);
        desc2 = (TextView) findViewById(R.id.description2);
        temp = (TextView) findViewById(R.id.temp);
        mGifImageView = (GifImageView) findViewById(R.id.gifImageView);
        back = (ImageView) findViewById(R.id.imageView);
        time1 = (TextView) findViewById(R.id.textView3);
        time2 = (TextView) findViewById(R.id.textView2);
        time3 = (TextView) findViewById(R.id.textView5);
        time4 = (TextView) findViewById(R.id.textView6);
        time5 = (TextView) findViewById(R.id.textView7);
        time6 = (TextView) findViewById(R.id.textView8);
        time7 = (TextView) findViewById(R.id.textView9);
        time8 = (TextView) findViewById(R.id.textView10);
        time9 = (TextView) findViewById(R.id.textView11);
        time10 = (TextView) findViewById(R.id.textView12);
        time11= (TextView) findViewById(R.id.textView13);
        time12 = (TextView) findViewById(R.id.textView14);
        time13= (TextView) findViewById(R.id.textView15);
        time14= (TextView) findViewById(R.id.textView16);
        time15= (TextView) findViewById(R.id.textView17);
        time16= (TextView) findViewById(R.id.textView18);
        time17= (TextView) findViewById(R.id.textView19);
        time18= (TextView) findViewById(R.id.textView20);
        time19= (TextView) findViewById(R.id.textView21);
        time20= (TextView) findViewById(R.id.textView22);
        time21= (TextView) findViewById(R.id.textView23);
        time22= (TextView) findViewById(R.id.textView24);
        image1 = (ImageView) findViewById(R.id.imageView2);
        image2 = (ImageView) findViewById(R.id.imageView3);
        image3 = (ImageView) findViewById(R.id.imageView4);
        image4 = (ImageView) findViewById(R.id.imageView5);
        image5 = (ImageView) findViewById(R.id.imageView6);
        image6 = (ImageView) findViewById(R.id.imageView7);
        image7 = (ImageView) findViewById(R.id.imageView8);
        image8 = (ImageView) findViewById(R.id.imageView9);
        image9 = (ImageView) findViewById(R.id.imageView10);
        image10 = (ImageView) findViewById(R.id.imageView11);
        image11 = (ImageView) findViewById(R.id.imageView12);
        sunrise = (TextView) findViewById(R.id.sunrise);
        sunset = (TextView) findViewById(R.id.sunset);
        wspeed = (TextView) findViewById(R.id.wspeed);
        wdirection = (TextView) findViewById(R.id.wdirection);
        humidity = (TextView) findViewById(R.id.humidity);
        pressure = (TextView) findViewById(R.id.pressure);
    }

    String classifyWeather(double speed){
        String result = new String();
        if(speed<1)
            result = "calm";
        if(speed >= 1 && speed <= 5)
            result = "light air";
        if(speed > 5 && speed <= 11)
            result = "light breeze";
        if(speed > 11 && speed <= 19)
            result = "gentle breeze";
        if(speed > 19 && speed <= 28)
            result = "moderate breeze";
        if(speed > 28 && speed <= 38)
            result = "fresh breeze";
        if(speed > 38 && speed <= 49)
            result = "strong gale";
        if(speed > 49)
            result = "strong wind";
        return result;
    }

    public void handleSearchButton(View view){
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).setFilter(typeFilter)
                            .build(this);
            startActivityForResult(intent, 5);
        } catch (GooglePlayServicesRepairableException e) {
            Toast.makeText(this, "Google Play Services should be updated", Toast.LENGTH_SHORT);
        } catch (GooglePlayServicesNotAvailableException e) {
            Toast.makeText(this, "Google Play Services not available in your phone", Toast.LENGTH_SHORT);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 5) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);

                String lat = String.valueOf(place.getLatLng()).split(" ")[1].split(",")[0].substring(1, 6);
                String longitude = String.valueOf(place.getLatLng()).split(" ")[1].split(",")[1].substring(0, 5);
                cityName = String.valueOf(place.getName());
                isFirst = false;
                getWeatherForSelectedLocation(lat, longitude);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Toast.makeText(this, "Error!", Toast.LENGTH_SHORT);
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }
}