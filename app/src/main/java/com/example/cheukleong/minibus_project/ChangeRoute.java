package com.example.cheukleong.minibus_project;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class ChangeRoute extends AppCompatActivity {

    public ImageButton button_11;
    public ImageButton button_11m;
    public ImageButton button_11s;
    public ImageButton button_11a;
    public ImageButton button_11b;
    public ImageButton button_12;
    public ImageButton button_12a;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_route);

        hideSystemUI();
        button_11 = findViewById(R.id.button_11);
        button_11m = findViewById(R.id.button_11m);
        button_11s = findViewById(R.id.button_11s);
        button_11a = findViewById(R.id.button_11a);
        button_11b = findViewById(R.id.button_11b);
        button_12 = findViewById(R.id.button_12);
        button_12a = findViewById(R.id.button_12a);

        button_11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("GASH: ", "button_11 onClick: ");;
                MainActivity.choose_route = "11";
                MainActivity.delete_tmp_file();
                MainActivity.write_tmp_file("11");
                MainActivity.handler.sendEmptyMessage(0);
                new_GPSTracker.go_station = new_GPSTracker.test_11_go_station;
                new_GPSTracker.back_station = new_GPSTracker.test_11_back_station;
                new_GPSTracker.go_stations_with_name = MainActivity.get_go_stations("11");
                new_GPSTracker.back_stations_with_name =MainActivity.get_back_stations("11");
                startActivity(new Intent(ChangeRoute.this, MainActivity.class));
            }
        });

        button_11m.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.choose_route = "11M";
                MainActivity.delete_tmp_file();
                MainActivity.write_tmp_file("11M");
                MainActivity.handler.sendEmptyMessage(0);
                new_GPSTracker.go_station = new_GPSTracker.test_11m_go_station;
                new_GPSTracker.back_station = new_GPSTracker.test_11m_back_station;
                new_GPSTracker.go_stations_with_name = MainActivity.get_go_stations("11M");
                new_GPSTracker.back_stations_with_name =MainActivity.get_back_stations("11M");
                startActivity(new Intent(ChangeRoute.this, MainActivity.class));
            }
        });

        button_11s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.choose_route = "11S";
                MainActivity.delete_tmp_file();
                MainActivity.write_tmp_file("11S");
                MainActivity.handler.sendEmptyMessage(0);
                new_GPSTracker.go_station = new_GPSTracker.test_11_go_station;
                new_GPSTracker.back_station = new_GPSTracker.test_11_back_station;
                new_GPSTracker.go_stations_with_name = MainActivity.get_go_stations("11S");
                new_GPSTracker.back_stations_with_name =MainActivity.get_back_stations("11S");
                startActivity(new Intent(ChangeRoute.this, MainActivity.class));
            }
        });

        button_11a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.choose_route = "11A";
                MainActivity.delete_tmp_file();
                MainActivity.write_tmp_file("11A");
                MainActivity.handler.sendEmptyMessage(0);
                new_GPSTracker.go_station = new_GPSTracker.test_11_go_station;
                new_GPSTracker.back_station = new_GPSTracker.test_11_back_station;
                new_GPSTracker.go_stations_with_name = MainActivity.get_go_stations("11A");
                new_GPSTracker.back_stations_with_name =MainActivity.get_back_stations("11A");
                startActivity(new Intent(ChangeRoute.this, MainActivity.class));
            }
        });

        button_11b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.choose_route = "11B";
                MainActivity.delete_tmp_file();
                MainActivity.write_tmp_file("11B");
                MainActivity.handler.sendEmptyMessage(0);
                new_GPSTracker.go_station = new_GPSTracker.test_11_go_station;
                new_GPSTracker.back_station = new_GPSTracker.test_11_back_station;
                new_GPSTracker.go_stations_with_name = MainActivity.get_go_stations("11B");
                new_GPSTracker.back_stations_with_name =MainActivity.get_back_stations("11B");
                startActivity(new Intent(ChangeRoute.this, MainActivity.class));
            }
        });

        button_12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.choose_route = "12";
                MainActivity.delete_tmp_file();
                MainActivity.write_tmp_file("12");
                MainActivity.handler.sendEmptyMessage(0);
                new_GPSTracker.go_station = new_GPSTracker.test_11_go_station;
                new_GPSTracker.back_station = new_GPSTracker.test_11_back_station;
                new_GPSTracker.go_stations_with_name = MainActivity.get_go_stations("12");
                new_GPSTracker.back_stations_with_name =MainActivity.get_back_stations("12");
                startActivity(new Intent(ChangeRoute.this, MainActivity.class));
            }
        });

        button_12a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.choose_route = "12A";
                MainActivity.delete_tmp_file();
                MainActivity.write_tmp_file("12A");
                MainActivity.handler.sendEmptyMessage(0);
                new_GPSTracker.go_station = new_GPSTracker.test_11_go_station;
                new_GPSTracker.back_station = new_GPSTracker.test_11_back_station;
                new_GPSTracker.go_stations_with_name = MainActivity.get_go_stations("12A");
                new_GPSTracker.back_stations_with_name =MainActivity.get_back_stations("12A");
                startActivity(new Intent(ChangeRoute.this, MainActivity.class));
            }
        });


    };

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    };
}
