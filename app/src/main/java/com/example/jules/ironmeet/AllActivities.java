package com.example.jules.ironmeet;

import android.Manifest;
import android.app.ActionBar;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.vision.text.Line;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.concurrent.ExecutionException;

public class AllActivities extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private FusedLocationProviderClient mFusedLocationClient;

    private static final int PERMISSION_ACCESS_COARSE_LOCATION = 1;
    private GoogleApiClient googleApiClient;

    private Location loc = null;
    private NestedScrollView scroll = null;
    private LinearLayout scrollLayout = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_activities);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Récupérer toutes les activités
        //Mais premièremeent récupérer la géolocalisation
        // Acquire a reference to the system Location Manager
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_COARSE_LOCATION },
                    PERMISSION_ACCESS_COARSE_LOCATION);
        }

        googleApiClient = new GoogleApiClient.Builder(this, this, this).addApi(LocationServices.API).build();

        scroll = (NestedScrollView) findViewById(R.id.scroll);
        scrollLayout = (LinearLayout) findViewById(R.id.l);


    }

    public String formatText(String s){
        StringBuilder sb = new StringBuilder(s);
        sb.deleteCharAt(0);
        /*String firstLetter = String.valueOf(s.charAt(1));
        firstLetter.toUpperCase();
        sb.deleteCharAt(s.length()-2);
        sb.setCharAt(1, firstLetter.charAt(0)) ;*/
        sb.deleteCharAt(s.length()-2);
        s = sb.toString();
        s = s.substring(0,1).toUpperCase() + s.substring(1);

        return s;
    }

    public void fecthActivities(double x, double y){
        RequestTask getfollow = new RequestTask(getApplicationContext());
        String urlGetFollow = "http://juleseschbach.com/ironmeet/fetchActivity.php?geo=" + String.valueOf(x) + ";" + String.valueOf(y);

        //Result est le fichier json avec toutes les activités
        String result = null;
        try {
            result = getfollow.execute(urlGetFollow).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }



        if(result != null){
            //Toast.makeText(this, result.toString(), Toast.LENGTH_SHORT).show();
            for(int j = 0; j < 10; j++){
                JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                jsonObject = jsonObject.getAsJsonObject("1");

                //Toast.makeText(this, jsonObject.get("name").toString(), Toast.LENGTH_SHORT).show();

                //Ajouter tout dans le scrollview

                LinearLayout l1 = new LinearLayout(this);
                l1.setOrientation(LinearLayout.VERTICAL);

                ImageView topbar = new ImageView(this);
                topbar.setMinimumHeight(20);
                topbar.setBackgroundColor(Color.rgb(142, 35, 35));
                l1.addView(topbar);

                LinearLayout top1 = new LinearLayout(this);
                top1.setOrientation(LinearLayout.HORIZONTAL);
                top1.setMinimumHeight(200);
                top1.setBackgroundColor(Color.rgb(234, 234,234));

                LinearLayout topleft = new LinearLayout(this);
                topleft.setOrientation(LinearLayout.VERTICAL);

                topleft.setLayoutParams(new TableLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
                topleft.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                LinearLayout bottom = new LinearLayout(this);
                bottom.setOrientation(LinearLayout.HORIZONTAL);
                bottom.setMinimumHeight(200);
                bottom.setBackgroundColor(Color.rgb(234, 234, 234));

                TextView name = new TextView(this);
                TextView title = new TextView(this);
                TextView location = new TextView(this);
                TextView speed = new TextView(this);
                TextView distance = new TextView(this);
                TextView datehour = new TextView(this);

                name.setText(formatText(jsonObject.get("meneur").toString()));
                title.setText(formatText(jsonObject.get("name").toString()));
                location.setText("unknown");//a bosser la géoloc ici
                speed.setText(formatText(jsonObject.get("speed").toString()));
                distance.setText(formatText(jsonObject.get("distance").toString()));
                speed.setText(formatText(jsonObject.get("speed").toString()));
                datehour.setText(formatText(jsonObject.get("date").toString()) + " " + formatText(jsonObject.get("heure").toString()));

                name.setTextSize(20f);
                name.setTypeface(null, Typeface.BOLD);
                name.setTextColor(Color.BLACK);

                location.setLayoutParams(new TableLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
                location.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                speed.setLayoutParams(new TableLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.MATCH_PARENT, 1f));
                speed.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                speed.setGravity(Gravity.CENTER);

                distance.setLayoutParams(new TableLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.MATCH_PARENT, 1f));
                distance.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                distance.setGravity(Gravity.CENTER);

                datehour.setLayoutParams(new TableLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.MATCH_PARENT, 1f));
                datehour.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                datehour.setGravity(Gravity.CENTER);

                ImageView map = new ImageView(this);
                map.setMinimumHeight(500);
                map.setBackgroundColor(Color.BLUE);

                topleft.addView(name);
                topleft.addView(title);

                bottom.addView(speed);
                bottom.addView(distance);
                bottom.addView(datehour);

                top1.addView(topleft);
                top1.addView(location);

                l1.addView(top1);
                l1.addView(map);
                l1.addView(bottom);

                scrollLayout.addView(l1);
            }


        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_all_activities, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    //-------------------------------------------------------------------

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ACCESS_COARSE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // All good!
                } else {
                    Toast.makeText(this, "Need your location!", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(AllActivities.class.getSimpleName(), "Connected to Google Play Services!");

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

            double lat = lastLocation.getLatitude(), lon = lastLocation.getLongitude();
            Toast.makeText(this,String.valueOf(lat) + "  " + String.valueOf(lon), Toast.LENGTH_SHORT).show();
            fecthActivities(lat, lon);

        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(AllActivities.class.getSimpleName(), "Can't connect to Google Play Services!");
    }
}
