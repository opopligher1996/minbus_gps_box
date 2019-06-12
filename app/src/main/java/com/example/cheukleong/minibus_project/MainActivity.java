package com.example.cheukleong.minibus_project;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
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

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
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


public class MainActivity extends Activity implements TextureView.SurfaceTextureListener {
    private static Context context;
    public ImageView img;
    public ImageButton sound_button;
    private  View main;
    private Button start;
    private Button button_capture;
    public static TextView show_CarId;
    private EditText Car_ID;
    private Spinner route_spinner;
    private ImageButton route_change;
    private TextView show_battery_level;
    public static  TextView station_name;
    public static  int versionCode;
    private List<String> route_ids = new ArrayList<String>();
    public static String choose_route = "11";
//    public final Context context=this;
    public static int battery_level;
    public ImageView capscreen_image_view;
    private Camera mCamera;
    private TextureView textureView;
    private SurfaceTexture surface;
    public static Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String Id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        Car_ID=findViewById(R.id.Car_ID);
        start=findViewById(R.id.startButton);
        route_spinner=findViewById(R.id.route_spinner);
        button_capture = findViewById(R.id.button_capture);
        show_CarId = findViewById(R.id.show_carid);
        route_spinner = findViewById(R.id.route_spinner);
        route_change = findViewById(R.id.route_change);
        show_battery_level = findViewById(R.id.battery_level);
        station_name = findViewById(R.id.station_name);
        capscreen_image_view = findViewById(R.id.capscreen_image_view);
        sound_button = findViewById(R.id.sound_button);
        main = findViewById(R.id.main);
        context=this;
        Car_ID.setText(Id);
        //Car_ID.setText("7591");
        route_ids.add("線路");
        route_ids.add("8x");
        route_ids.add("8");
        route_ids.add("8s");
        route_ids.add("11M");
        route_ids.add("11");
        route_ids.add("油站");
        show_CarId.setText(choose_route);

        handler=new Handler(){
            public void handleMessage(Message msg) {
                show_CarId.setText(MainActivity.choose_route);
            }
        };

        try {
            String tmp_route = readFromFile();
            Log.i(TAG, "tmp_route: "+tmp_route);
            if(tmp_route.equals("11") || tmp_route.equals("11M") || tmp_route.equals("11A") || tmp_route.equals("12") || tmp_route.equals("12A") || tmp_route.equals("11S")){
                MainActivity.choose_route = tmp_route;
                show_CarId.setText(tmp_route);
            }
        }catch (Exception e){

        }
        this.getConfigs();

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionCode = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        final MediaPlayer station_sound_player = MediaPlayer.create(this,R.raw.station);
//        textureView = (TextureView) findViewById(R.id.textureView);
//        textureView.setSurfaceTextureListener(this);
//        surface = textureView.getSurfaceTexture();


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

//        button_capture.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                try {
//                    Bitmap b = textureView.getBitmap();
//                    Log.i(TAG, "Bitmap b : "+b);
//                    capscreen_image_view.setImageBitmap(b);
//                }
//                catch (Exception e){
//                    textureView.setSurfaceTextureListener(MainActivity.this);
//                    surface = textureView.getSurfaceTexture();
//                    Log.e(TAG, "onClick: enter error" );
//                    e.printStackTrace();
//                }
//            }
//        });

        route_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,ChangeRoute.class));
            }
        });

        sound_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                station_sound_player.start();
            }
        });

//        new_GPSTracker.go_stations_with_name = get_go_stations(choose_route);
//
//        new_GPSTracker.back_stations_with_name =get_back_stations(choose_route);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            if(choose_route.equals("11M")){
                Log.i("Gash","choose 11M");
                new_GPSTracker.go_station = new_GPSTracker.test_11m_go_station;
                new_GPSTracker.back_station = new_GPSTracker.test_11m_back_station;
            }
            else if(choose_route.equals("11")){
                Log.i("Gash","choose 11");
                new_GPSTracker.go_station = new_GPSTracker.test_11_go_station;
                new_GPSTracker.back_station = new_GPSTracker.test_11_back_station;
            }
            else{
                Log.i("Gash","choose 8");
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
        if(resultCode == RESULT_OK){
            Bundle extras = data.getExtras();
            Bitmap bitmap = (Bitmap) extras.get("data");
            img = (ImageView) findViewById(R.id.image);
            img.setImageBitmap(bitmap);
        }
    }


    private void checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            Log.e(TAG, "checkCameraHardware: true");
        } else {
            // no camera on this device
            Log.e(TAG, "checkCameraHardware: false");
        }
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
            //request.setURI(new URI("http://minibus-staging.azurewebsites.net/api/v2/data/getStations/?routeid="+route+"&seq=1"));
            //request.setURI(new URI("http://minibus.azurewebsites.net/api/v2/data/getStations/?routeid="+route+"&seq=1"));
            request.setURI(new URI("http://minibus-api.socif.co/api/v2/data/getStations/?routeid="+route+"&seq=1"));
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
            //request.setURI(new URI("http://minibus-staging.azurewebsites.net/api/v2/data/getStations/?routeid="+route+"&seq=2"));
            //request.setURI(new URI("http://minibus.azurewebsites.net/api/v2/data/getStations/?routeid="+route+"&seq=2"));
            request.setURI(new URI("http://minibus-api.socif.co/api/v2/data/getStations/?routeid="+route+"&seq=2"));
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

    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        try {
            findCamera();
            mCamera = Camera.open(0);
            mCamera.lock();
            // 取得相機參數
            Camera.Parameters parameters = mCamera.getParameters();

            // 關閉閃光燈
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);

            // 設定最佳預覽尺寸
            List<Camera.Size> listPreview = parameters.getSupportedPreviewSizes();
            parameters.setPreviewSize(listPreview.get(0).width, listPreview.get(0).height);
            listPreview = null;

            // 設定最佳照片尺寸
            List<Camera.Size> listPicture = parameters.getSupportedPictureSizes();
            parameters.setPictureSize(listPicture.get(0).width, listPicture.get(0).height);
            listPicture = null;

            // 設定照片輸出為90度
            parameters.setRotation(90);

            // 設定預覽畫面為90度
            mCamera.setDisplayOrientation(90);

            // 設定相機參數
            mCamera.setParameters(parameters);

            // 設定顯示的Surface
            mCamera.setPreviewTexture(surface);
            // 開始顯示
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.unlock();
            mCamera.release();
        }
        return true;
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }


    private int findCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                Log.d(TAG, "Camera found");
                cameraId = i;
                break;
            }
        }
        Log.i(TAG, "findCamera: "+cameraId);
        return cameraId;
    }

    public static void getConfigs(){
        HttpResponse response = null;
        Log.e(TAG, "getConfigs:" );
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
            //request.setURI(new URI("http://minibus-staging.azurewebsites.net/api/v2/minibus/getCarConfigs/?license="+new_GPSTracker.CAR_ID));
            //request.setURI(new URI("http://minibus.azurewebsites.net/api/v2/minibus/getCarConfigs/?license="+new_GPSTracker.CAR_ID));
            request.setURI(new URI("http://minibus-api.socif.co/api/v2/minibus/getCarConfigs/?license="+new_GPSTracker.CAR_ID));
            response = client.execute(request);
            HttpEntity entity = response.getEntity();
            String text_responese = EntityUtils.toString(entity);
            JSONObject obj = new JSONObject(text_responese);
            JSONObject getResponse = obj.getJSONObject("response");
            Log.e(TAG, "getResponse" + getResponse.toString());
            Log.e(TAG, "getConfigs: "+getResponse.getBoolean("set") );
            boolean set = getResponse.getBoolean("set");
            if(set)
            {
                Log.e(TAG, "enter set" );
                Log.e(TAG, getResponse.getString("seq"));
                new_GPSTracker.routeid = getResponse.getString("seq");
                MainActivity.choose_route = getResponse.getString("route");
                new_GPSTracker.init=false;
                new_GPSTracker.journeyid=null;
                new_GPSTracker.Arr_station = -1;
                new_GPSTracker.Pre_station = -2;
                if(MainActivity.choose_route.equals("11M")){
                    new_GPSTracker.go_station = new_GPSTracker.test_11m_go_station;
                    new_GPSTracker.back_station = new_GPSTracker.test_11m_back_station;
                }
                else if(MainActivity.choose_route.equals("11")){
                    new_GPSTracker.go_station = new_GPSTracker.test_11_go_station;
                    new_GPSTracker.back_station = new_GPSTracker.test_11_back_station;
                }
                else{
                    new_GPSTracker.go_station = new_GPSTracker.test_11_go_station;
                    new_GPSTracker.back_station = new_GPSTracker.test_11_back_station;
                }

                handler.sendEmptyMessage(0);
                String tmp_route = "";
                try {
                    tmp_route = readFromFile();
                }catch (Exception e){}
                if(! tmp_route.equals(MainActivity.choose_route))
                {
                    delete_tmp_file();
                    write_tmp_file(MainActivity.choose_route);
                }
            }

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
    }

    public static void write_tmp_file(String route){
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("socif_temp.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(route);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public static void delete_tmp_file(){
        File dir = context.getFilesDir();
        File file = new File(dir, "socif_temp.txt");
        file.delete();
    }

    public static String readFromFile() {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput("socif_temp.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    };


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Do nothing or catch the keys you want to block
        return false;
    };

    @Override
    protected void onPause() {
        super.onPause();
    };

}
