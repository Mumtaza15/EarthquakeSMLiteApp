package com.example.earthquakesmliteapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity implements LocationListener {

    GoogleMap mGoogleMap;
    ProgressBar pbMap;
//    Spinner spPlace;
//    String [] sPlace = {"park", "stadium", "hospital"};
//    String xPlace;

    double mLatitude = 0;
    double mLongitude = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        setID();

        Spinner spinnerCari = findViewById(R.id.spPlace);
        pbMap = findViewById(R.id.pbMap);

//        // Construct a PlacesClient
//        Places.initialize(getApplicationContext(), getString(R.string.google_map_key));
//        placesClient = Places.createClient(this);
//
//        // Construct a FusedLocationProviderClient.
//        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


        SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fMap);
        fragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mGoogleMap = googleMap;
                initMap();
//                enableMyLocation();
                setPoiClicked(mGoogleMap);
            }
        });

        ArrayAdapter<String> myAdapter = new ArrayAdapter<>(MainActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.cari_tempat));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCari.setAdapter(myAdapter);
        spinnerCari.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long i) {
                String xPlace ="";
                if (position == 1)
                    xPlace = "park";
                else if (position == 2)
                    xPlace = "stadium";
                else if (position == 3)
                    xPlace = "hospital";
                if (position != 0) {
                    String sb = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" +
                            "location=" + mLatitude + "," + mLongitude +
                            "&radius=5000" +
                            "&types=" + xPlace +
                            "&key=" + getResources().getString(R.string.google_map_key);
//                    String sb = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=mLatitude,mLongitude&radius=500&key=AIzaSyAgWvPM1ZiB3tAtJzbojWaAwmkviv-MHYU";
//                    .initialize(getApplicationContext(),"@string/API_KEY");
                    startProg();
                    new PlacesTask().execute(sb);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

//    private void enableMyLocation(){
//        if (ContextCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_FINE_LOCATION)
//                == PackageManager.PERMISSION_GRANTED){
//            mGoogleMap.setMyLocationEnabled(true);
//        }
//        else {
//            ActivityCompat.requestPermissions(this, new String[]{
//                    Manifest.permission.ACCESS_FINE_LOCATION}, 1);
//        }
//    }

        private void initMap() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 115);
            return;
        }

        if (mGoogleMap != null){
            startProg();
            mGoogleMap.setMyLocationEnabled(true);

            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider(criteria, true);
            Location location = locationManager.getLastKnownLocation(provider);

            if (location != null){
                onLocationChanged(location);
            }else
                stopProg();
            locationManager.requestLocationUpdates(provider, 20000, 0, this);
        }
    }

//    Menerima request, jika berhasil langsung mengaktifkan lokasi
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1 :
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                    enableMyLocation();
                    initMap();
                    break;
                }
        }
    }

//    @Override
//    public void onLocationChanged(@NonNull Location location) {
//        mLatitude = location.getLatitude();
//        mLongitude = location.getLongitude();
//        LatLng latLng = new LatLng(mLatitude, mLongitude);
//
//        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(20));
//        stopProg();
//    }

    private void setPoiClicked(final GoogleMap map){
        map.setOnPoiClickListener(new GoogleMap.OnPoiClickListener() {
            @Override
            public void onPoiClick(@NonNull PointOfInterest pointOfInterest) {
                Marker poiMarker = mGoogleMap.addMarker(new MarkerOptions()
                        .position(pointOfInterest.latLng)
                        .title(pointOfInterest.name)
                        //Kasih icon untuk naruh info tempat
                        .icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_info)));

                poiMarker.showInfoWindow();
            }
        });
    }

    private BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {
        // below line is use to generate a drawable.
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);

        // below line is use to set bounds to our vector drawable.
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());

        // below line is use to create a bitmap for our
        // drawable which we have added.
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        // below line is use to add bitmap in our canvas.
        Canvas canvas = new Canvas(bitmap);

        // below line is use to draw our
        // vector drawable in canvas.
        vectorDrawable.draw(canvas);

        // after generating our bitmap we are returning our bitmap.
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onLocationChanged(Location location) {
        mLatitude = location.getLatitude();
        mLongitude = location.getLongitude();
        LatLng latLng = new LatLng(mLatitude, mLongitude);
        /*Circle circle = mGoogleMap.addCircle((new CircleOptions()
                .center(new LatLng(mLatitude, mLongitude))
                .radius(500)
                .strokeWidth(6)
                .strokeColor(0xffff0000)
                .fillColor(0x55ff0000)));*/
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(8));
        stopProg();
    }

    private void stopProg() {
        pbMap.setVisibility(View.GONE);
    }

    private void startProg() {
        pbMap.setVisibility(View.VISIBLE);
    }

    @SuppressLint("StaticFieldLeak")
    private class PlacesTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... url) {
            String data = null;
            try {
                data = downloadUrl (url[0]);
            } catch (Exception e) {
                stopProg();
                e.printStackTrace();
            }
            return data;
        }

        protected void onPostExecute(String result){
            new ParserTask().execute(result);
        }
    }

    private String downloadUrl(String strUrl) {
        String data= "";
        InputStream iStream;
        HttpURLConnection urlConnection;
        try {
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();

            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuilder sb = new StringBuilder();

            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            br.close();
            iStream.close();
            urlConnection.disconnect();
        } catch (Exception e){
            e.printStackTrace();
        }
        return data;
    }

    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>>{
        JSONObject jObject;

        @Override
        protected List<HashMap<String, String>> doInBackground(String... jsonData) {
            List<HashMap<String, String>> places = null;
            ParserPlace parserPlace = new ParserPlace();
            try {
                jObject = new JSONObject(jsonData[0]);
                places = parserPlace.parse(jObject);
            } catch (Exception e){
                stopProg();
                e.printStackTrace();
            }
            return places;
        }

        //untuk menampilkan jumlah lokasi terdekat
        @Override
        protected void onPostExecute(List<HashMap<String, String>> list) {
        mGoogleMap.clear();
        for (int i = 0; i < list.size(); i++){
            MarkerOptions markerOptions = new MarkerOptions();
            HashMap<String, String> hmPlace = list.get(i);
            BitmapDescriptor pinDrop = BitmapDescriptorFactory.fromResource(R.drawable.ic_tes);

            double lat = Double.parseDouble(hmPlace.get("lat"));
            double lng = Double.parseDouble(hmPlace.get("lng"));
            String nama = hmPlace.get("place_name");
            String namaJln = hmPlace.get("vicinity");
            LatLng latLng = new LatLng(lat, lng);

            markerOptions.icon(pinDrop);
            markerOptions.position(latLng);
            markerOptions.title(nama + " : " + namaJln);
            mGoogleMap.addMarker(markerOptions);
        }
        stopProg();
        }
    }

//    @Override
//    public void onStatusChanged(String provider, int status, Bundle extras) {
////        LocationListener.super.onStatusChanged(provider, status, extras);
//    }
//
//    @Override
//    public void onProviderEnabled(@NonNull String provider) {
////        LocationListener.super.onProviderEnabled(provider);
//    }
//
//    @Override
//    public void onProviderDisabled(@NonNull String provider) {
////        LocationListener.super.onProviderDisabled(provider);
//    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
//        LocationListener.super.onStatusChanged(provider, status, extras);
    }

    @Override
    public void onProviderEnabled(String s) {
//        LocationListener.super.onProviderEnabled(provider);
    }

    @Override
    public void onProviderDisabled(String s) {
//        LocationListener.super.onProviderDisabled(provider);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.tempatAman){
            startActivity(new Intent(this, MainActivity.class));
        }
        else if (item.getItemId() == R.id.sensorGetar) {
            startActivity(new Intent(this, SensorActivity.class));
        }

        else if (item.getItemId() == R.id.kompas) {
            startActivity(new Intent(this, CompassActivity.class));
        }

        else if (item.getItemId() == R.id.informasi) {
            startActivity(new Intent(this, InformationActivity.class));
        }
        else if (item.getItemId() == R.id.bahasa) {
            Intent locationIntent = new Intent(Settings.ACTION_LOCALE_SETTINGS);
            startActivity(locationIntent);
        }
        else if (item.getItemId() == R.id.pengaturan) {
            Intent locationIntent = new Intent(Settings.ACTION_SETTINGS);
            startActivity(locationIntent);
        }
        return true;
    }
}