package com.example.soupgameproject;

import static com.example.soupgameproject.SettingsPage.SHARED_PREF;
import static com.example.soupgameproject.SettingsPage.SWITCH;
import static com.example.soupgameproject.SettingsPage.isOn;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

public class TitleActivity extends AppCompatActivity {

    public static float DENSITY;
    public static int WIDTH, HEIGHT;
    private ConstraintLayout titleLayout, titleActivityLayout;
    private TextView titleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title);

        titleActivityLayout = findViewById(R.id.titleActivityLayout);
        titleLayout = findViewById(R.id.titleLayout);
        titleTextView = findViewById(R.id.titleTextView);

        // Device Screen Dimensions
        DENSITY = getResources().getDisplayMetrics().density;
        WIDTH = getResources().getDisplayMetrics().widthPixels;
        HEIGHT = getResources().getDisplayMetrics().heightPixels;

        playTitleAnimation();

    }

    private void playTitleAnimation(){

        Character kirby = new Character(this, "Kirby", 300, 200, 0, 0,
                new HitBox(this, false,0,0,0,0,0,0), true, R.drawable.kirbyidle);
        kirby.setObjectResource(R.drawable.kirbyidle);

        Runnable intro = kirby.animatedAction(kirby.getAHandler(), false, R.drawable.kirbyintro, null,
                new GameObject.CollisionListener() {
                    @Override
                    public void onCollision(GameObject object1, GameObject object2) {
                    }
                },
                new Character.CharacterListener() {
                    @Override
                    public void onActionComplete() {
                        kirby.setObjectResource(kirby.getIdleResource());
                    }
                });
        kirby.getAllActions().put("Intro", intro);

        kirby.setCenterXPosition(WIDTH/(2 * DENSITY));
        kirby.setYPosition(HEIGHT/DENSITY);

        titleActivityLayout.addView(kirby);

        kirby.getAHandler().postDelayed(kirby.getAllActions().get("Intro"), 0);

        Handler handler = new Handler();

        Runnable animation = new Runnable() {

            int t = 0;
            int a = 0;
            boolean part1 = true;
            boolean part2 = false;
            boolean part3 = false;
            boolean brighten = true;

            @Override
            public void run() {
                if(part1) {
                    if (kirby.getYPosition() > -300) {
                        t++;
                        kirby.setYPosition(kirby.getYPosition() - t / 3F);
                        if(kirby.getCenterYPosition() > HEIGHT/DENSITY/2) {
                            titleLayout.setTranslationY(titleLayout.getTranslationY() + t / 3F * DENSITY);
                        }
                    }
                    else{
                        part1 = false;
                        part2 = true;
                        t = 0;
                    }
                }
                else if(part2){
                    if(kirby.getCenterYPosition() < HEIGHT/DENSITY/2){
                        t++;
                        kirby.setYPosition(kirby.getYPosition() + t / 3F);
                    }
                    else{
                        part2 = false;
                        part3 = true;
                        t = 0;
                    }
                }
                else if(part3){
                    if(kirby.getYPosition() > 5){
                        t++;
                        kirby.setYPosition(kirby.getYPosition() - t / 3F);
                    }
                    else{
                        part3 = false;
                    }
                }

                if(a<160 && brighten){
                    a++;
                }
                else{
                    brighten = false;
                }

                if(a > 0 && !brighten){
                    a--;
                }
                else{
                    brighten = true;
                }
                titleTextView.setBackgroundTintMode(PorterDuff.Mode.ADD);
                titleTextView.setBackgroundTintList(ColorStateList.valueOf(Color.argb(a/2,255,255,255)));

                handler.postDelayed(this,1);
            }
        };

        handler.postDelayed(animation,0);
    }

    public void startGame(View view){
        Intent intent = new Intent(TitleActivity.this, InGameActivity.class);
        startActivity(intent);
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
                        // Hide the nav bar and status bar
                        // | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        isOn = sharedPreferences.getBoolean(SWITCH, false);

        editor.commit();

        editor.apply();
    }

    public void loadData(){
        //SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
    }

    public void updateViews(){


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