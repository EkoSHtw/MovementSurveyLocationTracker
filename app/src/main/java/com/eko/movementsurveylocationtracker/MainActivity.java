package com.eko.movementsurveylocationtracker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import java.util.Date;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private Button walkingButton;
    private Button drivingButton;
    private Button tramButton;
    private Button subwayButton;
    private Button otherButton;
    private TextView usedLast;
    private File stateFile;
    private OutputStream outputStream;
    private Button busButton;
    private final int MY_PERMISSIONS_REQUESTS = 01;
    private LocationManager locationManager;
    private String provider;
    private Location location;
    private Criteria criteria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usedLast = (TextView) findViewById(R.id.used_Last_Text);
        usedLast.setText("Just started");

        walkingButton = (Button) findViewById(R.id.am_Walking_Button);
        walkingButton.setText("Walking");
        drivingButton = (Button) findViewById(R.id.am_Driving_Button);
        drivingButton.setText("Driving");
        tramButton = (Button) findViewById(R.id.am_in_Tram_Button);
        tramButton.setText("Tram");
        subwayButton = (Button) findViewById(R.id.am_in_Subway_Button);
        subwayButton.setText("Subway");
        otherButton = (Button) findViewById(R.id.other_Button);
        otherButton.setText("Other");
        busButton = (Button) findViewById(R.id.bus_button);
        busButton.setText("Bus");

        locationManager = (LocationManager) getSystemService(this.LOCATION_SERVICE);
        criteria = new Criteria();


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.INTERNET},
                    MY_PERMISSIONS_REQUESTS);
        }

        walkingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type = "Walking";
                submit(type);
            }
        });


        drivingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type = "Driving";
                submit(type);
            }
        });

        tramButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type = "Tram";
                submit(type);
            }
        });

        subwayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type = "Subway";
                submit(type);
            }
        });

        otherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type = "Other";
                submit(type);

            }
        });

        busButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type = "Bus";
                submit(type);
            }
        });




    }


    public void submit(String status){

        if(provider == null){
            provider = locationManager.getBestProvider(criteria, false);
            if(provider == null){
                Toast.makeText(this,"provider ist null", Toast.LENGTH_LONG).show();
                return;
            }
        }
        try {

            locationManager.requestLocationUpdates(provider, 100, 1.0f, this);
            location = locationManager.getLastKnownLocation(provider);
        }catch(SecurityException e){
            e.printStackTrace();
        }
        if(stateFile == null) {
            File root = Environment.getExternalStorageDirectory();
            File dir = new File(root.getAbsolutePath(), "/MovementSurvey");
            dir.mkdirs();
            stateFile = new File(dir, "StateLog.txt");
            Uri contentUri = Uri.fromFile(stateFile);
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(contentUri);
            sendBroadcast(mediaScanIntent);
        }
        try {
            OutputStream outputStream = new FileOutputStream(stateFile, true);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(outputStream);
            Date date = new Date();

            String content;
            if(location != null)
                content = date.toString() + " Current MovementType: " + status + ", Location: " + "longitude: " + location.getLongitude() + ", latitude: " + location.getLatitude() + "\n\r";
            else{
                Toast.makeText(this, "Location null", Toast.LENGTH_LONG).show();
                content = "location is null at the moment";
            }
            Log.d("MOVEMENTLOG", ", Current MovementType: " + status);
            usedLast.setText("Current status: " + status);

            myOutWriter.write(content);

            myOutWriter.flush();
            myOutWriter.close();
            outputStream.flush();
            outputStream.close();


        }catch(IOException e){
            e.printStackTrace();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUESTS: {
                // If request is cancelled, the result arrays are empty.
                for (int i =0; i< grantResults.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {

                        // permission was granted, yay! Do the
                        // contacts-related task you need to do.
                        Toast.makeText(getBaseContext(), "Granted", Toast.LENGTH_SHORT).show();
                    } else {

                        // permission denied, boo! Disable the
                        // functionality that depends on this permission.
                        Toast.makeText(getBaseContext(), "Denied", Toast.LENGTH_SHORT).show();
                    }
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "Enabled new provider " + provider,
                Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Disabled provider " + provider,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            locationManager.requestLocationUpdates(provider, 200, 1.0f, this);
        }catch(SecurityException e){
            e.printStackTrace();
        }
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

}
