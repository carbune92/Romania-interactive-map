package com.example.romania;

import android.os.Bundle;
import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class MyTabbedActivity extends TabActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_tabbed);
        
        Intent locationIntent = getIntent();
        String location = locationIntent.getStringExtra("Location");
         
        TabHost tabHost = getTabHost();
         
        TabSpec citySpec = tabHost.newTabSpec("City Information");
        citySpec.setIndicator("City");
        Intent cityIntent = new Intent(this, CityActivity.class);
        cityIntent.putExtra("Location", location);
        citySpec.setContent(cityIntent);
         
        
        TabSpec meteoSpec = tabHost.newTabSpec("Meteo");       
        meteoSpec.setIndicator("Meteo");
        Intent meteoIntent = new Intent(this, MeteoActivity.class);
        meteoIntent.putExtra("Location", location);
        meteoSpec.setContent(meteoIntent);
        
        tabHost.addTab(citySpec);
        tabHost.addTab(meteoSpec);
      
    }
}
