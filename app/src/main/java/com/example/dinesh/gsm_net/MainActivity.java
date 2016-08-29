package com.example.dinesh.gsm_net;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.Calendar;

public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    public Button start, stop, restart;
    public  TextView text;
    private LocationManager locationManagerNET;
    private LocationManager locationManagerGPS;
    public Location s;
    public int flagstrt = 0, flagstop = 0;
    public File file;
    public TextView fname;
    public String sfile="";
    public double gpslat,gpslon,gpsacc,netlat,netlon,netacc;
    public TelephonyManager tm;
    public String cellid;
    public String operatorName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text = (TextView) findViewById(R.id.text);
        start = (Button) findViewById(R.id.start);
        stop = (Button) findViewById(R.id.stop);
        restart = (Button) findViewById(R.id.restart);
        fname = (TextView)findViewById(R.id.fname);

        start.setOnClickListener(this);
        stop.setOnClickListener(this);
        restart.setOnClickListener(this);
        locationManagerNET = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationManagerGPS = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    0);

            return;
        }


    }


    LocationListener locationListenerNET = new LocationListener() {
        public void onLocationChanged(Location location) {

//            Toast.makeText(MainActivity.this, "location changed", Toast.LENGTH_SHORT).show();
//            Log.d("Listener", "Location changed");

            Calendar c = Calendar.getInstance();
            int hr = c.get(Calendar.HOUR);
            int mn = c.get(Calendar.MINUTE);
            int sec = c.get(Calendar.SECOND);

            GsmCellLocation loc=(GsmCellLocation)tm.getCellLocation();
            cellid = String.valueOf(loc.getCid() & 0xffff);
            operatorName = tm.getSimOperatorName();

            netlat = location.getLatitude();
            netlon = location.getLongitude();
            netacc = location.getAccuracy();

            sfile = sfile + hr+"::"+mn+"::"+sec+" || "+gpslat+" || "+gpslon+" || "+gpsacc+" || "+netlat+" || "+netlon+" || "+netacc+" || "+cellid+" || "+operatorName+"\n";
            text.setText(hr+"::"+mn+"::"+sec+" || "+gpslat+" || "+gpslon+" || "+gpsacc+" || "+netlat+" || "+netlon+" || "+netacc+" || "+cellid+" || "+operatorName);

        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    };


    LocationListener locationListenerGPS = new LocationListener() {
        public void onLocationChanged(Location location) {

//            Toast.makeText(MainActivity.this, "location changed", Toast.LENGTH_SHORT).show();
//            Log.d("Listener", "Location changed");

            Calendar c = Calendar.getInstance();
            int hr = c.get(Calendar.HOUR);
            int mn = c.get(Calendar.MINUTE);
            int sec = c.get(Calendar.SECOND);

            GsmCellLocation loc=(GsmCellLocation)tm.getCellLocation();
            cellid = String.valueOf(loc.getCid() & 0xffff);
            operatorName = tm.getSimOperatorName();

            gpslat = location.getLatitude();
            gpslon = location.getLongitude();
            gpsacc = location.getAccuracy();

            sfile = sfile + hr+"::"+mn+"::"+sec+" || "+gpslat+" || "+gpslon+" || "+gpsacc+" || "+netlat+" || "+netlon+" || "+netacc+" || "+cellid+" || "+operatorName+"\n";
            text.setText(hr+"::"+mn+"::"+sec+" || "+gpslat+" || "+gpslon+" || "+gpsacc+" || "+netlat+" || "+netlon+" || "+netacc+" || "+cellid+" || "+operatorName);

        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    };


    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.start && flagstrt==0 && flagstop==0) {
            tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            locationManagerNET = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            locationManagerNET.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNET);
            locationManagerGPS = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            locationManagerGPS.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGPS);

            Toast.makeText(MainActivity.this, " Data Collection Started ", Toast.LENGTH_SHORT).show();
            flagstrt=1;

        }

        if (v.getId() == R.id.stop && flagstrt==1 && flagstop==0) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            flagstop=1;

            if(locationManagerNET!=null){
                locationManagerNET.removeUpdates(locationListenerNET);
                locationManagerNET = null;
            }

            if(locationManagerGPS!=null){
                locationManagerGPS.removeUpdates(locationListenerGPS);
                locationManagerGPS = null;
            }

            text.setText("DONE!!!");
            writeToFile(sfile);

            Toast.makeText(MainActivity.this, " Data Collection Stopped ", Toast.LENGTH_SHORT).show();

        }
        if(v.getId()==R.id.restart){
            if(locationManagerNET!=null){
                locationManagerNET.removeUpdates(locationListenerNET);
                locationManagerNET = null;
                }

            if(locationManagerGPS!=null){
                locationManagerGPS.removeUpdates(locationListenerGPS);
                locationManagerGPS = null;
            }
            Intent i = getBaseContext().getPackageManager()
                    .getLaunchIntentForPackage( getBaseContext().getPackageName() );
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            startActivity(i);
        }
    }
    public boolean writeToFile(String data)
    {
        try
        {    Toast.makeText(getApplicationContext(), "writing to file", Toast.LENGTH_SHORT).show();
            File file=new File(getExternalFilesDir(null).toString());
            file.mkdirs();
            File f=new File(file, fname.getText().toString());
            //Log.d("nkn", String.valueOf(fname));
            FileWriter fw=new FileWriter(f,true);
            BufferedWriter out=new BufferedWriter(fw);
            out.append(data);
            out.close();
            return true;
        }
        catch(FileNotFoundException f)
        {
            return false;
        }
        catch(Exception e)
        {
            return false;
        }
    }
}






