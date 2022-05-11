package com.example.soupgameproject;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.effect.Effect;
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
import android.widget.ToggleButton;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.Random;


public class SettingsPage extends AppCompatActivity {

    private ToggleButton anotherSwitch;
    private ToggleButton controlSwitch;
    private ToggleButton soundEffectSwitch;
    public static final String SWITCH = "switch1";
    public static final String SWITCH1 = "ButtonPlacement";
    public static final String SWITCH3 = "EffectOn";
    public static final String SHARED_PREF = "sharedPref";
    public static boolean isOn;
    public static boolean isRight;
    public static boolean isEffect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_page);
        Intent intent = getIntent();
        anotherSwitch = findViewById(R.id.switch1);
        anotherSwitch.setTextOff("   Music is not Playing   ");
// Sets the text for when the button is not in the checked state.

        anotherSwitch.setTextOn("   Music is Playing   ");
        anotherSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isOn = b;
            }
        });

        controlSwitch = findViewById(R.id.ButtonPlacement);
        controlSwitch.setTextOff("   Currently on Left Side   ");
// Sets the text for when the button is not in the checked state.

        controlSwitch.setTextOn("   Currently on Right Side   ");
        controlSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isRight = b;
            }
        });

        soundEffectSwitch = findViewById(R.id.switch3);
        soundEffectSwitch.setTextOff("   Effects are not Playing   ");
// Sets the text for when the button is not in the checked state.

        soundEffectSwitch.setTextOn("   Effects are Playing   ");
        soundEffectSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isEffect = b;
            }
        });

        loadData();
        updateViews();
    }

    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean(SWITCH, anotherSwitch.isChecked());
        editor.putBoolean(SWITCH1, controlSwitch.isChecked());
        editor.putBoolean(SWITCH3, soundEffectSwitch.isChecked());

        editor.commit();

        editor.apply();

        Toast.makeText(this, "Data Saved", Toast.LENGTH_SHORT).show();
    }

    public void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        isOn = sharedPreferences.getBoolean(SWITCH, false);
        isRight = sharedPreferences.getBoolean(SWITCH1, false);
        isEffect = sharedPreferences.getBoolean(SWITCH3, false);
    }

    public void gameActivityPage(View view) {
        Intent intent = new Intent(this, InGameActivity.class);
        startActivity(intent);
    }

    public void titlePage(View v) {
        Intent intent = new Intent(this, TitleActivity.class);
        startActivity(intent);
    }

    public void updateViews(){
        anotherSwitch.setChecked(isOn);
        controlSwitch.setChecked(isRight);
        soundEffectSwitch.setChecked(isEffect);
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