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
import java.util.HashMap;

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

    // NPC Handler
    private Handler npcHandler;

    // Deal with other animations unrelated to characters
    private Handler oHandler;

    // Deal with other runnables unrelated to animations
    private Handler rHandler;

    // Game Camera variables
    private Camera gameCamera;

    // Deal with camera movement
    private Handler cHandler;

    private Runnable leftWalkCamera, leftRunCamera, rightWalkCamera, rightRunCamera;

    // Character variables
    // (SAVE)
    private Character kirby;
    private float walkSpeed, runSpeed, jumpHeight, highJumpHeight, floatJumpHeight;

    private ArrayList<HitBox> kirbyWalkHitBoxes, kirbyRunHitBoxes, kirbyFallHitBoxes, kirbyFlipFallHitBoxes,
            kirbyFloatHitBoxes, kirbyJumpHitBox, kirbyFloatFallHitBoxes, kirbyStartFloatHitBoxes, kirbyStopFloatHitBoxes;
    private boolean isFloating;
    private boolean startFloatFinished;
    private int jumpCount;

    private HashMap<String, Character> allNPCs = new HashMap<String, Character>();
    private ArrayList<HitBox> waddleDeeWalkHitBoxes, waddleDeeRunHitBoxes, waddleDeeFlipFallHitBoxes, waddleDeeJumpHitBox;
    
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
        cHandler = new Handler();
        oHandler = new Handler();
        npcHandler = new Handler();


        // Debugging variables
        gameCamera = new Camera(scalingFrameLayout,gameContainerLayout);
        centerX = gameCamera.getXPosition();
        centerY = gameCamera.getYPosition();
        zoomed = true;

        // Set up time once
        timeOfDay = "Morning";

        // Set up characters once only.
        characterSetUp();

        // Set up the camera to the appropriate environment and then instantiate all environment objects. Repeat for each environment.
        // Set up once only
        cameraSetUp("test");

        testEnvironmentBackgroundGameObjects = new ArrayList<GameObject>();
        testEnvironmentCollisionGameObjects = new ArrayList<GameObject>();
        testEnvironmentForegroundGameObjects = new ArrayList<GameObject>();

        allNPCs.get("Waddle Dee 1").setYPosition(gameCamera.getBottomYPosition() + 6);
        allNPCs.get("Waddle Dee 1").setXPosition(100);
        testEnvironmentCollisionGameObjects.add(allNPCs.get("Waddle Dee 1"));

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
        // *Should only do this part of the set up once...and then save it.*
        gameCamera = new Camera(scalingFrameLayout, gameContainerLayout);
        leftWalkCamera = gameCamera.moveLeft(cHandler, walkSpeed * TitleActivity.DENSITY);
        leftRunCamera = gameCamera.moveLeft(cHandler, runSpeed * TitleActivity.DENSITY);
        rightWalkCamera = gameCamera.moveRight(cHandler, walkSpeed * TitleActivity.DENSITY);
        rightRunCamera = gameCamera.moveRight(cHandler, runSpeed * TitleActivity.DENSITY);


        if(environment.toLowerCase().equals("test")){
            gameCamera.setScale(fitZoom(3832,359));
            gameCamera.setLeftXPosition(0);
        }
        else if(environment.toLowerCase().equals("test2")){
            gameCamera.setScale(fitZoom(3832,500));
            gameCamera.setLeftXPosition(0);
        }
    }

    // Sets up a chosen in-game environment
    private void environmentSetUp(String environment){
        try{
            backgroundGameLayout.removeAllLayoutObjects();
            backgroundGameLayout.setBackgroundImage(android.R.color.transparent);
            collisionGameLayout.removeAllLayoutObjects();
            foregroundGameLayout.removeAllLayoutObjects();

            backgroundGameLayout = null;
            collisionGameLayout = null;
            foregroundGameLayout = null;
        }
        catch(Exception e){
            Log.i("EnvironmentSetup","GameLayouts not created yet");
        }

        try{
            oHandler.removeCallbacksAndMessages(null);
        }
        catch(Exception e){
            Log.i("EnvironmentSetup", "Some handlers not created yet");
        }

        backgroundGameLayout = new GameLayout(this,"Background", backgroundLayout);
        backgroundGameLayout.setBackgroundImageView(findViewById(R.id.backgroundImage));
        collisionGameLayout = new GameLayout(this, "Collision", collisionLayout);
        foregroundGameLayout = new GameLayout(this, "Foreground",foregroundLayout);

        cameraSetUp(environment);

        // In general: First add Kirby, then add Items, then add the various ArrayLists of GameObjects to appropriate GameLayouts,
        // then deal with NPC actions.

        if(environment.toLowerCase().equals("test")){
            backgroundGameLayout.setBackgroundImage(R.drawable.cloudsbackgroundextended);
            backgroundGameLayout.setLayoutObjects(testEnvironmentBackgroundGameObjects);

            collisionGameLayout.removeLayoutObject(kirby);
            kirby.setYPosition(gameCamera.getBottomYPosition() + 6 - kirby.getHitBox().getYBottom());
            itemSetup(environment);
            collisionGameLayout.addLayoutObject(kirby);
            collisionGameLayout.addLayoutObjects(testEnvironmentCollisionGameObjects);

            gameCamera.setFixedPosition(true);

            npcHandler.postDelayed(new Runnable() {

                private boolean isWalking = false;

                @Override
                public void run() {
                    if(allNPCs.get("Waddle Dee 1").isGrounded()) {
                        allNPCs.get("Waddle Dee 1").getUdHandler().removeCallbacksAndMessages(null);
                        allNPCs.get("Waddle Dee 1").stopJump();
                        allNPCs.get("Waddle Dee 1").stopFall();
                        allNPCs.get("Waddle Dee 1").getUdHandler().postDelayed(allNPCs.get("Waddle Dee 1").getAllActions().get("Jump"), 0);
                    }

                    if(!isWalking){
                        allNPCs.get("Waddle Dee 1").getLrHandler().postDelayed(allNPCs.get("Waddle Dee 1").getAllActions().get("Left Walk"),0);
                        isWalking = true;
                    }


                    npcHandler.postDelayed(this, 1500);
                }
            },2000);



        }
        else if(environment.toLowerCase().equals("test2")){
            kirby.setYPosition(gameCamera.getBottomYPosition() + 6 - kirby.getHitBox().getYBottom());

            collisionGameLayout.addLayoutObject(kirby);
            collisionGameLayout.addLayoutObject(new GameObject(this, "Ground", (int)(TitleActivity.WIDTH/TitleActivity.DENSITY),10,
                    R.drawable.testground, 0, gameCamera.getBottomYPosition(), true, new HitBox(this,true,
                    (int)(TitleActivity.WIDTH/TitleActivity.DENSITY), 6, 0, gameCamera.getBottomYPosition(),0,0)));
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
                            if(!specialCollisionHandler(object1, object2, GameObject.getCollisionType(object1, object2)) && !object2.isCharacter() && !object2.isIngredient()) {
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

    // Sets up all characters for the first time the app is used
    private void characterSetUp(){
        // In general: Create Character, define attributes of character (ex: walk speed), define all hit boxes, and finally define all actions

        // Kirby Set Up
        HitBox kirbyIdleHitBox = new HitBox(this, true, (int) (30 * 20/59F),(int)(20 * 18/39F),
                0, 0, 30 * 19/59F,0);

        kirby = new Character(this, "Kirby", 30, 20, 0, 0,
                kirbyIdleHitBox, true, R.drawable.kirbyidle);
        kirby.setObjectResource(R.drawable.kirbyidle);

        walkSpeed = 1/3F;
        runSpeed = 1/2F;
        jumpHeight = 10;
        highJumpHeight = 20;
        floatJumpHeight = 5;

        // Hit Boxes Set Up

        kirbyWalkHitBoxes = new ArrayList<HitBox>();
        kirbyRunHitBoxes = new ArrayList<HitBox>();
        kirbyFallHitBoxes = new ArrayList<HitBox>();
        kirbyFlipFallHitBoxes = new ArrayList<HitBox>();
        kirbyFloatHitBoxes = new ArrayList<HitBox>();
        kirbyJumpHitBox = new ArrayList<HitBox>();
        kirbyFloatFallHitBoxes = new ArrayList<HitBox>();
        kirbyStartFloatHitBoxes = new ArrayList<HitBox>();
        kirbyStopFloatHitBoxes = new ArrayList<HitBox>();

        // Walking Hit Boxes
        kirbyWalkHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 20/59F),
                (int)(kirby.getObjectHeight() * 18/39F), 0, 0, kirby.getObjectWidth() * 19/59F,
                0));
        kirbyWalkHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 21/59F),
                (int)(kirby.getObjectHeight() * 19/39F), 0, 0, kirby.getObjectWidth() * 19/59F,
                0));
        kirbyWalkHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 20/59F),
                (int)(kirby.getObjectHeight() * 18/39F), 0, 0, kirby.getObjectWidth() * 19/59F,
                0));
        kirbyWalkHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 19/59F),
                (int)(kirby.getObjectHeight() * 16/39F), 0, 0, kirby.getObjectWidth() * 20/59F,
                0));
        kirbyWalkHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 17/59F),
                (int)(kirby.getObjectHeight() * 17/39F), 0, 0, kirby.getObjectWidth() * 21/59F,
                0));
        kirbyWalkHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 17/59F),
                (int)(kirby.getObjectHeight() * 18/39F), 0, 0, kirby.getObjectWidth() * 21/59F,
                0));
        kirbyWalkHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 21/59F),
                (int)(kirby.getObjectHeight() * 19/39F), 0, 0, kirby.getObjectWidth() * 19/59F,
                0));
        kirbyWalkHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 20/59F),
                (int)(kirby.getObjectHeight() * 18/39F), 0, 0, kirby.getObjectWidth() * 19/59F,
                0));
        kirbyWalkHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 19/59F),
                (int)(kirby.getObjectHeight() * 16/39F), 0, 0, kirby.getObjectWidth() * 20/59F,
                0));
        kirbyWalkHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 19/59F),
                (int)(kirby.getObjectHeight() * 17/39F), 0, 0, kirby.getObjectWidth() * 20/59F,
                0));

        // Running Hit Boxes
        kirbyRunHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 24/59F),
                (int)(kirby.getObjectHeight() * 19/39F), 0, 0, kirby.getObjectWidth() * 17/59F,
                0));
        kirbyRunHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 19/59F),
                (int)(kirby.getObjectHeight() * 19/39F), 0, 0, kirby.getObjectWidth() * 20/59F,
                0));
        kirbyRunHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 17/59F),
                (int)(kirby.getObjectHeight() * 18/39F), 0, 0, kirby.getObjectWidth() * 21/59F,
                0));
        kirbyRunHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 18/59F),
                (int)(kirby.getObjectHeight() * 19/39F), 0, 0, kirby.getObjectWidth() * 20/59F,
                0));
        kirbyRunHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 19/59F),
                (int)(kirby.getObjectHeight() * 19/39F), 0, 0, kirby.getObjectWidth() * 20/59F,
                0));
        kirbyRunHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 18/59F),
                (int)(kirby.getObjectHeight() * 19/39F), 0, 0, kirby.getObjectWidth() * 20/59F,
                0));
        kirbyRunHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 17/59F),
                (int)(kirby.getObjectHeight() * 18/39F), 0, 0, kirby.getObjectWidth() * 21/59F,
                0));
        kirbyRunHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 19/59F),
                (int)(kirby.getObjectHeight() * 19/39F), 0, 0, kirby.getObjectWidth() * 20/59F,
                0));

        // Fall Hit Boxes
        kirbyFallHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 20/59F),
                (int)(kirby.getObjectHeight() * 19/39F), 0, 0, kirby.getObjectWidth() * 19/59F,
                0));
        kirbyFallHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 21/59F),
                (int)(kirby.getObjectHeight() * 19/39F), 0, 0, kirby.getObjectWidth() * 19/59F,
                0));

        // Flip Fall Hit Boxes
        kirbyFlipFallHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 21/59F),
                (int)(kirby.getObjectHeight() * 19/39F), 0, 0, kirby.getObjectWidth() * 19/59F,
                0));
        kirbyFlipFallHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 20/59F),
                (int)(kirby.getObjectHeight() * 18/39F), 0, 0, kirby.getObjectWidth() * 19/59F,
                0));
        kirbyFlipFallHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 21/59F),
                (int)(kirby.getObjectHeight() * 19/39F), 0, 0, kirby.getObjectWidth() * 19/59F,
                0));
        kirbyFlipFallHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 20/59F),
                (int)(kirby.getObjectHeight() * 20/39F), 0, 0, kirby.getObjectWidth() * 19/59F,
                0));
        kirbyFlipFallHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 21/59F),
                (int)(kirby.getObjectHeight() * 17/39F), 0, 0, kirby.getObjectWidth() * 19/59F,
                0));
        kirbyFlipFallHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 22/59F),
                (int)(kirby.getObjectHeight() * 18/39F), 0, 0, kirby.getObjectWidth() * 18/59F,
                0));

        for(int i = 0; i < 10; i ++) {
            kirbyFlipFallHitBoxes.add(new HitBox(this, true, (int) (kirby.getObjectWidth() * 20 / 59F),
                    (int) (kirby.getObjectHeight() * 19 / 39F), 0, 0, kirby.getObjectWidth() * 19 / 59F,
                    0));
            kirbyFlipFallHitBoxes.add(new HitBox(this, true, (int) (kirby.getObjectWidth() * 21 / 59F),
                    (int) (kirby.getObjectHeight() * 19 / 39F), 0, 0, kirby.getObjectWidth() * 19 / 59F,
                    0));
        }

        // Float Hit Boxes
        kirbyFloatHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 24/59F),
                (int)(kirby.getObjectHeight() * 25/39F), 0, 0, kirby.getObjectWidth() * 17/59F,
                0));
        kirbyFloatHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 24/59F),
                (int)(kirby.getObjectHeight() * 24/39F), 0, 0, kirby.getObjectWidth() * 17/59F,
                0));
        kirbyFloatHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 24/59F),
                (int)(kirby.getObjectHeight() * 24/39F), 0, 0, kirby.getObjectWidth() * 17/59F,
                0));
        kirbyFloatHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 25/59F),
                (int)(kirby.getObjectHeight() * 24/39F), 0, 0, kirby.getObjectWidth() * 17/59F,
                0));
        kirbyFloatHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 26/59F),
                (int)(kirby.getObjectHeight() * 24/39F), 0, 0, kirby.getObjectWidth() * 16/59F,
                0));
        kirbyFloatHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 25/59F),
                (int)(kirby.getObjectHeight() * 24/39F), 0, 0, kirby.getObjectWidth() * 17/59F,
                0));
        kirbyFloatHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 24/59F),
                (int)(kirby.getObjectHeight() * 24/39F), 0, 0, kirby.getObjectWidth() * 17/59F,
                0));
        kirbyFloatHitBoxes.add(new HitBox(this, true, (int)(kirby.getObjectWidth() * 24/59F),
                (int)(kirby.getObjectHeight() * 24/39F), 0, 0, kirby.getObjectWidth() * 17/59F,
                0));

        // Jump Hit Box
        kirbyJumpHitBox.add(new HitBox(InGameActivity.this, true, (int)(kirby.getObjectWidth() * 22/59F),
                (int)(kirby.getObjectHeight() * 20/39F), 0, 0, kirby.getObjectWidth() * 18/59F,
                0));

        // Float Fall Hit Boxes
        kirbyFloatFallHitBoxes.add(new HitBox(InGameActivity.this, true, (int)(kirby.getObjectWidth() * 24/59F),
                (int)(kirby.getObjectHeight() * 23/39F), 0, 0, kirby.getObjectWidth() * 17/59F,
                0));
        kirbyFloatFallHitBoxes.add(new HitBox(InGameActivity.this, true, (int)(kirby.getObjectWidth() * 26/59F),
                (int)(kirby.getObjectHeight() * 23/39F), 0, 0, kirby.getObjectWidth() * 16/59F,
                0));
        kirbyFloatFallHitBoxes.add(new HitBox(InGameActivity.this, true, (int)(kirby.getObjectWidth() * 25/59F),
                (int)(kirby.getObjectHeight() * 23/39F), 0, 0, kirby.getObjectWidth() * 17/59F,
                0));

        // Start Float Hit Boxes
        kirbyStartFloatHitBoxes.add(new HitBox(InGameActivity.this, true, (int)(kirby.getObjectWidth() * 19/59F),
                (int)(kirby.getObjectHeight() * 20/39F), 0, 0, kirby.getObjectWidth() * 20/59F,
                0));
        kirbyStartFloatHitBoxes.add(new HitBox(InGameActivity.this, true, (int)(kirby.getObjectWidth() * 20/59F),
                (int)(kirby.getObjectHeight() * 22/39F), 0, 0, kirby.getObjectWidth() * 19/59F,
                0));
        kirbyStartFloatHitBoxes.add(new HitBox(InGameActivity.this, true, (int)(kirby.getObjectWidth() * 21/59F),
                (int)(kirby.getObjectHeight() * 24/39F), 0, 0, kirby.getObjectWidth() * 19/59F,
                0));
        kirbyStartFloatHitBoxes.add(new HitBox(InGameActivity.this, true, (int)(kirby.getObjectWidth() * 21/59F),
                (int)(kirby.getObjectHeight() * 31/39F), 0, 0, kirby.getObjectWidth() * 19/59F,
                0));
        kirbyStartFloatHitBoxes.add(new HitBox(InGameActivity.this, true, (int)(kirby.getObjectWidth() * 23/59F),
                (int)(kirby.getObjectHeight() * 23/39F), 0, 0, kirby.getObjectWidth() * 18/59F,
                0));
        
        // Stop Float Hit Boxes
        kirbyStopFloatHitBoxes.add(new HitBox(InGameActivity.this, true, (int)(kirby.getObjectWidth() * 23/59F),
                (int)(kirby.getObjectHeight() * 23/39F), 0, 0, kirby.getObjectWidth() * 18/59F,
                0));
        kirbyStopFloatHitBoxes.add(new HitBox(InGameActivity.this, true, (int)(kirby.getObjectWidth() * 21/59F),
                (int)(kirby.getObjectHeight() * 31/39F), 0, 0, kirby.getObjectWidth() * 19/59F,
                0));
        kirbyStopFloatHitBoxes.add(new HitBox(InGameActivity.this, true, (int)(kirby.getObjectWidth() * 21/59F),
                (int)(kirby.getObjectHeight() * 24/39F), 0, 0, kirby.getObjectWidth() * 19/59F,
                0));
        kirbyStopFloatHitBoxes.add(new HitBox(InGameActivity.this, true, (int)(kirby.getObjectWidth() * 20/59F),
                (int)(kirby.getObjectHeight() * 22/39F), 0, 0, kirby.getObjectWidth() * 19/59F,
                0));
        kirbyStopFloatHitBoxes.add(new HitBox(InGameActivity.this, true, (int)(kirby.getObjectWidth() * 19/59F),
                (int)(kirby.getObjectHeight() * 20/39F), 0, 0, kirby.getObjectWidth() * 20/59F,
                0));

        // Actions Set Up
        Runnable leftWalk = kirby.walk(kirby.getLrHandler(), R.drawable.kirbywalk,"left", walkSpeed, kirbyWalkHitBoxes,
                new GameObject.CollisionListener() {
                    @Override
                    public void onCollision(GameObject object1, GameObject object2) {
                        if (GameObject.getCollisionType(object1, object2).contains("right")) {
                            if(!specialCollisionHandler(object1, object2, GameObject.getCollisionType(object1, object2))){
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
                        if(!kirby.isJumpStarted() && kirby.isStopJump() && !kirby.isFallStarted() && kirby.isStopFall()) {
                            kirby.getUdHandler().removeCallbacksAndMessages(null);
                            kirby.stopFall();
                            kirby.getUdHandler().postDelayed(kirby.getAllActions().get("Fall"), 0);
                        }
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

        Runnable leftRun = kirby.walk(kirby.getLrHandler(), R.drawable.kirbyrun,"left", runSpeed, kirbyRunHitBoxes,
                new GameObject.CollisionListener() {
                    @Override
                    public void onCollision(GameObject object1, GameObject object2) {
                        if (GameObject.getCollisionType(object1, object2).contains("right")) {
                            if(!specialCollisionHandler(object1, object2, GameObject.getCollisionType(object1, object2))) {
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
                        if(!kirby.isJumpStarted() && kirby.isStopJump() && !kirby.isFallStarted() && kirby.isStopFall()) {
                            kirby.getUdHandler().removeCallbacksAndMessages(null);
                            kirby.stopFall();
                            kirby.getUdHandler().postDelayed(kirby.getAllActions().get("Fall"), 0);
                        }
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

        Runnable rightWalk = kirby.walk(kirby.getLrHandler(), R.drawable.kirbywalk,"right", walkSpeed, kirbyWalkHitBoxes,
                new GameObject.CollisionListener() {
                    @Override
                    public void onCollision(GameObject object1, GameObject object2) {
                        if (GameObject.getCollisionType(object1, object2).contains("left")) {
                            if(!specialCollisionHandler(object1, object2, GameObject.getCollisionType(object1, object2))) {
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
                        if(!kirby.isJumpStarted() && kirby.isStopJump() && !kirby.isFallStarted() && kirby.isStopFall()) {
                            kirby.getUdHandler().removeCallbacksAndMessages(null);
                            kirby.stopFall();
                            kirby.getUdHandler().postDelayed(kirby.getAllActions().get("Fall"), 0);
                        }
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

        Runnable rightRun = kirby.walk(kirby.getLrHandler(), R.drawable.kirbyrun,"right", runSpeed, kirbyRunHitBoxes,
                new GameObject.CollisionListener() {
                    @Override
                    public void onCollision(GameObject object1, GameObject object2) {
                        if (GameObject.getCollisionType(object1, object2).contains("left")) {
                            if(!specialCollisionHandler(object1, object2, GameObject.getCollisionType(object1, object2))) {
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
                        if(!kirby.isJumpStarted() && kirby.isStopJump() && !kirby.isFallStarted() && kirby.isStopFall()) {
                            kirby.getUdHandler().removeCallbacksAndMessages(null);
                            kirby.stopFall();
                            kirby.getUdHandler().postDelayed(kirby.getAllActions().get("Fall"), 0);
                        }
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

        Runnable jump = kirby.jump(kirby.getUdHandler(), R.drawable.kirby672, false, jumpHeight, kirbyJumpHitBox,
                new GameObject.CollisionListener() {
                    @Override
                    public void onCollision(GameObject object1, GameObject object2) {
                        if(GameObject.getCollisionType(object1, object2).contains("bottom")){
                            if(!specialCollisionHandler(object1, object2, GameObject.getCollisionType(object1, object2))) {
                                kirby.getUdHandler().removeCallbacksAndMessages(null);
                                kirby.stopJump();
                                kirby.setYPosition(object2.getHitBox().bottomRight().y -
                                        kirby.getHitBox().getHitHeight() - kirby.getHitBox().getYBottom());
                                kirby.stopFall();
                                kirby.getUdHandler().postDelayed(kirby.getAllActions().get("Fall"), 0);
                            }
                            Log.i("Collision", object1.getObjectName() + " collided with bottom of " + object2.getObjectName());
                        }
                    }
                },
                new Character.CharacterListener() {
                    @Override
                    public void onActionComplete() {
                        kirby.getUdHandler().removeCallbacksAndMessages(null);
                        kirby.getAHandler().removeCallbacksAndMessages(null);
                        kirby.stopFall();
                        kirby.getUdHandler().postDelayed(kirby.getAllActions().get("Fall"),0);
                    }
                },
                new Character.PositionListener() {
                    @Override
                    public void atPosition(float xPosition, float yPosition) {

                    }
                });

        Runnable fall = kirby.fall(kirby.getUdHandler(), R.drawable.kirbyfall, true, kirbyFallHitBoxes,
                new GameObject.CollisionListener() {
                    @Override
                    public void onCollision(GameObject object1, GameObject object2) {
                        if (GameObject.getCollisionType(object1, object2).contains("top")) {
                            if(!specialCollisionHandler(object1, object2, GameObject.getCollisionType(object1, object2))) {
                                isFloating = false;
                                startFloatFinished = false;
                                jumpCount = 0;
                                kirby.getUdHandler().removeCallbacksAndMessages(null);
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

        Runnable highJump = kirby.jump(kirby.getUdHandler(), R.drawable.kirby672, false, highJumpHeight, kirbyJumpHitBox,
                new GameObject.CollisionListener() {
                    @Override
                    public void onCollision(GameObject object1, GameObject object2) {
                        if(GameObject.getCollisionType(object1, object2).contains("bottom")){
                            if(!specialCollisionHandler(object1, object2, GameObject.getCollisionType(object1, object2))) {
                                kirby.getUdHandler().removeCallbacksAndMessages(null);
                                kirby.stopJump();
                                kirby.setYPosition(object2.getHitBox().bottomRight().y -
                                        kirby.getHitBox().getHitHeight() - kirby.getHitBox().getYBottom());
                                kirby.stopFall();
                                kirby.getUdHandler().postDelayed(kirby.getAllActions().get("Flip Fall"), 0);
                            }
                            Log.i("Collision", object1.getObjectName() + " collided with bottom of " + object2.getObjectName());
                        }
                    }
                },
                new Character.CharacterListener() {
                    @Override
                    public void onActionComplete() {
                        kirby.getUdHandler().removeCallbacksAndMessages(null);
                        kirby.getAHandler().removeCallbacksAndMessages(null);
                        kirby.stopFall();
                        kirby.getUdHandler().postDelayed(kirby.getAllActions().get("Flip Fall"),0);
                    }
                },
                new Character.PositionListener() {
                    @Override
                    public void atPosition(float xPosition, float yPosition) {

                    }
                });

        Runnable flipFall = kirby.fall(kirby.getUdHandler(), R.drawable.kirbyflipfall, true, kirbyFlipFallHitBoxes,
                new GameObject.CollisionListener() {
                    @Override
                    public void onCollision(GameObject object1, GameObject object2) {
                        if (GameObject.getCollisionType(object1, object2).contains("top")) {
                            if(!specialCollisionHandler(object1, object2, GameObject.getCollisionType(object1, object2))) {
                                kirby.getUdHandler().removeCallbacksAndMessages(null);
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

        Runnable startFloat = kirby.animatedAction(kirby.getUdHandler(), false, R.drawable.kirbystartfloat, kirbyStartFloatHitBoxes,
                new GameObject.CollisionListener() {
                    @Override
                    public void onCollision(GameObject object1, GameObject object2) {
                        specialCollisionHandler(object1, object2, GameObject.getCollisionType(object1, object2));
                    }
                },
                new Character.CharacterListener() {
                    @Override
                    public void onActionComplete() {
                        startFloatFinished = true;
                        kirby.getUdHandler().removeCallbacksAndMessages(null);
                        kirby.getAHandler().removeCallbacksAndMessages(null);
                        kirby.stopJump();
                        kirby.getUdHandler().postDelayed(kirby.getAllActions().get("Float Jump"), 0);
                    }
                });

        Runnable floatJump = kirby.jump(kirby.getUdHandler(), R.drawable.kirbyfloat, true, floatJumpHeight, kirbyFloatHitBoxes,
                new GameObject.CollisionListener() {
                    @Override
                    public void onCollision(GameObject object1, GameObject object2) {
                        if(GameObject.getCollisionType(object1, object2).contains("bottom")){
                            if(!specialCollisionHandler(object1, object2, GameObject.getCollisionType(object1, object2))) {
                                kirby.getUdHandler().removeCallbacksAndMessages(null);
                                kirby.stopJump();
                                kirby.setYPosition(object2.getHitBox().bottomRight().y -
                                        kirby.getHitBox().getHitHeight() - kirby.getHitBox().getYBottom());
                                kirby.stopFall();
                                kirby.getUdHandler().postDelayed(kirby.getAllActions().get("Float Fall"), 0);
                            }
                            Log.i("Collision", object1.getObjectName() + " collided with bottom of " + object2.getObjectName());
                        }
                    }
                },
                new Character.CharacterListener() {
                    @Override
                    public void onActionComplete() {
                        kirby.getUdHandler().removeCallbacksAndMessages(null);
                        kirby.getAHandler().removeCallbacksAndMessages(null);
                        kirby.stopFall();
                        kirby.getUdHandler().postDelayed(kirby.getAllActions().get("Float Fall"),0);
                    }
                },
                new Character.PositionListener() {
                    @Override
                    public void atPosition(float xPosition, float yPosition) {
                    }
                });

        Runnable floatFall = kirby.fall(kirby.getUdHandler(), R.drawable.kirbyfloatfall, true, GameObject.GRAVITY/3F, kirbyFloatFallHitBoxes,
                new GameObject.CollisionListener() {
                    @Override
                    public void onCollision(GameObject object1, GameObject object2) {
                        if (GameObject.getCollisionType(object1, object2).contains("top")) {
                            if(!specialCollisionHandler(object1, object2, GameObject.getCollisionType(object1, object2))) {
                                isFloating = false;
                                kirby.getUdHandler().removeCallbacksAndMessages(null);
                                kirby.stopFall();
                                kirby.stopJump();
                                kirby.setGrounded(false);

                                kirby.setYPosition(object2.getHitBox().topLeft().y - kirby.getHitBox().getYBottom());

                                kirby.getUdHandler().postDelayed(kirby.getAllActions().get("Stop Float"), 0);
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

        Runnable stopFloat = kirby.animatedAction(kirby.getUdHandler(), false, R.drawable.kirbystopfloat, kirbyStopFloatHitBoxes,
                new GameObject.CollisionListener() {
                    @Override
                    public void onCollision(GameObject object1, GameObject object2) {
                        specialCollisionHandler(object1, object2, GameObject.getCollisionType(object1, object2));
                    }
                },
                new Character.CharacterListener() {
                    @Override
                    public void onActionComplete() {
                        isFloating = false;
                        startFloatFinished = false;
                        kirby.getUdHandler().removeCallbacksAndMessages(null);
                        kirby.getAHandler().removeCallbacksAndMessages(null);
                        kirby.stopFall();
                        kirby.getUdHandler().postDelayed(kirby.getAllActions().get("Fall"), 0);
                    }
                });

        kirby.getAllActions().put("Left Walk", leftWalk);
        kirby.getAllActions().put("Left Run", leftRun);
        kirby.getAllActions().put("Right Walk", rightWalk);
        kirby.getAllActions().put("Right Run", rightRun);
        kirby.getAllActions().put("Jump", jump);
        kirby.getAllActions().put("Fall", fall);
        kirby.getAllActions().put("High Jump", highJump);
        kirby.getAllActions().put("Flip Fall", flipFall);
        kirby.getAllActions().put("Start Float", startFloat);
        kirby.getAllActions().put("Float Jump", floatJump);
        kirby.getAllActions().put("Float Fall", floatFall);
        kirby.getAllActions().put("Stop Float", stopFloat);



        // NPC Set Ups
        // Waddle Dee
        HitBox waddleDeeIdleHitBox = new HitBox(this, true, (int) (14 * 20/27F),(int)(12 * 18/23F),
                0, 0, 14 * 3 /27F,0);
        Character waddleDee = new Character(this, "Waddle Dee", 14, 12, 0, 0,
                waddleDeeIdleHitBox, true, R.drawable.waddledeeidle);
        waddleDee.setObjectResource(R.drawable.waddledeeidle);

        waddleDeeWalkHitBoxes = new ArrayList<HitBox>();
        waddleDeeRunHitBoxes = new ArrayList<HitBox>();
        waddleDeeFlipFallHitBoxes = new ArrayList<HitBox>();
        waddleDeeJumpHitBox = new ArrayList<HitBox>();

        // Walk Hit Boxes
        waddleDeeWalkHitBoxes.add(new HitBox(InGameActivity.this, true, (int)(waddleDee.getObjectWidth() * 20/27F),
                (int)(waddleDee.getObjectHeight() * 18/23F), 0, 0, waddleDee.getObjectWidth() * 3/27F,
                0));
        waddleDeeWalkHitBoxes.add(new HitBox(InGameActivity.this, true, (int)(waddleDee.getObjectWidth() * 21/27F),
                (int)(waddleDee.getObjectHeight() * 19/23F), 0, 0, waddleDee.getObjectWidth() * 3/27F,
                0));
        waddleDeeWalkHitBoxes.add(new HitBox(InGameActivity.this, true, (int)(waddleDee.getObjectWidth() * 20/27F),
                (int)(waddleDee.getObjectHeight() * 18/23F), 0, 0, waddleDee.getObjectWidth() * 3/27F,
                0));
        waddleDeeWalkHitBoxes.add(new HitBox(InGameActivity.this, true, (int)(waddleDee.getObjectWidth() * 19/27F),
                (int)(waddleDee.getObjectHeight() * 16/23F), 0, 0, waddleDee.getObjectWidth() * 4/27F,
                0));
        waddleDeeWalkHitBoxes.add(new HitBox(InGameActivity.this, true, (int)(waddleDee.getObjectWidth() * 17/27F),
                (int)(waddleDee.getObjectHeight() * 17/23F), 0, 0, waddleDee.getObjectWidth() * 5/27F,
                0));
        waddleDeeWalkHitBoxes.add(new HitBox(InGameActivity.this, true, (int)(waddleDee.getObjectWidth() * 17/27F),
                (int)(waddleDee.getObjectHeight() * 18/23F), 0, 0, waddleDee.getObjectWidth() * 5/27F,
                0));
        waddleDeeWalkHitBoxes.add(new HitBox(InGameActivity.this, true, (int)(waddleDee.getObjectWidth() * 21/27F),
                (int)(waddleDee.getObjectHeight() * 19/23F), 0, 0, waddleDee.getObjectWidth() * 3/27F,
                0));
        waddleDeeWalkHitBoxes.add(new HitBox(InGameActivity.this, true, (int)(waddleDee.getObjectWidth() * 20/27F),
                (int)(waddleDee.getObjectHeight() * 18/23F), 0, 0, waddleDee.getObjectWidth() * 3/27F,
                0));
        waddleDeeWalkHitBoxes.add(new HitBox(InGameActivity.this, true, (int)(waddleDee.getObjectWidth() * 19/27F),
                (int)(waddleDee.getObjectHeight() * 16/23F), 0, 0, waddleDee.getObjectWidth() * 4/27F,
                0));
        waddleDeeWalkHitBoxes.add(new HitBox(InGameActivity.this, true, (int)(waddleDee.getObjectWidth() * 19/27F),
                (int)(waddleDee.getObjectHeight() * 17/23F), 0, 0, waddleDee.getObjectWidth() * 4/27F,
                0));

        // Run Hit Boxes
        waddleDeeRunHitBoxes.add(new HitBox(InGameActivity.this, true, (int)(waddleDee.getObjectWidth() * 24/27F),
                (int)(waddleDee.getObjectHeight() * 19/23F), 0, 0, waddleDee.getObjectWidth() * 1/27F,
                0));
        waddleDeeRunHitBoxes.add(new HitBox(InGameActivity.this, true, (int)(waddleDee.getObjectWidth() * 19/27F),
                (int)(waddleDee.getObjectHeight() * 19/23F), 0, 0, waddleDee.getObjectWidth() * 4/27F,
                0));
        waddleDeeRunHitBoxes.add(new HitBox(InGameActivity.this, true, (int)(waddleDee.getObjectWidth() * 17/27F),
                (int)(waddleDee.getObjectHeight() * 18/23F), 0, 0, waddleDee.getObjectWidth() * 5/27F,
                0));
        waddleDeeRunHitBoxes.add(new HitBox(InGameActivity.this, true, (int)(waddleDee.getObjectWidth() * 18/27F),
                (int)(waddleDee.getObjectHeight() * 19/23F), 0, 0, waddleDee.getObjectWidth() * 4/27F,
                0));
        waddleDeeRunHitBoxes.add(new HitBox(InGameActivity.this, true, (int)(waddleDee.getObjectWidth() * 19/27F),
                (int)(waddleDee.getObjectHeight() * 19/23F), 0, 0, waddleDee.getObjectWidth() * 4/27F,
                0));
        waddleDeeRunHitBoxes.add(new HitBox(InGameActivity.this, true, (int)(waddleDee.getObjectWidth() * 18/27F),
                (int)(waddleDee.getObjectHeight() * 19/23F), 0, 0, waddleDee.getObjectWidth() * 4/27F,
                0));
        waddleDeeRunHitBoxes.add(new HitBox(InGameActivity.this, true, (int)(waddleDee.getObjectWidth() * 17/27F),
                (int)(waddleDee.getObjectHeight() * 18/23F), 0, 0, waddleDee.getObjectWidth() * 5/27F,
                0));
        waddleDeeRunHitBoxes.add(new HitBox(InGameActivity.this, true, (int)(waddleDee.getObjectWidth() * 19/27F),
                (int)(waddleDee.getObjectHeight() * 19/23F), 0, 0, waddleDee.getObjectWidth() * 4/27F,
                0));

        // Flip Fall Hit Boxes
        waddleDeeFlipFallHitBoxes.add(new HitBox(InGameActivity.this, true, (int)(waddleDee.getObjectWidth() * 21/27F),
                (int)(waddleDee.getObjectHeight() * 19/23F), 0, 0, waddleDee.getObjectWidth() * 3/27F,
                0));
        waddleDeeFlipFallHitBoxes.add(new HitBox(InGameActivity.this, true, (int)(waddleDee.getObjectWidth() * 20/27F),
                (int)(waddleDee.getObjectHeight() * 18/23F), 0, 0, waddleDee.getObjectWidth() * 3/27F,
                0));
        waddleDeeFlipFallHitBoxes.add(new HitBox(InGameActivity.this, true, (int)(waddleDee.getObjectWidth() * 21/27F),
                (int)(waddleDee.getObjectHeight() * 19/23F), 0, 0, waddleDee.getObjectWidth() * 3/27F,
                0));
        waddleDeeFlipFallHitBoxes.add(new HitBox(InGameActivity.this, true, (int)(waddleDee.getObjectWidth() * 20/27F),
                (int)(waddleDee.getObjectHeight() * 20/23F), 0, 0, waddleDee.getObjectWidth() * 3/27F,
                0));
        waddleDeeFlipFallHitBoxes.add(new HitBox(InGameActivity.this, true, (int)(waddleDee.getObjectWidth() * 21/27F),
                (int)(waddleDee.getObjectHeight() * 17/23F), 0, 0, waddleDee.getObjectWidth() * 3/27F,
                0));
        waddleDeeFlipFallHitBoxes.add(new HitBox(InGameActivity.this, true, (int)(waddleDee.getObjectWidth() * 22/27F),
                (int)(waddleDee.getObjectHeight() * 18/23F), 0, 0, waddleDee.getObjectWidth() * 2/27F,
                0));

        for(int i = 0; i < 10; i ++) {
            waddleDeeFlipFallHitBoxes.add(new HitBox(InGameActivity.this, true, (int)(waddleDee.getObjectWidth() * 21/27F),
                    (int)(waddleDee.getObjectHeight() * 19/23F), 0, 0, waddleDee.getObjectWidth() * 3/27F,
                    0));
            waddleDeeFlipFallHitBoxes.add(new HitBox(InGameActivity.this, true, (int)(waddleDee.getObjectWidth() * 20/27F),
                    (int)(waddleDee.getObjectHeight() * 19/23F), 0, 0, waddleDee.getObjectWidth() * 3/27F,
                    0));
        }

        // Jump Hit Box
        waddleDeeJumpHitBox.add(new HitBox(InGameActivity.this, true, (int)(waddleDee.getObjectWidth() * 20/27F),
                (int)(waddleDee.getObjectHeight() * 20/23F), 0, 0, waddleDee.getObjectWidth() * 3/27F,
                0));

        // Actions Set Up
        float walkSpeed = 1/3F;
        float runSpeed = 1/2F;
        

        Runnable wFall = waddleDee.fall(waddleDee.getUdHandler(), R.drawable.waddledeeflipfall, true, waddleDeeFlipFallHitBoxes,
                new GameObject.CollisionListener() {
                    @Override
                    public void onCollision(GameObject object1, GameObject object2) {
                        if (GameObject.getCollisionType(object1, object2).contains("top")) {
                            if(!specialCollisionHandler(object1, object2, GameObject.getCollisionType(object1, object2))) {
                                waddleDee.getUdHandler().removeCallbacksAndMessages(null);
                                waddleDee.stopFall();
                                waddleDee.setGrounded(true);
                                waddleDee.setObjectResource(waddleDee.getIdleResource());

                                waddleDee.setYPosition(object2.getHitBox().topLeft().y -
                                        waddleDee.getHitBox().getYBottom());

                                waddleDee.setHitBox(waddleDee.getIdleHitBox());
                                waddleDee.showHitBox();
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

        Runnable wJump = waddleDee.jump(waddleDee.getUdHandler(), R.drawable.waddledee41, false, 15, waddleDeeJumpHitBox,
                new GameObject.CollisionListener() {
                    @Override
                    public void onCollision(GameObject object1, GameObject object2) {
                        if(GameObject.getCollisionType(object1, object2).contains("bottom")){
                            if(!specialCollisionHandler(object1, object2, GameObject.getCollisionType(object1, object2))) {
                                waddleDee.getUdHandler().removeCallbacksAndMessages(null);
                                waddleDee.stopJump();
                                waddleDee.setYPosition(object2.getHitBox().bottomRight().y -
                                        waddleDee.getHitBox().getHitHeight() -
                                        waddleDee.getHitBox().getYBottom());
                                waddleDee.stopFall();
                                waddleDee.getUdHandler().postDelayed(waddleDee.getAllActions().get("Fall"), 0);
                            }
                            Log.i("Collision", object1.getObjectName() + " collided with bottom of " + object2.getObjectName());
                        }
                    }
                },
                new Character.CharacterListener() {
                    @Override
                    public void onActionComplete() {
                        waddleDee.getUdHandler().removeCallbacksAndMessages(null);
                        waddleDee.getAHandler().removeCallbacksAndMessages(null);
                        waddleDee.stopFall();
                        waddleDee.getUdHandler().postDelayed(waddleDee.getAllActions().get("Fall"),0);
                    }
                },
                new Character.PositionListener() {
                    @Override
                    public void atPosition(float xPosition, float yPosition) {

                    }
                });

        Runnable wLeftWalk = waddleDee.walk(waddleDee.getLrHandler(), R.drawable.waddledeewalk, "left", walkSpeed, waddleDeeWalkHitBoxes,
                new GameObject.CollisionListener() {
                    @Override
                    public void onCollision(GameObject object1, GameObject object2) {
                        if (GameObject.getCollisionType(object1, object2).contains("right")) {
                            if(!specialCollisionHandler(object1, object2, GameObject.getCollisionType(object1, object2))){
                                waddleDee.getLrHandler().removeCallbacksAndMessages(null);
                                waddleDee.setXPosition(waddleDee.getXPosition() + walkSpeed);
                            }

                            Log.i("Collision", object1.getObjectName() + " collided with the right of " + object2.getObjectName());
                        }
                    }
                },
                new Character.NotGroundedListener() {
                    @Override
                    public void notGrounded() {
                        if(!waddleDee.isJumpStarted() && waddleDee.isStopJump() && !waddleDee.isFallStarted() && waddleDee.isStopFall()) {
                            waddleDee.getUdHandler().removeCallbacksAndMessages(null);
                            waddleDee.stopFall();
                            waddleDee.getUdHandler().postDelayed(waddleDee.getAllActions().get("Fall"), 0);
                        }
                    }
                },
                new Character.PositionListener() {
                    @Override
                    public void atPosition(float xPosition, float yPosition) {

                    }
                });

        Runnable wRightWalk = waddleDee.walk(waddleDee.getLrHandler(), R.drawable.waddledeewalk, "right", walkSpeed, waddleDeeWalkHitBoxes,
                new GameObject.CollisionListener() {
                    @Override
                    public void onCollision(GameObject object1, GameObject object2) {
                        if (GameObject.getCollisionType(object1, object2).contains("left")) {
                            if(!specialCollisionHandler(object1, object2, GameObject.getCollisionType(object1, object2))) {
                                waddleDee.setXPosition(waddleDee.getXPosition() - walkSpeed);
                            }

                            Log.i("Collision", object1.getObjectName() + " collided with the left of " + object2.getObjectName());
                        }
                    }
                },
                new Character.NotGroundedListener() {
                    @Override
                    public void notGrounded() {
                        if(!waddleDee.isJumpStarted() && waddleDee.isStopJump() && !waddleDee.isFallStarted() && waddleDee.isStopFall()) {
                            waddleDee.getUdHandler().removeCallbacksAndMessages(null);
                            waddleDee.stopFall();
                            waddleDee.getUdHandler().postDelayed(waddleDee.getAllActions().get("Fall"), 0);
                        }
                    }
                },
                new Character.PositionListener() {
                    @Override
                    public void atPosition(float xPosition, float yPosition) {

                    }
                });

        Runnable wLeftRun = waddleDee.walk(waddleDee.getLrHandler(), R.drawable.waddledeewalk, "left", runSpeed, waddleDeeRunHitBoxes,
                new GameObject.CollisionListener() {
                    @Override
                    public void onCollision(GameObject object1, GameObject object2) {
                        if (GameObject.getCollisionType(object1, object2).contains("right")) {
                            if(!specialCollisionHandler(object1, object2, GameObject.getCollisionType(object1, object2))){
                                waddleDee.setXPosition(waddleDee.getXPosition() + runSpeed);
                            }

                            Log.i("Collision", object1.getObjectName() + " collided with the right of " + object2.getObjectName());
                        }
                    }
                },
                new Character.NotGroundedListener() {
                    @Override
                    public void notGrounded() {
                        if(!waddleDee.isJumpStarted() && waddleDee.isStopJump() && !waddleDee.isFallStarted() && waddleDee.isStopFall()) {
                            waddleDee.getUdHandler().removeCallbacksAndMessages(null);
                            waddleDee.stopFall();
                            waddleDee.getUdHandler().postDelayed(waddleDee.getAllActions().get("Fall"), 0);
                        }
                    }
                },
                new Character.PositionListener() {
                    @Override
                    public void atPosition(float xPosition, float yPosition) {

                    }
                });

        Runnable wRightRun = waddleDee.walk(waddleDee.getLrHandler(), R.drawable.waddledeewalk, "right", runSpeed, waddleDeeRunHitBoxes,
                new GameObject.CollisionListener() {
                    @Override
                    public void onCollision(GameObject object1, GameObject object2) {
                        if (GameObject.getCollisionType(object1, object2).contains("left")) {
                            if(!specialCollisionHandler(object1, object2, GameObject.getCollisionType(object1, object2))) {
                                waddleDee.setXPosition(waddleDee.getXPosition() - runSpeed);
                            }

                            Log.i("Collision", object1.getObjectName() + " collided with the left of " + object2.getObjectName());
                        }
                    }
                },
                new Character.NotGroundedListener() {
                    @Override
                    public void notGrounded() {
                        if(!waddleDee.isJumpStarted() && waddleDee.isStopJump() && !waddleDee.isFallStarted() && waddleDee.isStopFall()) {
                            waddleDee.getUdHandler().removeCallbacksAndMessages(null);
                            waddleDee.stopFall();
                            waddleDee.getUdHandler().postDelayed(waddleDee.getAllActions().get("Fall"), 0);
                        }
                    }
                },
                new Character.PositionListener() {
                    @Override
                    public void atPosition(float xPosition, float yPosition) {

                    }
                });

        waddleDee.getAllActions().put("Left Walk", wLeftWalk);
        waddleDee.getAllActions().put("Left Run", wLeftRun);
        waddleDee.getAllActions().put("Right Walk", wRightWalk);
        waddleDee.getAllActions().put("Right Run", wRightRun);
        waddleDee.getAllActions().put("Jump", wJump);
        waddleDee.getAllActions().put("Fall", wFall);

        // Add all NPCs to HashMap
        allNPCs.put("Waddle Dee 1", waddleDee);
    }

    // Sets up character controls/interactions
    // Majority of in-game logic resides here
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
                        kirby.getLrHandler().removeCallbacksAndMessages(null);
                        cHandler.removeCallbacksAndMessages(null);
                        kirby.getAHandler().removeCallbacksAndMessages(null);
                        
                        if(isDoubleClick){
                            cHandler.postDelayed(leftRunCamera,0);
                            kirby.getLrHandler().postDelayed(kirby.getAllActions().get("Left Run"),0);
                        }
                        else{
                            cHandler.postDelayed(leftWalkCamera,0);
                            kirby.getLrHandler().postDelayed(kirby.getAllActions().get("Left Walk"),0);
                        }
                        
                        isDown = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        view.performClick();
                        if (!isDown) return true;

                        kirby.getLrHandler().removeCallbacks(kirby.getAllActions().get("Left Walk"));
                        cHandler.removeCallbacks(leftWalkCamera);

                        kirby.getLrHandler().removeCallbacks(kirby.getAllActions().get("Left Run"));
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
                        kirby.getLrHandler().removeCallbacksAndMessages(null);
                        cHandler.removeCallbacksAndMessages(null);
                        kirby.getAHandler().removeCallbacksAndMessages(null);

                        if(isDoubleClick){
                            cHandler.postDelayed(rightRunCamera,0);
                            kirby.getLrHandler().postDelayed(kirby.getAllActions().get("Right Run"),0);
                        }
                        else{
                            cHandler.postDelayed(rightWalkCamera,0);
                            kirby.getLrHandler().postDelayed(kirby.getAllActions().get("Right Walk"),0);
                        }

                        isDown = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        view.performClick();
                        if (!isDown) return true;

                        kirby.getLrHandler().removeCallbacks(kirby.getAllActions().get("Right Walk"));
                        cHandler.removeCallbacks(rightWalkCamera);

                        kirby.getLrHandler().removeCallbacks(kirby.getAllActions().get("Right Run"));
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
                                    kirby.getUdHandler().removeCallbacksAndMessages(null);
                                    kirby.getAHandler().removeCallbacksAndMessages(null);
                                    kirby.stopJump();
                                    kirby.getUdHandler().postDelayed(kirby.getAllActions().get("High Jump"), 0);
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
                                kirby.getUdHandler().removeCallbacksAndMessages(null);
                                kirby.getAHandler().removeCallbacksAndMessages(null);
                                kirby.stopJump();
                                kirby.getUdHandler().postDelayed(kirby.getAllActions().get("Jump"),0);
                            }
                            else {
                                if(!isFloating && jumpCount < 6) {
                                    jumpCount++;
                                    shortJump = true;
                                    kirby.getUdHandler().removeCallbacksAndMessages(null);
                                    kirby.getAHandler().removeCallbacksAndMessages(null);
                                    kirby.stopJump();
                                    kirby.stopFall();
                                    kirby.getUdHandler().postDelayed(kirby.getAllActions().get("Start Float"), 0);
                                    isFloating = true;
                                }
                                else if(startFloatFinished && jumpCount < 6){
                                    jumpCount++;
                                    shortJump = true;
                                    kirby.getUdHandler().removeCallbacksAndMessages(null);
                                    kirby.getAHandler().removeCallbacksAndMessages(null);
                                    kirby.stopJump();
                                    kirby.getUdHandler().postDelayed(kirby.getAllActions().get("Float Jump"),0);
                                }
                            }
                        }

                        isDown = false;

                        break;
                }
                return false;
            }

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
                                kirby.getUdHandler().removeCallbacksAndMessages(null);
                                kirby.getAHandler().removeCallbacksAndMessages(null);
                                kirby.stopJump();
                                kirby.stopFall();
                                kirby.getUdHandler().postDelayed(kirby.getAllActions().get("Stop Float"), 0);
                            }
                        }

                        isDown = false;

                        break;
                }
                return false;
            }

        });

    }

    private boolean specialCollisionHandler(GameObject object1, GameObject object2, String collisionType){

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
       // else if(object1.isCharacter() && object2.getObjectName().toLowerCase().equals("boundary")){
            //environmentSetUp("test2");
        //}
        else if(object1.isCharacter() && object2.isCharacter() && collisionType.contains("top")){
            ((Character) object1).getUdHandler().removeCallbacksAndMessages(null);
            ((Character) object1).stopFall();
            ((Character) object1).setObjectResource(((Character) object1).getIdleResource());
            ((Character) object1).setHitBox(((Character) object1).getIdleHitBox());
            ((Character) object1).showHitBox();

            ((Character) object1).stopJump();
            if(object1.getObjectName().toLowerCase().equals("kirby")) {
                ((Character) object1).getUdHandler().postDelayed(((Character) object1).getAllActions().get("High Jump"), 0);
            }
            else{
                ((Character) object1).getUdHandler().postDelayed(((Character) object1).getAllActions().get("Jump"), 0);
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
    public void viewInfoDebug(View view){

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