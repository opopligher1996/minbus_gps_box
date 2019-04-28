package com.example.cheukleong.minibus_project;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import me.aflak.ezcam.EZCam;
import me.aflak.ezcam.EZCamCallback;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
import static com.example.cheukleong.minibus_project.Configs.TAG;


public class MainActivity extends Activity {
    final static int CAMERA_RESULT = 0;
    public ImageView img;
    String imageFilePath;

    private EZCam cam;
    public TextureView textureView;
    private Button start;
    private Button button_capture;
    public static TextView show_CarId;
    private EditText Car_ID;
    private Spinner route_spinner;
    private ImageButton route_change;
    private TextView show_battery_level;
    public static  TextView station_name;
    private List<String> route_ids = new ArrayList<String>();
    public static String choose_route = "11";
    public final Context context=this;
    public static int battery_level;
    public static Camera mCamera;
    private CameraPreview mPreview;
    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            Log.e(TAG, "onPictureTaken: " );
            File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            if (pictureFile == null){
                Log.d(TAG, "Error creating media file, check storage permissions");
                return;
            }
            else{
                Log.d(TAG, "create FILE");
                Log.d(TAG, data.toString());
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
            }

            restartCamera();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Car_ID=findViewById(R.id.Car_ID);
        start=findViewById(R.id.startButton);
        route_spinner=findViewById(R.id.route_spinner);
        button_capture = findViewById(R.id.button_capture);
        show_CarId = findViewById(R.id.show_carid);
        route_spinner = findViewById(R.id.route_spinner);
        route_change = findViewById(R.id.route_change);
        show_battery_level = findViewById(R.id.battery_level);
        textureView = findViewById(R.id.textureView);
        station_name = findViewById(R.id.station_name);
//        Car_ID.setText(Build.ID);
        Car_ID.setText("gps_box");
        route_ids.add("線路");
        route_ids.add("8x");
        route_ids.add("8");
        route_ids.add("8s");
        route_ids.add("11M");
        route_ids.add("11");
        route_ids.add("油站");
        show_CarId.setText(choose_route);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { // API 21
            Car_ID.setShowSoftInputOnFocus(false);
        } else { // API 11-20
            Car_ID.setTextIsSelectable(true);
        }

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Gash:","Start");
                new_GPSTracker.CAR_ID=Car_ID.getText().toString();
                Intent intent = new Intent(context, new_GPSTracker.class);
                startService(intent);
            }
        });

        button_capture.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // get an image from the camera
                        try{
                            cam.takePicture();
                        }catch (Exception e){

                        }
                    }
                }
        );

        new_GPSTracker.go_stations_with_name = get_go_stations(choose_route);

        new_GPSTracker.back_stations_with_name =get_back_stations(choose_route);

//        checkCameraHardware(this);
//
//
//        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivityForResult(i,CAMERA_RESULT);
//        mCamera = getCameraInstance();
//        mPreview = new CameraPreview(this, mCamera);
//        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
//        preview.addView(mPreview);




        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            if(choose_route.equals("11M")){
                Log.e("Gash","choose 11M");
                new_GPSTracker.go_station = new_GPSTracker.test_11m_go_station;
                new_GPSTracker.back_station = new_GPSTracker.test_11m_back_station;
            }
            else if(choose_route.equals("11")){
                Log.e("Gash","choose 11");
                new_GPSTracker.go_station = new_GPSTracker.test_11_go_station;
                new_GPSTracker.back_station = new_GPSTracker.test_11_back_station;
            }
            else{
                Log.e("Gash","choose 8");
                new_GPSTracker.go_station = new_GPSTracker.test_8x_go_station;
                new_GPSTracker.back_station = new_GPSTracker.test_8x_back_station;
            }
            start.callOnClick();
        }

        this.registerReceiver(this.mBatInfoReceiver,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

    }

    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context arg0, Intent intent) {
            // TODO Auto-generated method stub
            int level = intent.getIntExtra("level", 0);
            battery_level = level;
            show_battery_level.setText(String.valueOf(level) + "%");
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult: " );
        if(resultCode == RESULT_OK){
            Bundle extras = data.getExtras();
            Bitmap bitmap = (Bitmap) extras.get("data");
            img = (ImageView) findViewById(R.id.image);
            img.setImageBitmap(bitmap);
        }
    }

    public double[][] get_stations(String route, int seq) {
        HttpResponse response = null;
        try {
            if (android.os.Build.VERSION.SDK_INT > 9)
            {
                StrictMode.ThreadPolicy policy = new
                        StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet();
            request.setHeader("Content-Type", "application/json");
            request.setURI(new URI("http://128.199.88.79:3002/api/v2/minibus/getStations/?route="+route+"&seq="+Integer.toString(seq)));
            response = client.execute(request);
            HttpEntity entity = response.getEntity();
            String text_responese = EntityUtils.toString(entity);
            JSONObject obj = new JSONObject(text_responese);
            JSONArray array_stations = obj.getJSONArray("response");

            double results[][] = new double[array_stations.length()][2];
            for(int i = 0; i < array_stations.length(); i++)
            {
                JSONObject station = (JSONObject) array_stations.get(i);
                JSONObject station_location = new JSONObject(String.valueOf(station.get("stationLocation")));
                double station_long_lat[] = {(double) station_location.get("latitude"), (double) station_location.get("longitude")};
                results[i] = station_long_lat;
            }

            return results;

        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    };

    private void checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            Log.e(TAG, "checkCameraHardware: true");
        } else {
            // no camera on this device
            Log.e(TAG, "checkCameraHardware: false");
        }
    }

    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(0); // attempt to get a Camera instance
        }
        catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "getCameraInstance: ");
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        Log.e(TAG, "getOutputMediaFile: "+mediaStorageDir );
        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();              // release the camera immediately on pause event
    }

    private void releaseCamera(){
        if (mCamera != null){
            mCamera.release();
            mCamera = null;
        }
    }

    private void restartCamera(){
        mCamera = getCameraInstance();
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);
    }

    public static Station[] get_go_stations(String route){
        HttpResponse response = null;
        try {
            if (android.os.Build.VERSION.SDK_INT > 9)
            {
                StrictMode.ThreadPolicy policy = new
                        StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet();
            request.setHeader("Content-Type", "application/json");
            request.setURI(new URI("http://minibus-api-prod.socif.co/api/v2/data/getStations/?routeid="+route+"&seq=1"));
            response = client.execute(request);
            HttpEntity entity = response.getEntity();
            String text_responese = EntityUtils.toString(entity);
            JSONObject obj = new JSONObject(text_responese);
            JSONArray array_stations = obj.getJSONArray("response");

            Log.i(TAG, "get_all_station: "+array_stations);
            Station go_stations[] = new Station[array_stations.length()];

            for(int i =0; i<array_stations.length(); i++){
                JSONObject station = (JSONObject) array_stations.get(i);
                JSONObject station_location = station.getJSONObject("location");
                String station_name = station.getString("name");
                go_stations[i] = new Station(station_name, station_location.getDouble("lat"),station_location.getDouble("lng"));
            }
            return go_stations;

        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    };

    public static Station[] get_back_stations(String route){
        HttpResponse response = null;
        try {
            if (android.os.Build.VERSION.SDK_INT > 9)
            {
                StrictMode.ThreadPolicy policy = new
                        StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet();
            request.setHeader("Content-Type", "application/json");
            request.setURI(new URI("http://minibus-api-prod.socif.co/api/v2/data/getStations/?routeid="+route+"&seq=2"));
            response = client.execute(request);
            HttpEntity entity = response.getEntity();
            String text_responese = EntityUtils.toString(entity);
            JSONObject obj = new JSONObject(text_responese);
            JSONArray array_stations = obj.getJSONArray("response");

            Log.i(TAG, "get_all_station: "+array_stations);
            Station back_stations[] = new Station[array_stations.length()];

            for(int i =0; i<array_stations.length(); i++){
                JSONObject station = (JSONObject) array_stations.get(i);
                JSONObject station_location = station.getJSONObject("location");
                String station_name = station.getString("name");
                back_stations[i] = new Station(station_name, station_location.getDouble("lat"),station_location.getDouble("lng"));
            }
            return back_stations;

        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
