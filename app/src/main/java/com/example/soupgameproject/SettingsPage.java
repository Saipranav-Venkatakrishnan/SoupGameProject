package com.example.soupgameproject;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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

    public static MediaPlayer mediaPlayer;
    public static boolean shouldPlay;

    private Switch anotherSwitch;
    public static final String SWITCH = "switch2";
    public static final String SHARED_PREF = "sharedPref";
    private boolean isOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_page);
        Intent intent = getIntent();
        anotherSwitch = findViewById(R.id.switch1);
        anotherSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    playAudio();
                }
                else{
                    pauseAudio();
                }
            }
        });
    }

    public void playAudio() {

        String audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3";
        //audioUrl = "https://mvnja.xyz/lp/6/indextwo.html?7fk8qechol";
        if(mediaPlayer == null){
            // initializing media player
            mediaPlayer = new MediaPlayer();

            // below line is use to set the audio
            // stream type for our media player.
//            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            // below line is use to set our
//            // url to our media player.
//            try {
//                mediaPlayer.setDataSource(audioUrl);
//                // below line is use to prepare
//                // and start our media player.
//                mediaPlayer.prepareAsync();
//                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                    @Override
//                    public void onPrepared(MediaPlayer mediaPlayer) {
//                        mediaPlayer.start();
//                    }
//                });
//
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

            mediaPlayer = MediaPlayer.create(this, R.raw.kirbythemesong);
            mediaPlayer.start();
//            mediaPlayer.setLooping(true);

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    randomSong();
                }
            });

            // below line is use to display a toast message.
            Toast.makeText(this, "Audio started playing..", Toast.LENGTH_SHORT).show();
            Log.i("Sai", "Playing is: " + mediaPlayer.isPlaying());
        }
        else{
            Toast.makeText(this, "Audio is already playing", Toast.LENGTH_SHORT).show();
        }

        Log.i("Sai", "Playing is: " + mediaPlayer.isPlaying());
    }
    public void pauseAudio() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                // pausing the media player if media player
                // is playing we are calling below line to
                // stop our media player.
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer = null;

                // below line is to display a message
                // when media player is paused.
                Toast.makeText(SettingsPage.this, "Audio has been paused", Toast.LENGTH_SHORT).show();
            } else {
                // this method is called when media
                // player is not playing.
                Toast.makeText(SettingsPage.this, "Audio has not played", Toast.LENGTH_SHORT).show();
            }

        }
        shouldPlay = false;
        Log.i("Sai", "Should Play = " + shouldPlay);
    }
    public void randomSong(){
        pauseAudio();
        mediaPlayer = new MediaPlayer();
        Random rand = new Random(); //instance of random class
        int upperbound = 6;
        //generate random values from 0-24
        int randomSong = rand.nextInt(upperbound);
        if(randomSong == 0){
            mediaPlayer = MediaPlayer.create(this, R.raw.bornforthis);
            mediaPlayer.start();
        }
        else if(randomSong == 1){
            mediaPlayer = MediaPlayer.create(this, R.raw.glitterandgold);
            mediaPlayer.start();
        }
        else if(randomSong == 2){
            mediaPlayer = MediaPlayer.create(this, R.raw.legends);
            mediaPlayer.start();
        }
        else if(randomSong == 3){
            mediaPlayer = MediaPlayer.create(this, R.raw.sinners);
            mediaPlayer.start();
        }
        else if(randomSong == 4){
            mediaPlayer = MediaPlayer.create(this, R.raw.unstoppable);
            mediaPlayer.start();
        }
        else if(randomSong == 5){
            mediaPlayer = MediaPlayer.create(this, R.raw.kirbythemesong);
            mediaPlayer.start();
        }

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                randomSong();
            }
        });
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

        if(isOn){
            playAudio();
        } else{
            pauseAudio();
        }

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