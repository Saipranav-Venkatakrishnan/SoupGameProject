package com.example.soupgameproject;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
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
    private ArrayList<HitBox> walkHitBoxes, runHitBoxes, fallHitBoxes, flipFallHitBoxes, floatHitBoxes, jumpHitBox;


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
                R.drawable.testground, 0, gameCamera.getBottomYPosition(), true, new HitBox(this,true,
                (int)(TitleActivity.WIDTH/TitleActivity.DENSITY), 5, 0, gameCamera.getBottomYPosition(),0,0)));
        kirby.setYPosition(gameCamera.getBottomYPosition() + 9);
        testEnvironmentGameObjects.add(kirby);

        // Call to show the chosen environment.
        environmentSetUp("test");

        controllerSetUp(1/3F,1/2F, 1,20,1);
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
                50, 0, 30 * 19/59F,0);

        kirby = new Character(this, "Kirby", 30, 20, 50, 0,
                idleHitBox, true, R.drawable.kirbyidle);
        kirby.setObjectResource(R.drawable.kirbyidle);

        walkHitBoxes = new ArrayList<HitBox>();
        runHitBoxes = new ArrayList<HitBox>();
        fallHitBoxes = new ArrayList<HitBox>();
        flipFallHitBoxes = new ArrayList<HitBox>();
        floatHitBoxes = new ArrayList<HitBox>();
        jumpHitBox = new ArrayList<HitBox>();

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

    }

    // Sets up character controls/interactions
    // Majority of game logic resides here
    @SuppressLint("ClickableViewAccessibility")
    private void controllerSetUp(float walkSpeed, float runSpeed, float jumpHeight, float highJumpHeight, float floatJumpHeight){
        lrHandler = new Handler();
        udHandler = new Handler();
        aHandler = new Handler();
        cHandler = new Handler();

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

            Runnable leftWalk = kirby.walk(lrHandler, R.drawable.kirbywalk,"left", walkSpeed, walkHitBoxes, new GameObject.CollisionListener() {
                        @Override
                        public void onCollision(GameObject object1, GameObject object2) {
                            if (GameObject.getCollisionType(object1, object2).contains("right")) {
                                if(gameCamera.getRightXPosition() < (TitleActivity.WIDTH/TitleActivity.DENSITY)) {
                                    gameCamera.setXPosition(gameCamera.getXPosition() + walkSpeed);
                                }
                                kirby.setXPosition(kirby.getXPosition() + walkSpeed);
                                Log.i("Collision", object1.getObjectName() + " collided with the right of " + object2.getObjectName());
                            }
                        }
                    },
                    new Character.NotGroundedListener() {
                        @Override
                        public void notGrounded() {
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

            Runnable leftRun = kirby.walk(lrHandler, R.drawable.kirbyrun,"left", runSpeed, runHitBoxes, new GameObject.CollisionListener() {
                        @Override
                        public void onCollision(GameObject object1, GameObject object2) {
                            if (GameObject.getCollisionType(object1, object2).contains("right")) {
                                if(gameCamera.getRightXPosition() < (TitleActivity.WIDTH/TitleActivity.DENSITY)) {
                                    gameCamera.setXPosition(gameCamera.getXPosition() + runSpeed);
                                }
                                kirby.setXPosition(kirby.getXPosition() + runSpeed);
                                Log.i("Collision", object1.getObjectName() + " collided with the right of " + object2.getObjectName());
                            }
                        }
                    },
                    new Character.NotGroundedListener() {
                        @Override
                        public void notGrounded() {
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
                        udHandler.removeCallbacksAndMessages(null);
                        kirby.stopFall();
                        kirby.setGrounded(true);
                        kirby.setObjectResource(kirby.getIdleResource());

                        kirby.setYPosition(object2.getHitBox().topLeft().y - kirby.getHitBox().getYBottom());

                        kirby.setHitBox(kirby.getIdleHitBox());
                        kirby.showHitBox();

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

            Runnable rightWalk = kirby.walk(lrHandler, R.drawable.kirbywalk,"right", walkSpeed, walkHitBoxes, new GameObject.CollisionListener() {
                        @Override
                        public void onCollision(GameObject object1, GameObject object2) {
                            if (GameObject.getCollisionType(object1, object2).contains("right")) {
                                if(gameCamera.getRightXPosition() < (TitleActivity.WIDTH/TitleActivity.DENSITY)) {
                                    gameCamera.setXPosition(gameCamera.getXPosition() + walkSpeed);
                                }
                                kirby.setXPosition(kirby.getXPosition() + walkSpeed);
                                Log.i("Collision", object1.getObjectName() + " collided with the right of " + object2.getObjectName());
                            }
                        }
                    },
                    new Character.NotGroundedListener() {
                        @Override
                        public void notGrounded() {
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

            Runnable rightRun = kirby.walk(lrHandler, R.drawable.kirbyrun,"right", runSpeed, runHitBoxes, new GameObject.CollisionListener() {
                        @Override
                        public void onCollision(GameObject object1, GameObject object2) {
                            if (GameObject.getCollisionType(object1, object2).contains("right")) {
                                if(gameCamera.getRightXPosition() < (TitleActivity.WIDTH/TitleActivity.DENSITY)) {
                                    gameCamera.setXPosition(gameCamera.getXPosition() + runSpeed);
                                }
                                kirby.setXPosition(kirby.getXPosition() + runSpeed);
                                Log.i("Collision", object1.getObjectName() + " collided with the right of " + object2.getObjectName());
                            }
                        }
                    },
                    new Character.NotGroundedListener() {
                        @Override
                        public void notGrounded() {
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
                                udHandler.removeCallbacksAndMessages(null);
                                kirby.stopFall();
                                kirby.setGrounded(true);
                                kirby.setObjectResource(kirby.getIdleResource());

                                kirby.setYPosition(object2.getHitBox().topLeft().y - kirby.getHitBox().getYBottom());

                                kirby.setHitBox(kirby.getIdleHitBox());
                                kirby.showHitBox();

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

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (isDown) return true;
                        udHandler.removeCallbacksAndMessages(null);
                        aHandler.removeCallbacksAndMessages(null);


                        isDown = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        view.performClick();
                        if (!isDown) return true;

                        isDown = false;

                        break;
                }
                return false;
            }
        });

        jumpButton.setOnLongClickListener(new View.OnLongClickListener() {

            private boolean isLongClick = false;

            @Override
            public boolean onLongClick(View view) {
                if(isLongClick) {
                    isLongClick = false;
                    return true;
                }

                udHandler.removeCallbacksAndMessages(null);
                aHandler.removeCallbacksAndMessages(null);
                udHandler.postDelayed(highJump,0);

                isLongClick = true;
                return false;
            }

            Runnable highJump = kirby.jump(udHandler, R.drawable.kirby672, false, highJumpHeight, jumpHitBox,
                    new GameObject.CollisionListener() {
                @Override
                public void onCollision(GameObject object1, GameObject object2) {
                    if(GameObject.getCollisionType(object1, object2).contains("bottom")){
                        udHandler.removeCallbacksAndMessages(null);
                        kirby.stopJump();
                        kirby.setYPosition(object2.getHitBox().bottomRight().y -
                                kirby.getHitBox().getHitHeight() - kirby.getHitBox().getYBottom());
                        udHandler.postDelayed(flipFall,0);
                        Log.i("Collision", object1.getObjectName() + " collided with bottom of " + object2.getObjectName());
                    }
                }
            },
                    new Character.CharacterListener() {
                @Override
                public void onActionComplete() {
                    udHandler.removeCallbacksAndMessages(null);
                    aHandler.removeCallbacksAndMessages(null);
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
                                udHandler.removeCallbacksAndMessages(null);
                                kirby.stopFall();
                                kirby.setGrounded(true);
                                kirby.setObjectResource(kirby.getIdleResource());

                                kirby.setYPosition(object2.getHitBox().topLeft().y - kirby.getHitBox().getYBottom());

                                kirby.setHitBox(kirby.getIdleHitBox());
                                kirby.showHitBox();

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
    }

    // This method helps to find the scale for the game camera to zoom to
    private float fitZoom(float backgroundWidth, float backgroundHeight){
        return ((float) TitleActivity.HEIGHT/TitleActivity.WIDTH) * (backgroundWidth/backgroundHeight);
    }


    // Debugging method
    public void viewInfoDebug(View view){
        GameObject.displayHitBoxes = true;
        for(GameObject object : testEnvironmentGameObjects){
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