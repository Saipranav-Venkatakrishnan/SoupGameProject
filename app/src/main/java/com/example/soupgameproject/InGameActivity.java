package com.example.soupgameproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import java.util.ArrayList;

public class InGameActivity extends AppCompatActivity {

    // Debugging variables
    float centerX, centerY;
    boolean zoomed;

    // Game layout set up variables

    // Layouts
    private FrameLayout scalingFrameLayout;
    private ConstraintLayout gameContainerLayout, backgroundLayout, collisionLayout, foregroundLayout;

    // GameLayouts (SAVE)
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
    // (SAVE)
    private Character kirby;
    private ArrayList<HitBox> walkHitBoxes, runHitBoxes, fallHitBoxes, flipFallHitBoxes, floatHitBoxes;


    // Environment variables (SAVE)
    // Test Environment GameObjects
    private ArrayList<GameObject> testEnvironmentGameObjects;

    // User Interface variables
    private Button leftButton, rightButton, jumpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_game);

        // Get all layouts/buttons/etc
        scalingFrameLayout = findViewById(R.id.ScalingFrameLayout);
        gameContainerLayout = findViewById(R.id.GameContainerLayout);
        backgroundLayout = findViewById(R.id.BackgroundLayout);
        collisionLayout = findViewById(R.id.CollisionLayout);
        foregroundLayout = findViewById(R.id.ForegroundLayout);
        leftButton = findViewById(R.id.leftButton);
        rightButton = findViewById(R.id.rightButton);
        jumpButton = findViewById(R.id.jumpButton);


        // Debugging variables
        gameCamera = new Camera(scalingFrameLayout,gameContainerLayout);
        centerX = gameCamera.getXPosition();
        centerY = gameCamera.getYPosition();
        zoomed = true;

        // Set up character once only.
        characterSetUp();

        // Set up the camera to the appropriate environment and then instantiate all environment objects. Repeat for each environment.
        cameraSetUp("test");
        testEnvironmentGameObjects = new ArrayList<GameObject>();
        testEnvironmentGameObjects.add(new GameObject(this, "Ground", (int)(TitleActivity.WIDTH/TitleActivity.DENSITY),10,
                R.drawable.testground, 0, gameCamera.getBottomYPosition(), true));
        kirby.setYPosition(gameCamera.getBottomYPosition() + 9);
        testEnvironmentGameObjects.add(kirby);

        // Call to show the chosen environment.
        environmentSetUp("test");

    }

    // Sets up the camera for a chosen environment
    private void cameraSetUp(String environment){
        gameCamera = new Camera(scalingFrameLayout, gameContainerLayout);

        if(environment.toLowerCase().equals("test")){
            gameCamera.setScale(fitZoom(3832,359));
            gameCamera.setLeftXPosition(0);
        }
    }

    // Sets up a chosen in-game environment
    private void environmentSetUp(String environment){
        backgroundGameLayout = new GameLayout(this, backgroundLayout);
        backgroundGameLayout.setBackgroundImageView(findViewById(R.id.backgroundImage));
        collisionGameLayout = new GameLayout(this, collisionLayout);
        foregroundGameLayout = new GameLayout(this, foregroundLayout);

        if(environment.toLowerCase().equals("test")){
            backgroundGameLayout.setBackgroundImage(R.drawable.cloudsbackgroundextended);
            collisionGameLayout.setLayoutObjects(testEnvironmentGameObjects);
        }
    }

    // Sets up the character for the first time the app is used
    private void characterSetUp(){
        HitBox idleHitBox = new HitBox(this, true, (int) (30 * 20/59F),(int)(20 * 18/39F),
                0, 0, 30 * 19/59F,20 * 1/39F);

        kirby = new Character(this, "Kirby", 30, 20, 0, 0,
                idleHitBox, true, R.drawable.kirbyidle);
        kirby.setObjectResource(R.drawable.kirbyidle);

        walkHitBoxes = new ArrayList<HitBox>();
        runHitBoxes = new ArrayList<HitBox>();
        fallHitBoxes = new ArrayList<HitBox>();
        flipFallHitBoxes = new ArrayList<HitBox>();
        floatHitBoxes = new ArrayList<HitBox>();

        // Walking Hit Boxes
        walkHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 20/59F),
                (int)(kirby.getObjectHeight() * 18/39F), 0, 0, kirby.getObjectWidth() * 19/59F,
                kirby.getObjectHeight() * 1/39F));
        walkHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 21/59F),
                (int)(kirby.getObjectHeight() * 19/39F), 0, 0, kirby.getObjectWidth() * 19/59F,
                kirby.getObjectHeight() * 1/39F));
        walkHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 20/59F),
                (int)(kirby.getObjectHeight() * 18/39F), 0, 0, kirby.getObjectWidth() * 19/59F,
                kirby.getObjectHeight() * 1/39F));
        walkHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 19/59F),
                (int)(kirby.getObjectHeight() * 16/39F), 0, 0, kirby.getObjectWidth() * 20/59F,
                kirby.getObjectHeight() * 1/39F));
        walkHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 17/59F),
                (int)(kirby.getObjectHeight() * 17/39F), 0, 0, kirby.getObjectWidth() * 21/59F,
                kirby.getObjectHeight() * 1/39F));
        walkHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 17/59F),
                (int)(kirby.getObjectHeight() * 18/39F), 0, 0, kirby.getObjectWidth() * 21/59F,
                kirby.getObjectHeight() * 1/39F));
        walkHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 21/59F),
                (int)(kirby.getObjectHeight() * 19/39F), 0, 0, kirby.getObjectWidth() * 19/59F,
                kirby.getObjectHeight() * 1/39F));
        walkHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 20/59F),
                (int)(kirby.getObjectHeight() * 18/39F), 0, 0, kirby.getObjectWidth() * 19/59F,
                kirby.getObjectHeight() * 1/39F));
        walkHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 19/59F),
                (int)(kirby.getObjectHeight() * 16/39F), 0, 0, kirby.getObjectWidth() * 20/59F,
                kirby.getObjectHeight() * 1/39F));
        walkHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 19/59F),
                (int)(kirby.getObjectHeight() * 17/39F), 0, 0, kirby.getObjectWidth() * 20/59F,
                kirby.getObjectHeight() * 1/39F));

        // Running Hit Boxes
        runHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 24/59F),
                (int)(kirby.getObjectHeight() * 19/39F), 0, 0, kirby.getObjectWidth() * 17/59F,
                kirby.getObjectHeight() * 0/39F));
        runHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 19/59F),
                (int)(kirby.getObjectHeight() * 19/39F), 0, 0, kirby.getObjectWidth() * 20/59F,
                kirby.getObjectHeight() * 0/39F));
        runHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 17/59F),
                (int)(kirby.getObjectHeight() * 18/39F), 0, 0, kirby.getObjectWidth() * 21/59F,
                kirby.getObjectHeight() * 0/39F));
        runHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 18/59F),
                (int)(kirby.getObjectHeight() * 19/39F), 0, 0, kirby.getObjectWidth() * 20/59F,
                kirby.getObjectHeight() * 0/39F));
        runHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 19/59F),
                (int)(kirby.getObjectHeight() * 19/39F), 0, 0, kirby.getObjectWidth() * 20/59F,
                kirby.getObjectHeight() * 0/39F));
        runHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 18/59F),
                (int)(kirby.getObjectHeight() * 19/39F), 0, 0, kirby.getObjectWidth() * 20/59F,
                kirby.getObjectHeight() * 0/39F));
        runHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 17/59F),
                (int)(kirby.getObjectHeight() * 18/39F), 0, 0, kirby.getObjectWidth() * 21/59F,
                kirby.getObjectHeight() * 0/39F));
        runHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 19/59F),
                (int)(kirby.getObjectHeight() * 19/39F), 0, 0, kirby.getObjectWidth() * 20/59F,
                kirby.getObjectHeight() * 0/39F));

        // Fall Hit Boxes
        fallHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 20/59F),
                (int)(kirby.getObjectHeight() * 19/39F), 0, 0, kirby.getObjectWidth() * 19/59F,
                kirby.getObjectHeight() * 0/39F));
        fallHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 21/59F),
                (int)(kirby.getObjectHeight() * 19/39F), 0, 0, kirby.getObjectWidth() * 19/59F,
                kirby.getObjectHeight() * 0/39F));

        // Flip Fall Hit Boxes
        flipFallHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 21/59F),
                (int)(kirby.getObjectHeight() * 19/39F), 0, 0, kirby.getObjectWidth() * 19/59F,
                kirby.getObjectHeight() * 0/39F));
        flipFallHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 20/59F),
                (int)(kirby.getObjectHeight() * 18/39F), 0, 0, kirby.getObjectWidth() * 19/59F,
                kirby.getObjectHeight() * 0/39F));
        flipFallHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 21/59F),
                (int)(kirby.getObjectHeight() * 19/39F), 0, 0, kirby.getObjectWidth() * 19/59F,
                kirby.getObjectHeight() * 0/39F));
        flipFallHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 20/59F),
                (int)(kirby.getObjectHeight() * 20/39F), 0, 0, kirby.getObjectWidth() * 19/59F,
                kirby.getObjectHeight() * 0/39F));
        flipFallHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 21/59F),
                (int)(kirby.getObjectHeight() * 17/39F), 0, 0, kirby.getObjectWidth() * 19/59F,
                kirby.getObjectHeight() * 0/39F));
        flipFallHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 22/59F),
                (int)(kirby.getObjectHeight() * 18/39F), 0, 0, kirby.getObjectWidth() * 18/59F,
                kirby.getObjectHeight() * 0/39F));

        for(int i = 0; i < 10; i ++) {
            flipFallHitBoxes.add(new HitBox(this, true, (int) (kirby.getObjectWidth() * 20 / 59F),
                    (int) (kirby.getObjectHeight() * 19 / 39F), 0, 0, kirby.getObjectWidth() * 19 / 59F,
                    kirby.getObjectHeight() * 0 / 39F));
            flipFallHitBoxes.add(new HitBox(this, true, (int) (kirby.getObjectWidth() * 21 / 59F),
                    (int) (kirby.getObjectHeight() * 19 / 39F), 0, 0, kirby.getObjectWidth() * 19 / 59F,
                    kirby.getObjectHeight() * 0 / 39F));
        }

        // Float Hit Boxes
        floatHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 24/59F),
                (int)(kirby.getObjectHeight() * 25/39F), 0, 0, kirby.getObjectWidth() * 17/59F,
                kirby.getObjectHeight() * 0/39F));
        floatHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 24/59F),
                (int)(kirby.getObjectHeight() * 24/39F), 0, 0, kirby.getObjectWidth() * 17/59F,
                kirby.getObjectHeight() * 0/39F));
        floatHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 24/59F),
                (int)(kirby.getObjectHeight() * 24/39F), 0, 0, kirby.getObjectWidth() * 17/59F,
                kirby.getObjectHeight() * 0/39F));
        floatHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 25/59F),
                (int)(kirby.getObjectHeight() * 24/39F), 0, 0, kirby.getObjectWidth() * 17/59F,
                kirby.getObjectHeight() * 0/39F));
        floatHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 26/59F),
                (int)(kirby.getObjectHeight() * 24/39F), 0, 0, kirby.getObjectWidth() * 16/59F,
                kirby.getObjectHeight() * 0/39F));
        floatHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 25/59F),
                (int)(kirby.getObjectHeight() * 24/39F), 0, 0, kirby.getObjectWidth() * 17/59F,
                kirby.getObjectHeight() * 0/39F));
        floatHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 24/59F),
                (int)(kirby.getObjectHeight() * 24/39F), 0, 0, kirby.getObjectWidth() * 17/59F,
                kirby.getObjectHeight() * 0/39F));
        floatHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 24/59F),
                (int)(kirby.getObjectHeight() * 24/39F), 0, 0, kirby.getObjectWidth() * 17/59F,
                kirby.getObjectHeight() * 0/39F));



    }

    // Sets up character controls/interactions
    // Majority of game logic resides here
    private void controllerSetUp(){
        
    }

    // This method helps to find the scale for the game camera to zoom to
    private float fitZoom(float backgroundWidth, float backgroundHeight){
        return ((float) TitleActivity.HEIGHT/TitleActivity.WIDTH) * (backgroundWidth/backgroundHeight);
    }


    // Debugging method
    public void viewInfoDebug(View view){
        GameObject.displayHitBoxes = true;
        kirby.showHitBox();
        try {
            Log.i("CharacterDebug", "Character: Position: xPosition = " + String.valueOf(kirby.getXPosition()) +
                    " yPosition = " + String.valueOf(kirby.getYPosition()) + " | Width = " + String.valueOf(kirby.getObjectWidth()) +
                    " Height = " + String.valueOf(kirby.getObjectHeight()) + " | Attributes: isGrounded = " + String.valueOf(kirby.isGrounded()) +
                    " isFacingRight = " + String.valueOf(kirby.isFacingRight()) + " | Hitbox: xPosition = " + String.valueOf(kirby.getHitBox().getXPosition()) +
                    " yPosition = " + String.valueOf(kirby.getHitBox().getYPosition())
                    + " | Animation Running = " + String.valueOf(kirby.getCharacterAnimation().isRunning()));
        }
        catch (Exception e){
        }

        Log.i("CameraDebug", gameCamera.toString());
        if(zoomed) {
            gameCamera.setFixedPosition(false);
            gameCamera.moveTo(centerX, centerY, 100);
            gameCamera.zoomTo(3/4F, 10);
            zoomed = false;
        }
        else{
            gameCamera.setFixedPosition(false);
            gameCamera.zoomTo(fitZoom(3832,359), 50, new Camera.CameraCallBack() {
                @Override
                public void onActionComplete(Camera camera) {
                    try {
                        gameCamera.moveTo(kirby.getXPosition(), centerY, 10);
                    }
                    catch (Exception e){
                        gameCamera.setLeftXPosition(0);
                    }
                }
            });
            zoomed = true;
        }

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