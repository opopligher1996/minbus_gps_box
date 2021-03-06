package com.example.cheukleong.minibus_project;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.HardwarePropertiesManager;
import android.os.IBinder;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import static com.example.cheukleong.minibus_project.Configs.TAG;

public class new_GPSTracker extends Service {
    public static String CAR_ID;
    private static final String TAG = "Gash";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;
    public static String journeyid = null;
    public static String routeid = null;
    public static Long Station_startTime;
    public static Long Station_endTime;
    public static Long Current_time;
    public static long mStartRX = 0;
    public static long mStartTX = 0;
    public static boolean first_request = true;
    public static Location Current_location = new Location("");
    public static Location Compare_location = new Location("");
    public static Location Sent_location = new Location("");
    // -1 is equal to the minbus don't arrive any station
    public static int Arr_station = -1;
    // -2 is equal to the minibus don't arrive any station before
    public static int Pre_station = -2;
    public static boolean init = false;
    public static int dans = 100;
    public static int num_satellites = 0;
    public static float hardware_temp = 0;
    public int Bat_info = 100;
    public static double go_station[][] = {
            {22.2837, 114.1588},
            {22.2841445, 114.1392645},
            {22.2836933, 114.1366914},
            {22.26823162, 114.12865509},
            {22.26642942, 114.12825444},
            {22.261973, 114.134431},
            {22.2619, 114.1319}
    };
    public static double back_station[][] = {
            {22.2619, 114.1319},
            {22.266572, 114.128184},
            {22.269442, 114.129753},
            {22.2843794, 114.13428},
            {22.2837, 114.1588}
    };

    //hard code route
    public static double test_8x_go_station[][] = {
            {22.2837, 114.1588},
            {22.2841445, 114.1392645},
            {22.2836933, 114.1366914},
            {22.26823162, 114.12865509},
            {22.26642942, 114.12825444},
            {22.261973, 114.134431},
            {22.2619, 114.1319}
    };

    public static double test_8x_back_station[][] = {
            {22.2619, 114.1319},
            {22.266572, 114.128184},
            {22.269442, 114.129753},
            {22.2843794, 114.13428},
            {22.2837, 114.1588}
    };

    public static double test_11m_go_station[][] = {
            {22.3155480062186, 114.2643589},
            {22.3239136706781, 114.2686509332},
            {22.3369558728058, 114.259236763901},
            {22.3386360145456, 114.262161534528}
    };

    public static double test_11m_back_station[][] = {
            {22.3386508695072, 114.262131520301},
            {22.3201032027678, 114.270050575393},
            {22.3169961979765, 114.270752545671},
            {22.3154247969597, 114.265771784544}
    };

    public static double test_11_go_station[][] = {
            {22.334882292983, 114.208199802671},
            {22.333872547164, 114.221120459639},
            {22.3169858225788, 114.270759645098},
            {22.3205275205682, 114.266430590858}
    };

    public static double test_11_back_station[][] = {
            {22.3205081059827, 114.266441690369},
            {22.3330777153122, 114.262999610027},
            {22.3397613538672, 114.246774213957},
            {22.3338762522471, 114.221025296549},
            {22.3343050891782, 114.210504183526}
    };

    public static Station go_stations_with_name[];

    public static Station back_stations_with_name[];

    private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;

        public LocationListener(String provider) {
            Log.i(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {

            Process process;
            try {
                process = Runtime.getRuntime().exec("cat sys/class/thermal/thermal_zone0/temp");
                process.waitFor();
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line = reader.readLine();
                if(line!=null) {
                    float temp = Float.parseFloat(line);
                    hardware_temp = temp / 1000.0f;
                }
                else{
                    hardware_temp = 0.0f;
                }
            }
            catch (Exception e) {
                    e.printStackTrace();
            }

            try {
                Set_Current_time();
                Set_Current_location(location);
                Log.i(TAG, "onLocationChanged:" + location.toString());
                double distance = Sent_location.distanceTo(location);
                if (distance > 5) {
                    Set_Sent_location(location);
                    new_GPSTracker.update_location();
                }
            }catch (Exception e){
                Log.i("Location Onchange", e.toString());
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
//            Calendar cal = Calendar.getInstance();
//            Date currentLocalTime = cal.getTime();
//            DateFormat date = new SimpleDateFormat("dd-mm-yy hh:mm:ss");
//            String localTime = date.format(currentLocalTime);
        }
    }


    public GpsStatus.Listener GpsStatusListener = new GpsStatus.Listener() {
        public void onGpsStatusChanged(int event) {
            try {
                switch (event) {
                    case GpsStatus.GPS_EVENT_FIRST_FIX:
                        num_satellites = 0;
                        break;
                    case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                        if (ActivityCompat.checkSelfPermission(new_GPSTracker.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        GpsStatus gpsStatus = mLocationManager.getGpsStatus(null);
                        int maxSatellites = gpsStatus.getMaxSatellites();
                        Iterator<GpsSatellite> iters = gpsStatus.getSatellites().iterator();
                        int count = 0;
                        while (iters.hasNext() && count <= maxSatellites) {
                            GpsSatellite s = iters.next();
                            count++;
                        }
                        new_GPSTracker.num_satellites = count;
                        break;
                }
            }
            catch (Exception e)
            {
                Log.i(TAG, "onGpsStatusChanged: "+e);
            }
        };
    };

    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initializeLocationManager();
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }


        Thread t = new Thread() {
            public void run() {
                while (true) {
                    try {
                        sleep(10000);
                        Calendar cal = Calendar.getInstance();
                        Date currentLocalTime = cal.getTime();
                        Long Current_time = currentLocalTime.getTime();
                        new_GPSTracker.Current_time = Current_time;
                        new_GPSTracker.update_location();
                        MainActivity.getConfigs();
                    } catch (Exception e) {
                    }
                }
            }
        };

        t.start();
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            }
            mLocationManager.addGpsStatusListener(GpsStatusListener);
        }
    }

    public void init() {

        if (init)
            return;
        init=true;
        Set_Compare_location(go_station[0][0], go_station[0][1]);


        double distance_start_point = LocationDistance(Current_location, Compare_location);


        Set_Compare_location(back_station[0][0], back_station[0][1]);


        double distance_end_point = LocationDistance(Current_location, Compare_location);
        if (distance_start_point < 200 && routeid == null) {
            Set_routeid(1);
            Set_journeyid();
            init = true;
        } else if (distance_end_point < 200 && routeid == null) {
            Set_routeid(2);
            Set_journeyid();
            init = true;
        }
    }

    public void Set_New_Journey() {
        Set_Arr_station(0);
        if (routeid.equals("1"))
            Set_routeid(2);
        else
            Set_routeid(1);
        Set_journeyid();
    }

    public boolean Check_Quitting(double station[][]) {
        if ((Arr_station == 0 && routeid.equals("1")) || (Arr_station == back_station.length - 1 && routeid.equals("2")))
            dans = 100;
        Set_Compare_location(station[Arr_station][0], station[Arr_station][1]);
        Log.d("Gash: ", "Debug distance = " + LocationDistance(Current_location, Compare_location));
        if (LocationDistance(Current_location, Compare_location) > dans) {
            dans = 100;
            return true;
        }
        dans = 100;
        return false;
    }


    public int Enter_Which_Station() {
        double station[][];
        if (routeid.equals("1"))
            station = go_station;
        else
            station = back_station;
        for (int i = 0; i < station.length; i++) {
            Log.e(TAG, "station " + i + " = ");
            if ((i == 0 && routeid.equals("1")) || (i == station.length - 1 && routeid.equals("2")))
                dans = 100;
            Set_Compare_location(station[i][0], station[i][1]);
            if (LocationDistance(Current_location, Compare_location) < dans) {
                dans = 100;
                return i;
            }
            dans = 100;
        }
        return -1;
    }

    public double LocationDistance(Location current_location, Location compare_location) {
        Log.e(TAG, "LocationDistance: " + current_location.distanceTo(compare_location));
        return current_location.distanceTo(compare_location);
    }

    public static void update_location() {
        Log.i(TAG, "update_location: "+Sent_location.toString());

        try {
            mStartRX = TrafficStats.getTotalRxBytes();
            mStartTX = TrafficStats.getTotalTxBytes();
            Calendar cal = Calendar.getInstance();
            Date currentLocalTime = cal.getTime();
            Long Current_time = currentLocalTime.getTime();
            new_GPSTracker.Current_time = Current_time;
            Log.i(TAG, "Start_update_location");
            Log.i(TAG, "CAR_ID = " + CAR_ID);
            Log.i(TAG, "route = " + MainActivity.choose_route);
            Log.i(TAG, "seq = " + routeid);
            Log.i(TAG, "mStartRX: "+Long.toString(mStartRX));
            Log.i(TAG, "mStartTX: "+Long.toString(mStartTX));
            Map<String, Object> jsonValues = new HashMap<String, Object>();
            jsonValues.put("lat", Sent_location.getLatitude());
            jsonValues.put("lng", Sent_location.getLongitude());
            float accuracy = Sent_location.getAccuracy();
            float speed = Sent_location.getSpeed();
            String provider = Sent_location.getProvider();
            String GPS_PROVIDER = LocationManager.GPS_PROVIDER;
            MainActivity.versionCode = 23;
            Log.i(TAG, "provider: " + provider);
            Log.i(TAG, "GPS_PROVIDER: " + GPS_PROVIDER);
            Log.i(TAG, "speed : " + speed);
            Log.i(TAG, "accuracy : " + accuracy);
            Log.i(TAG, "num_satellites: "+ Integer.toString(num_satellites));
            Log.i(TAG, "version : "+Integer.toString(MainActivity.versionCode));
            Log.i(TAG, "first_request: "+first_request);
            JSONObject update_location = new JSONObject(jsonValues);
            Log.i(TAG, "update_location: "+update_location.toString());
            Log.i(TAG, "batteryLeft: "+Integer.toString(MainActivity.battery_level));
            Log.i(TAG, "serial: "+android.os.Build.SERIAL);
            Log.i(TAG, "car_id: "+new_GPSTracker.CAR_ID);
            Log.i(TAG, "temp: "+Float.toString(hardware_temp));
            DefaultHttpClient client = new DefaultHttpClient();
            //HttpPost httppost = new HttpPost("http://minibus-staging.azurewebsites.net/api/v2/record/addLocationRecord");
            //HttpPost httppost = new HttpPost("http://minibus.azurewebsites.net/api/v2/record/addLocationRecord");
            HttpPost httppost = new HttpPost("http://minibus-api.socif.co/api/v2/record/addLocationRecord");
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

            if(first_request==true){
                nameValuePairs.add(new BasicNameValuePair("first_request", "true"));
                new_GPSTracker.first_request = false;
            }
            else{
                nameValuePairs.add(new BasicNameValuePair("first_request", "false"));
            }
            nameValuePairs.add(new BasicNameValuePair("mStartRX",Long.toString(mStartRX)));
            nameValuePairs.add(new BasicNameValuePair("mStartTX",Long.toString(mStartTX)));
            nameValuePairs.add(new BasicNameValuePair("location", update_location.toString()));
            nameValuePairs.add(new BasicNameValuePair("license", android.os.Build.SERIAL));
            nameValuePairs.add(new BasicNameValuePair("route", MainActivity.choose_route));
            nameValuePairs.add(new BasicNameValuePair("seq", routeid));
            nameValuePairs.add(new BasicNameValuePair("speed", Float.toString(speed)));
            nameValuePairs.add(new BasicNameValuePair("accuracy", Float.toString(accuracy)));
            nameValuePairs.add(new BasicNameValuePair("timestamp", Long.toString(Current_time)));
            nameValuePairs.add(new BasicNameValuePair("batteryLeft", Integer.toString(MainActivity.battery_level)));
            nameValuePairs.add(new BasicNameValuePair("provider", provider));
            nameValuePairs.add(new BasicNameValuePair("num_satellites", Integer.toString(num_satellites)));
            nameValuePairs.add(new BasicNameValuePair("version", String.valueOf(MainActivity.versionCode)));
            nameValuePairs.add(new BasicNameValuePair("serial", android.os.Build.SERIAL));
            nameValuePairs.add(new BasicNameValuePair("car_id",new_GPSTracker.CAR_ID));
            nameValuePairs.add(new BasicNameValuePair("temp",Float.toString(hardware_temp)));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            try {
                if (android.os.Build.VERSION.SDK_INT > 9) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                }
                HttpResponse response = client.execute(httppost);
                Log.i(TAG, "response: "+response.toString());
            } catch (ClientProtocolException e) {
                Log.d("Error:", "ClientProtocol");
            } catch (IOException e) {
                Log.d("Error:", "IOException");
            }
        } catch (UnsupportedEncodingException e) {
            Log.d("Error:", "UnsupportedEncodingException");
        }
        Log.d("Gash:", "Finish update location");
        return;
    }

    public void Set_Arr_station(int station_id) {
        Arr_station = station_id;
    }

    public void Set_Current_time() {
        Calendar cal = Calendar.getInstance();
        Date currentLocalTime = cal.getTime();
        Current_time = currentLocalTime.getTime();
    }

    public void Set_Current_location(Location location) {
        Current_location = location;
    }

    public void Set_Compare_location(double x, double y) {
        Compare_location.setLatitude(x);
        Compare_location.setLongitude(y);
    }

    public void Set_Sent_location(Location location) {

        Sent_location = location;
    }

    public void Set_routeid(int go) {
        routeid = String.valueOf(go);
    }

    public void Set_journeyid() {
        journeyid = Current_time + CAR_ID;
    }
}