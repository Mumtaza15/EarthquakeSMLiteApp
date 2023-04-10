package com.example.earthquakesmliteapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SensorActivity extends AppCompatActivity implements SensorEventListener {
    private TextView xValue, yValue, zValue, cShake;
    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    private boolean isAccelorometerSensorAvailable, itIsNotFirstTime = false;
    private float currentX, currentY, currentZ, lastx, lasty, lastz, xDifference, yDifference, zDifference;
    private float shakeThreshold;
    private Vibrator vibrator;
    private Button BtnLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

//        shakeThreshold = 1;

        xValue = findViewById(R.id.x_value);
        yValue = findViewById(R.id.y_value);
        zValue = findViewById(R.id.z_value);

        cShake = findViewById(R.id.cek_shake);

        BtnLocation = findViewById(R.id.btn_location);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)!=null){
            accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            isAccelorometerSensorAvailable = true;
        }else {
            xValue.setText("Accelorometer sensor is not available");
            isAccelorometerSensorAvailable = false;
        }

        BtnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent locationIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(locationIntent);
            }
        });

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        xValue.setText(event.values[0]+"m/s2");
        yValue.setText(event.values[1]+"m/s2");
        zValue.setText(event.values[2]+"m/s2");

        currentX = event.values[0];
        currentY = event.values[1];
        currentZ = event.values[2];

        if(itIsNotFirstTime)
        {
            float xDifference = Math.abs(lastx - currentX);
            float yDifference = Math.abs(lasty - currentY);
            float zDifference = Math.abs(lastz - currentZ);

            float floatSum = currentX + currentY + currentZ;

            if (floatSum > 16){
                cShake.setText("Yes, Shaking");
                if ((xDifference > shakeThreshold && yDifference > shakeThreshold) ||
                        (xDifference > shakeThreshold && zDifference > shakeThreshold) ||
                        (yDifference > shakeThreshold && zDifference > shakeThreshold))
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                    }else{
                        vibrator.vibrate(500);
                        //deprecated in API 26
                    }
                }
            }
            else {
                cShake.setText("No, NOT Shaking");
            }

//            if ((xDifference > shakeThreshold && yDifference > shakeThreshold) ||
//            (xDifference > shakeThreshold && zDifference > shakeThreshold) ||
//                    (yDifference > shakeThreshold && zDifference > shakeThreshold))
//            {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
//                }else{
//                    vibrator.vibrate(500);
//                    //deprecated in API 26
//                }
//            }
        }

        lastx = currentX;
        lasty = currentY;
        lastz = currentZ;

        itIsNotFirstTime = true;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isAccelorometerSensorAvailable)
            sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isAccelorometerSensorAvailable)
            sensorManager.unregisterListener(this);
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