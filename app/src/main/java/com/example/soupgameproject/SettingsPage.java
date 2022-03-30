package com.example.soupgameproject;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.Random;


public class SettingsPage extends AppCompatActivity {

    private Switch anotherSwitch;
    public static final String SWITCH = "switch2";
    public static final String SHARED_PREF = "sharedPref";
    public static boolean isOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_page);
        Intent intent = getIntent();
        anotherSwitch = findViewById(R.id.switch1);
        anotherSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isOn = b;
            }
        });

        loadData();
        updateViews();
    }

    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean(SWITCH, anotherSwitch.isChecked());

        editor.commit();

        editor.apply();

        Toast.makeText(this, "Data Saved", Toast.LENGTH_SHORT).show();
    }

    public void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        isOn = sharedPreferences.getBoolean(SWITCH, false);
    }

    public void updateViews(){
        anotherSwitch.setChecked(isOn);

    }

    protected void onRestart() {
        super.onRestart();
        loadData();
        updateViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
        updateViews();
    }

    @Override
    protected void onStop() {
        super.onStop();

        saveData();

    }

    @Override
    protected void onPause() {
        super.onPause();

        saveData();
    }
}