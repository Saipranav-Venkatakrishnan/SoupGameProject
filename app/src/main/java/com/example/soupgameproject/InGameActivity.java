package com.example.soupgameproject;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
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

    // Deal with other animations unrelated to characters
    private Handler oHandler;

    // Deal with other runnables unrelated to animations
    private Handler rHandler;

    // Game Camera variables
    private Camera gameCamera;

    // Character variables
    // (SAVE)
    private Character kirby;
    private ArrayList<HitBox> walkHitBoxes, runHitBoxes, fallHitBoxes, flipFallHitBoxes,
            floatHitBoxes, jumpHitBox, floatFallHitBoxes, startFloatHitBoxes, stopFloatHitBoxes;
    private boolean isFloating;
    private boolean startFloatFinished;
    private int jumpCount;
    
    // Day/Night cycle variables
    // (SAVE)
    private String timeOfDay;

    // Environment variables (SAVE)
    // Test Environment GameObjects
    private ArrayList<GameObject> testEnvironmentBackgroundGameObjects;
    private ArrayList<GameObject> testEnvironmentCollisionGameObjects;
    private ArrayList<GameObject> testEnvironmentForegroundGameObjects;

    // User Interface variables
    private Button leftButton, rightButton, jumpButton, actionButton;

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
        actionButton = findViewById(R.id.actionButton);

        // Set up Handlers
        lrHandler = new Handler();
        udHandler = new Handler();
        aHandler = new Handler();
        cHandler = new Handler();
        oHandler = new Handler();


        // Debugging variables
        gameCamera = new Camera(scalingFrameLayout,gameContainerLayout);
        centerX = gameCamera.getXPosition();
        centerY = gameCamera.getYPosition();
        zoomed = true;

        // Set up time once
        timeOfDay = "Morning";

        // Set up character once only.
        characterSetUp();

        // Set up the camera to the appropriate environment and then instantiate all environment objects. Repeat for each environment.
        // Set up once only
        cameraSetUp("test");

        testEnvironmentBackgroundGameObjects = new ArrayList<GameObject>();
        testEnvironmentCollisionGameObjects = new ArrayList<GameObject>();
        testEnvironmentForegroundGameObjects = new ArrayList<GameObject>();


        testEnvironmentCollisionGameObjects.add(new GameObject(this, "Ground", (int)(TitleActivity.WIDTH/TitleActivity.DENSITY),10,
                R.drawable.testground, 0, gameCamera.getBottomYPosition(), true, new HitBox(this,true,
                (int)(TitleActivity.WIDTH/TitleActivity.DENSITY), 6, 0, gameCamera.getBottomYPosition(),0,0)));

        for(int i = 0; i < 50; i++){
            float ratio = (int) (Math.random() * 6 + 4)/10F;
            int xPosition = (int) (Math.random() * (TitleActivity.WIDTH/TitleActivity.DENSITY));
            testEnvironmentCollisionGameObjects.add(new GameObject(InGameActivity.this, "Tree", (int)(39 * ratio),(int)(43* ratio),
                    R.drawable.testtree, xPosition, gameCamera.getBottomYPosition() + 6, true,
                    new HitBox(InGameActivity.this, true, (int)(7* ratio),(int)(39* ratio),xPosition
                            ,gameCamera.getBottomYPosition()+6,(float)(16* ratio),0)));
        }

        GameObject rightBoundary = new GameObject(this, "Boundary", 50, (int)(TitleActivity.HEIGHT/TitleActivity.DENSITY),
                        R.drawable.boundary, -50,0,true);
        GameObject leftBoundary = new GameObject(this, "Boundary", 50, (int)(TitleActivity.HEIGHT/TitleActivity.DENSITY),
                R.drawable.boundary, (TitleActivity.WIDTH/TitleActivity.DENSITY),0,true);
        GameObject topBoundary = new GameObject(this, "Boundary", (int)(TitleActivity.WIDTH/TitleActivity.DENSITY),
                50, R.drawable.boundary, 0,gameCamera.getTopYPosition(),true);

        testEnvironmentCollisionGameObjects.add(rightBoundary);
        testEnvironmentCollisionGameObjects.add(leftBoundary);
        testEnvironmentCollisionGameObjects.add(topBoundary);

        // Call to show the chosen environment.
        environmentSetUp("test");

        controllerSetUp(1/3F,1/2F, 10,20,5);

        dayNightCycle();
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
        backgroundGameLayout = new GameLayout(this,"Background", backgroundLayout);
        backgroundGameLayout.setBackgroundImageView(findViewById(R.id.backgroundImage));
        collisionGameLayout = new GameLayout(this, "Collision", collisionLayout);
        foregroundGameLayout = new GameLayout(this, "Foreground",foregroundLayout);

        if(environment.toLowerCase().equals("test")){
            backgroundGameLayout.setBackgroundImage(R.drawable.cloudsbackgroundextended);
            backgroundGameLayout.setLayoutObjects(testEnvironmentBackgroundGameObjects);

            collisionGameLayout.removeLayoutObject(kirby);
            kirby.setYPosition(gameCamera.getBottomYPosition() + 6 - kirby.getHitBox().getYBottom());
            itemSetup(environment);
            collisionGameLayout.addLayoutObject(kirby);
            collisionGameLayout.addLayoutObjects(testEnvironmentCollisionGameObjects);

            gameCamera.setFixedPosition(true);
        }
    }

    private void itemSetup(String environment){

        if(environment.toLowerCase().equals("test")){
            for(int i = 0; i < 50; i++){
                Ingredient ingredient = new Ingredient(this, "Heart",10,10,
                        R.drawable.testitem,
                        (float) (Math.random() * (TitleActivity.WIDTH/TitleActivity.DENSITY - 10)),
                        (float)(gameCamera.getTopYPosition()));
                
                collisionGameLayout.addLayoutObject(ingredient);
                
                Runnable fall = ingredient.fall(oHandler, GameObject.GRAVITY, new GameObject.CollisionListener() {
                    @Override
                    public void onCollision(GameObject object1, GameObject object2) {
                        if (GameObject.getCollisionType(object1, object2).contains("top")) {
                            if(!specialCollisionHandler(object1, object2) && !object2.isCharacter() && !object2.isIngredient()) {
                                object1.stopFall();

                                object1.setYPosition(object2.getHitBox().topLeft().y);
                                
                                object1.getHitBox().setYPosition(object1.getYPosition());
                                object1.setHitBox(object1.getHitBox());
                                object1.showHitBox();
                            }

                            Log.i("Collision", object1.getObjectName() + " collided with top of " + object2.getObjectName());
                        }
                    }
                });

                oHandler.postDelayed(fall, 5000);
            }
        }

    }

    // Sets up the character for the first time the app is used
    private void characterSetUp(){
        HitBox idleHitBox = new HitBox(this, true, (int) (30 * 20/59F),(int)(20 * 18/39F),
                0, 0, 30 * 19/59F,0);

        kirby = new Character(this, "Kirby", 30, 20, 0, 0,
                idleHitBox, true, R.drawable.kirbyidle);
        kirby.setObjectResource(R.drawable.kirbyidle);

        walkHitBoxes = new ArrayList<HitBox>();
        runHitBoxes = new ArrayList<HitBox>();
        fallHitBoxes = new ArrayList<HitBox>();
        flipFallHitBoxes = new ArrayList<HitBox>();
        floatHitBoxes = new ArrayList<HitBox>();
        jumpHitBox = new ArrayList<HitBox>();
        floatFallHitBoxes = new ArrayList<HitBox>();
        startFloatHitBoxes = new ArrayList<HitBox>();
        stopFloatHitBoxes = new ArrayList<HitBox>();

        // Walking Hit Boxes
        walkHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 20/59F),
                (int)(kirby.getObjectHeight() * 18/39F), 0, 0, kirby.getObjectWidth() * 19/59F,
                0));
        walkHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 21/59F),
                (int)(kirby.getObjectHeight() * 19/39F), 0, 0, kirby.getObjectWidth() * 19/59F,
                0));
        walkHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 20/59F),
                (int)(kirby.getObjectHeight() * 18/39F), 0, 0, kirby.getObjectWidth() * 19/59F,
                0));
        walkHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 19/59F),
                (int)(kirby.getObjectHeight() * 16/39F), 0, 0, kirby.getObjectWidth() * 20/59F,
                0));
        walkHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 17/59F),
                (int)(kirby.getObjectHeight() * 17/39F), 0, 0, kirby.getObjectWidth() * 21/59F,
                0));
        walkHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 17/59F),
                (int)(kirby.getObjectHeight() * 18/39F), 0, 0, kirby.getObjectWidth() * 21/59F,
                0));
        walkHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 21/59F),
                (int)(kirby.getObjectHeight() * 19/39F), 0, 0, kirby.getObjectWidth() * 19/59F,
                0));
        walkHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 20/59F),
                (int)(kirby.getObjectHeight() * 18/39F), 0, 0, kirby.getObjectWidth() * 19/59F,
                0));
        walkHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 19/59F),
                (int)(kirby.getObjectHeight() * 16/39F), 0, 0, kirby.getObjectWidth() * 20/59F,
                0));
        walkHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 19/59F),
                (int)(kirby.getObjectHeight() * 17/39F), 0, 0, kirby.getObjectWidth() * 20/59F,
                0));

        // Running Hit Boxes
        runHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 24/59F),
                (int)(kirby.getObjectHeight() * 19/39F), 0, 0, kirby.getObjectWidth() * 17/59F,
                0));
        runHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 19/59F),
                (int)(kirby.getObjectHeight() * 19/39F), 0, 0, kirby.getObjectWidth() * 20/59F,
                0));
        runHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 17/59F),
                (int)(kirby.getObjectHeight() * 18/39F), 0, 0, kirby.getObjectWidth() * 21/59F,
                0));
        runHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 18/59F),
                (int)(kirby.getObjectHeight() * 19/39F), 0, 0, kirby.getObjectWidth() * 20/59F,
                0));
        runHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 19/59F),
                (int)(kirby.getObjectHeight() * 19/39F), 0, 0, kirby.getObjectWidth() * 20/59F,
                0));
        runHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 18/59F),
                (int)(kirby.getObjectHeight() * 19/39F), 0, 0, kirby.getObjectWidth() * 20/59F,
                0));
        runHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 17/59F),
                (int)(kirby.getObjectHeight() * 18/39F), 0, 0, kirby.getObjectWidth() * 21/59F,
                0));
        runHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 19/59F),
                (int)(kirby.getObjectHeight() * 19/39F), 0, 0, kirby.getObjectWidth() * 20/59F,
                0));

        // Fall Hit Boxes
        fallHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 20/59F),
                (int)(kirby.getObjectHeight() * 19/39F), 0, 0, kirby.getObjectWidth() * 19/59F,
                0));
        fallHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 21/59F),
                (int)(kirby.getObjectHeight() * 19/39F), 0, 0, kirby.getObjectWidth() * 19/59F,
                0));

        // Flip Fall Hit Boxes
        flipFallHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 21/59F),
                (int)(kirby.getObjectHeight() * 19/39F), 0, 0, kirby.getObjectWidth() * 19/59F,
                0));
        flipFallHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 20/59F),
                (int)(kirby.getObjectHeight() * 18/39F), 0, 0, kirby.getObjectWidth() * 19/59F,
                0));
        flipFallHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 21/59F),
                (int)(kirby.getObjectHeight() * 19/39F), 0, 0, kirby.getObjectWidth() * 19/59F,
                0));
        flipFallHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 20/59F),
                (int)(kirby.getObjectHeight() * 20/39F), 0, 0, kirby.getObjectWidth() * 19/59F,
                0));
        flipFallHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 21/59F),
                (int)(kirby.getObjectHeight() * 17/39F), 0, 0, kirby.getObjectWidth() * 19/59F,
                0));
        flipFallHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 22/59F),
                (int)(kirby.getObjectHeight() * 18/39F), 0, 0, kirby.getObjectWidth() * 18/59F,
                0));

        for(int i = 0; i < 10; i ++) {
            flipFallHitBoxes.add(new HitBox(this, true, (int) (kirby.getObjectWidth() * 20 / 59F),
                    (int) (kirby.getObjectHeight() * 19 / 39F), 0, 0, kirby.getObjectWidth() * 19 / 59F,
                    0));
            flipFallHitBoxes.add(new HitBox(this, true, (int) (kirby.getObjectWidth() * 21 / 59F),
                    (int) (kirby.getObjectHeight() * 19 / 39F), 0, 0, kirby.getObjectWidth() * 19 / 59F,
                    0));
        }

        // Float Hit Boxes
        floatHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 24/59F),
                (int)(kirby.getObjectHeight() * 25/39F), 0, 0, kirby.getObjectWidth() * 17/59F,
                0));
        floatHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 24/59F),
                (int)(kirby.getObjectHeight() * 24/39F), 0, 0, kirby.getObjectWidth() * 17/59F,
                0));
        floatHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 24/59F),
                (int)(kirby.getObjectHeight() * 24/39F), 0, 0, kirby.getObjectWidth() * 17/59F,
                0));
        floatHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 25/59F),
                (int)(kirby.getObjectHeight() * 24/39F), 0, 0, kirby.getObjectWidth() * 17/59F,
                0));
        floatHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 26/59F),
                (int)(kirby.getObjectHeight() * 24/39F), 0, 0, kirby.getObjectWidth() * 16/59F,
                0));
        floatHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 25/59F),
                (int)(kirby.getObjectHeight() * 24/39F), 0, 0, kirby.getObjectWidth() * 17/59F,
                0));
        floatHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 24/59F),
                (int)(kirby.getObjectHeight() * 24/39F), 0, 0, kirby.getObjectWidth() * 17/59F,
                0));
        floatHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 24/59F),
                (int)(kirby.getObjectHeight() * 24/39F), 0, 0, kirby.getObjectWidth() * 17/59F,
                0));

        // Jump Hit Box
        jumpHitBox.add(new HitBox(InGameActivity.this, true, (int)(kirby.getObjectWidth() * 22/59F),
                (int)(kirby.getObjectHeight() * 20/39F), 0, 0, kirby.getObjectWidth() * 18/59F,
                0));

        // Float Fall Hit Boxes
        floatFallHitBoxes.add(new HitBox(InGameActivity.this, true, (int)(kirby.getObjectWidth() * 24/59F),
                (int)(kirby.getObjectHeight() * 23/39F), 0, 0, kirby.getObjectWidth() * 17/59F,
                0));
        floatFallHitBoxes.add(new HitBox(InGameActivity.this, true, (int)(kirby.getObjectWidth() * 26/59F),
                (int)(kirby.getObjectHeight() * 23/39F), 0, 0, kirby.getObjectWidth() * 16/59F,
                0));
        floatFallHitBoxes.add(new HitBox(InGameActivity.this, true, (int)(kirby.getObjectWidth() * 25/59F),
                (int)(kirby.getObjectHeight() * 23/39F), 0, 0, kirby.getObjectWidth() * 17/59F,
                0));

        // Start Float Hit Boxes
        startFloatHitBoxes.add(new HitBox(InGameActivity.this, true, (int)(kirby.getObjectWidth() * 19/59F),
                (int)(kirby.getObjectHeight() * 20/39F), 0, 0, kirby.getObjectWidth() * 20/59F,
                0));
        startFloatHitBoxes.add(new HitBox(InGameActivity.this, true, (int)(kirby.getObjectWidth() * 20/59F),
                (int)(kirby.getObjectHeight() * 22/39F), 0, 0, kirby.getObjectWidth() * 19/59F,
                0));
        startFloatHitBoxes.add(new HitBox(InGameActivity.this, true, (int)(kirby.getObjectWidth() * 21/59F),
                (int)(kirby.getObjectHeight() * 24/39F), 0, 0, kirby.getObjectWidth() * 19/59F,
                0));
        startFloatHitBoxes.add(new HitBox(InGameActivity.this, true, (int)(kirby.getObjectWidth() * 21/59F),
                (int)(kirby.getObjectHeight() * 31/39F), 0, 0, kirby.getObjectWidth() * 19/59F,
                0));
        startFloatHitBoxes.add(new HitBox(InGameActivity.this, true, (int)(kirby.getObjectWidth() * 23/59F),
                (int)(kirby.getObjectHeight() * 23/39F), 0, 0, kirby.getObjectWidth() * 18/59F,
                0));
        
        // Stop Float Hit Boxes
        stopFloatHitBoxes.add(new HitBox(InGameActivity.this, true, (int)(kirby.getObjectWidth() * 23/59F),
                (int)(kirby.getObjectHeight() * 23/39F), 0, 0, kirby.getObjectWidth() * 18/59F,
                0));
        stopFloatHitBoxes.add(new HitBox(InGameActivity.this, true, (int)(kirby.getObjectWidth() * 21/59F),
                (int)(kirby.getObjectHeight() * 31/39F), 0, 0, kirby.getObjectWidth() * 19/59F,
                0));
        stopFloatHitBoxes.add(new HitBox(InGameActivity.this, true, (int)(kirby.getObjectWidth() * 21/59F),
                (int)(kirby.getObjectHeight() * 24/39F), 0, 0, kirby.getObjectWidth() * 19/59F,
                0));
        stopFloatHitBoxes.add(new HitBox(InGameActivity.this, true, (int)(kirby.getObjectWidth() * 20/59F),
                (int)(kirby.getObjectHeight() * 22/39F), 0, 0, kirby.getObjectWidth() * 19/59F,
                0));
        stopFloatHitBoxes.add(new HitBox(InGameActivity.this, true, (int)(kirby.getObjectWidth() * 19/59F),
                (int)(kirby.getObjectHeight() * 20/39F), 0, 0, kirby.getObjectWidth() * 20/59F,
                0));

    }

    // Sets up character controls/interactions
    // Majority of game logic resides here
    @SuppressLint("ClickableViewAccessibility")
    private void controllerSetUp(float walkSpeed, float runSpeed, float jumpHeight, float highJumpHeight, float floatJumpHeight){

        leftButton.setOnTouchListener(new View.OnTouchListener() {
            
            private boolean isDown = false;

            private Handler dcHandler = new Handler();

            private boolean isDoubleClick = false;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (isDown) return true;
                        lrHandler.removeCallbacksAndMessages(null);
                        cHandler.removeCallbacksAndMessages(null);
                        aHandler.removeCallbacksAndMessages(null);
                        
                        if(isDoubleClick){
                            cHandler.postDelayed(leftRunCamera,0);
                            lrHandler.postDelayed(leftRun,0);
                        }
                        else{
                            cHandler.postDelayed(leftWalkCamera,0);
                            lrHandler.postDelayed(leftWalk,0);
                        }
                        
                        isDown = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        view.performClick();
                        if (!isDown) return true;

                        lrHandler.removeCallbacks(leftWalk);
                        cHandler.removeCallbacks(leftWalkCamera);

                        lrHandler.removeCallbacks(leftRun);
                        cHandler.removeCallbacks(leftRunCamera);
                        
                        if(kirby.isGrounded()) {
                            kirby.setObjectResource(kirby.getIdleResource());
                            kirby.setHitBox(kirby.getIdleHitBox());
                            kirby.showHitBox();
                        }
                        
                        isDown = false;

                        isDoubleClick = true;
                        
                        dcHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                isDoubleClick = false;
                            }
                        }, 100);

                        break;
                }
                return false;
            }

            Runnable leftWalk = kirby.walk(lrHandler, R.drawable.kirbywalk,"left", walkSpeed, walkHitBoxes,
                    new GameObject.CollisionListener() {
                        @Override
                        public void onCollision(GameObject object1, GameObject object2) {
                            if (GameObject.getCollisionType(object1, object2).contains("right")) {
                                if(!specialCollisionHandler(object1, object2)){
                                    if (!gameCamera.isFixedPosition()) {
                                        gameCamera.setXPosition(gameCamera.getXPosition() + walkSpeed);
                                    }
                                    kirby.setXPosition(kirby.getXPosition() + walkSpeed);
                                }

                                Log.i("Collision", object1.getObjectName() + " collided with the right of " + object2.getObjectName());
                            }
                        }
                    },
                    new Character.NotGroundedListener() {
                        @Override
                        public void notGrounded() {
                            kirby.stopFall();
                            udHandler.postDelayed(fall, 0);
                        }
                    },
                    new Character.PositionListener() {
                        @Override
                        public void atPosition(float xPosition, float yPosition) {
                            if((xPosition + kirby.getObjectWidth()/2F <= TitleActivity.WIDTH/TitleActivity.DENSITY - (gameCamera.getRightXPosition()-gameCamera.getLeftXPosition())/2
                                    + walkSpeed && xPosition + kirby.getObjectWidth()/2F >= TitleActivity.WIDTH/TitleActivity.DENSITY -
                                    (gameCamera.getRightXPosition()-gameCamera.getLeftXPosition())/2-walkSpeed)){
                                if(gameCamera.isFixedPosition()) {
                                    gameCamera.setFixedPosition(false);
                                    cHandler.removeCallbacksAndMessages(null);
                                    cHandler.postDelayed(leftWalkCamera, 0);
                                    kirby.setCenterXPosition(gameCamera.getXPosition());
                                }
                            }
                        }
                    });
            
            Runnable leftWalkCamera = gameCamera.moveLeft(cHandler, walkSpeed * TitleActivity.DENSITY);

            Runnable leftRun = kirby.walk(lrHandler, R.drawable.kirbyrun,"left", runSpeed, runHitBoxes,
                    new GameObject.CollisionListener() {
                        @Override
                        public void onCollision(GameObject object1, GameObject object2) {
                            if (GameObject.getCollisionType(object1, object2).contains("right")) {
                                if(!specialCollisionHandler(object1, object2)) {
                                    if (!gameCamera.isFixedPosition()) {
                                        gameCamera.setXPosition(gameCamera.getXPosition() + runSpeed);
                                    }
                                    kirby.setXPosition(kirby.getXPosition() + runSpeed);
                                }
                                Log.i("Collision", object1.getObjectName() + " collided with the right of " + object2.getObjectName());
                            }
                        }
                    },
                    new Character.NotGroundedListener() {
                        @Override
                        public void notGrounded() {
                            kirby.stopFall();
                            udHandler.postDelayed(fall, 0);
                        }
                    },
                    new Character.PositionListener() {
                        @Override
                        public void atPosition(float xPosition, float yPosition) {
                            if((xPosition + kirby.getObjectWidth()/2F <= TitleActivity.WIDTH/TitleActivity.DENSITY - (gameCamera.getRightXPosition()-gameCamera.getLeftXPosition())/2
                                    + runSpeed && xPosition + kirby.getObjectWidth()/2F >= TitleActivity.WIDTH/TitleActivity.DENSITY -
                                    (gameCamera.getRightXPosition()-gameCamera.getLeftXPosition())/2-runSpeed)){
                                if(gameCamera.isFixedPosition()) {
                                    gameCamera.setFixedPosition(false);
                                    cHandler.removeCallbacksAndMessages(null);
                                    cHandler.postDelayed(leftRunCamera, 0);
                                    kirby.setCenterXPosition(gameCamera.getXPosition());
                                }
                            }
                        }
                    });

            Runnable leftRunCamera = gameCamera.moveLeft(cHandler, runSpeed * TitleActivity.DENSITY);
            
            
            Runnable fall = kirby.fall(udHandler, R.drawable.kirbyfall, true, fallHitBoxes, new GameObject.CollisionListener() {
                @Override
                public void onCollision(GameObject object1, GameObject object2) {
                    if (GameObject.getCollisionType(object1, object2).contains("top")) {
                        if(!specialCollisionHandler(object1, object2)) {
                            udHandler.removeCallbacksAndMessages(null);
                            kirby.stopFall();
                            kirby.setGrounded(true);
                            kirby.setObjectResource(kirby.getIdleResource());

                            kirby.setYPosition(object2.getHitBox().topLeft().y - kirby.getHitBox().getYBottom());

                            kirby.setHitBox(kirby.getIdleHitBox());
                            kirby.showHitBox();
                        }

                        Log.i("Collision", object1.getObjectName() + " collided with top of " + object2.getObjectName());
                    }
                }
            }, 
                    new Character.PositionListener() {
                @Override
                public void atPosition(float xPosition, float yPosition) {
                    
                }
            });
        });

        rightButton.setOnTouchListener(new View.OnTouchListener() {
            private boolean isDown = false;

            private Handler dcHandler = new Handler();

            private boolean isDoubleClick = false;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (isDown) return true;
                        lrHandler.removeCallbacksAndMessages(null);
                        cHandler.removeCallbacksAndMessages(null);
                        aHandler.removeCallbacksAndMessages(null);

                        if(isDoubleClick){
                            cHandler.postDelayed(rightRunCamera,0);
                            lrHandler.postDelayed(rightRun,0);
                        }
                        else{
                            cHandler.postDelayed(rightWalkCamera,0);
                            lrHandler.postDelayed(rightWalk,0);
                        }

                        isDown = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        view.performClick();
                        if (!isDown) return true;

                        lrHandler.removeCallbacks(rightWalk);
                        cHandler.removeCallbacks(rightWalkCamera);

                        lrHandler.removeCallbacks(rightRun);
                        cHandler.removeCallbacks(rightRunCamera);

                        if(kirby.isGrounded()) {
                            kirby.setObjectResource(kirby.getIdleResource());
                            kirby.setHitBox(kirby.getIdleHitBox());
                            kirby.showHitBox();
                        }

                        isDown = false;

                        isDoubleClick = true;

                        dcHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                isDoubleClick = false;
                            }
                        }, 100);

                        break;
                }
                return false;
            }

            Runnable rightWalk = kirby.walk(lrHandler, R.drawable.kirbywalk,"right", walkSpeed, walkHitBoxes,
                    new GameObject.CollisionListener() {
                        @Override
                        public void onCollision(GameObject object1, GameObject object2) {
                            if (GameObject.getCollisionType(object1, object2).contains("left")) {
                                if(!specialCollisionHandler(object1, object2)) {
                                    if (!gameCamera.isFixedPosition()) {
                                        gameCamera.setXPosition(gameCamera.getXPosition() - walkSpeed);
                                    }
                                    kirby.setXPosition(kirby.getXPosition() - walkSpeed);
                                }

                                Log.i("Collision", object1.getObjectName() + " collided with the left of " + object2.getObjectName());
                            }
                        }
                    },
                    new Character.NotGroundedListener() {
                        @Override
                        public void notGrounded() {
                            kirby.stopFall();
                            udHandler.postDelayed(fall, 0);
                        }
                    },
                    new Character.PositionListener() {
                        @Override
                        public void atPosition(float xPosition, float yPosition) {
                            if((xPosition + kirby.getObjectWidth()/2F<= (gameCamera.getRightXPosition()-gameCamera.getLeftXPosition())/2 + walkSpeed
                                    && xPosition + kirby.getObjectWidth()/2F>= (gameCamera.getRightXPosition()-gameCamera.getLeftXPosition())/2 - walkSpeed)){
                                if(gameCamera.isFixedPosition()) {
                                    gameCamera.setFixedPosition(false);
                                    cHandler.removeCallbacksAndMessages(null);
                                    cHandler.postDelayed(rightWalkCamera, 0);
                                    kirby.setCenterXPosition(gameCamera.getXPosition());
                                }
                            }
                        }
                    });

            Runnable rightWalkCamera = gameCamera.moveRight(cHandler, walkSpeed * TitleActivity.DENSITY);

            Runnable rightRun = kirby.walk(lrHandler, R.drawable.kirbyrun,"right", runSpeed, runHitBoxes,
                    new GameObject.CollisionListener() {
                        @Override
                        public void onCollision(GameObject object1, GameObject object2) {
                            if (GameObject.getCollisionType(object1, object2).contains("left")) {
                                if(!specialCollisionHandler(object1, object2)) {
                                    if (!gameCamera.isFixedPosition()) {
                                        gameCamera.setXPosition(gameCamera.getXPosition() - runSpeed);
                                    }
                                    kirby.setXPosition(kirby.getXPosition() - runSpeed);
                                }
                                Log.i("Collision", object1.getObjectName() + " collided with the left of " + object2.getObjectName());
                            }
                        }
                    },
                    new Character.NotGroundedListener() {
                        @Override
                        public void notGrounded() {
                            kirby.stopFall();
                            udHandler.postDelayed(fall, 0);
                        }
                    },
                    new Character.PositionListener() {
                        @Override
                        public void atPosition(float xPosition, float yPosition) {
                            if((xPosition + kirby.getObjectWidth()/2F <= (gameCamera.getRightXPosition()-gameCamera.getLeftXPosition())/2 + runSpeed
                                    && xPosition + kirby.getObjectWidth()/2F >= (gameCamera.getRightXPosition()-gameCamera.getLeftXPosition())/2 - runSpeed)){
                                if(gameCamera.isFixedPosition()) {
                                    gameCamera.setFixedPosition(false);
                                    cHandler.removeCallbacksAndMessages(null);
                                    cHandler.postDelayed(rightRunCamera, 0);
                                    kirby.setCenterXPosition(gameCamera.getXPosition());
                                }
                            }
                        }
                    });

            Runnable rightRunCamera = gameCamera.moveRight(cHandler, runSpeed * TitleActivity.DENSITY);


            Runnable fall = kirby.fall(udHandler, R.drawable.kirbyfall, true, fallHitBoxes, new GameObject.CollisionListener() {
                        @Override
                        public void onCollision(GameObject object1, GameObject object2) {
                            if (GameObject.getCollisionType(object1, object2).contains("top")) {
                                if(!specialCollisionHandler(object1, object2)) {
                                    udHandler.removeCallbacksAndMessages(null);
                                    kirby.stopFall();
                                    kirby.setGrounded(true);
                                    kirby.setObjectResource(kirby.getIdleResource());

                                    kirby.setYPosition(object2.getHitBox().topLeft().y - kirby.getHitBox().getYBottom());

                                    kirby.setHitBox(kirby.getIdleHitBox());
                                    kirby.showHitBox();
                                }

                                Log.i("Collision", object1.getObjectName() + " collided with top of " + object2.getObjectName());
                            }
                        }
                    },
                    new Character.PositionListener() {
                        @Override
                        public void atPosition(float xPosition, float yPosition) {

                        }
                    });
        });

        jumpButton.setOnTouchListener(new View.OnTouchListener() {

            private boolean isDown = false;
            private Handler thHandler = new Handler();
            private boolean isClick = true;
            private boolean shortJump = false;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (isDown) return true;

                        thHandler.removeCallbacksAndMessages(null);

                        isClick = true;
                        shortJump = false;


                        thHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // High jump
                                if(!shortJump && kirby.isGrounded()) {
                                    isClick = false;
                                    udHandler.removeCallbacksAndMessages(null);
                                    aHandler.removeCallbacksAndMessages(null);
                                    kirby.stopJump();
                                    udHandler.postDelayed(highJump, 0);
                                }
                            }
                        }, 150);

                        isDown = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        view.performClick();
                        if (!isDown) return true;

                        // Normal jump/float
                        if(isClick){

                            if(kirby.isGrounded()){
                                shortJump = true;
                                udHandler.removeCallbacksAndMessages(null);
                                aHandler.removeCallbacksAndMessages(null);
                                kirby.stopJump();
                                udHandler.postDelayed(jump,0);
                            }
                            else {
                                if(!isFloating && jumpCount < 6) {
                                    jumpCount++;
                                    shortJump = true;
                                    udHandler.removeCallbacksAndMessages(null);
                                    aHandler.removeCallbacksAndMessages(null);
                                    udHandler.postDelayed(startFloat, 0);
                                    isFloating = true;
                                }
                                else if(startFloatFinished && jumpCount < 6){
                                    jumpCount++;
                                    shortJump = true;
                                    udHandler.removeCallbacksAndMessages(null);
                                    aHandler.removeCallbacksAndMessages(null);
                                    kirby.stopJump();
                                    udHandler.postDelayed(floatJump,0);
                                }
                            }
                        }

                        isDown = false;

                        break;
                }
                return false;
            }



            Runnable jump = kirby.jump(udHandler, R.drawable.kirby672, false, jumpHeight, jumpHitBox,
                    new GameObject.CollisionListener() {
                        @Override
                        public void onCollision(GameObject object1, GameObject object2) {
                            if(GameObject.getCollisionType(object1, object2).contains("bottom")){
                                if(!specialCollisionHandler(object1, object2)) {
                                    udHandler.removeCallbacksAndMessages(null);
                                    kirby.stopJump();
                                    kirby.setYPosition(object2.getHitBox().bottomRight().y -
                                            kirby.getHitBox().getHitHeight() - kirby.getHitBox().getYBottom());
                                    kirby.stopFall();
                                    udHandler.postDelayed(fall, 0);
                                }
                                Log.i("Collision", object1.getObjectName() + " collided with bottom of " + object2.getObjectName());
                            }
                        }
                    },
                    new Character.CharacterListener() {
                        @Override
                        public void onActionComplete() {
                            udHandler.removeCallbacksAndMessages(null);
                            aHandler.removeCallbacksAndMessages(null);
                            kirby.stopFall();
                            udHandler.postDelayed(fall,0);
                        }
                    },
                    new Character.PositionListener() {
                        @Override
                        public void atPosition(float xPosition, float yPosition) {

                        }
                    });

            Runnable fall = kirby.fall(udHandler, R.drawable.kirbyfall, true, fallHitBoxes, new GameObject.CollisionListener() {
                        @Override
                        public void onCollision(GameObject object1, GameObject object2) {
                            if (GameObject.getCollisionType(object1, object2).contains("top")) {
                                if(!specialCollisionHandler(object1, object2)) {
                                    isFloating = false;
                                    startFloatFinished = false;
                                    jumpCount = 0;
                                    udHandler.removeCallbacksAndMessages(null);
                                    kirby.stopFall();
                                    kirby.setGrounded(true);
                                    kirby.setObjectResource(kirby.getIdleResource());

                                    kirby.setYPosition(object2.getHitBox().topLeft().y - kirby.getHitBox().getYBottom());

                                    kirby.setHitBox(kirby.getIdleHitBox());
                                    kirby.showHitBox();
                                }

                                Log.i("Collision", object1.getObjectName() + " collided with top of " + object2.getObjectName());
                            }
                        }
                    },
                    new Character.PositionListener() {
                        @Override
                        public void atPosition(float xPosition, float yPosition) {

                        }
                    });

            Runnable highJump = kirby.jump(udHandler, R.drawable.kirby672, false, highJumpHeight, jumpHitBox,
                    new GameObject.CollisionListener() {
                        @Override
                        public void onCollision(GameObject object1, GameObject object2) {
                            if(GameObject.getCollisionType(object1, object2).contains("bottom")){
                                if(!specialCollisionHandler(object1, object2)) {
                                    udHandler.removeCallbacksAndMessages(null);
                                    kirby.stopJump();
                                    kirby.setYPosition(object2.getHitBox().bottomRight().y -
                                            kirby.getHitBox().getHitHeight() - kirby.getHitBox().getYBottom());
                                    kirby.stopFall();
                                    udHandler.postDelayed(flipFall, 0);
                                }
                                Log.i("Collision", object1.getObjectName() + " collided with bottom of " + object2.getObjectName());
                            }
                        }
                    },
                    new Character.CharacterListener() {
                        @Override
                        public void onActionComplete() {
                            udHandler.removeCallbacksAndMessages(null);
                            aHandler.removeCallbacksAndMessages(null);
                            kirby.stopFall();
                            udHandler.postDelayed(flipFall,0);
                        }
                    },
                    new Character.PositionListener() {
                        @Override
                        public void atPosition(float xPosition, float yPosition) {

                        }
                    });

            Runnable flipFall = kirby.fall(udHandler, R.drawable.kirbyflipfall, true, flipFallHitBoxes, new GameObject.CollisionListener() {
                        @Override
                        public void onCollision(GameObject object1, GameObject object2) {
                            if (GameObject.getCollisionType(object1, object2).contains("top")) {
                                if(!specialCollisionHandler(object1, object2)) {
                                    udHandler.removeCallbacksAndMessages(null);
                                    kirby.stopFall();
                                    kirby.setGrounded(true);
                                    kirby.setObjectResource(kirby.getIdleResource());

                                    kirby.setYPosition(object2.getHitBox().topLeft().y - kirby.getHitBox().getYBottom());

                                    kirby.setHitBox(kirby.getIdleHitBox());
                                    kirby.showHitBox();
                                }

                                Log.i("Collision", object1.getObjectName() + " collided with top of " + object2.getObjectName());
                            }
                        }
                    },
                    new Character.PositionListener() {
                        @Override
                        public void atPosition(float xPosition, float yPosition) {

                        }
                    });

            Runnable startFloat = kirby.animatedAction(udHandler, R.drawable.kirbystartfloat, startFloatHitBoxes,
                    new GameObject.CollisionListener() {
                        @Override
                        public void onCollision(GameObject object1, GameObject object2) {
                            specialCollisionHandler(object1, object2);
                        }
                    },
                    new Character.CharacterListener() {
                        @Override
                        public void onActionComplete() {
                            startFloatFinished = true;
                            udHandler.removeCallbacksAndMessages(null);
                            aHandler.removeCallbacksAndMessages(null);
                            kirby.stopJump();
                            udHandler.postDelayed(floatJump, 0);
                        }
                    });

            Runnable floatJump = kirby.jump(udHandler, R.drawable.kirbyfloat, true, floatJumpHeight, floatHitBoxes,
                    new GameObject.CollisionListener() {
                        @Override
                        public void onCollision(GameObject object1, GameObject object2) {
                            if(GameObject.getCollisionType(object1, object2).contains("bottom")){
                                if(!specialCollisionHandler(object1, object2)) {
                                    udHandler.removeCallbacksAndMessages(null);
                                    kirby.stopJump();
                                    kirby.setYPosition(object2.getHitBox().bottomRight().y -
                                            kirby.getHitBox().getHitHeight() - kirby.getHitBox().getYBottom());
                                    kirby.stopFall();
                                    udHandler.postDelayed(floatFall, 0);
                                }
                                Log.i("Collision", object1.getObjectName() + " collided with bottom of " + object2.getObjectName());
                            }
                        }
                    },
                    new Character.CharacterListener() {
                        @Override
                        public void onActionComplete() {
                            udHandler.removeCallbacksAndMessages(null);
                            aHandler.removeCallbacksAndMessages(null);
                            kirby.stopFall();
                            udHandler.postDelayed(floatFall,0);
                        }
                    },
                    new Character.PositionListener() {
                        @Override
                        public void atPosition(float xPosition, float yPosition) {
                        }
                    });

            Runnable floatFall = kirby.fall(udHandler, R.drawable.kirbyfloatfall, true, GameObject.GRAVITY/3F, floatFallHitBoxes,
                    new GameObject.CollisionListener() {
                        @Override
                        public void onCollision(GameObject object1, GameObject object2) {
                            if (GameObject.getCollisionType(object1, object2).contains("top")) {
                                if(!specialCollisionHandler(object1, object2)) {
                                    isFloating = false;
                                    udHandler.removeCallbacksAndMessages(null);
                                    kirby.stopFall();
                                    kirby.setGrounded(false);

                                    kirby.setYPosition(object2.getHitBox().topLeft().y - kirby.getHitBox().getYBottom());

                                    udHandler.postDelayed(stopFloat, 0);
                                }


                                Log.i("Collision", object1.getObjectName() + " collided with top of " + object2.getObjectName());
                            }
                        }
                    },
                    new Character.PositionListener() {
                        @Override
                        public void atPosition(float xPosition, float yPosition) {

                        }
                    });

            Runnable stopFloat = kirby.animatedAction(udHandler, R.drawable.kirbystopfloat, stopFloatHitBoxes,
                    new GameObject.CollisionListener() {
                        @Override
                        public void onCollision(GameObject object1, GameObject object2) {
                            specialCollisionHandler(object1, object2);
                        }
                    },
                    new Character.CharacterListener() {
                        @Override
                        public void onActionComplete() {
                            isFloating = false;
                            startFloatFinished = false;
                            udHandler.removeCallbacksAndMessages(null);
                            aHandler.removeCallbacksAndMessages(null);
                            udHandler.postDelayed(fall, 0);
                        }
                    });


        });

        actionButton.setOnTouchListener(new View.OnTouchListener() {

            private boolean isDown = false;
            private Handler thHandler = new Handler();
            private boolean isClick = true;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (isDown) return true;

                        thHandler.removeCallbacksAndMessages(null);

                        isClick = true;


                        thHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // Hold

                            }
                        }, 150);

                        isDown = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        view.performClick();
                        if (!isDown) return true;

                        // Click
                        if(isClick){
                            if(isFloating && startFloatFinished) {
                                udHandler.removeCallbacksAndMessages(null);
                                aHandler.removeCallbacksAndMessages(null);
                                udHandler.postDelayed(stopFloat, 0);
                            }
                        }

                        isDown = false;

                        break;
                }
                return false;
            }

            Runnable fall = kirby.fall(udHandler, R.drawable.kirbyfall, true, fallHitBoxes, new GameObject.CollisionListener() {
                        @Override
                        public void onCollision(GameObject object1, GameObject object2) {
                            if (GameObject.getCollisionType(object1, object2).contains("top")) {
                                if(!specialCollisionHandler(object1, object2)) {
                                    isFloating = false;
                                    startFloatFinished = false;
                                    jumpCount = 0;
                                    udHandler.removeCallbacksAndMessages(null);
                                    kirby.stopFall();
                                    kirby.setGrounded(true);
                                    kirby.setObjectResource(kirby.getIdleResource());

                                    kirby.setYPosition(object2.getHitBox().topLeft().y - kirby.getHitBox().getYBottom());

                                    kirby.setHitBox(kirby.getIdleHitBox());
                                    kirby.showHitBox();
                                }

                                Log.i("Collision", object1.getObjectName() + " collided with top of " + object2.getObjectName());
                            }
                        }
                    },
                    new Character.PositionListener() {
                        @Override
                        public void atPosition(float xPosition, float yPosition) {

                        }
                    });

            Runnable stopFloat = kirby.animatedAction(udHandler, R.drawable.kirbystopfloat, stopFloatHitBoxes,
                    new GameObject.CollisionListener() {
                        @Override
                        public void onCollision(GameObject object1, GameObject object2) {
                            specialCollisionHandler(object1, object2);
                        }
                    },
                    new Character.CharacterListener() {
                        @Override
                        public void onActionComplete() {
                            isFloating = false;
                            startFloatFinished = false;
                            udHandler.removeCallbacksAndMessages(null);
                            aHandler.removeCallbacksAndMessages(null);
                            kirby.stopFall();
                            udHandler.postDelayed(fall, 0);
                        }
                    });

        });

    }

    private boolean specialCollisionHandler(GameObject object1, GameObject object2){

        if(object2.isIngredient() && object1.isCharacter()){
            if(!((Ingredient) object2).isCollected()) {
                ((Ingredient) object2).setCollected(true);
                Runnable collectAnimation = ((Ingredient) object2).collected(oHandler);
                oHandler.postDelayed(collectAnimation, 0);
            }
            return true;
        }
        else if(object1.isIngredient() && object2.isCharacter()){
            if(!((Ingredient) object1).isCollected()) {
                ((Ingredient) object1).setCollected(true);
                Runnable collectAnimation = ((Ingredient) object1).collected(oHandler);
                oHandler.postDelayed(collectAnimation, 0);
            }
            return true;
        }

        return false;
    }

    // This method helps to find the scale for the game camera to zoom to
    private float fitZoom(float backgroundWidth, float backgroundHeight){
        return ((float) TitleActivity.HEIGHT/TitleActivity.WIDTH) * (backgroundWidth/backgroundHeight);
    }

    // Debugging method
    public void viewInfoDebug(View view) throws XmlPullParserException, IOException {

        GameObject.displayHitBoxes = true;
        for(GameObject object : collisionGameLayout.getLayoutObjects()){
            object.showHitBox();
        }


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
                        gameCamera.moveTo(kirby.getCenterXPosition(), centerY, 10);
                    }
                    catch (Exception e){
                        gameCamera.setLeftXPosition(0);
                    }
                }
            });
            zoomed = true;
        }

    }

    public void dayNightCycle(){
        rHandler = new Handler();

        Runnable lighting = new Runnable() {

            private int bA = 0;
            private int bR = 255;
            private int bG = 255;
            private int bB = 190;

            private int oA = 0;
            private int oR = 255;
            private int oG = 255;
            private int oB = 190;


            @Override
            public void run() {

                if(timeOfDay.toLowerCase().equals("morning")) {

                    if (bA < 45) {
                        bA++;
                        oA++;
                        GameLayout.brightenBackgroundLighting(Color.argb(bA, bR, bG, bB));
                        GameLayout.brightenObjectLighting(Color.argb(oA, oR, oG, oB));
                    }
                    else{
                        timeOfDay = "Noon";
                    }

                    rHandler.postDelayed(this,3667);
                }
                else if(timeOfDay.toLowerCase().equals("noon")) {
                    if (bA > 0) {
                        bA--;
                        oA--;
                        GameLayout.brightenBackgroundLighting(Color.argb(bA, bR, bG, bB));
                        GameLayout.brightenObjectLighting(Color.argb(oA, oR, oG, oB));
                    }
                    else{
                        timeOfDay = "Sunset";
                        bA = 255;
                        bR = 255;
                        bG = 255;
                        bB = 255;

                        oA = 255;
                        oR = 255;
                        oG = 255;
                        oB = 255;
                    }

                    rHandler.postDelayed(this,3667);
                }
                else if(timeOfDay.toLowerCase().equals("sunset")){
                    if(toColor(255,70,70,1,40,40,40,1)){
                        timeOfDay = "Night";
                    }
                    rHandler.postDelayed(this,280);
                }
                else if(timeOfDay.toLowerCase().equals("night")){
                    if(toColor(50,50,70,1,35,35,35,1)){
                        timeOfDay = "Sunrise1";
                    }
                    rHandler.postDelayed(this,690);
                }
                else if(timeOfDay.toLowerCase().equals("sunrise1")){
                    if(toColor(194,64,64,2,60,60,60,1)){
                        timeOfDay = "Sunrise2";
                    }
                    rHandler.postDelayed(this,625);
                }
                else if(timeOfDay.toLowerCase().equals("sunrise2")){
                    if(toColor(255,255,255,1,255,255,255,1)){
                        bA = 0;
                        oA = 0;
                        bB = 190;
                        oB = 190;
                        timeOfDay = "Morning";
                    }
                    rHandler.postDelayed(this,231);
                }

            }

            // rates must lead to color values being equal to the desired color.
            private boolean toColor(int br, int bg, int bb, int bRate, int or, int og, int ob, int oRate){
                if(br==bR && bg == bG && bb==bB && or==oR && og==oG && ob==oB){
                    return true;
                }

                if(bR<br){
                    bR+=bRate;
                }
                else if(bR>br){
                    bR-=bRate;
                }

                if(bG<bg){
                    bG+=bRate;
                }
                else if(bG>bg){
                    bG-=bRate;
                }

                if(bB<bb){
                    bB+=bRate;
                }
                else if(bB>bb){
                    bB-=bRate;
                }

                if(oR<or){
                    oR+=oRate;
                }
                else if(oR>or){
                    oR-=oRate;
                }

                if(oG<og){
                    oG+=oRate;
                }
                else if(oG>og){
                    oG-=oRate;
                }

                if(oB<ob){
                    oB+=oRate;
                }
                else if(oB>ob){
                    oB-=oRate;
                }


                GameLayout.darkenBackgroundLighting(Color.argb(255, bR, bG, bB));
                GameLayout.darkenObjectLighting(Color.argb(255, oR, oG, oB));
                return false;
            }
        };

        rHandler.postDelayed(lighting,0);
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