package com.example.soupgameproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

public class InGameActivity extends AppCompatActivity {

    // Game layout set up variables

    // Layouts
    private FrameLayout scalingFrameLayout;
    private ConstraintLayout gameContainerLayout, backgroundLayout, collisionLayout, foregroundLayout;

    // GameLayouts
    private GameLayout backgroundGameLayout;
    @SuppressLint("StaticFieldLeak")
    public static GameLayout collisionGameLayout;
    private GameLayout foregroundGameLayout;

    // Handlers to deal with motion

    // Deal with left and right movement
    private Handler lrHandler;
    // Deal with up and down movement
    private Handler udHandler;
    // Deal with non-movement actions
    private Handler aHandler;
    // Deal with camera movement
    private Handler cHandler;

    // Game Camera variables
    private Camera gameCamera;

    // Character variables
    private Character character;

    // Environment variables
    private GameObject ground;

    // User Interface variables
    private Button leftButton, rightButton, jumpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_game);
    }

    // The following code was from https://developer.android.com/training/system-ui/immersive to create a fullscreen (has changed)
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        //| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the status bar
                        // | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }
}