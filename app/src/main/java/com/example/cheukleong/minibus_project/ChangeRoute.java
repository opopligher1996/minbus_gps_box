package com.example.cheukleong.minibus_project;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class ChangeRoute extends AppCompatActivity {

    public ImageButton button_11;
    public ImageButton button_11m;
    public ImageButton button_11s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_route);

        button_11 = findViewById(R.id.button_11);
        button_11m = findViewById(R.id.button_11m);
        button_11s = findViewById(R.id.button_11s);


        button_11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.show_CarId.setText("11");
                MainActivity.choose_route = "11";
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
                MainActivity.show_CarId.setText("11M");
                MainActivity.choose_route = "11M";
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
                MainActivity.show_CarId.setText("11S");
                MainActivity.choose_route = "11S";
                new_GPSTracker.go_station = new_GPSTracker.test_11_go_station;
                new_GPSTracker.back_station = new_GPSTracker.test_11_back_station;
                new_GPSTracker.go_stations_with_name = MainActivity.get_go_stations("11S");
                new_GPSTracker.back_stations_with_name =MainActivity.get_back_stations("11S");
                startActivity(new Intent(ChangeRoute.this, MainActivity.class));
            }
        });
    }
}
