package com.example.soupgameproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class TitleActivity extends AppCompatActivity {

    public static float DENSITY;
    public static int WIDTH, HEIGHT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title);

        // Device Screen Dimensions
        DENSITY = getResources().getDisplayMetrics().density;
        WIDTH = getResources().getDisplayMetrics().widthPixels;
        HEIGHT = getResources().getDisplayMetrics().heightPixels;

    }
}