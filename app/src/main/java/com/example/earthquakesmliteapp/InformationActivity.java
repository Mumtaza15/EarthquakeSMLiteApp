package com.example.earthquakesmliteapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class InformationActivity extends AppCompatActivity {

    private Button mButton;

    private boolean isFragmentDisplayed = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        mButton = findViewById(R.id.buttonz);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment;
                if (!isFragmentDisplayed) {
                    fragment = SimpleFragment.newInstance();
                    mButton.setText(R.string.open);
                } else {
                    fragment = DisplayFragment.newInstance();
                    mButton.setText(R.string.close);
                }
                displayFragment(fragment);
                isFragmentDisplayed = !isFragmentDisplayed;
            }
        });

        displayFragment(SimpleFragment.newInstance());
    }

    private void displayFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager
                .beginTransaction();

        fragmentTransaction.replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
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
        else if (item.getItemId() == R.id.tes) {
            startActivity(new Intent(this, NearbyLocation.class));
        }
        return true;
    }
}