package com.example.soupgameproject;

import static com.example.soupgameproject.SettingsPage.SHARED_PREF;
import static com.example.soupgameproject.SettingsPage.isOn;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams;
import androidx.constraintlayout.widget.ConstraintSet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class InGameActivity extends AppCompatActivity {

    //MusicPlayer Activity
    public static MediaPlayer mediaPlayer;
    public static MediaPlayer walkEffectPlayer;
    public static MediaPlayer jumpEffectPlayer;
    public static boolean shouldPlay;

    // Final strings for saving
    public static final String TIME_OF_DAY = "timeOfDay";
    public static final String LIGHTING_BA = "lightingBA";
    public static final String LIGHTING_BR = "lightingBR";
    public static final String LIGHTING_BG = "lightingBG";
    public static final String LIGHTING_BB = "lightingBB";
    public static final String LIGHTING_OA = "lightingOA";
    public static final String LIGHTING_OR = "lightingOR";
    public static final String LIGHTING_OG = "lightingOG";
    public static final String LIGHTING_OB = "lightingOB";
    public static final String NEGATE_DAY_NIGHT = "negateDayNight";

    public static final String IS_FLOATING = "isFloating";
    public static final String START_FLOAT_FINISHED = "startFloatFinished";
    public static final String JUMP_COUNT = "jumpCount";
    public static final String IS_GROUNDED = "isGrounded";

    public static final String GAME_CAMERA_XPOSITION = "gameCameraXPosition";
    public static final String GAME_CAMERA_YPOSITION = "gameCameraYPosition";
    public static final String GAME_CAMERA_FIXED = "gameCameraFixed";

    public static final String KIRBY_XPOSITION = "kirbyXPosition";
    public static final String KIRBY_YPOSITION = "kirbyYPosition";

    public static final String ENVIRONMENT = "environment";
    public static final String ITEMS_ARE_SET = "itemsAreSet";
    public static final String FOREST_CLOUD_COORDINATES = "forestCloudCoordinates";

    public static final String INV_DRAWABLES = "invDrawables";
    public static final String ITEM_NAMES = "itemNames";

    public static final String ALL_FOREST_CURRENT_ITEM_NAMES = "allForestCurrentItemNames";
    public static final String ALL_FOREST_CURRENT_ITEM_LOCATIONS = "allForestCurrentItemLocations";
    
    public static final String ALL_SWAMP_CURRENT_ITEM_NAMES = "allSwampCurrentItemNames";
    public static final String ALL_SWAMP_CURRENT_ITEM_LOCATIONS = "allSwampCurrentItemLocations";

    public static final String SOUP_INGREDIENTS = "soupIngredients";
    public static final String SOUP_RANKS = "soupRanks";

    public static final String SPECIALDEE_XPOSITION = "specialDeeXPosition";
    public static final String SPECIALDEE_YPOSITION = "specialDeeYPosition";
    public static final String IS_SPECIALDEE_PRESENT = "isSpecialDeePresent";
    public static final String SPECIALDEE_DIRECTION = "specialDeeDirection";
    public static final String FOREST_HINT_NUMBER = "forestHintNumber";

    public static final String SOUP_NUM = "soupNum";


    public static float kirbyPreviousXPos;
    public static float kirbyPreviousYPos;

    private final String tutorialText = "Welcome to Kirby's Soup Adventure! Here's how to play. WALK by holding down the left or right button. " +
            "Double click and hold the left or right button to RUN in that direction. Click the jump button to do a SHORT JUMP. Hold the jump button while" +
            " on the ground to do a HIGH JUMP! While in the air, you can FLOAT JUMP in the air up to 6 times. The grey circle button is the action button. " +
            "This button will change depending on the action that can be performed in a given situation. For example, while floating in the air, a down button " +
            "will appear. Click it to exhale and fall faster. At the top of the screen, from left to right, you can open the settings, inventory, and catalog. " +
            "View settings to adjust audio and control placement. View your inventory to see what ingredients you have collected and select any ingredients you " +
            "want to remove, and then click remove, to get rid of ingredients. When the inventory bag turns red, this means that you have collected the max " +
            "amount of ingredients. Go make a soup or remove some ingredients to collect more. The catalog is where you can view all of the unique soups you have made. " +
            "The goal of the game is to collect ingredients and discover all of the different kinds of soups you can make. Soups are ranked from 1 star to 3 stars. " +
            "Experiment with different combinations of ingredients to discover the secrets to soup making! And that is all I will tell you! Go out there, " +
            "explore the land, discover new soups, uncover a secret recipe, and most importantly, HAVE FUN!";


    // General Screen Size Variables in DP
    private float tWidth = TitleActivity.WIDTH/TitleActivity.DENSITY;
    private float tHeight = TitleActivity.HEIGHT/TitleActivity.DENSITY;
    
    // Debugging variables
    private float centerX = (TitleActivity.WIDTH/2F)/ TitleActivity.DENSITY;
    private float centerY = (TitleActivity.HEIGHT/2F)/ TitleActivity.DENSITY;
    private boolean zoomed;

    // Game layout set up variables

    // Layouts
    private FrameLayout scalingFrameLayout;
    private ConstraintLayout gameContainerLayout, backgroundLayout, collisionLayout, foregroundLayout, userInterfaceLayout;

    // GameLayouts 
    @SuppressLint("StaticFieldLeak")
    public static GameLayout backgroundGameLayout;
    public static GameLayout collisionGameLayout;
    private GameLayout foregroundGameLayout;

    // NPC Handler
    private Handler npcHandler;

    // Deal with item animations
    private Handler itemHandler;

    // Deal with environment animations
    private Handler eHandler;

    // Deal with other runnables unrelated to animations (Day night cycle)
    private Handler rHandler;

    // Game Camera variables 
    private Camera gameCamera;
    private float gameCameraXPosition;
    private float gameCameraYPosition;
    private boolean gameCameraFixed;

    // Deal with camera movement
    private Handler cHandler;
    private Runnable leftWalkCamera, leftRunCamera, rightWalkCamera, rightRunCamera;

    // Character variables
    private Character kirby;
    private float kirbyXPosition;
    private float kirbyYPosition;
    private float walkSpeed, runSpeed, jumpHeight, highJumpHeight, floatJumpHeight;
    private boolean isFloating;
    private boolean startFloatFinished;
    private int jumpCount;
    private boolean isGrounded;

    // Hashmap of all npcs
    private HashMap<String, Character> allNPCs;

    // specialDee variables
    private float specialDeeXPosition;
    private float specialDeeYPosition;
    private boolean isSpecialDeePresent;
    private String specialDeeDirection;
    private int forestHintNumber;

    private final String[] forestHints = new String[]{"Did you know that there is SPECIAL kind of soup that can only be made using all three ingredients found in this forest? " +
            "It's a soup that very few know about. Intrigued? Well you're lucky you ran into me because making SPECIAL soups is my SPECIALTY! " +
            "If you want to learn more about this SPECIAL soup, look for me during the NIGHT. I'll be lurking in the shadows waiting for you.",
            "You found me! You must be eager to make this SPECIAL soup. I'll give you a hint on how to make it: It is mostly made of CARROTS! " +
                    "If you want another hint, you can find me somewhere in the forest in the DAY TIME.",
            "...wow you look just like me. I like your eyes...hey Kirby, you're in the way! ... Oh you want to learn more about the SPECIAL soup? " +
                    "Okay, here's another hint: You'll need TWO MUSHROOMS for this soup. If you want another hint, I'll be around the forest at SUNRISE. Bye!",
            "Hey there! Isn't the sunrise nice? I wish there were more Waddle Dees around to see this. I'm glad that you are here Kirby. Are you ready for " +
                    "my last hint to make the forest SPECIAL soup? The soup is made from exactly NINE ingredients with only ONE of them being a TOMATO. And" +
                    " that is all! You have all the information you need to make the forest SPECIAL soup. Good luck on your soup making!",
            "Hey Kirby! I hope you were able to make the SPECIAL soup of the forest. Good luck on the rest of your soup making!"};

    private ArrayList<Character> npcCopyList;

    // Day/Night cycle variables
    private String timeOfDay;
    private int bA;
    private int bR;
    private int bG;
    private int bB;

    private int oA;
    private int oR;
    private int oG;
    private int oB;

    private boolean negateDayNight;

    // Environment variables
    private String environment;
    private boolean isCloseToHouse;
    private boolean isByTutorialWaddleDee;
    private boolean isByForestSoupHintWaddleDee;
    private boolean isCloseToCauldron;
    private boolean isCloseToForestDoor;
    private boolean isCloseToSwampDoor;
    private boolean isCloseToHouseBack;
    private boolean itemsAreSet;

    private ArrayList<GameObject> testEnvironmentBackgroundGameObjects;
    private ArrayList<GameObject> testEnvironmentCollisionGameObjects;
    private ArrayList<GameObject> testEnvironmentForegroundGameObjects;

    private ArrayList<GameObject> forestEnvironmentBackgroundGameObjects;
    private ArrayList<GameObject> forestEnvironmentCollisionGameObjects;
    private ArrayList<GameObject> forestEnvironmentForegroundGameObjects;

    private ArrayList<GameObject> forestClouds;
    private float[][] forestCloudCoordinates;

    private ArrayList<GameObject> houseEnvironmentBackgroundGameObjects;
    private ArrayList<GameObject> houseEnvironmentCollisionGameObjects;
    private ArrayList<GameObject> houseEnvironmentForegroundGameObjects;

    private ArrayList<GameObject> swampEnvironmentBackgroundGameObjects;
    private ArrayList<GameObject> swampEnvironmentCollisionGameObjects;
    private ArrayList<GameObject> swampEnvironmentForegroundGameObjects;

    // User Interface variables
    private Button leftButton, rightButton, jumpButton, actionButton;

    // Inventory variables
    private ImageView iv_1, iv_2, iv_3, iv_4, iv_5, iv_6, iv_7, iv_8, iv_9, iv_10, iv_11, iv_12, iv_13, iv_14, iv_15;

    private ImageView[] invImages;
    private int[] invRes = new int[] {R.id.iv1, R.id.iv2, R.id.iv3, R.id.iv4, R.id.iv5, R.id.iv6, R.id.iv7,
            R.id.iv8, R.id.iv9, R.id.iv10, R.id.iv11, R.id.iv12, R.id.iv13, R.id.iv14, R.id.iv15};
    private int[] invDrawables;

    private String[] itemNames;

    private Ingredient[] userIngredients;

    private HashMap<String,Ingredient> ingredientKey;

    private ArrayList<Ingredient> allForestCurrentItems;
    private ArrayList<String> allForestCurrentItemNames;
    private float[] allForestCurrentItemLocations;

    private ArrayList<Ingredient> allSwampCurrentItems;
    private ArrayList<String> allSwampCurrentItemNames;
    private float[] allSwampCurrentItemLocations;

    // inventory layout
    private ConstraintLayout layout;

    private int[] inventoryItemClickCounter;

    private ArrayList<Ingredient> selectedIngredients;
    private ArrayList<Integer> selectedIngredientsIndex;

    public static ArrayList<Soup> userSoups = new ArrayList<Soup>();

    public static int soupNum = 0;

    // for recreating/saving soups
    private ArrayList<String> soupIngredients;
    private int[] soupRanks;

    // making soup button and removing ingredients button
    private Button makeBttn;
    private Button removeBttn;

    // Dialogue Box variables
    private ConstraintLayout dialogueBoxLayout;
    private TextView dialogueNameTextView;
    private ImageView dialoguePortraitImageView;
    private TextView dialogueTextView;


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
        userInterfaceLayout = findViewById(R.id.UserInterfaceLayout);
        leftButton = findViewById(R.id.leftButton);
        rightButton = findViewById(R.id.rightButton);
        jumpButton = findViewById(R.id.jumpButton);
        actionButton = findViewById(R.id.actionButton);
        dialogueBoxLayout = findViewById(R.id.DialogueBoxLayout);
        dialogueNameTextView = findViewById(R.id.characterNameTextView);
        dialoguePortraitImageView = findViewById(R.id.characterPortrait);
        dialogueTextView = findViewById(R.id.characterDialogueTextView);
        makeBttn = findViewById(R.id.makeSoupBttn);
        removeBttn = findViewById(R.id.removeItemBttn);

        // inventory stuff
        layout = (ConstraintLayout) this.findViewById(R.id.inventoryLayout);

        iv_1 = (ImageView) findViewById(R.id.iv1);
        iv_2 = (ImageView) findViewById(R.id.iv2);
        iv_3 = (ImageView) findViewById(R.id.iv3);
        iv_4 = (ImageView) findViewById(R.id.iv4);
        iv_5 = (ImageView) findViewById(R.id.iv5);
        iv_6 = (ImageView) findViewById(R.id.iv6);
        iv_7 = (ImageView) findViewById(R.id.iv7);
        iv_8 = (ImageView) findViewById(R.id.iv8);
        iv_9 = (ImageView) findViewById(R.id.iv9);
        iv_10 = (ImageView) findViewById(R.id.iv10);
        iv_11 = (ImageView) findViewById(R.id.iv11);
        iv_12 = (ImageView) findViewById(R.id.iv12);
        iv_13 = (ImageView) findViewById(R.id.iv13);
        iv_14 = (ImageView) findViewById(R.id.iv14);
        iv_15 = (ImageView) findViewById(R.id.iv15);

        invImages = new ImageView[] {iv_1, iv_2, iv_3, iv_4, iv_5, iv_6, iv_7, iv_8, iv_9, iv_10, iv_11, iv_12, iv_13, iv_14, iv_15};

        isSpecialDeePresent = true;

        // Set up Handlers
        cHandler = new Handler();
        itemHandler = new Handler();
        eHandler = new Handler();
        npcHandler = new Handler();

        rHandler = new Handler();

        controllerSetUp();
    }

    // INITIAL SETUPS:

    // Camera creation and set up
    private void initialCameraSetUp(){
        walkSpeed = 1/2F;
        runSpeed = walkSpeed * 2;
        gameCamera = new Camera(scalingFrameLayout, gameContainerLayout);
        leftWalkCamera = gameCamera.moveLeft(cHandler, walkSpeed * TitleActivity.DENSITY);
        leftRunCamera = gameCamera.moveLeft(cHandler, runSpeed * TitleActivity.DENSITY);
        rightWalkCamera = gameCamera.moveRight(cHandler, walkSpeed * TitleActivity.DENSITY);
        rightRunCamera = gameCamera.moveRight(cHandler, runSpeed * TitleActivity.DENSITY);

        // Debugging Variable
        zoomed = true;
    }

    // Environment initial set up. Populates ArrayLists of objects for each environment.
    private void initialEnvironmentSetUp(){
        // For each environment, set the camera to that environment and then populate ArrayLists
        GameObject leftBoundary = new GameObject(this, "Boundary", 50, (int)(tHeight),
                R.drawable.boundary, -50,0,true);
        GameObject rightBoundary = new GameObject(this, "Boundary", 50, (int)(tHeight),
                R.drawable.boundary, (tWidth),0,true);

        // Test environment
        cameraSetUp("test");

        testEnvironmentBackgroundGameObjects = new ArrayList<GameObject>();
        testEnvironmentCollisionGameObjects = new ArrayList<GameObject>();
        testEnvironmentForegroundGameObjects = new ArrayList<GameObject>();


//        for(int i = 0; i < 30; i++){
//            float ratio = (int) (Math.random() * 6 + 4)/10F;
//            int xPosition = (int) (Math.random() * (tWidth));
//            testEnvironmentBackgroundGameObjects.add(new GameObject(InGameActivity.this, "Tree", (int)(39 * ratio),(int)(43* ratio),
//                    R.drawable.testtree, xPosition, gameCamera.getBottomYPosition() + 6, false));
//        }
//
//        for(int i = 0; i < 20; i++){
//            float ratio = (int) (Math.random() * 6 + 4)/10F;
//            int xPosition = (int) (Math.random() * (tWidth));
//            testEnvironmentForegroundGameObjects.add(new GameObject(InGameActivity.this, "Tree", (int)(39 * ratio),(int)(43* ratio),
//                    R.drawable.testtree, xPosition, gameCamera.getBottomYPosition() + 6, false));
//        }

        for(int i = 0; i < npcCopyList.size(); i ++) {
            allNPCs.get("Waddle Dee " + String.valueOf(i)).setYPosition((int) (Math.random() * 40) + gameCamera.getTopYPosition());
            allNPCs.get("Waddle Dee " + String.valueOf(i)).setXPosition((int)(Math.random() * (tWidth)/14F) * 14);
            testEnvironmentCollisionGameObjects.add(allNPCs.get("Waddle Dee " + String.valueOf(i)));
        }

        testEnvironmentCollisionGameObjects.add(new GameObject(this, "Ground", (int)(tWidth),10,
                R.drawable.testground, 0, gameCamera.getBottomYPosition(), true, new HitBox(this,true,
                (int)(tWidth), 300, 0, gameCamera.getBottomYPosition(),0,-294)));

        for(int i = 0; i < 50; i++){
            float ratio = (int) (Math.random() * 6 + 4)/10F;
            int xPosition = (int) (Math.random() * (tWidth));
            testEnvironmentCollisionGameObjects.add(new GameObject(InGameActivity.this, "Tree", (int)(39 * ratio),(int)(43* ratio),
                    R.drawable.testtree, xPosition, gameCamera.getBottomYPosition() + 6, true,
                    new HitBox(InGameActivity.this, true, (int)(7* ratio),(int)(39* ratio),xPosition
                            ,gameCamera.getBottomYPosition()+6,(float)(16* ratio),0)));
        }

        testEnvironmentCollisionGameObjects.add(rightBoundary);
        testEnvironmentCollisionGameObjects.add(leftBoundary);

        // Forest Environment
        cameraSetUp("Forest");

        forestEnvironmentBackgroundGameObjects = new ArrayList<GameObject>();
        forestEnvironmentCollisionGameObjects = new ArrayList<GameObject>();
        forestEnvironmentForegroundGameObjects = new ArrayList<GameObject>();

        forestClouds = new ArrayList<GameObject>();

        // NPC Waddle Dee 0 (Tutorial Waddle Dee)
        allNPCs.get("Waddle Dee 0").setXPosition(2 * tWidth/21F);
        allNPCs.get("Waddle Dee 0").setYPosition(gameCamera.getBottomYPosition() + 6);
        allNPCs.get("Waddle Dee 0").faceDirection("Left");
        forestEnvironmentCollisionGameObjects.add(allNPCs.get("Waddle Dee 0"));

        // NPC Waddle Dee 1 (SpecialDee)
        allNPCs.get("Waddle Dee 1").setXPosition(specialDeeXPosition);

        if(specialDeeYPosition == -1){
            cameraSetUp("forest");
            specialDeeYPosition = gameCamera.getBottomYPosition() + 6;
        }
        allNPCs.get("Waddle Dee 1").setYPosition(specialDeeYPosition);
        allNPCs.get("Waddle Dee 1").faceDirection(specialDeeDirection);
        if(isSpecialDeePresent) {
            forestEnvironmentBackgroundGameObjects.add(allNPCs.get("Waddle Dee 1"));
        }

        forestEnvironmentCollisionGameObjects.add(new GameObject(this, "Ground", (int)(tWidth),10,
                R.drawable.testground, 0, gameCamera.getBottomYPosition(), true, new HitBox(this,true,
                (int)(tWidth), 300, 0, gameCamera.getBottomYPosition(),0,-294)));
        forestEnvironmentCollisionGameObjects.add(rightBoundary);
        forestEnvironmentCollisionGameObjects.add(leftBoundary);

        // Clouds
        for(int i = 0; i < 20; i++) {
            float cloudX, cloudY, cloudRatio;
            if(forestCloudCoordinates == null){
                cloudX = (float)(Math.random() * tWidth);
                cloudY = (float)(gameCamera.getBottomYPosition() + 2 * (gameCamera.getTopYPosition() - gameCamera.getBottomYPosition())/5F
                        + Math.random() * (gameCamera.getTopYPosition() - gameCamera.getBottomYPosition()) / 2F);
                cloudRatio = (float)(Math.random() * (1/7F - 1/20F) + 1/20F);
            }
            else {
                cloudX = forestCloudCoordinates[i][0];
                cloudY = forestCloudCoordinates[i][1];
                cloudRatio = forestCloudCoordinates[i][2];
            }

            GameObject cloud = new GameObject(InGameActivity.this, "Cloud", (int) (180 * cloudRatio), (int) (94 * cloudRatio),
                    R.drawable.cloud, cloudX, cloudY, false);
            forestClouds.add(cloud);
            forestEnvironmentBackgroundGameObjects.add(cloud);
        }

        float ratio = 4/7F;
        float ratio2 = 2/3F;

        forestEnvironmentBackgroundGameObjects.add(new GameObject(InGameActivity.this, "Tree", (int)(61 * ratio),(int)(88* ratio),
                R.drawable.tree7_bg, tWidth/18, gameCamera.getBottomYPosition() + 6, false));
        forestEnvironmentBackgroundGameObjects.add(new GameObject(InGameActivity.this, "Tree", (int)(61 * ratio),(int)(88* ratio),
                R.drawable.tree7_bg, tWidth/4, gameCamera.getBottomYPosition() + 6, false));
        forestEnvironmentBackgroundGameObjects.add(new GameObject(InGameActivity.this, "Tree", (int)(61 * ratio2),(int)(88 * ratio2),
                R.drawable.tree7_bg, tWidth/3, gameCamera.getBottomYPosition() + 6, false));
        forestEnvironmentBackgroundGameObjects.add(new GameObject(InGameActivity.this, "Tree", (int)(61 * ratio),(int)(88 * ratio),
                R.drawable.tree7_bg, tWidth/2, gameCamera.getBottomYPosition() + 6, false));
        forestEnvironmentBackgroundGameObjects.add(new GameObject(InGameActivity.this, "Tree", (int)(61 * ratio2),(int)(88 * ratio2),
                R.drawable.tree7_bg, tWidth - tWidth/3, gameCamera.getBottomYPosition() + 6, false));
        forestEnvironmentBackgroundGameObjects.add(new GameObject(InGameActivity.this, "Tree", (int)(61 * ratio),(int)(88 * ratio),
                R.drawable.tree7_bg, tWidth - tWidth/7, gameCamera.getBottomYPosition() + 6, false));

        float ratio3 = 2/5F;
        float ratio4 = 3/5F;
        float ratio5 = 5/7F;
        float ratio7 = 1/3F;

        forestEnvironmentBackgroundGameObjects.add(new GameObject(InGameActivity.this, "Tree", (int)(75 * ratio3),(int)(104* ratio3),
                R.drawable.tree3_bg, tWidth/7, gameCamera.getBottomYPosition() + 6, false));
        forestEnvironmentBackgroundGameObjects.add(new GameObject(InGameActivity.this, "Tree", (int)(75 * ratio3),(int)(104* ratio3),
                R.drawable.tree3_bg, tWidth/2 - tWidth/12, gameCamera.getBottomYPosition() + 6, false));
        forestEnvironmentBackgroundGameObjects.add(new GameObject(InGameActivity.this, "Tree", (int)(75 * ratio3),(int)(104* ratio3),
                R.drawable.tree3_bg, tWidth/2 + tWidth/11, gameCamera.getBottomYPosition() + 6, false));
        forestEnvironmentBackgroundGameObjects.add(new GameObject(InGameActivity.this, "Tree", (int)(75*ratio7),(int)(104*ratio7),
                R.drawable.tree3_bg, tWidth/2 + tWidth/7, gameCamera.getBottomYPosition() + 6, false));
        forestEnvironmentBackgroundGameObjects.add(new GameObject(InGameActivity.this, "Tree", (int)(75 * ratio3),(int)(104* ratio3),
                R.drawable.tree3_bg, tWidth - tWidth/5, gameCamera.getBottomYPosition() + 6, false));

        forestEnvironmentCollisionGameObjects.add(new GameObject(InGameActivity.this, "Tree", (int)(39*ratio5),(int)(43*ratio5),
                R.drawable.tree1, tWidth/10, gameCamera.getBottomYPosition() + 6, true,
                new HitBox(InGameActivity.this, true, (int)(7*ratio5),(int)(39*ratio5),tWidth/10
                        ,gameCamera.getBottomYPosition()+6,(float)(16*ratio5),0)));
        forestEnvironmentCollisionGameObjects.add(new GameObject(InGameActivity.this, "Tree", (int)(39*ratio5),(int)(43*ratio5),
                R.drawable.tree1, tWidth/5, gameCamera.getBottomYPosition() + 6, true,
                new HitBox(InGameActivity.this, true, (int)(7*ratio5),(int)(39*ratio5),tWidth/5
                        ,gameCamera.getBottomYPosition()+6,(float)(16*ratio5),0)));
        forestEnvironmentCollisionGameObjects.add(new GameObject(InGameActivity.this, "Tree", (int)(39 * ratio4),(int)(43 * ratio4),
                R.drawable.tree1, tWidth/5 + tWidth/10, gameCamera.getBottomYPosition() + 6, true,
                new HitBox(InGameActivity.this, true, (int)(7 * ratio4),(int)(39 * ratio4),tWidth/5 + tWidth/10
                        ,gameCamera.getBottomYPosition()+6,(float)(16 * ratio4),0)));
        forestEnvironmentCollisionGameObjects.add(new GameObject(InGameActivity.this, "Tree", (int)(39*ratio5),(int)(43*ratio5),
                R.drawable.tree1, tWidth/2 - tWidth/20, gameCamera.getBottomYPosition() + 6, true,
                new HitBox(InGameActivity.this, true, (int)(7*ratio5),(int)(39*ratio5),tWidth/2 - tWidth/20
                        ,gameCamera.getBottomYPosition()+6,(float)(16*ratio5),0)));
        forestEnvironmentCollisionGameObjects.add(new GameObject(InGameActivity.this, "Tree", (int)(39 * ratio4),(int)(43 * ratio4),
                R.drawable.tree1, tWidth/4 + tWidth/2, gameCamera.getBottomYPosition() + 6, true,
                new HitBox(InGameActivity.this, true, (int)(7 * ratio4),(int)(39 * ratio4),tWidth/4 + tWidth/2
                        ,gameCamera.getBottomYPosition()+6,(float)(16 * ratio4),0)));
        forestEnvironmentCollisionGameObjects.add(new GameObject(InGameActivity.this, "Tree", (int)(39*ratio5),(int)(43*ratio5),
                R.drawable.tree1, tWidth - tWidth/6, gameCamera.getBottomYPosition() + 6, true,
                new HitBox(InGameActivity.this, true, (int)(7*ratio5),(int)(39*ratio5),tWidth - tWidth/6
                        ,gameCamera.getBottomYPosition()+6,(float)(16*ratio5),0)));

        float ratio6 = 6/7F;
        float ratio8 = 8/7F;

        forestEnvironmentCollisionGameObjects.add(new GameObject(InGameActivity.this, "Tree", (int)(39*ratio6),(int)(43*ratio6),
                R.drawable.tree2, tWidth/4 - tWidth/60, gameCamera.getBottomYPosition() + 6, true,
                new HitBox(InGameActivity.this, true, (int)(7*ratio6),(int)(39*ratio6),tWidth/4 - tWidth/60
                        ,gameCamera.getBottomYPosition()+6,(float)(16*ratio6),0)));
        forestEnvironmentCollisionGameObjects.add(new GameObject(InGameActivity.this, "Tree", (int)(39*ratio6),(int)(43*ratio6),
                R.drawable.tree2, tWidth/2 - tWidth/8, gameCamera.getBottomYPosition() + 6, true,
                new HitBox(InGameActivity.this, true, (int)(7*ratio6),(int)(39*ratio6),tWidth/2 - tWidth/8
                        ,gameCamera.getBottomYPosition()+6,(float)(16*ratio6),0)));
        forestEnvironmentCollisionGameObjects.add(new GameObject(InGameActivity.this, "Tree", (int)(39*ratio6),(int)(43*ratio6),
                R.drawable.tree2, tWidth/2 + tWidth/17, gameCamera.getBottomYPosition() + 6, true,
                new HitBox(InGameActivity.this, true, (int)(7*ratio6),(int)(39*ratio6),tWidth/2 + tWidth/17
                        ,gameCamera.getBottomYPosition()+6,(float)(16*ratio6),0)));
        forestEnvironmentCollisionGameObjects.add(new GameObject(InGameActivity.this, "Tree", (int)(39*ratio6),(int)(43*ratio6),
                R.drawable.tree2, tWidth/2 + tWidth/5, gameCamera.getBottomYPosition() + 6, true,
                new HitBox(InGameActivity.this, true, (int)(7*ratio6),(int)(39*ratio6),tWidth/2 + tWidth/5
                        ,gameCamera.getBottomYPosition()+6,(float)(16*ratio6),0)));
        forestEnvironmentCollisionGameObjects.add(new GameObject(InGameActivity.this, "Tree", (int)(39*ratio8),(int)(43*ratio8),
                R.drawable.tree2, tWidth - tWidth/3 - tWidth/25, gameCamera.getBottomYPosition() + 6, true,
                new HitBox(InGameActivity.this, true, (int)(7*ratio8),(int)(39*ratio8),tWidth - tWidth/3 - tWidth/25
                        ,gameCamera.getBottomYPosition()+6,(float)(16*ratio8),0)));

        float houseRatio = 1/7F;
        GameObject mushroomHouse = new GameObject(InGameActivity.this, "Mushroom House", (int)(600 * houseRatio),
                (int)(600 * houseRatio), R.drawable.mushroom_house, tWidth-tWidth/9F, gameCamera.getBottomYPosition() + 6 - (31 * houseRatio), true,
                new HitBox(InGameActivity.this, true, (int)(140 * houseRatio),(int)(100 * houseRatio),tWidth-tWidth/9F,
                        gameCamera.getBottomYPosition()+6 - (int)(31 * houseRatio), (int)(220 * houseRatio), (int)(31 * houseRatio)));
        forestEnvironmentBackgroundGameObjects.add(mushroomHouse);


        // House Environment
        cameraSetUp("House");

        GameObject leftBoundaryHouse = new GameObject(this, "Boundary", 50, (int)(tHeight),
                R.drawable.boundary, -50 + (centerX - tWidth * 3440/30000F),0,true);
        GameObject rightBoundaryHouse = new GameObject(this, "Boundary", 50, (int)(tHeight),
                R.drawable.boundary, (centerX + tWidth * 3440/30000F),0,true);

        GameObject topBoundaryHouse = new GameObject(this, "Boundary", (int)(tWidth),
                300, R.drawable.boundary, 0,gameCamera.getTopYPosition(),true);

        GameObject forestDoor = new GameObject(this, "Forest Door",(int)(tWidth * 328/15000F),(int)(tWidth * 533/15000F),
                android.R.color.transparent,(centerX - tWidth * 3440/30000F) + (tWidth * 167/15000F), gameCamera.getBottomYPosition() + 3, true);

        GameObject swampDoor = new GameObject(this, "Swamp Door",(int)(tWidth * 328/15000F),(int)(tWidth * 533/15000F),
                android.R.color.transparent,(centerX + tWidth * 3440/30000F) - (tWidth * 180/15000F) - (int)(tWidth * 328/15000F), gameCamera.getBottomYPosition() + 3, true);

        houseEnvironmentBackgroundGameObjects = new ArrayList<GameObject>();
        houseEnvironmentCollisionGameObjects = new ArrayList<GameObject>();
        houseEnvironmentForegroundGameObjects = new ArrayList<GameObject>();

        houseEnvironmentBackgroundGameObjects.add(new GameObject(this, "Ground", (int)(tWidth),4,
                R.drawable.house_floortest, 0, gameCamera.getBottomYPosition(), true, new HitBox(this,true,
                (int)(tWidth), 295, 0, gameCamera.getBottomYPosition(),0,-294)));

        houseEnvironmentForegroundGameObjects.add(topBoundaryHouse);
        houseEnvironmentForegroundGameObjects.add(leftBoundaryHouse);
        houseEnvironmentForegroundGameObjects.add(rightBoundaryHouse);

        // Cauldron
        float cauldronRatio = 1/60F;
        GameObject cauldron = new GameObject(this, "Cauldron", (int)(1650 * cauldronRatio), (int)(1289 * cauldronRatio), R.drawable.cauldronbasecompressed,
                centerX - (int)(1650 * cauldronRatio)/2F, gameCamera.getBottomYPosition() + 3/4F, true);
        cauldron.setScaleType(ImageView.ScaleType.FIT_START);
        cauldron.setImageResource(R.drawable.cauldrontop);

        houseEnvironmentCollisionGameObjects.add(cauldron);
        houseEnvironmentCollisionGameObjects.add(forestDoor);
        houseEnvironmentCollisionGameObjects.add(swampDoor);


        // Swamp Environment
        cameraSetUp("Swamp");

        swampEnvironmentBackgroundGameObjects = new ArrayList<GameObject>();
        swampEnvironmentCollisionGameObjects = new ArrayList<GameObject>();
        swampEnvironmentForegroundGameObjects = new ArrayList<GameObject>();

        swampEnvironmentCollisionGameObjects.add(leftBoundary);
        swampEnvironmentCollisionGameObjects.add(rightBoundary);

        swampEnvironmentCollisionGameObjects.add(new GameObject(this, "Ground", (int)(tWidth),10,
                R.drawable.testground2, 0, gameCamera.getBottomYPosition(), true, new HitBox(this,true,
                (int)(tWidth), 300, 0, gameCamera.getBottomYPosition(),0,-294)));

        swampEnvironmentBackgroundGameObjects.add(new GameObject(InGameActivity.this, "Tree", (int)(66 * ratio3),(int)(128 * ratio3),
                R.drawable.tree4_bg, tWidth/16, gameCamera.getBottomYPosition() + 6, false));
        swampEnvironmentBackgroundGameObjects.add(new GameObject(InGameActivity.this, "Tree", (int)(66 * ratio),(int)(128 * ratio),
                R.drawable.tree4_bg, tWidth/4, gameCamera.getBottomYPosition() + 6, false));
        swampEnvironmentBackgroundGameObjects.add(new GameObject(InGameActivity.this, "Tree", (int)(66 * ratio),(int)(128 * ratio),
                R.drawable.tree4_bg, tWidth/2, gameCamera.getBottomYPosition() + 6, false));
        swampEnvironmentBackgroundGameObjects.add(new GameObject(InGameActivity.this, "Tree", (int)(66 * ratio),(int)(128 * ratio),
                R.drawable.tree4_bg, tWidth/2 + tWidth/8, gameCamera.getBottomYPosition() + 6, false));
        swampEnvironmentBackgroundGameObjects.add(new GameObject(InGameActivity.this, "Tree", (int)(66 * ratio3),(int)(128 * ratio3),
                R.drawable.tree4_bg, tWidth/2 + tWidth/5, gameCamera.getBottomYPosition() + 6, false));

        swampEnvironmentBackgroundGameObjects.add(new GameObject(InGameActivity.this, "Tree", (int)(66 * ratio),(int)(99 * ratio),
                R.drawable.tree5_bg, tWidth/8, gameCamera.getBottomYPosition() + 6, false));
        swampEnvironmentBackgroundGameObjects.add(new GameObject(InGameActivity.this, "Tree", (int)(66 * ratio),(int)(99 * ratio),
                R.drawable.tree5_bg, tWidth/3, gameCamera.getBottomYPosition() + 6, false));
        swampEnvironmentBackgroundGameObjects.add(new GameObject(InGameActivity.this, "Tree", (int)(66 * ratio6),(int)(99 * ratio6),
                R.drawable.tree5_bg, tWidth/2 - tWidth/10, gameCamera.getBottomYPosition() + 6, false));
        swampEnvironmentBackgroundGameObjects.add(new GameObject(InGameActivity.this, "Tree", (int)(66 * ratio),(int)(99 * ratio),
                R.drawable.tree5_bg, tWidth/2 + tWidth/3, gameCamera.getBottomYPosition() + 6, false));

        swampEnvironmentCollisionGameObjects.add(new GameObject(InGameActivity.this, "Tree", (int)(43*ratio5),(int)(58*ratio5),
                R.drawable.tree6, tWidth/10, gameCamera.getBottomYPosition() + 6, true,
                new HitBox(InGameActivity.this, true, (int)(9*ratio5),(int)(50*ratio5),tWidth/10
                        ,gameCamera.getBottomYPosition()+6,(float)(23*ratio5),0)));
        swampEnvironmentCollisionGameObjects.add(new GameObject(InGameActivity.this, "Tree", (int)(43*ratio5),(int)(58*ratio5),
                R.drawable.tree6, tWidth/2 + tWidth/10, gameCamera.getBottomYPosition() + 6, true,
                new HitBox(InGameActivity.this, true, (int)(9*ratio5),(int)(50*ratio5),tWidth/2 + tWidth/10
                        ,gameCamera.getBottomYPosition()+6,(float)(23*ratio5),0)));
        swampEnvironmentCollisionGameObjects.add(new GameObject(InGameActivity.this, "Tree", (int)(43*ratio4),(int)(58*ratio4),
                R.drawable.tree6, tWidth/2 + tWidth/4, gameCamera.getBottomYPosition() + 6, true,
                new HitBox(InGameActivity.this, true, (int)(9*ratio4),(int)(50*ratio4),tWidth/2 + tWidth/4
                        ,gameCamera.getBottomYPosition()+6,(float)(23*ratio4),0)));

        swampEnvironmentCollisionGameObjects.add(new GameObject(InGameActivity.this, "Tree", (int)(66*ratio3),(int)(99*ratio3),
                R.drawable.tree5, tWidth/5, gameCamera.getBottomYPosition() + 6, true,
                new HitBox(InGameActivity.this, true, (int)(18*ratio3),(int)(74*ratio3),tWidth/5
                        ,gameCamera.getBottomYPosition()+6,(float)(20*ratio3),0)));

        GameObject mushroomHouseSwamp = new GameObject(InGameActivity.this, "Mushroom House (Back)", (int)(600 * houseRatio),
                (int)(600 * houseRatio), R.drawable.mushroom_house_back, -tWidth/20F, gameCamera.getBottomYPosition() + 6 - (31 * houseRatio), true,
                new HitBox(InGameActivity.this, true, (int)(140 * houseRatio),(int)(100 * houseRatio),-tWidth/20F,
                        gameCamera.getBottomYPosition()+6 - (int)(31 * houseRatio), (int)(220 * houseRatio), (int)(31 * houseRatio)));
        swampEnvironmentBackgroundGameObjects.add(mushroomHouseSwamp);

        // After populating all ArrayLists, set up items and then the environment
        initialItemSetUp();
        environmentSetUp(environment);
    }

    // Sets up the previously saved items
    private void initialItemSetUp(){
        Log.i("ItemGeneration","Initial item set up");
        allForestCurrentItems = new ArrayList<Ingredient>();
        allSwampCurrentItems = new ArrayList<Ingredient>();

        for(int i = 0; i < allForestCurrentItemNames.size(); i++){
            Ingredient ingredient;
            if(allForestCurrentItemNames.get(i).toLowerCase().equals("carrot")){
                ingredient = new Ingredient(this, "Carrot", 10, 6,
                        R.drawable.carrot, allForestCurrentItemLocations[2 * i],
                        allForestCurrentItemLocations[2 * i + 1], 150,242,149,27);
            }
            else if(allForestCurrentItemNames.get(i).toLowerCase().equals("mushroom")){
                ingredient = new Ingredient(this, "Mushroom", 8, 8,
                        R.drawable.mushroom, allForestCurrentItemLocations[2 * i],
                        allForestCurrentItemLocations[2 * i + 1], 150,201, 87, 48);
            }
            else if(allForestCurrentItemNames.get(i).toLowerCase().equals("radish")){
                ingredient = new Ingredient(this, "Radish", 8, 8,
                        R.drawable.radish, allForestCurrentItemLocations[2 * i],
                        allForestCurrentItemLocations[2 * i + 1], 150,243, 222, 255);
            }
            else if(allForestCurrentItemNames.get(i).toLowerCase().equals("tomato")){
                ingredient = new Ingredient(this, "Tomato", 8, 8,
                        R.drawable.tomato, allForestCurrentItemLocations[2 * i],
                        allForestCurrentItemLocations[2 * i + 1], 150,230, 16, 37);
            }
            else if(allForestCurrentItemNames.get(i).toLowerCase().equals("plant")){
                ingredient = new Ingredient(this, "Plant", 8, 8,
                        R.drawable.plant3, allForestCurrentItemLocations[2 * i],
                        allForestCurrentItemLocations[2 * i + 1], 150,113, 214, 79);
            }
            else{
                ingredient = null;
            }

            allForestCurrentItems.add(ingredient);

        }

        for(int i = 0; i < allSwampCurrentItemNames.size(); i++){
            Ingredient ingredient;
            if(allSwampCurrentItemNames.get(i).toLowerCase().equals("carrot")){
                ingredient = new Ingredient(this, "Carrot", 10, 6,
                        R.drawable.carrot, allSwampCurrentItemLocations[2 * i],
                        allSwampCurrentItemLocations[2 * i + 1], 150,242,149,27);
            }
            else if(allSwampCurrentItemNames.get(i).toLowerCase().equals("mushroom")){
                ingredient = new Ingredient(this, "Mushroom", 8, 8,
                        R.drawable.mushroom, allSwampCurrentItemLocations[2 * i],
                        allSwampCurrentItemLocations[2 * i + 1], 150,201, 87, 48);
            }
            else if(allSwampCurrentItemNames.get(i).toLowerCase().equals("radish")){
                ingredient = new Ingredient(this, "Radish", 12, 12,
                        R.drawable.radish, allSwampCurrentItemLocations[2 * i],
                        allSwampCurrentItemLocations[2 * i + 1], 150,243, 222, 255);
            }
            else if(allSwampCurrentItemNames.get(i).toLowerCase().equals("tomato")){
                ingredient = new Ingredient(this, "Tomato", 8, 8,
                        R.drawable.tomato, allSwampCurrentItemLocations[2 * i],
                        allSwampCurrentItemLocations[2 * i + 1], 150,230, 16, 37);
            }
            else if(allSwampCurrentItemNames.get(i).toLowerCase().equals("plant")){
                ingredient = new Ingredient(this, "Plant", 10, 10,
                        R.drawable.plant3, allSwampCurrentItemLocations[2 * i],
                        allSwampCurrentItemLocations[2 * i + 1], 150,113, 214, 79);
            }
            else{
                ingredient = null;
            }

            allSwampCurrentItems.add(ingredient);

        }
    }

    // Sets up all characters
    private void initialCharacterSetUp(){
        // In general: Create Character, define attributes of character (ex: walk speed), define all hit boxes, and finally define all actions

        // Kirby Set Up

        // Kirby Attributes
        walkSpeed = 1/2F;
        runSpeed = walkSpeed * 2;
        jumpHeight = 15;
        highJumpHeight = jumpHeight * 2;
        floatJumpHeight = jumpHeight/2;

        isFloating = false;
        startFloatFinished = false;
        jumpCount = 0;

        int kirbyWidth = 30;
        int kirbyHeight = 20;

        HitBox kirbyIdleHitBox = new HitBox(this, true, (int) (kirbyWidth * 20/59F),(int)(kirbyHeight * 18/39F),
                0, 0, 30 * 19/59F,0);

        kirby = new Character(this, "Kirby", kirbyWidth, kirbyHeight, 0, 0,
                kirbyIdleHitBox, true, R.drawable.kirbyidle);
        kirby.setObjectResource(R.drawable.kirbyidle);


        // Hit Boxes Set Up

        ArrayList<HitBox> kirbyWalkHitBoxes, kirbyRunHitBoxes, kirbyFallHitBoxes, kirbyFlipFallHitBoxes,
                kirbyFloatHitBoxes, kirbyJumpHitBox, kirbyFloatFallHitBoxes, kirbyStartFloatHitBoxes, kirbyStopFloatHitBoxes;

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
                        if (!specialCollisionHandler(object1, object2, GameObject.getCollisionType(object1, object2))) {
                            if(GameObject.getCollisionType(object1, object2).contains("right")){
                                if (!gameCamera.isFixedPosition()) {
                                    gameCamera.setXPosition(gameCamera.getXPosition() + walkSpeed);
                                }
                                kirby.setXPosition(kirby.getXPosition() + walkSpeed);

                                Log.i("Collision", object1.getObjectName() + " collided with the right of " + object2.getObjectName());
                            }

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
                            pauseWalkEffect();
                        }
                    }

                    @Override
                    public void isGrounded() {
                        if(SettingsPage.isEffect&&(walkEffectPlayer == null || !walkEffectPlayer.isPlaying())){
                            playWalkEffect(R.raw.runningongrass, true);
                            walkEffectPlayer.setVolume(1f, 1f);
                        }

                    }
                },
                new Character.PositionListener() {
                    @Override
                    public void atPosition(float xPosition, float yPosition) {
                        if((xPosition + kirby.getObjectWidth()/2F <= tWidth - (gameCamera.getRightXPosition()-gameCamera.getLeftXPosition())/2
                                + walkSpeed && xPosition + kirby.getObjectWidth()/2F >= tWidth -
                                (gameCamera.getRightXPosition()-gameCamera.getLeftXPosition())/2-walkSpeed)){
                            if(gameCamera.isFixedPosition()) {
                                gameCamera.setFixedPosition(false);
                                cHandler.removeCallbacksAndMessages(null);
                                cHandler.postDelayed(leftWalkCamera, 0);
                                kirby.setCenterXPosition(gameCamera.getXPosition());
                            }
                        }

                        if(environment.toLowerCase().equals("house")){
                            if(gameCamera.getLeftXPosition() < (centerX - tWidth * 3440/30000F) && !gameCamera.isFixedPosition()) {
                                gameCamera.setFixedPosition(true);
                            }

                            if(kirby.getCenterXPosition() < (centerX + tWidth * 3440/30000F) - (gameCamera.getRightXPosition()-gameCamera.getLeftXPosition())/2
                                    && kirby.getCenterXPosition() > centerX && gameCamera.isFixedPosition()){
                                gameCamera.setFixedPosition(false);
                                cHandler.postDelayed(leftWalkCamera, 0);
                                kirby.setCenterXPosition(gameCamera.getXPosition());
                            }
                        }

                        if(yPosition < gameCamera.getBottomYPosition() - 100){
                            kirby.setYPosition(centerY);
                            kirby.stopFall();
                            kirby.getUdHandler().removeCallbacksAndMessages(null);
                            kirby.getUdHandler().postDelayed(kirby.getAllActions().get("Fall"),0);
                        }
                        isCloseToHouse = false;
                        isByTutorialWaddleDee = false;
                        isByForestSoupHintWaddleDee = false;
                        isCloseToCauldron = false;
                        isCloseToForestDoor = false;
                        isCloseToSwampDoor = false;
                        isCloseToHouseBack = false;
                        if(!actionButton.getBackground().getConstantState().equals(getDrawable(R.drawable.downarrow).getConstantState())) {
                            actionButton.setBackgroundResource(android.R.drawable.presence_invisible);
                        }

                    }
                });

        Runnable leftRun = kirby.walk(kirby.getLrHandler(), R.drawable.kirbyrun,"left", runSpeed, kirbyRunHitBoxes,
                new GameObject.CollisionListener() {
                    @Override
                    public void onCollision(GameObject object1, GameObject object2) {
                        if (!specialCollisionHandler(object1, object2, GameObject.getCollisionType(object1, object2))) {
                            if(GameObject.getCollisionType(object1, object2).contains("right")) {
                                if (!gameCamera.isFixedPosition()) {
                                    gameCamera.setXPosition(gameCamera.getXPosition() + runSpeed);
                                }
                                kirby.setXPosition(kirby.getXPosition() + runSpeed);
                                Log.i("Collision", object1.getObjectName() + " collided with the right of " + object2.getObjectName());
                            }
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
                            pauseWalkEffect();
                        }
                    }

                    @Override
                    public void isGrounded() {
                        if(SettingsPage.isEffect&&(walkEffectPlayer == null || !walkEffectPlayer.isPlaying())){
                            playWalkEffect(R.raw.runningongrass, true);
                            walkEffectPlayer.setVolume(1f, 1f);
                        }
                    }
                },
                new Character.PositionListener() {
                    @Override
                    public void atPosition(float xPosition, float yPosition) {
                        if((xPosition + kirby.getObjectWidth()/2F <= tWidth - (gameCamera.getRightXPosition()-gameCamera.getLeftXPosition())/2
                                + runSpeed && xPosition + kirby.getObjectWidth()/2F >= tWidth -
                                (gameCamera.getRightXPosition()-gameCamera.getLeftXPosition())/2-runSpeed)){
                            if(gameCamera.isFixedPosition()) {
                                gameCamera.setFixedPosition(false);
                                cHandler.removeCallbacksAndMessages(null);
                                cHandler.postDelayed(leftRunCamera, 0);
                                kirby.setCenterXPosition(gameCamera.getXPosition());
                            }
                        }

                        if(environment.toLowerCase().equals("house")){
                            if(gameCamera.getLeftXPosition() < (centerX - tWidth * 3440/30000F) && !gameCamera.isFixedPosition()) {
                                gameCamera.setFixedPosition(true);
                            }

                            if(kirby.getCenterXPosition() < (centerX + tWidth * 3440/30000F) - (gameCamera.getRightXPosition()-gameCamera.getLeftXPosition())/2
                                    && kirby.getCenterXPosition() > centerX && gameCamera.isFixedPosition()){
                                gameCamera.setFixedPosition(false);
                                cHandler.postDelayed(leftRunCamera, 0);
                                kirby.setCenterXPosition(gameCamera.getXPosition());
                            }
                        }

                        if(yPosition < gameCamera.getBottomYPosition() - 100){
                            kirby.setYPosition(centerY);
                            kirby.stopFall();
                            kirby.getUdHandler().removeCallbacksAndMessages(null);
                            kirby.getUdHandler().postDelayed(kirby.getAllActions().get("Fall"),0);
                        }
                        isCloseToHouse = false;
                        isByTutorialWaddleDee = false;
                        isByForestSoupHintWaddleDee = false;
                        isCloseToCauldron = false;
                        isCloseToForestDoor = false;
                        isCloseToSwampDoor = false;
                        isCloseToHouseBack = false;
                        if(!actionButton.getBackground().getConstantState().equals(getDrawable(R.drawable.downarrow).getConstantState())) {
                            actionButton.setBackgroundResource(android.R.drawable.presence_invisible);
                        }

                    }
                });

        Runnable rightWalk = kirby.walk(kirby.getLrHandler(), R.drawable.kirbywalk,"right", walkSpeed, kirbyWalkHitBoxes,
                new GameObject.CollisionListener() {
                    @Override
                    public void onCollision(GameObject object1, GameObject object2) {
                        if (!specialCollisionHandler(object1, object2, GameObject.getCollisionType(object1, object2))) {
                            if(GameObject.getCollisionType(object1, object2).contains("left")) {
                                if (!gameCamera.isFixedPosition()) {
                                    gameCamera.setXPosition(gameCamera.getXPosition() - walkSpeed);
                                }
                                kirby.setXPosition(kirby.getXPosition() - walkSpeed);
                                Log.i("Collision", object1.getObjectName() + " collided with the left of " + object2.getObjectName());
                            }

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
                            pauseWalkEffect();
                        }
                    }

                    @Override
                    public void isGrounded() {
                        if(SettingsPage.isEffect&&(walkEffectPlayer == null || !walkEffectPlayer.isPlaying())){
                            playWalkEffect(R.raw.runningongrass, true);
                            walkEffectPlayer.setVolume(1f, 1f);
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

                        if(environment.toLowerCase().equals("house")){
                            if(gameCamera.getRightXPosition() > (centerX + tWidth * 3440/30000F) && !gameCamera.isFixedPosition()) {
                                gameCamera.setFixedPosition(true);
                            }

                            if(kirby.getCenterXPosition() > (centerX - tWidth * 3440/30000F) + (gameCamera.getRightXPosition()-gameCamera.getLeftXPosition())/2
                                    && kirby.getCenterXPosition() < centerX && gameCamera.isFixedPosition()){
                                gameCamera.setFixedPosition(false);
                                cHandler.removeCallbacksAndMessages(null);
                                cHandler.postDelayed(rightWalkCamera, 0);
                                kirby.setCenterXPosition(gameCamera.getXPosition());
                            }
                        }

                        if(yPosition < gameCamera.getBottomYPosition() - 100){
                            kirby.setYPosition(centerY);
                            kirby.stopFall();
                            kirby.getUdHandler().removeCallbacksAndMessages(null);
                            kirby.getUdHandler().postDelayed(kirby.getAllActions().get("Fall"),0);
                        }
                        isCloseToHouse = false;
                        isByTutorialWaddleDee = false;
                        isByForestSoupHintWaddleDee = false;
                        isCloseToCauldron = false;
                        isCloseToForestDoor = false;
                        isCloseToSwampDoor = false;
                        isCloseToHouseBack = false;
                        if(!actionButton.getBackground().getConstantState().equals(getDrawable(R.drawable.downarrow).getConstantState())) {
                            actionButton.setBackgroundResource(android.R.drawable.presence_invisible);
                        }

                    }
                });

        Runnable rightRun = kirby.walk(kirby.getLrHandler(), R.drawable.kirbyrun,"right", runSpeed, kirbyRunHitBoxes,
                new GameObject.CollisionListener() {
                    @Override
                    public void onCollision(GameObject object1, GameObject object2) {
                        if (!specialCollisionHandler(object1, object2, GameObject.getCollisionType(object1, object2))) {
                            if(GameObject.getCollisionType(object1, object2).contains("left")) {
                                if (!gameCamera.isFixedPosition()) {
                                    gameCamera.setXPosition(gameCamera.getXPosition() - runSpeed);
                                }
                                kirby.setXPosition(kirby.getXPosition() - runSpeed);
                                Log.i("Collision", object1.getObjectName() + " collided with the left of " + object2.getObjectName());
                            }
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
                            pauseWalkEffect();
                        }
                    }

                    @Override
                    public void isGrounded() {
                        if(SettingsPage.isEffect&&(walkEffectPlayer == null || !walkEffectPlayer.isPlaying())){
                            playWalkEffect(R.raw.runningongrass, true);
                            walkEffectPlayer.setVolume(1f, 1f);
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

                        if(environment.toLowerCase().equals("house")){
                            if(gameCamera.getRightXPosition() > (centerX + tWidth * 3440/30000F) && !gameCamera.isFixedPosition()) {
                                gameCamera.setFixedPosition(true);
                            }

                            if(kirby.getCenterXPosition() > (centerX - tWidth * 3440/30000F) + (gameCamera.getRightXPosition()-gameCamera.getLeftXPosition())/2
                                    && kirby.getCenterXPosition() < centerX && gameCamera.isFixedPosition()){
                                gameCamera.setFixedPosition(false);
                                cHandler.removeCallbacksAndMessages(null);
                                cHandler.postDelayed(rightRunCamera, 0);
                                kirby.setCenterXPosition(gameCamera.getXPosition());
                            }
                        }

                        if(yPosition < gameCamera.getBottomYPosition() - 100){
                            kirby.setYPosition(centerY);
                            kirby.stopFall();
                            kirby.getUdHandler().removeCallbacksAndMessages(null);
                            kirby.getUdHandler().postDelayed(kirby.getAllActions().get("Fall"),0);
                        }
                        isCloseToHouse = false;
                        isByTutorialWaddleDee = false;
                        isByForestSoupHintWaddleDee = false;
                        isCloseToCauldron = false;
                        isCloseToForestDoor = false;
                        isCloseToSwampDoor = false;
                        isCloseToHouseBack = false;
                        if(!actionButton.getBackground().getConstantState().equals(getDrawable(R.drawable.downarrow).getConstantState())) {
                            actionButton.setBackgroundResource(android.R.drawable.presence_invisible);
                        }

                    }
                });

        Runnable jump = kirby.jump(kirby.getUdHandler(), R.drawable.kirby672, false, jumpHeight, kirbyJumpHitBox,
                new GameObject.CollisionListener() {
                    @Override
                    public void onCollision(GameObject object1, GameObject object2) {
                        if(!specialCollisionHandler(object1, object2, GameObject.getCollisionType(object1, object2))){
                            if(GameObject.getCollisionType(object1, object2).contains("bottom")) {
                                kirby.getUdHandler().removeCallbacksAndMessages(null);
                                kirby.stopJump();
                                kirby.setYPosition(object2.getHitBox().bottomRight().y -
                                        kirby.getHitBox().getHitHeight() - kirby.getHitBox().getYBottom());
                                kirby.stopFall();
                                kirby.getUdHandler().postDelayed(kirby.getAllActions().get("Fall"), 0);
                                Log.i("Collision", object1.getObjectName() + " collided with bottom of " + object2.getObjectName());
                            }
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
                        pauseWalkEffect();
                    }
                });

        Runnable fall = kirby.fall(kirby.getUdHandler(), R.drawable.kirbyfall, true, kirbyFallHitBoxes,
                new GameObject.CollisionListener() {
                    @Override
                    public void onCollision(GameObject object1, GameObject object2) {
                        if (!specialCollisionHandler(object1, object2, GameObject.getCollisionType(object1, object2))) {
                            if(GameObject.getCollisionType(object1, object2).contains("top")) {
                                isFloating = false;
                                startFloatFinished = false;
                                jumpCount = 0;
                                kirby.getUdHandler().removeCallbacksAndMessages(null);
                                kirby.stopFall();
                                kirby.setGrounded(true);
                                kirby.setObjectResource(kirby.getIdleResource());

                                kirby.setYPosition(object2.getHitBox().topLeft().y - kirby.getHitBox().getYBottom());
                                actionButton.setBackgroundResource(android.R.drawable.presence_invisible);
                                kirby.setHitBox(kirby.getIdleHitBox());
                                kirby.showHitBox();
                                Log.i("Collision", object1.getObjectName() + " collided with top of " + object2.getObjectName());
                            }

                        }
                    }
                },
                new Character.PositionListener() {
                    @Override
                    public void atPosition(float xPosition, float yPosition) {
                        if(kirbyPreviousXPos == 0.00 || kirbyPreviousYPos == 0.00){
                            kirbyPreviousXPos = xPosition;
                            kirbyPreviousYPos = yPosition;
                        }
                        if(Math.abs(xPosition - kirbyPreviousXPos) > walkSpeed  * 10){
                            Log.i("MovementLogging", "Fall");
                            kirbyPreviousXPos = xPosition;
                            kirbyPreviousYPos = yPosition;
                        }
                        if(yPosition < gameCamera.getBottomYPosition() - 100){
                            kirby.setYPosition(centerY);
                            kirby.stopFall();
                            kirby.getUdHandler().removeCallbacksAndMessages(null);
                            kirby.getUdHandler().postDelayed(kirby.getAllActions().get("Fall"),0);
                        }
                        pauseWalkEffect();
                    }
                });

        Runnable highJump = kirby.jump(kirby.getUdHandler(), R.drawable.kirby672, false, highJumpHeight, kirbyJumpHitBox,
                new GameObject.CollisionListener() {
                    @Override
                    public void onCollision(GameObject object1, GameObject object2) {
                        if(!specialCollisionHandler(object1, object2, GameObject.getCollisionType(object1, object2))){
                            if(GameObject.getCollisionType(object1, object2).contains("bottom")) {
                                kirby.getUdHandler().removeCallbacksAndMessages(null);
                                kirby.stopJump();
                                kirby.setYPosition(object2.getHitBox().bottomRight().y -
                                        kirby.getHitBox().getHitHeight() - kirby.getHitBox().getYBottom());
                                kirby.stopFall();
                                kirby.getUdHandler().postDelayed(kirby.getAllActions().get("Flip Fall"), 0);
                                Log.i("Collision", object1.getObjectName() + " collided with bottom of " + object2.getObjectName());
                            }
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
                        if(kirbyPreviousXPos == 0.00 || kirbyPreviousYPos == 0.00){
                            kirbyPreviousXPos = xPosition;
                            kirbyPreviousYPos = yPosition;
                        }
                        if(Math.abs(xPosition - kirbyPreviousXPos) > walkSpeed  * 10){
                            Log.i("MovementLogging", "High Jump");
                            kirbyPreviousXPos = xPosition;
                            kirbyPreviousYPos = yPosition;
                        }
                        if(yPosition < gameCamera.getBottomYPosition() - 100){
                            kirby.setYPosition(centerY);
                            kirby.stopFall();
                            kirby.getUdHandler().removeCallbacksAndMessages(null);
                            kirby.getUdHandler().postDelayed(kirby.getAllActions().get("Fall"),0);
                        }
                        pauseWalkEffect();
                    }
                });

        Runnable flipFall = kirby.fall(kirby.getUdHandler(), R.drawable.kirbyflipfall, true, kirbyFlipFallHitBoxes,
                new GameObject.CollisionListener() {
                    @Override
                    public void onCollision(GameObject object1, GameObject object2) {
                        if (!specialCollisionHandler(object1, object2, GameObject.getCollisionType(object1, object2))) {
                            if(GameObject.getCollisionType(object1, object2).contains("top")) {
                                kirby.getUdHandler().removeCallbacksAndMessages(null);
                                kirby.stopFall();
                                kirby.setGrounded(true);
                                kirby.setObjectResource(kirby.getIdleResource());

                                kirby.setYPosition(object2.getHitBox().topLeft().y - kirby.getHitBox().getYBottom());

                                kirby.setHitBox(kirby.getIdleHitBox());
                                kirby.showHitBox();
                                Log.i("Collision", object1.getObjectName() + " collided with top of " + object2.getObjectName());
                            }

                        }
                    }
                },
                new Character.PositionListener() {
                    @Override
                    public void atPosition(float xPosition, float yPosition) {
                        if(kirbyPreviousXPos == 0.00 || kirbyPreviousYPos == 0.00){
                            kirbyPreviousXPos = xPosition;
                            kirbyPreviousYPos = yPosition;
                        }
                        if(Math.abs(xPosition - kirbyPreviousXPos) > walkSpeed  * 10){
                            Log.i("MovementLogging", "Flip");
                            kirbyPreviousXPos = xPosition;
                            kirbyPreviousYPos = yPosition;
                        }
                        if(yPosition < gameCamera.getBottomYPosition() - 100){
                            kirby.setYPosition(centerY);
                            kirby.stopFall();
                            kirby.getUdHandler().removeCallbacksAndMessages(null);
                            kirby.getUdHandler().postDelayed(kirby.getAllActions().get("Fall"),0);
                        }
                        pauseWalkEffect();
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
                        if(!specialCollisionHandler(object1, object2, GameObject.getCollisionType(object1, object2))){
                            if(GameObject.getCollisionType(object1, object2).contains("bottom")) {
                                kirby.getUdHandler().removeCallbacksAndMessages(null);
                                kirby.stopJump();
                                kirby.setYPosition(object2.getHitBox().bottomRight().y -
                                        kirby.getHitBox().getHitHeight() - kirby.getHitBox().getYBottom());
                                kirby.stopFall();
                                kirby.getUdHandler().postDelayed(kirby.getAllActions().get("Float Fall"), 0);
                                Log.i("Collision", object1.getObjectName() + " collided with bottom of " + object2.getObjectName());
                            }
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
                        if(kirbyPreviousXPos == 0.00 || kirbyPreviousYPos == 0.00){
                            kirbyPreviousXPos = xPosition;
                            kirbyPreviousYPos = yPosition;
                        }
                        if(Math.abs(xPosition - kirbyPreviousXPos) > walkSpeed  * 10){
                            Log.i("MovementLogging", "Float Jump");
                            kirbyPreviousXPos = xPosition;
                            kirbyPreviousYPos = yPosition;
                        }
                        if(yPosition < gameCamera.getBottomYPosition() - 100){
                            kirby.setYPosition(centerY);
                            kirby.stopFall();
                            kirby.getUdHandler().removeCallbacksAndMessages(null);
                            kirby.getUdHandler().postDelayed(kirby.getAllActions().get("Fall"),0);
                        }
                        pauseWalkEffect();
                    }
                });

        Runnable floatFall = kirby.fall(kirby.getUdHandler(), R.drawable.kirbyfloatfall, true, GameObject.GRAVITY/3F, kirbyFloatFallHitBoxes,
                new GameObject.CollisionListener() {
                    @Override
                    public void onCollision(GameObject object1, GameObject object2) {
                        if (!specialCollisionHandler(object1, object2, GameObject.getCollisionType(object1, object2))) {
                            if(GameObject.getCollisionType(object1, object2).contains("top")) {
                                isFloating = false;
                                jumpCount = 0;
                                kirby.getUdHandler().removeCallbacksAndMessages(null);
                                kirby.stopFall();
                                kirby.stopJump();
                                kirby.setGrounded(false);

                                kirby.setYPosition(object2.getHitBox().topLeft().y - kirby.getHitBox().getYBottom());

                                kirby.getUdHandler().postDelayed(kirby.getAllActions().get("Stop Float"), 0);
                                Log.i("Collision", object1.getObjectName() + " collided with top of " + object2.getObjectName());
                            }

                        }
                    }
                },
                new Character.PositionListener() {
                    @Override
                    public void atPosition(float xPosition, float yPosition) {
                        if(kirbyPreviousXPos == 0.00 || kirbyPreviousYPos == 0.00){
                            kirbyPreviousXPos = xPosition;
                            kirbyPreviousYPos = yPosition;
                        }
                        if(Math.abs(xPosition - kirbyPreviousXPos) > walkSpeed  * 10){
                            Log.i("MovementLogging", "Float Fall");
                            kirbyPreviousXPos = xPosition;
                            kirbyPreviousYPos = yPosition;
                        }
                        if(yPosition < gameCamera.getBottomYPosition() - 100){
                            kirby.setYPosition(centerY);
                            kirby.stopFall();
                            kirby.getUdHandler().removeCallbacksAndMessages(null);
                            kirby.getUdHandler().postDelayed(kirby.getAllActions().get("Fall"),0);
                        }
                        pauseWalkEffect();
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

        Runnable dance1 = kirby.animatedAction(kirby.getAHandler(), true, R.drawable.kirbydance1, null,
                new GameObject.CollisionListener() {
                    @Override
                    public void onCollision(GameObject object1, GameObject object2) {
                    }
                },
                new Character.CharacterListener() {
                    @Override
                    public void onActionComplete() {
                        kirby.setObjectResource(kirby.getIdleResource());
                        kirby.setHitBox(kirby.getIdleHitBox());
                        kirby.showHitBox();
                    }
                });

        Runnable dance2 = kirby.animatedAction(kirby.getAHandler(), true, R.drawable.kirbydance2, null,
                new GameObject.CollisionListener() {
                    @Override
                    public void onCollision(GameObject object1, GameObject object2) {
                    }
                },
                new Character.CharacterListener() {
                    @Override
                    public void onActionComplete() {
                        kirby.setObjectResource(kirby.getIdleResource());
                        kirby.setHitBox(kirby.getIdleHitBox());
                        kirby.showHitBox();
                    }
                });

        Runnable dance3 = kirby.animatedAction(kirby.getAHandler(), true, R.drawable.kirbydance3, null,
                new GameObject.CollisionListener() {
                    @Override
                    public void onCollision(GameObject object1, GameObject object2) {
                    }
                },
                new Character.CharacterListener() {
                    @Override
                    public void onActionComplete() {
                        kirby.setObjectResource(kirby.getIdleResource());
                        kirby.setHitBox(kirby.getIdleHitBox());
                        kirby.showHitBox();
                    }
                });

        Runnable dance4 = kirby.animatedAction(kirby.getAHandler(), true, R.drawable.kirbydance4, null,
                new GameObject.CollisionListener() {
                    @Override
                    public void onCollision(GameObject object1, GameObject object2) {
                    }
                },
                new Character.CharacterListener() {
                    @Override
                    public void onActionComplete() {
                        kirby.setObjectResource(kirby.getIdleResource());
                        kirby.setHitBox(kirby.getIdleHitBox());
                        kirby.showHitBox();
                    }
                });

        Runnable dance5 = kirby.animatedAction(kirby.getAHandler(), true, R.drawable.kirbydance5, null,
                new GameObject.CollisionListener() {
                    @Override
                    public void onCollision(GameObject object1, GameObject object2) {
                    }
                },
                new Character.CharacterListener() {
                    @Override
                    public void onActionComplete() {
                        kirby.setObjectResource(kirby.getIdleResource());
                        kirby.setHitBox(kirby.getIdleHitBox());
                        kirby.showHitBox();
                    }
                });

        Runnable dance6 = kirby.animatedAction(kirby.getAHandler(), true, R.drawable.kirbydance6, null,
                new GameObject.CollisionListener() {
                    @Override
                    public void onCollision(GameObject object1, GameObject object2) {
                    }
                },
                new Character.CharacterListener() {
                    @Override
                    public void onActionComplete() {
                        kirby.setObjectResource(kirby.getIdleResource());
                        kirby.setHitBox(kirby.getIdleHitBox());
                        kirby.showHitBox();
                    }
                });

        Runnable dance7 = kirby.animatedAction(kirby.getAHandler(), true, R.drawable.kirbydance7, null,
                new GameObject.CollisionListener() {
                    @Override
                    public void onCollision(GameObject object1, GameObject object2) {
                    }
                },
                new Character.CharacterListener() {
                    @Override
                    public void onActionComplete() {
                        kirby.setObjectResource(kirby.getIdleResource());
                        kirby.setHitBox(kirby.getIdleHitBox());
                        kirby.showHitBox();
                    }
                });

        Runnable dance8 = kirby.animatedAction(kirby.getAHandler(), true, R.drawable.kirbydance8, null,
                new GameObject.CollisionListener() {
                    @Override
                    public void onCollision(GameObject object1, GameObject object2) {
                    }
                },
                new Character.CharacterListener() {
                    @Override
                    public void onActionComplete() {
                        kirby.setObjectResource(kirby.getIdleResource());
                        kirby.setHitBox(kirby.getIdleHitBox());
                        kirby.showHitBox();
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
        kirby.getAllActions().put("Dance 1", dance1);
        kirby.getAllActions().put("Dance 2", dance2);
        kirby.getAllActions().put("Dance 3", dance3);
        kirby.getAllActions().put("Dance 4", dance4);
        kirby.getAllActions().put("Dance 5", dance5);
        kirby.getAllActions().put("Dance 6", dance6);
        kirby.getAllActions().put("Dance 7", dance7);
        kirby.getAllActions().put("Dance 8", dance8);


        // NPC Set Up
        allNPCs = new HashMap<String, Character>();

        // Create Waddle Dee + Action Set Up
        float walkSpeed = 1/3F;
        float runSpeed = 1/2F;

        // Create multiple Waddle Dee
        npcCopyList = new ArrayList<Character>();
        // ~30-40 limit on number of NPCs
        for(int i = 0; i < 2; i++){
            npcCopyList.add(null);
        }

        int x = 0;
        for(Character npc : npcCopyList){
            float ratio = 1;
            int waddleWidth = (int)(14 * ratio);
            int waddleHeight = (int)(12 * ratio);

            HitBox waddleDeeIdleHitBox = new HitBox(this, true, (int) (waddleWidth * 20/27F),(int)(waddleHeight * 18/23F),
                    0, 0, 14 * 3 /27F,0);

            npc = new Character(this, "Waddle Dee", waddleWidth, waddleHeight, 0, 0,
                    waddleDeeIdleHitBox, true, R.drawable.waddledeeidle);
            npc.setObjectResource(R.drawable.waddledeeidle);

            ArrayList<HitBox> waddleDeeWalkHitBoxes = new ArrayList<HitBox>();
            ArrayList<HitBox> waddleDeeRunHitBoxes = new ArrayList<HitBox>();
            ArrayList<HitBox> waddleDeeFlipFallHitBoxes = new ArrayList<HitBox>();
            ArrayList<HitBox> waddleDeeJumpHitBox = new ArrayList<HitBox>();

            // Walk Hit Boxes
            waddleDeeWalkHitBoxes.add(new HitBox(InGameActivity.this, true, (int)(npc.getObjectWidth() * 20/27F),
                    (int)(npc.getObjectHeight() * 18/23F), 0, 0, npc.getObjectWidth() * 3/27F,
                    0));
            waddleDeeWalkHitBoxes.add(new HitBox(InGameActivity.this, true, (int)(npc.getObjectWidth() * 21/27F),
                    (int)(npc.getObjectHeight() * 19/23F), 0, 0, npc.getObjectWidth() * 3/27F,
                    0));
            waddleDeeWalkHitBoxes.add(new HitBox(InGameActivity.this, true, (int)(npc.getObjectWidth() * 20/27F),
                    (int)(npc.getObjectHeight() * 18/23F), 0, 0, npc.getObjectWidth() * 3/27F,
                    0));
            waddleDeeWalkHitBoxes.add(new HitBox(InGameActivity.this, true, (int)(npc.getObjectWidth() * 19/27F),
                    (int)(npc.getObjectHeight() * 16/23F), 0, 0, npc.getObjectWidth() * 4/27F,
                    0));
            waddleDeeWalkHitBoxes.add(new HitBox(InGameActivity.this, true, (int)(npc.getObjectWidth() * 17/27F),
                    (int)(npc.getObjectHeight() * 17/23F), 0, 0, npc.getObjectWidth() * 5/27F,
                    0));
            waddleDeeWalkHitBoxes.add(new HitBox(InGameActivity.this, true, (int)(npc.getObjectWidth() * 17/27F),
                    (int)(npc.getObjectHeight() * 18/23F), 0, 0, npc.getObjectWidth() * 5/27F,
                    0));
            waddleDeeWalkHitBoxes.add(new HitBox(InGameActivity.this, true, (int)(npc.getObjectWidth() * 21/27F),
                    (int)(npc.getObjectHeight() * 19/23F), 0, 0, npc.getObjectWidth() * 3/27F,
                    0));
            waddleDeeWalkHitBoxes.add(new HitBox(InGameActivity.this, true, (int)(npc.getObjectWidth() * 20/27F),
                    (int)(npc.getObjectHeight() * 18/23F), 0, 0, npc.getObjectWidth() * 3/27F,
                    0));
            waddleDeeWalkHitBoxes.add(new HitBox(InGameActivity.this, true, (int)(npc.getObjectWidth() * 19/27F),
                    (int)(npc.getObjectHeight() * 16/23F), 0, 0, npc.getObjectWidth() * 4/27F,
                    0));
            waddleDeeWalkHitBoxes.add(new HitBox(InGameActivity.this, true, (int)(npc.getObjectWidth() * 19/27F),
                    (int)(npc.getObjectHeight() * 17/23F), 0, 0, npc.getObjectWidth() * 4/27F,
                    0));

            // Run Hit Boxes
            waddleDeeRunHitBoxes.add(new HitBox(InGameActivity.this, true, (int)(npc.getObjectWidth() * 24/27F),
                    (int)(npc.getObjectHeight() * 19/23F), 0, 0, npc.getObjectWidth() * 1/27F,
                    0));
            waddleDeeRunHitBoxes.add(new HitBox(InGameActivity.this, true, (int)(npc.getObjectWidth() * 19/27F),
                    (int)(npc.getObjectHeight() * 19/23F), 0, 0, npc.getObjectWidth() * 4/27F,
                    0));
            waddleDeeRunHitBoxes.add(new HitBox(InGameActivity.this, true, (int)(npc.getObjectWidth() * 17/27F),
                    (int)(npc.getObjectHeight() * 18/23F), 0, 0, npc.getObjectWidth() * 5/27F,
                    0));
            waddleDeeRunHitBoxes.add(new HitBox(InGameActivity.this, true, (int)(npc.getObjectWidth() * 18/27F),
                    (int)(npc.getObjectHeight() * 19/23F), 0, 0, npc.getObjectWidth() * 4/27F,
                    0));
            waddleDeeRunHitBoxes.add(new HitBox(InGameActivity.this, true, (int)(npc.getObjectWidth() * 19/27F),
                    (int)(npc.getObjectHeight() * 19/23F), 0, 0, npc.getObjectWidth() * 4/27F,
                    0));
            waddleDeeRunHitBoxes.add(new HitBox(InGameActivity.this, true, (int)(npc.getObjectWidth() * 18/27F),
                    (int)(npc.getObjectHeight() * 19/23F), 0, 0, npc.getObjectWidth() * 4/27F,
                    0));
            waddleDeeRunHitBoxes.add(new HitBox(InGameActivity.this, true, (int)(npc.getObjectWidth() * 17/27F),
                    (int)(npc.getObjectHeight() * 18/23F), 0, 0, npc.getObjectWidth() * 5/27F,
                    0));
            waddleDeeRunHitBoxes.add(new HitBox(InGameActivity.this, true, (int)(npc.getObjectWidth() * 19/27F),
                    (int)(npc.getObjectHeight() * 19/23F), 0, 0, npc.getObjectWidth() * 4/27F,
                    0));

            // Flip Fall Hit Boxes
            waddleDeeFlipFallHitBoxes.add(new HitBox(InGameActivity.this, true, (int)(npc.getObjectWidth() * 21/27F),
                    (int)(npc.getObjectHeight() * 19/23F), 0, 0, npc.getObjectWidth() * 3/27F,
                    0));
            waddleDeeFlipFallHitBoxes.add(new HitBox(InGameActivity.this, true, (int)(npc.getObjectWidth() * 20/27F),
                    (int)(npc.getObjectHeight() * 18/23F), 0, 0, npc.getObjectWidth() * 3/27F,
                    0));
            waddleDeeFlipFallHitBoxes.add(new HitBox(InGameActivity.this, true, (int)(npc.getObjectWidth() * 21/27F),
                    (int)(npc.getObjectHeight() * 19/23F), 0, 0, npc.getObjectWidth() * 3/27F,
                    0));
            waddleDeeFlipFallHitBoxes.add(new HitBox(InGameActivity.this, true, (int)(npc.getObjectWidth() * 20/27F),
                    (int)(npc.getObjectHeight() * 20/23F), 0, 0, npc.getObjectWidth() * 3/27F,
                    0));
            waddleDeeFlipFallHitBoxes.add(new HitBox(InGameActivity.this, true, (int)(npc.getObjectWidth() * 21/27F),
                    (int)(npc.getObjectHeight() * 17/23F), 0, 0, npc.getObjectWidth() * 3/27F,
                    0));
            waddleDeeFlipFallHitBoxes.add(new HitBox(InGameActivity.this, true, (int)(npc.getObjectWidth() * 22/27F),
                    (int)(npc.getObjectHeight() * 18/23F), 0, 0, npc.getObjectWidth() * 2/27F,
                    0));

            for(int i = 0; i < 10; i ++) {
                waddleDeeFlipFallHitBoxes.add(new HitBox(InGameActivity.this, true, (int)(npc.getObjectWidth() * 21/27F),
                        (int)(npc.getObjectHeight() * 19/23F), 0, 0, npc.getObjectWidth() * 3/27F,
                        0));
                waddleDeeFlipFallHitBoxes.add(new HitBox(InGameActivity.this, true, (int)(npc.getObjectWidth() * 20/27F),
                        (int)(npc.getObjectHeight() * 19/23F), 0, 0, npc.getObjectWidth() * 3/27F,
                        0));
            }

            // Jump Hit Box
            waddleDeeJumpHitBox.add(new HitBox(InGameActivity.this, true, (int)(npc.getObjectWidth() * 20/27F),
                    (int)(npc.getObjectHeight() * 20/23F), 0, 0, npc.getObjectWidth() * 3/27F,
                    0));

            Character tempNPC = npc;
            Runnable wFall = tempNPC.fall(tempNPC.getUdHandler(), R.drawable.waddledeeflipfall, true, waddleDeeFlipFallHitBoxes,
                    new GameObject.CollisionListener() {
                        @Override
                        public void onCollision(GameObject object1, GameObject object2) {
                            if (!specialCollisionHandler(object1, object2, GameObject.getCollisionType(object1, object2))) {
                                if(GameObject.getCollisionType(object1, object2).contains("top")) {
                                    tempNPC.getUdHandler().removeCallbacksAndMessages(null);
                                    tempNPC.stopFall();
                                    tempNPC.setGrounded(true);
                                    tempNPC.setObjectResource(tempNPC.getIdleResource());

                                    tempNPC.setYPosition(object2.getHitBox().topLeft().y -
                                            tempNPC.getHitBox().getYBottom());

                                    tempNPC.setHitBox(tempNPC.getIdleHitBox());
                                    tempNPC.showHitBox();

                                    Log.i("Collision", object1.getObjectName() + " collided with top of " + object2.getObjectName());
                                }
                            }
                        }
                    },
                    new Character.PositionListener() {
                        @Override
                        public void atPosition(float xPosition, float yPosition) {
                            if(yPosition < gameCamera.getBottomYPosition() - 100){
                                tempNPC.setYPosition(centerY);
                                tempNPC.stopFall();
                                tempNPC.getUdHandler().removeCallbacksAndMessages(null);
                                tempNPC.getUdHandler().postDelayed(kirby.getAllActions().get("Fall"),0);
                            }
                        }
                    });

            Runnable wJump = tempNPC.jump(tempNPC.getUdHandler(), R.drawable.waddledee41, false, 15, waddleDeeJumpHitBox,
                    new GameObject.CollisionListener() {
                        @Override
                        public void onCollision(GameObject object1, GameObject object2) {
                            if(!specialCollisionHandler(object1, object2, GameObject.getCollisionType(object1, object2))){
                                if(GameObject.getCollisionType(object1, object2).contains("bottom")) {
                                    tempNPC.getUdHandler().removeCallbacksAndMessages(null);
                                    tempNPC.stopJump();
                                    tempNPC.setYPosition(object2.getHitBox().bottomRight().y -
                                            tempNPC.getHitBox().getHitHeight() -
                                            tempNPC.getHitBox().getYBottom());
                                    tempNPC.stopFall();
                                    tempNPC.getUdHandler().postDelayed(tempNPC.getAllActions().get("Fall"), 0);
                                    Log.i("Collision", object1.getObjectName() + " collided with bottom of " + object2.getObjectName());
                                }
                            }
                        }
                    },
                    new Character.CharacterListener() {
                        @Override
                        public void onActionComplete() {
                            tempNPC.getUdHandler().removeCallbacksAndMessages(null);
                            tempNPC.getAHandler().removeCallbacksAndMessages(null);
                            tempNPC.stopFall();
                            tempNPC.getUdHandler().postDelayed(tempNPC.getAllActions().get("Fall"),0);
                        }
                    },
                    new Character.PositionListener() {
                        @Override
                        public void atPosition(float xPosition, float yPosition) {
                            if(yPosition < gameCamera.getBottomYPosition() - 100){
                                tempNPC.setYPosition(centerY);
                                tempNPC.stopFall();
                                tempNPC.getUdHandler().removeCallbacksAndMessages(null);
                                tempNPC.getUdHandler().postDelayed(kirby.getAllActions().get("Fall"),0);
                            }
                        }
                    });

            Runnable wLeftWalk = tempNPC.walk(tempNPC.getLrHandler(), R.drawable.waddledeewalk, "left", walkSpeed, waddleDeeWalkHitBoxes,
                    new GameObject.CollisionListener() {
                        @Override
                        public void onCollision(GameObject object1, GameObject object2) {
                            if (!specialCollisionHandler(object1, object2, GameObject.getCollisionType(object1, object2))) {
                                if(GameObject.getCollisionType(object1, object2).contains("right")){
                                    tempNPC.setXPosition(tempNPC.getXPosition() + walkSpeed);
                                    tempNPC.setStopMoving(true);
                                    tempNPC.getLrHandler().postDelayed(tempNPC.getAllActions().get("Right Walk"),0);
                                    Log.i("Collision", object1.getObjectName() + " collided with the right of " + object2.getObjectName());
                                }
                            }
                        }
                    },
                    new Character.NotGroundedListener() {
                        @Override
                        public void notGrounded() {
                            if(!tempNPC.isJumpStarted() && tempNPC.isStopJump() && !tempNPC.isFallStarted() && tempNPC.isStopFall()) {
                                tempNPC.getUdHandler().removeCallbacksAndMessages(null);
                                tempNPC.stopFall();
                                tempNPC.getUdHandler().postDelayed(tempNPC.getAllActions().get("Fall"), 0);
                            }
                        }

                        @Override
                        public void isGrounded() {

                        }
                    },
                    new Character.PositionListener() {
                        @Override
                        public void atPosition(float xPosition, float yPosition) {
                            if(yPosition < gameCamera.getBottomYPosition() - 100){
                                tempNPC.setYPosition(centerY);
                                tempNPC.stopFall();
                                tempNPC.getUdHandler().removeCallbacksAndMessages(null);
                                tempNPC.getUdHandler().postDelayed(kirby.getAllActions().get("Fall"),0);
                            }
                        }
                    });

            Runnable wRightWalk = tempNPC.walk(tempNPC.getLrHandler(), R.drawable.waddledeewalk, "right", walkSpeed, waddleDeeWalkHitBoxes,
                    new GameObject.CollisionListener() {
                        @Override
                        public void onCollision(GameObject object1, GameObject object2) {
                            if (!specialCollisionHandler(object1, object2, GameObject.getCollisionType(object1, object2))) {
                                if(GameObject.getCollisionType(object1, object2).contains("left")) {
                                    tempNPC.setXPosition(tempNPC.getXPosition() - walkSpeed);
                                    tempNPC.setStopMoving(true);
                                    tempNPC.getLrHandler().postDelayed(tempNPC.getAllActions().get("Left Walk"),0);
                                    Log.i("Collision", object1.getObjectName() + " collided with the left of " + object2.getObjectName());
                                }
                            }
                        }
                    },
                    new Character.NotGroundedListener() {
                        @Override
                        public void notGrounded() {
                            if(!tempNPC.isJumpStarted() && tempNPC.isStopJump() && !tempNPC.isFallStarted() && tempNPC.isStopFall()) {
                                tempNPC.getUdHandler().removeCallbacksAndMessages(null);
                                tempNPC.stopFall();
                                tempNPC.getUdHandler().postDelayed(tempNPC.getAllActions().get("Fall"), 0);
                            }
                        }

                        @Override
                        public void isGrounded() {

                        }
                    },
                    new Character.PositionListener() {
                        @Override
                        public void atPosition(float xPosition, float yPosition) {
                            if(yPosition < gameCamera.getBottomYPosition() - 100){
                                tempNPC.setYPosition(centerY);
                                tempNPC.stopFall();
                                tempNPC.getUdHandler().removeCallbacksAndMessages(null);
                                tempNPC.getUdHandler().postDelayed(kirby.getAllActions().get("Fall"),0);
                            }
                        }
                    });

            Runnable wLeftRun = tempNPC.walk(tempNPC.getLrHandler(), R.drawable.waddledeerun, "left", runSpeed, waddleDeeRunHitBoxes,
                    new GameObject.CollisionListener() {
                        @Override
                        public void onCollision(GameObject object1, GameObject object2) {
                            if (!specialCollisionHandler(object1, object2, GameObject.getCollisionType(object1, object2))) {
                                if(GameObject.getCollisionType(object1, object2).contains("right")){
                                    tempNPC.setXPosition(tempNPC.getXPosition() + runSpeed);
                                    tempNPC.setStopMoving(true);
                                    tempNPC.getLrHandler().postDelayed(tempNPC.getAllActions().get("Right Run"),0);
                                    Log.i("Collision", object1.getObjectName() + " collided with the right of " + object2.getObjectName());
                                }
                            }
                        }
                    },
                    new Character.NotGroundedListener() {
                        @Override
                        public void notGrounded() {
                            if(!tempNPC.isJumpStarted() && tempNPC.isStopJump() && !tempNPC.isFallStarted() && tempNPC.isStopFall()) {
                                tempNPC.getUdHandler().removeCallbacksAndMessages(null);
                                tempNPC.stopFall();
                                tempNPC.getUdHandler().postDelayed(tempNPC.getAllActions().get("Fall"), 0);
                            }
                        }

                        @Override
                        public void isGrounded() {

                        }
                    },
                    new Character.PositionListener() {
                        @Override
                        public void atPosition(float xPosition, float yPosition) {
                            if(yPosition < gameCamera.getBottomYPosition() - 100){
                                tempNPC.setYPosition(centerY);
                                tempNPC.stopFall();
                                tempNPC.getUdHandler().removeCallbacksAndMessages(null);
                                tempNPC.getUdHandler().postDelayed(kirby.getAllActions().get("Fall"),0);
                            }
                        }
                    });

            Runnable wRightRun = tempNPC.walk(tempNPC.getLrHandler(), R.drawable.waddledeerun, "right", runSpeed, waddleDeeRunHitBoxes,
                    new GameObject.CollisionListener() {
                        @Override
                        public void onCollision(GameObject object1, GameObject object2) {
                            if (!specialCollisionHandler(object1, object2, GameObject.getCollisionType(object1, object2))) {
                                if(GameObject.getCollisionType(object1, object2).contains("left")) {
                                    tempNPC.setXPosition(tempNPC.getXPosition() - runSpeed);
                                    tempNPC.setStopMoving(true);
                                    tempNPC.getLrHandler().postDelayed(tempNPC.getAllActions().get("Left Run"),0);
                                    Log.i("Collision", object1.getObjectName() + " collided with the left of " + object2.getObjectName());
                                }
                            }
                        }
                    },
                    new Character.NotGroundedListener() {
                        @Override
                        public void notGrounded() {
                            if(!tempNPC.isJumpStarted() && tempNPC.isStopJump() && !tempNPC.isFallStarted() && tempNPC.isStopFall()) {
                                tempNPC.getUdHandler().removeCallbacksAndMessages(null);
                                tempNPC.stopFall();
                                tempNPC.getUdHandler().postDelayed(tempNPC.getAllActions().get("Fall"), 0);
                            }
                        }

                        @Override
                        public void isGrounded() {

                        }
                    },
                    new Character.PositionListener() {
                        @Override
                        public void atPosition(float xPosition, float yPosition) {
                            if(yPosition < gameCamera.getBottomYPosition() - 100){
                                tempNPC.setYPosition(centerY);
                                tempNPC.stopFall();
                                tempNPC.getUdHandler().removeCallbacksAndMessages(null);
                                tempNPC.getUdHandler().postDelayed(kirby.getAllActions().get("Fall"),0);
                            }
                        }
                    });

            tempNPC.getAllActions().put("Left Walk", wLeftWalk);
            tempNPC.getAllActions().put("Left Run", wLeftRun);
            tempNPC.getAllActions().put("Right Walk", wRightWalk);
            tempNPC.getAllActions().put("Right Run", wRightRun);
            tempNPC.getAllActions().put("Jump", wJump);
            tempNPC.getAllActions().put("Fall", wFall);

            // Add all NPCs to HashMap
            allNPCs.put("Waddle Dee " + String.valueOf(x), tempNPC);
            x++;
        }
    }

    // Inventory set up after loading data
    private void initialInventorySetUp(){
        userIngredients = new Ingredient[15];
        ingredientKey = new HashMap<String, Ingredient>();
        ingredientKey.put("Carrot", new Ingredient(this, "Carrot",0,0,
                R.drawable.carrot,0,0,150,242,149,27));
        ingredientKey.put("Mushroom", new Ingredient(this, "Mushroom",0,0,
                R.drawable.mushroom,0,0,150,201, 87, 48));
        ingredientKey.put("Radish",new Ingredient(this, "Radish",0,0,
                R.drawable.radish,0,0,150,243, 222, 255));
        ingredientKey.put("Tomato",new Ingredient(this, "Tomato",0,0,
                R.drawable.tomato,0,0,150,230, 16, 37));
        ingredientKey.put("Plant",new Ingredient(this, "Plant",0,0,
                R.drawable.plant3,0,0,150,113, 214, 79));

        Button inv = (Button) findViewById(R.id.invButton);

        boolean isFull = true;

        for(int i = 0; i < 15; i++){
            if(invDrawables[i] != 0) {
                invImages[i].setImageResource(invDrawables[i]);
                userIngredients[i] = ingredientKey.get(itemNames[i]);
            }
            if(invDrawables[i] == android.R.color.transparent || invDrawables[i] == 0){
                inv.setBackgroundResource(R.drawable.inventory_icon);
                isFull = false;
            }
        }

        if(isFull){
            inv.setBackgroundResource(R.drawable.inventory_full);
        }
    }

    // Recreating saved soups after loading data
    private void initialSoupSetUp(){
        userSoups = new ArrayList<Soup>();
        for(int i = 0; i < soupIngredients.size(); i++){
            ArrayList<Ingredient> ingredientList = new ArrayList<Ingredient>();
            int start = 0;
            for(int j = 0; j < soupIngredients.get(i).length(); j++){
                if(soupIngredients.get(i).substring(j,j+1).equals("/")){
                    String ingredientName = soupIngredients.get(i).substring(start,j);
                    Log.i("SoupMaking",ingredientName);
                    ingredientList.add(ingredientKey.get(ingredientName));
                    start = j+1;
                }
            }
            userSoups.add(new Soup(ingredientList, soupRanks[i]));
        }
    }

    // SETUPS:

    // Sets up the camera for a chosen environment.
    private void cameraSetUp(String environment){
        if(environment.toLowerCase().equals("test")){
            gameCamera.setScale(fitZoom(3832,359));
            gameCamera.setLeftXPosition(0);
        }
        else if(environment.toLowerCase().equals("forest")){
            gameCamera.setScale(fitZoom(3832,359));
            gameCamera.setLeftXPosition(0);
        }
        else if(environment.toLowerCase().equals("house")){
            // temporary camera set up
            gameCamera.setScale(fitZoom(15000,800));
            gameCamera.setLeftXPosition((centerX - tWidth * 3440/30000F));
        }
        else if(environment.toLowerCase().equals("swamp")){
            gameCamera.setScale(fitZoom(3832,359));
            gameCamera.setLeftXPosition(0);
        }
    }

    // Sets up a brand new chosen in-game environment. Used to change environments
    private void environmentSetUp(String environment){
        this.environment = environment;
        try{
            backgroundGameLayout.removeAllLayoutObjects();
            backgroundGameLayout.setBackgroundImage(android.R.color.transparent);
            collisionGameLayout.removeAllLayoutObjects();
            foregroundGameLayout.removeAllLayoutObjects();

            backgroundGameLayout = null;
            collisionGameLayout = null;
            foregroundGameLayout = null;
            GameLayout.allLayouts = new ArrayList<GameLayout>();
        }
        catch(Exception e){
            Log.i("EnvironmentSetUp","GameLayouts not created yet");
        }

        try{
            itemHandler.removeCallbacksAndMessages(null);
            eHandler.removeCallbacksAndMessages(null);
        }
        catch(Exception e){
            Log.i("EnvironmentSetUp", "Some handlers not created yet");
        }

        backgroundGameLayout = new GameLayout(this,"Background", backgroundLayout);
        backgroundGameLayout.setBackgroundImageView(findViewById(R.id.backgroundImage));
        collisionGameLayout = new GameLayout(this, "Collision", collisionLayout);
        foregroundGameLayout = new GameLayout(this, "Foreground",foregroundLayout);

        cameraSetUp(environment);

        if(gameCameraXPosition != -1 && gameCameraYPosition != -1) {
            gameCamera.setXPosition(gameCameraXPosition);
            gameCamera.setYPosition(gameCameraYPosition);
        }

        gameCamera.setFixedPosition(gameCameraFixed);

        // In general: First set background and foreground GameObjects. Then add Kirby, then set collision GameObjects, then set up items

        if(environment.toLowerCase().equals("test")){
            backgroundGameLayout.setBackgroundImage(R.drawable.cloudsbackgroundextended);
            backgroundGameLayout.setLayoutObjects(testEnvironmentBackgroundGameObjects);
            foregroundGameLayout.setLayoutObjects(testEnvironmentForegroundGameObjects);


            collisionGameLayout.removeLayoutObject(kirby);

            kirby.setXPosition(kirbyXPosition);
            kirby.setYPosition(kirbyYPosition);
            
            collisionGameLayout.addLayoutObject(kirby);
            collisionGameLayout.addLayoutObjects(testEnvironmentCollisionGameObjects);

            npcHandler.postDelayed(new Runnable() {

                private boolean isWalking = false;

                @Override
                public void run() {
                    for(int i = 0; i < npcCopyList.size(); i++) {
                        if (allNPCs.get("Waddle Dee " + String.valueOf(i)).isGrounded()) {
                            allNPCs.get("Waddle Dee " + String.valueOf(i)).getUdHandler().removeCallbacksAndMessages(null);
                            allNPCs.get("Waddle Dee " + String.valueOf(i)).stopJump();
                            allNPCs.get("Waddle Dee " + String.valueOf(i)).getUdHandler()
                                    .postDelayed(allNPCs.get("Waddle Dee " + String.valueOf(i)).getAllActions().get("Jump"), 0);
                        }


                        if (!isWalking) {
                            allNPCs.get("Waddle Dee " + String.valueOf(i)).getLrHandler().postDelayed(allNPCs.get("Waddle Dee " + String.valueOf(i))
                                    .getAllActions().get("Left Walk"), 0);
                            if(i == npcCopyList.size()-1) {
                                isWalking = true;
                            }
                        }
                    }


                    npcHandler.postDelayed(this, 2000);
                }
            },2000);

            kirby.getUdHandler().postDelayed(kirby.getAllActions().get("Fall"),200);

            setLightingTemporarily(255,255,255,255,255,255);
        }
        else if(environment.toLowerCase().equals("forest")){
            backgroundGameLayout.setBackgroundImage(R.drawable.cloudsbackgroundextended);
            backgroundGameLayout.setLayoutObjects(forestEnvironmentBackgroundGameObjects);
            foregroundGameLayout.setLayoutObjects(forestEnvironmentForegroundGameObjects);

            collisionGameLayout.removeLayoutObject(kirby);

            kirby.setXPosition(kirbyXPosition);
            kirby.setYPosition(kirbyYPosition);
            
            collisionGameLayout.addLayoutObject(kirby);
            collisionGameLayout.addLayoutObjects(forestEnvironmentCollisionGameObjects);

            if(!isGrounded) {
                kirby.setGrounded(false);
                kirby.getUdHandler().removeCallbacksAndMessages(null);
                kirby.stopFall();
                kirby.getUdHandler().postDelayed(kirby.getAllActions().get("Fall"), 200);
            }

            // Forest Clouds moving
            Runnable movingClouds = new Runnable() {
                @Override
                public void run() {
                    for(GameObject cloud : forestClouds){
                        if(cloud.getXPosition() > tWidth){
                            cloud.setXPosition(-cloud.getObjectWidth());
                        }
                        else{
                            float speed = (float)(Math.random() * (1/90F - 1/70F) + 1/90F);
                            cloud.setXPosition(cloud.getXPosition() + speed);
                        }
                    }
                    eHandler.postDelayed(this,1);
                }
            };

            eHandler.postDelayed(movingClouds,0);

        }
        else if(environment.toLowerCase().equals("house")){
            backgroundGameLayout.setBackgroundImage(R.drawable.house_bg_1test);
            backgroundGameLayout.setLayoutObjects(houseEnvironmentBackgroundGameObjects);
            foregroundGameLayout.setLayoutObjects(houseEnvironmentForegroundGameObjects);

            collisionGameLayout.removeLayoutObject(kirby);

            kirby.setXPosition(kirbyXPosition);
            kirby.setYPosition(kirbyYPosition);

            collisionGameLayout.addLayoutObjects(houseEnvironmentCollisionGameObjects);
            collisionGameLayout.addLayoutObject(kirby);

            if(!isGrounded) {
                kirby.setGrounded(false);
                kirby.getUdHandler().removeCallbacksAndMessages(null);
                kirby.stopFall();
                kirby.getUdHandler().postDelayed(kirby.getAllActions().get("Fall"), 200);
            }
        }
        else if(environment.toLowerCase().equals("swamp")){
            backgroundGameLayout.setBackgroundImage(R.drawable.swampyclouds2);
            backgroundGameLayout.setLayoutObjects(swampEnvironmentBackgroundGameObjects);
            foregroundGameLayout.setLayoutObjects(swampEnvironmentForegroundGameObjects);

            collisionGameLayout.removeLayoutObject(kirby);

            kirby.setXPosition(kirbyXPosition);
            kirby.setYPosition(kirbyYPosition);

            collisionGameLayout.addLayoutObject(kirby);
            collisionGameLayout.addLayoutObjects(swampEnvironmentCollisionGameObjects);

            if(!isGrounded) {
                kirby.setGrounded(false);
                kirby.getUdHandler().removeCallbacksAndMessages(null);
                kirby.stopFall();
                kirby.getUdHandler().postDelayed(kirby.getAllActions().get("Fall"), 200);
            }
        }

        itemSetUp(environment);

    }

    // Previously saved items fall from the sky
    private void itemSetUp(String environment){
        if(environment.toLowerCase().equals("forest")) {
            for(Ingredient ingredient: allForestCurrentItems) {
                Runnable fall = ingredient.fall(itemHandler, GameObject.GRAVITY, new GameObject.CollisionListener() {
                    @Override
                    public void onCollision(GameObject object1, GameObject object2) {
                        if (GameObject.getCollisionType(object1, object2).contains("top")) {
                            if (!specialCollisionHandler(object1, object2, GameObject.getCollisionType(object1, object2)) && !object2.isCharacter() && !object2.isIngredient()) {
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

                backgroundGameLayout.addLayoutObject(ingredient);

                itemHandler.postDelayed(fall, 5);
            }
        }
        else if(environment.toLowerCase().equals("swamp")) {
            for(Ingredient ingredient: allSwampCurrentItems) {
                Runnable fall = ingredient.fall(itemHandler, GameObject.GRAVITY, new GameObject.CollisionListener() {
                    @Override
                    public void onCollision(GameObject object1, GameObject object2) {
                        if (GameObject.getCollisionType(object1, object2).contains("top")) {
                            if (!specialCollisionHandler(object1, object2, GameObject.getCollisionType(object1, object2)) && !object2.isCharacter() && !object2.isIngredient()) {
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

                backgroundGameLayout.addLayoutObject(ingredient);

                itemHandler.postDelayed(fall, 5);
            }
        }
    }

    // Creates and drops new items (items depend on environment)
    private void newItemSetUp(String environment){

        if(environment.toLowerCase().equals("test")){
            for(int i = 0; i < 10; i++){
                double size = Math.random() * 2 + 1;

                Ingredient carrot = new Ingredient(this, "Carrot",(int)(size * 10),(int)(6 * size),
                        R.drawable.carrot,
                        (float) (Math.random() * (tWidth - 10)),
                        (float)(gameCamera.getTopYPosition()) + 70,0,0,0,0);

                collisionGameLayout.addLayoutObject(carrot);

                Runnable carrotFall = carrot.fall(itemHandler, GameObject.GRAVITY/10F, new GameObject.CollisionListener() {
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

                itemHandler.postDelayed(carrotFall, 0);

                Ingredient mushroom = new Ingredient(this, "Mushroom",(int)(8 * size),(int)(8*size),
                        R.drawable.mushroom,
                        (float) (Math.random() * (tWidth - 10)),
                        (float)(gameCamera.getTopYPosition()) + 70,0,0,0,0);

                collisionGameLayout.addLayoutObject(mushroom);

                Runnable mushroomFall = mushroom.fall(itemHandler, GameObject.GRAVITY/10F, new GameObject.CollisionListener() {
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

                itemHandler.postDelayed(mushroomFall, 0);

                Ingredient radish = new Ingredient(this, "Radish",(int)(8 * size),(int)(8* size),
                        R.drawable.radish,
                        (float) (Math.random() * (tWidth - 10)),
                        (float)(gameCamera.getTopYPosition()) + 70,0,0,0,0);

                collisionGameLayout.addLayoutObject(radish);

                Runnable radishFall = radish.fall(itemHandler, GameObject.GRAVITY/10F, new GameObject.CollisionListener() {
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

                itemHandler.postDelayed(radishFall, 0);

                Ingredient tomato = new Ingredient(this, "Tomato",(int)(8 * size),(int)(8* size),
                        R.drawable.tomato,
                        (float) (Math.random() * (tWidth - 10)),
                        (float)(gameCamera.getTopYPosition()) + 70,0,0,0,0);

                collisionGameLayout.addLayoutObject(tomato);

                Runnable tomatoFall = tomato.fall(itemHandler, GameObject.GRAVITY/10F, new GameObject.CollisionListener() {
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

                itemHandler.postDelayed(tomatoFall, 0);
            }
            Log.i("EnvironmentSetUp","Test Items");
        }
        else if(environment.toLowerCase().equals("forest")){
            allForestCurrentItems = new ArrayList<Ingredient>();
            int totalItemCount = 20;
            int carrotCount = (int)(Math.random() * totalItemCount) + 1;
            int mushroomCount = (int)(Math.random() * (totalItemCount - carrotCount));
            int tomatoCount = totalItemCount-carrotCount-mushroomCount;

            for(int i = 0; i < carrotCount; i++) {
                Ingredient carrot = new Ingredient(this, "Carrot", 10, 6,
                        R.drawable.carrot,
                        (float) (Math.random() * (tWidth-tWidth/5F) + tWidth/9F),
                        (float) (gameCamera.getTopYPosition()) + 70, 150,242,149,27);


                backgroundGameLayout.addLayoutObject(carrot);
                allForestCurrentItems.add(carrot);

                Runnable carrotFall = carrot.fall(itemHandler, GameObject.GRAVITY, new GameObject.CollisionListener() {
                    @Override
                    public void onCollision(GameObject object1, GameObject object2) {
                        if (GameObject.getCollisionType(object1, object2).contains("top")) {
                            if (!specialCollisionHandler(object1, object2, GameObject.getCollisionType(object1, object2)) && !object2.isCharacter() && !object2.isIngredient()) {
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

                itemHandler.postDelayed(carrotFall, 5);
            }

            for(int i = 0; i < mushroomCount; i++) {
                Ingredient mushroom = new Ingredient(this, "Mushroom", (int) (8), (int) (8),
                        R.drawable.mushroom,
                        (float) (Math.random() * (tWidth-tWidth/5F) + tWidth/9F),
                        (float) (gameCamera.getTopYPosition()) + 70, 150,201, 87, 48);

                backgroundGameLayout.addLayoutObject(mushroom);
                allForestCurrentItems.add(mushroom);

                Runnable mushroomFall = mushroom.fall(itemHandler, GameObject.GRAVITY, new GameObject.CollisionListener() {
                    @Override
                    public void onCollision(GameObject object1, GameObject object2) {
                        if (GameObject.getCollisionType(object1, object2).contains("top")) {
                            if (!specialCollisionHandler(object1, object2, GameObject.getCollisionType(object1, object2)) && !object2.isCharacter() && !object2.isIngredient()) {
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

                itemHandler.postDelayed(mushroomFall, 5);
            }

            for(int i = 0; i < tomatoCount; i++) {
                Ingredient tomato = new Ingredient(this, "Tomato", (int) (8), (int) (8),
                        R.drawable.tomato,
                        (float) (Math.random() * (tWidth-tWidth/5F) + tWidth/9F),
                        (float) (gameCamera.getTopYPosition()) + 70, 150,230, 16, 37);

                backgroundGameLayout.addLayoutObject(tomato);
                allForestCurrentItems.add(tomato);

                Runnable tomatoFall = tomato.fall(itemHandler, GameObject.GRAVITY, new GameObject.CollisionListener() {
                    @Override
                    public void onCollision(GameObject object1, GameObject object2) {
                        if (GameObject.getCollisionType(object1, object2).contains("top")) {
                            if (!specialCollisionHandler(object1, object2, GameObject.getCollisionType(object1, object2)) && !object2.isCharacter() && !object2.isIngredient()) {
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

                itemHandler.postDelayed(tomatoFall, 5);
            }

            Log.i("EnvironmentSetUp","Forest Items");

        }
        else if(environment.toLowerCase().equals("house")){
            Log.i("EnvironmentSetUp","There should be no items...");
        }
        else if(environment.toLowerCase().equals("swamp")){
            int totalItemCount = 15;
            int radishCount = (int)(Math.random() * totalItemCount) + 1;
            int plantCount = totalItemCount-radishCount;

            for(int i = 0; i < radishCount; i++) {
                Ingredient radish = new Ingredient(this, "Radish", 12, 12,
                        R.drawable.radish, (float) (Math.random() * (tWidth-tWidth/5F) + tWidth/9F),
                        (float) (gameCamera.getTopYPosition()) + 70, 150,243, 222, 255);

                backgroundGameLayout.addLayoutObject(radish);
                allSwampCurrentItems.add(radish);

                Runnable radishFall = radish.fall(itemHandler, GameObject.GRAVITY, new GameObject.CollisionListener() {
                    @Override
                    public void onCollision(GameObject object1, GameObject object2) {
                        if (GameObject.getCollisionType(object1, object2).contains("top")) {
                            if (!specialCollisionHandler(object1, object2, GameObject.getCollisionType(object1, object2)) && !object2.isCharacter() && !object2.isIngredient()) {
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

                itemHandler.postDelayed(radishFall, 5);
            }

            for(int i = 0; i < plantCount; i++) {
                Ingredient plant = new Ingredient(this, "Plant", 10, 10,
                        R.drawable.plant3,(float) (Math.random() * (tWidth-tWidth/5F) + tWidth/9F),
                        (float) (gameCamera.getTopYPosition()) + 70, 150,113, 214, 79);

                backgroundGameLayout.addLayoutObject(plant);
                allSwampCurrentItems.add(plant);

                Runnable plantFall = plant.fall(itemHandler, GameObject.GRAVITY, new GameObject.CollisionListener() {
                    @Override
                    public void onCollision(GameObject object1, GameObject object2) {
                        if (GameObject.getCollisionType(object1, object2).contains("top")) {
                            if (!specialCollisionHandler(object1, object2, GameObject.getCollisionType(object1, object2)) && !object2.isCharacter() && !object2.isIngredient()) {
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

                itemHandler.postDelayed(plantFall, 5);
            }

            Log.i("EnvironmentSetUp","Swamp Items");
        }
        
    }

    // Sets up character controls/interactions
    @SuppressLint("ClickableViewAccessibility")
    private void controllerSetUp(){

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
                            Log.i("MovementCheck", "Running Left");
                        }
                        else{
                            cHandler.postDelayed(leftWalkCamera,0);
                            kirby.getLrHandler().postDelayed(kirby.getAllActions().get("Left Walk"),0);
                            Log.i("MovementCheck", "Walking Left");
                        }

                        isDown = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        view.performClick();
                        if (!isDown) return true;
                        Log.i("MovementCheck", "Still");
                        pauseWalkEffect();
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
                            Log.i("MovementCheck", "Running Right");
                        }
                        else{
                            cHandler.postDelayed(rightWalkCamera,0);
                            kirby.getLrHandler().postDelayed(kirby.getAllActions().get("Right Walk"),0);
                            Log.i("MovementCheck", "Walking Right");
                        }

                        isDown = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        view.performClick();
                        if (!isDown) return true;
                        Log.i("MovementCheck", "Still");
                        pauseWalkEffect();
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
                                    actionButton.setBackgroundResource(android.R.drawable.presence_invisible);
                                    isClick = false;
                                    kirby.getUdHandler().removeCallbacksAndMessages(null);
                                    kirby.getAHandler().removeCallbacksAndMessages(null);
                                    kirby.stopJump();
                                    kirby.getUdHandler().postDelayed(kirby.getAllActions().get("High Jump"), 0);
                                    Log.i("MovementCheck", "Big Jump");
                                    if(SettingsPage.isEffect){
                                        playJumpEffect(R.raw.jump, false);
                                        pauseWalkEffect();
                                    }

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
                                actionButton.setBackgroundResource(android.R.drawable.presence_invisible);
                                kirby.getUdHandler().removeCallbacksAndMessages(null);
                                kirby.getAHandler().removeCallbacksAndMessages(null);
                                kirby.stopJump();
                                kirby.getUdHandler().postDelayed(kirby.getAllActions().get("Jump"),0);
                                Log.i("MovementCheck", "Short Jump");
                                if(SettingsPage.isEffect){
                                    playJumpEffect(R.raw.jump, false);
                                    pauseWalkEffect();
                                }
                                //jumpEffectPlayer.setVolume(25, 25);
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
                                    actionButton.setBackgroundResource(R.drawable.downarrow);
                                    Log.i("MovementCheck", "Start Float");
                                    if(SettingsPage.isEffect){
                                        playJumpEffect(R.raw.jump, false);
                                        pauseWalkEffect();
                                    }
                                }
                                else if(startFloatFinished && jumpCount < 6){
                                    jumpCount++;
                                    shortJump = true;
                                    kirby.getUdHandler().removeCallbacksAndMessages(null);
                                    kirby.getAHandler().removeCallbacksAndMessages(null);
                                    kirby.stopJump();
                                    actionButton.setBackgroundResource(R.drawable.downarrow);
                                    kirby.getUdHandler().postDelayed(kirby.getAllActions().get("Float Jump"),0);
                                    Log.i("MovementCheck", "Float Jump");
                                    if(SettingsPage.isEffect){
                                        playJumpEffect(R.raw.jump, false);
                                        pauseWalkEffect();
                                    }
                                    //jumpEffectPlayer.setVolume(25, 25);
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

            DialogueBox tutorial = new DialogueBox(InGameActivity.this, dialogueBoxLayout, dialogueNameTextView, "Waddle Dee",
                    dialogueTextView, tutorialText, 20,5000, dialoguePortraitImageView, R.drawable.waddledeeportrait,
                    new DialogueBox.DialogueListener() {
                        @Override
                        public void onComplete() {
                            Log.i("Dialogue", "Dialogue complete.");
                        }
                    });

            DialogueBox specialForestSoupHint1 = new DialogueBox(InGameActivity.this, dialogueBoxLayout, dialogueNameTextView, "SpecialDee",
                    dialogueTextView, forestHints[0], 20,3000, dialoguePortraitImageView, R.drawable.waddledeeportrait,
                    new DialogueBox.DialogueListener() {
                        @Override
                        public void onComplete() {
                            Log.i("Dialogue", "Dialogue complete.");
                            if(forestHintNumber < 4) {
                                forestHintNumber++;
                                moveSpecialDee();
                            }
                        }
                    });
            DialogueBox specialForestSoupHint2 = new DialogueBox(InGameActivity.this, dialogueBoxLayout, dialogueNameTextView, "SpecialDee",
                    dialogueTextView, forestHints[1], 20,3000, dialoguePortraitImageView, R.drawable.waddledeeportrait,
                    new DialogueBox.DialogueListener() {
                        @Override
                        public void onComplete() {
                            Log.i("Dialogue", "Dialogue complete.");
                            if(forestHintNumber < 4) {
                                forestHintNumber++;
                                moveSpecialDee();
                            }
                        }
                    });
            DialogueBox specialForestSoupHint3 = new DialogueBox(InGameActivity.this, dialogueBoxLayout, dialogueNameTextView, "SpecialDee",
                    dialogueTextView, forestHints[2], 20,3000, dialoguePortraitImageView, R.drawable.waddledeeportrait,
                    new DialogueBox.DialogueListener() {
                        @Override
                        public void onComplete() {
                            Log.i("Dialogue", "Dialogue complete.");
                            if(forestHintNumber < 4) {
                                forestHintNumber++;
                                moveSpecialDee();
                            }
                        }
                    });
            DialogueBox specialForestSoupHint4 = new DialogueBox(InGameActivity.this, dialogueBoxLayout, dialogueNameTextView, "SpecialDee",
                    dialogueTextView, forestHints[3], 20,3000, dialoguePortraitImageView, R.drawable.waddledeeportrait,
                    new DialogueBox.DialogueListener() {
                        @Override
                        public void onComplete() {
                            Log.i("Dialogue", "Dialogue complete.");
                            if(forestHintNumber < 4) {
                                forestHintNumber++;
                                moveSpecialDee();
                            }
                        }
                    });
            DialogueBox specialForestSoupHint5 = new DialogueBox(InGameActivity.this, dialogueBoxLayout, dialogueNameTextView, "SpecialDee",
                    dialogueTextView, forestHints[4], 20,3000, dialoguePortraitImageView, R.drawable.waddledeeportrait,
                    new DialogueBox.DialogueListener() {
                        @Override
                        public void onComplete() {
                            Log.i("Dialogue", "Dialogue complete.");
                            if(forestHintNumber < 4) {
                                forestHintNumber++;
                                moveSpecialDee();
                            }
                        }
                    });


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
                                if(kirby.isGrounded() && !leftButton.isPressed() && !rightButton.isPressed()) {
                                    isClick = false;
                                    Button debug = findViewById(R.id.debugButton);
                                    if(debug.getVisibility() == View.INVISIBLE) {
                                        debug.setVisibility(View.VISIBLE);
                                    }
                                    else{
                                        debug.setVisibility(View.INVISIBLE);
                                    }
                                }
                            }
                        }, 10000);

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
                                actionButton.setBackgroundResource(android.R.drawable.presence_invisible);
                            }

                            if(kirby.isGrounded()){
                                if(environment.toLowerCase().equals("forest")){
                                    if(isCloseToHouse) {
                                        gameCameraXPosition = -1;
                                        gameCameraYPosition = -1;
                                        gameCameraFixed = true;
                                        cameraSetUp("house");
                                        kirbyXPosition = (centerX - tWidth * 3440/30000F) + (tWidth * 30/15000F);
                                        kirbyYPosition = gameCamera.getBottomYPosition() + 1;
                                        negateDayNightCycle(true);
                                        setLightingTemporarily(255, 255, 255, 255, 255, 255);
                                        actionButton.setBackgroundResource(android.R.drawable.presence_invisible);
                                        environmentSetUp("house");
                                    }
                                    else if(isByTutorialWaddleDee){
                                        // Tutorial info
                                        if(!tutorial.isPlaying()){
                                            if(!tutorial.isDone()) {
                                                tutorial.getTextHandler().postDelayed(tutorial.getPlayDialogue(), 0);
                                                tutorial.showDialogBox();
                                            }
                                            else{
                                                DialogueBox.hideDialogBox();
                                                tutorial.resetDialogue();
                                                tutorial.getDialogueListener().onComplete();
                                            }
                                        }
                                    }
                                    else if(isByForestSoupHintWaddleDee){
                                        switch(forestHintNumber){
                                            case 0:
                                                if(!specialForestSoupHint1.isPlaying()){
                                                    if(!specialForestSoupHint1.isDone()) {
                                                        specialForestSoupHint1.getTextHandler().postDelayed(specialForestSoupHint1.getPlayDialogue(), 0);
                                                        specialForestSoupHint1.showDialogBox();
                                                    }
                                                    else{
                                                        DialogueBox.hideDialogBox();
                                                        specialForestSoupHint1.resetDialogue();
                                                        specialForestSoupHint1.getDialogueListener().onComplete();
                                                    }
                                                }
                                                break;
                                            case 1:
                                                if(!specialForestSoupHint2.isPlaying()){
                                                    if(!specialForestSoupHint2.isDone()) {
                                                        specialForestSoupHint2.getTextHandler().postDelayed(specialForestSoupHint2.getPlayDialogue(), 0);
                                                        specialForestSoupHint2.showDialogBox();
                                                    }
                                                    else{
                                                        DialogueBox.hideDialogBox();
                                                        specialForestSoupHint2.resetDialogue();
                                                        specialForestSoupHint2.getDialogueListener().onComplete();
                                                    }
                                                }
                                                break;
                                            case 2:
                                                if(!specialForestSoupHint3.isPlaying()){
                                                    if(!specialForestSoupHint3.isDone()) {
                                                        specialForestSoupHint3.getTextHandler().postDelayed(specialForestSoupHint3.getPlayDialogue(), 0);
                                                        specialForestSoupHint3.showDialogBox();
                                                    }
                                                    else{
                                                        DialogueBox.hideDialogBox();
                                                        specialForestSoupHint3.resetDialogue();
                                                        specialForestSoupHint3.getDialogueListener().onComplete();
                                                    }
                                                }
                                                break;
                                            case 3:
                                                if(!specialForestSoupHint4.isPlaying()){
                                                    if(!specialForestSoupHint4.isDone()) {
                                                        specialForestSoupHint4.getTextHandler().postDelayed(specialForestSoupHint4.getPlayDialogue(), 0);
                                                        specialForestSoupHint4.showDialogBox();
                                                    }
                                                    else{
                                                        DialogueBox.hideDialogBox();
                                                        specialForestSoupHint4.resetDialogue();
                                                        specialForestSoupHint4.getDialogueListener().onComplete();
                                                    }
                                                }
                                                break;
                                            case 4:
                                                if(!specialForestSoupHint5.isPlaying()){
                                                    if(!specialForestSoupHint5.isDone()) {
                                                        specialForestSoupHint5.getTextHandler().postDelayed(specialForestSoupHint5.getPlayDialogue(), 0);
                                                        specialForestSoupHint5.showDialogBox();
                                                    }
                                                    else{
                                                        DialogueBox.hideDialogBox();
                                                        specialForestSoupHint5.resetDialogue();
                                                        specialForestSoupHint5.getDialogueListener().onComplete();
                                                    }
                                                }
                                                break;
                                            default:
                                                break;

                                        }
                                    }
                                }
                                else if(environment.toLowerCase().equals("house")){
                                    if(isCloseToForestDoor){
                                        gameCameraFixed = true;
                                        cameraSetUp("forest");
                                        kirbyXPosition = tWidth - (tWidth/13F);
                                        kirbyYPosition = (float)(gameCamera.getBottomYPosition()+ 6);
                                        gameCamera.setRightXPosition(tWidth);
                                        gameCameraXPosition = gameCamera.getXPosition();
                                        gameCameraYPosition = gameCamera.getYPosition();
                                        negateDayNightCycle(false);
                                        actionButton.setBackgroundResource(android.R.drawable.presence_invisible);
                                        environmentSetUp("forest");
                                    }
                                    else if(isCloseToSwampDoor){
                                        gameCameraFixed = true;
                                        cameraSetUp("swamp");
                                        kirbyXPosition = 0;
                                        kirbyYPosition = (float)(gameCamera.getBottomYPosition() + 6);
                                        gameCameraXPosition = -1;
                                        gameCameraYPosition = -1;
                                        negateDayNightCycle(false);
                                        actionButton.setBackgroundResource(android.R.drawable.presence_invisible);
                                        environmentSetUp("swamp");
                                    }
                                    else if(isCloseToCauldron){
                                        // soup making here instead...
                                        makeSoup();
                                        isCloseToCauldron = false;
                                        actionButton.setBackgroundResource(android.R.drawable.presence_invisible);
                                    }
                                }
                                else if(environment.toLowerCase().equals("swamp")){
                                    if(isCloseToHouseBack) {
                                        cameraSetUp("house");
                                        kirby.faceDirection("left");
                                        kirbyXPosition = (centerX + tWidth * 3440 / 30000F) - kirby.getObjectWidth() - (tWidth * 30 / 15000F);
                                        kirbyYPosition = gameCamera.getBottomYPosition() + 1;
                                        gameCameraFixed = true;
                                        gameCamera.setRightXPosition(centerX + tWidth * 3440 / 30000F);
                                        gameCameraXPosition = gameCamera.getXPosition();
                                        gameCameraYPosition = gameCamera.getYPosition();
                                        negateDayNightCycle(true);
                                        setLightingTemporarily(255, 255, 255, 255, 255, 255);
                                        actionButton.setBackgroundResource(android.R.drawable.presence_invisible);
                                        environmentSetUp("house");
                                    }
                                }
                            }
                        }

                        isDown = false;
                        thHandler.removeCallbacksAndMessages(null);

                        break;
                }
                return false;
            }

        });

    }

    // INVENTORY/Item/Ingredient Methods:

    public void inventoryPage(View v) {
        if(layout.getVisibility() == View.INVISIBLE) {
            layout.setVisibility(View.VISIBLE);
            removeBttn.setVisibility(View.VISIBLE);
            removeBttn.setClickable(false);
        }
        else{
            closeInventory(v);
        }

        selectedIngredients = new ArrayList<Ingredient>();
        selectedIngredientsIndex = new ArrayList<Integer>();
        inventoryItemClickCounter = new int[15];

        for (int i = 0; i < invImages.length; i++) {
            invImages[i] = (ImageView) findViewById(invRes[i]);
            invImages[i].setImageResource(invDrawables[i]);
            invImages[i].setVisibility(View.VISIBLE);

            int itemNumber = i;
            if(userIngredients[itemNumber] != null) {
                invImages[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // users can remove items
                        inventoryItemClickCounter[itemNumber]++;
                        if (inventoryItemClickCounter[itemNumber] % 2 == 1) {
                            // select item
                            int numSelected = 0;
                            for (int i = 0; i < inventoryItemClickCounter.length; i++) {
                                if (inventoryItemClickCounter[i] % 2 == 1) {
                                    numSelected++;
                                }
                            }
                            if (numSelected > 0) {
                                removeBttn.setClickable(true);
                            }

                            invImages[itemNumber].setImageTintMode(PorterDuff.Mode.SRC_OVER);
                            invImages[itemNumber].setImageTintList(ColorStateList.valueOf(Color.argb(100, 255, 0, 0)));
                            selectedIngredients.add(userIngredients[itemNumber]);
                            selectedIngredientsIndex.add(itemNumber);

                        } else {
                            // deselect item
                            int numSelected = 0;
                            for (int i = 0; i < inventoryItemClickCounter.length; i++) {
                                if (inventoryItemClickCounter[i] % 2 == 1) {
                                    numSelected++;
                                }
                            }
                            // hmmmmm
                            if (numSelected < 1) {
                                removeBttn.setClickable(false);
                            }

                            invImages[itemNumber].setImageTintMode(PorterDuff.Mode.OVERLAY);
                            invImages[itemNumber].setImageTintList(ColorStateList.valueOf(Color.argb(0, 100, 100, 100)));
                            selectedIngredients.remove(userIngredients[itemNumber]);
                            selectedIngredientsIndex.remove((Integer) itemNumber);
                        }
                    }
                });
            }
            else {
                invImages[i].setOnClickListener(null);
            }
        }
    }

    public void closeInventory(View v) {
        layout.setVisibility(View.INVISIBLE);
        makeBttn.setVisibility(View.INVISIBLE);
        removeBttn.setVisibility(View.INVISIBLE);

        for (int i = 0; i < invImages.length; i++) {
            invImages[i].setImageTintMode(PorterDuff.Mode.OVERLAY);
            invImages[i].setImageTintList(ColorStateList.valueOf(Color.argb(0, 100, 100, 100)));
        }
    }

    public void removeItem(View view) {
        for(int i : selectedIngredientsIndex){
            removeIngredientFromInventory(i);
        }
        selectedIngredients = new ArrayList<Ingredient>();
        selectedIngredientsIndex = new ArrayList<Integer>();
    }

    // remove all items from environment
    private void removeAllItems(){
        if(environment.toLowerCase().equals("forest")) {
            if (allForestCurrentItems != null) {
                int i = 0;
                for (Ingredient item : allForestCurrentItems) {
                    i++;
                    Log.i("ItemGeneration", String.valueOf(i) + ": " + item.getName());
                    itemHandler.postDelayed(item.collected(itemHandler), 0);
                }

                allForestCurrentItems = new ArrayList<Ingredient>();
            }
        }
        else if(environment.toLowerCase().equals("swamp")) {
            if (allSwampCurrentItems != null) {
                int i = 0;
                for (Ingredient item : allSwampCurrentItems) {
                    i++;
                    Log.i("ItemGeneration", String.valueOf(i) + ": " + item.getName());
                    itemHandler.postDelayed(item.collected(itemHandler), 0);
                }

                allSwampCurrentItems = new ArrayList<Ingredient>();
            }
        }
    }

    private void collectIngredient(Ingredient ingredient){
        Button inv = (Button) findViewById(R.id.invButton);
        String itemName = ingredient.getName();

        int itemCount = -1;
        for(int i = 0; i < 15; i++){
            if(userIngredients[i] == null){
                itemCount = i;
                break;
            }
        }

        if(itemCount !=-1) {
            ingredient.setCollected(true);
            Runnable collectAnimation = ingredient.collected(itemHandler);
            itemHandler.postDelayed(collectAnimation, 0);
            if(environment.toLowerCase().equals("forest")) {
                allForestCurrentItems.remove(ingredient);
            }
            else if(environment.toLowerCase().equals("swamp")){
                allSwampCurrentItems.remove(ingredient);
            }
            switch(itemName) {
                case "Carrot":
                    invDrawables[itemCount] = R.drawable.carrot;
                    break;

                case "Tomato":
                    invDrawables[itemCount] = R.drawable.tomato;
                    break;

                case "Mushroom":
                    invDrawables[itemCount] = R.drawable.mushroom;
                    break;

                case "Radish":
                    invDrawables[itemCount] = R.drawable.radish;
                    break;

                case "Plant":
                    invDrawables[itemCount] = R.drawable.plant3;
                    break;
            }

            invImages[itemCount].setImageResource(invDrawables[itemCount]);
            itemNames[itemCount] = itemName;
            userIngredients[itemCount] = ingredientKey.get(itemName);

            int count = 0;
            for(int i = 0; i < 15; i++){
                if(userIngredients[i] != null){
                    count++;
                }
            }

            if(count == 15){
                Log.i("Items","Max items collected");
                inv.setBackgroundResource(R.drawable.inventory_full);
            }

        }

    }

    private void removeIngredientFromInventory(int index){
        Button inv = (Button) findViewById(R.id.invButton);
        invDrawables[index] = android.R.color.transparent;
        invImages[index].setImageResource(invDrawables[index]);
        invImages[index].setImageTintMode(PorterDuff.Mode.OVERLAY);
        invImages[index].setImageTintList(ColorStateList.valueOf(Color.argb(0, 100, 100, 100)));
        invImages[index].setOnClickListener(null);
        itemNames[index] = "";
        userIngredients[index] = null;
        inv.setBackgroundResource(R.drawable.inventory_icon);
    }

    // SOUP MAKING METHODS:

    private void makeSoup(){
        if(isCloseToCauldron) {
            selectedIngredients = new ArrayList<Ingredient>();
            selectedIngredientsIndex = new ArrayList<Integer>();

            makeBttn.setVisibility(View.VISIBLE);
            makeBttn.setClickable(false);

            inventoryItemClickCounter = new int[15];

            layout.setVisibility(View.VISIBLE);

            for (int i = 0; i < invImages.length; i++) {
                invImages[i] = (ImageView) findViewById(invRes[i]);
                invImages[i].setImageResource(invDrawables[i]);
                invImages[i].setVisibility(View.VISIBLE);

                int itemNumber = i;
                if (userIngredients[itemNumber] != null) {
                    invImages[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            inventoryItemClickCounter[itemNumber]++;
                            if (inventoryItemClickCounter[itemNumber] % 2 == 1) {
                                // select item
                                int numSelected = 0;
                                for (int i = 0; i < inventoryItemClickCounter.length; i++) {
                                    if (inventoryItemClickCounter[i] % 2 == 1) {
                                        numSelected++;
                                    }
                                }
                                if (numSelected >= 3) {
                                    makeBttn.setClickable(true);
                                }

                                invImages[itemNumber].setImageTintMode(PorterDuff.Mode.SRC_OVER);
                                invImages[itemNumber].setImageTintList(ColorStateList.valueOf(Color.argb(80, 0, 255, 0)));
                                selectedIngredients.add(userIngredients[itemNumber]);
                                selectedIngredientsIndex.add(itemNumber);

                            } else {
                                // deselect item
                                int numSelected = 0;
                                for (int i = 0; i < inventoryItemClickCounter.length; i++) {
                                    if (inventoryItemClickCounter[i] % 2 == 1) {
                                        numSelected++;
                                    }
                                }
                                if (numSelected < 3) {
                                    makeBttn.setClickable(false);
                                }

                                invImages[itemNumber].setImageTintMode(PorterDuff.Mode.OVERLAY);
                                invImages[itemNumber].setImageTintList(ColorStateList.valueOf(Color.argb(0, 100, 100, 100)));
                                selectedIngredients.remove(userIngredients[itemNumber]);
                                selectedIngredientsIndex.remove((Integer) itemNumber);
                            }
                        }
                    });
                }
            }
        }
    }

    public void createSoup(View view){
        String soupText = "";
        Soup createdSoup = new Soup(selectedIngredients);
        boolean alreadyHave = false;
        for(int i = 0; i < userSoups.size(); i++){
            if (createdSoup.getSoupName().equals(userSoups.get(i).getSoupName())) {
                if(createdSoup.getStarRank() > userSoups.get(i).getStarRank()) {
                    userSoups.set(i, createdSoup);
                }
                alreadyHave = true;
                break;

            }
        }

        if(!alreadyHave){
            userSoups.add(createdSoup);
            soupNum++;
            soupText = "Yay!! You made a " + String.valueOf(createdSoup.getStarRank()) +" star " + createdSoup.getSoupName()
                    +" for the first time! Check out your catalog for more details!";
        }
        else{
            soupText = "You made a " + String.valueOf(createdSoup.getStarRank()) +" star " + createdSoup.getSoupName()
                    +"! Check out your catalog for more details!";
        }
        // Whatever happens when soup is made:
        for(int i : selectedIngredientsIndex){
            removeIngredientFromInventory(i);
        }
        closeInventory(view);



        DialogueBox soupMessage = new DialogueBox(InGameActivity.this, dialogueBoxLayout, dialogueNameTextView, "Soup!",
                dialogueTextView, soupText, 20, 5000, dialoguePortraitImageView, R.drawable.soupbase,
                new DialogueBox.DialogueListener() {
                    @Override
                    public void onComplete() {
                        Log.i("Soup Dialogue", "Dialogue complete.");
                        createdSoup.stopShowingSoup(dialoguePortraitImageView);
                    }
                });

        soupMessage.getTextHandler().postDelayed(soupMessage.getPlayDialogue(), 0);
        createdSoup.showSoup(dialoguePortraitImageView);
        soupMessage.showDialogBox();


        if(kirby.isGrounded() && !leftButton.isPressed() && !rightButton.isPressed()) {
            kirby.getAHandler().removeCallbacksAndMessages(null);

            kirby.getAHandler().postDelayed(kirby.getAllActions().get("Dance " + String.valueOf((int)(Math.random() * 8 + 1))),0);
        }
    }

    // DAY NIGHT CYCLE METHODS:

    public void dayNightCycle(){
        Runnable lighting = new Runnable() {

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
                        if(environment.toLowerCase().equals("forest")) {
                            if (forestHintNumber == 2 || forestHintNumber == 4) {
                                allNPCs.get("Waddle Dee 1").fadeIn(backgroundGameLayout,
                                        new GameObject.FadeCompletionListener() {
                                            @Override
                                            public void fadeOnComplete() {
                                                isSpecialDeePresent = true;
                                            }
                                        });
                            }
                        }
                    }

                    if(!itemsAreSet) {
                        removeAllItems();
                        newItemSetUp(environment);
                        itemsAreSet = true;
                    }

                    rHandler.postDelayed(this,3667/4);
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
                        itemsAreSet = false;
                        if(!itemsAreSet) {
                            removeAllItems();
                            newItemSetUp(environment);
                            itemsAreSet = true;
                        }
                        bA = 255;
                        bR = 255;
                        bG = 255;
                        bB = 255;

                        oA = 255;
                        oR = 255;
                        oG = 255;
                        oB = 255;
                    }

                    rHandler.postDelayed(this,3667/4);
                }
                else if(timeOfDay.toLowerCase().equals("sunset")){
                    if(toColor(255,80,80,1,40,40,40,1)){
                        timeOfDay = "Night";
                        if(environment.toLowerCase().equals("forest")){
                            if(forestHintNumber == 1) {
                                allNPCs.get("Waddle Dee 1").fadeIn(backgroundGameLayout,
                                        new GameObject.FadeCompletionListener() {
                                            @Override
                                            public void fadeOnComplete() {
                                                isSpecialDeePresent = true;
                                            }
                                        });
                            }
                            else if(forestHintNumber == 2) {
                                allNPCs.get("Waddle Dee 1").fadeOut(backgroundGameLayout,
                                        new GameObject.FadeCompletionListener() {
                                            @Override
                                            public void fadeOnComplete() {
                                                isSpecialDeePresent = false;
                                                isByForestSoupHintWaddleDee = false;
                                            }
                                        });
                            }
                        }
                    }
                    rHandler.postDelayed(this,280/4);
                }
                else if(timeOfDay.toLowerCase().equals("night")){
                    if(toColor(100,100,120,1,35,35,35,1)){
                        timeOfDay = "Sunrise1";
                        if(environment.toLowerCase().equals("forest")){
                            if(forestHintNumber == 1) {
                                allNPCs.get("Waddle Dee 1").fadeOut(backgroundGameLayout,
                                        new GameObject.FadeCompletionListener() {
                                            @Override
                                            public void fadeOnComplete() {
                                                isSpecialDeePresent = false;
                                                isByForestSoupHintWaddleDee = false;
                                            }
                                        });
                            }
                            else if(forestHintNumber == 3) {
                                allNPCs.get("Waddle Dee 1").fadeIn(backgroundGameLayout,
                                        new GameObject.FadeCompletionListener() {
                                            @Override
                                            public void fadeOnComplete() {
                                                isSpecialDeePresent = true;
                                            }
                                        });
                            }
                        }
                    }
                    rHandler.postDelayed(this,690/4);
                }
                else if(timeOfDay.toLowerCase().equals("sunrise1")){
                    if(toColor(254,108,184,2,100,100,100,1)){
                        timeOfDay = "Sunrise2";
                    }
                    rHandler.postDelayed(this,625/4);
                }
                else if(timeOfDay.toLowerCase().equals("sunrise2")){
                    if(toColor(255,255,255,1,255,255,255,1)){
                        bA = 0;
                        oA = 0;
                        bB = 190;
                        oB = 190;
                        timeOfDay = "Morning";
                        itemsAreSet = false;
                        if(environment.toLowerCase().equals("forest")) {
                            if (forestHintNumber == 3) {
                                allNPCs.get("Waddle Dee 1").fadeOut(backgroundGameLayout,
                                        new GameObject.FadeCompletionListener() {
                                            @Override
                                            public void fadeOnComplete() {
                                                isSpecialDeePresent = false;
                                                isByForestSoupHintWaddleDee = false;
                                            }
                                        });
                            }
                        }
                    }
                    rHandler.postDelayed(this,231/4);
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

                if(!negateDayNight) {
                    GameLayout.darkenBackgroundLighting(Color.argb(255, bR, bG, bB));
                    GameLayout.darkenObjectLighting(Color.argb(255, oR, oG, oB));
                }
                return false;
            }
        };

        rHandler.postDelayed(lighting,0);
    }

    public void negateDayNightCycle(boolean negate){
        this.negateDayNight = negate;
    }

    public void setLightingTemporarily(int br, int bg, int bb, int or, int og, int ob){
        GameLayout.darkenBackgroundLighting(Color.argb(255, br, bg, bb));
        GameLayout.darkenObjectLighting(Color.argb(255, or, og, ob));
    }

    // AUDIO METHODS:

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
            mediaPlayer.setVolume(.25f,.25f);
            mediaPlayer.setLooping(true);

//            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                @Override
//                public void onCompletion(MediaPlayer mediaPlayer) {
//                    randomSong();
//                }
//            });

            // below line is use to display a toast message.
            //Toast.makeText(this, "Audio started playing..", Toast.LENGTH_SHORT).show();
            Log.i("Sai", "Playing is: " + mediaPlayer.isPlaying());
        }
        else{
            //Toast.makeText(this, "Audio is already playing", Toast.LENGTH_SHORT).show();
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
                //Toast.makeText(this, "Audio has been paused", Toast.LENGTH_SHORT).show();
            } else {
                // this method is called when media
                // player is not playing.
                //Toast.makeText(this, "Audio has not played", Toast.LENGTH_SHORT).show();
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

    public void playWalkEffect(int rawSound, boolean shouldLoop){
        if(SettingsPage.isEffect) {
            if (walkEffectPlayer == null) {
                // initializing media player
                walkEffectPlayer = new MediaPlayer();

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
                if(environment.toLowerCase().equals("house")){
                    walkEffectPlayer = MediaPlayer.create(this, R.raw.runningonwood);
                }
                else{
                    walkEffectPlayer = MediaPlayer.create(this, R.raw.runningongrass);
                }

                walkEffectPlayer.start();
                walkEffectPlayer.setLooping(shouldLoop);

                // below line is use to display a toast message.
//            Toast.makeText(this, "Audio started playing..", Toast.LENGTH_SHORT).show();
//            Log.i("Sai", "Playing is: " + walkEffectPlayer.isPlaying());
            }
//        else if(walkEffectPlayer != null){
//            pauseWalkEffect();
//            walkEffectPlayer = new MediaPlayer();
//
//            // below line is use to set the audio
//            // stream type for our media player.
////            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//
//            // below line is use to set our
////            // url to our media player.
////            try {
////                mediaPlayer.setDataSource(audioUrl);
////                // below line is use to prepare
////                // and start our media player.
////                mediaPlayer.prepareAsync();
////                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
////                    @Override
////                    public void onPrepared(MediaPlayer mediaPlayer) {
////                        mediaPlayer.start();
////                    }
////                });
////
////
////            } catch (IOException e) {
////                e.printStackTrace();
////            }
//
//            walkEffectPlayer = MediaPlayer.create(this, rawSound);
//            walkEffectPlayer.start();
//            walkEffectPlayer.setLooping(shouldLoop);
////            while(!isGrounded){
////                pauseWalkEffect();
////            }
//        }
//            else {
//                //Toast.makeText(this, "Audio is already playing", Toast.LENGTH_SHORT).show();
//            }
        }

//        Log.i("Sai", "Playing is: " + walkEffectPlayer.isPlaying());
    }

    public void pauseWalkEffect() {
        if (walkEffectPlayer != null) {
            if (walkEffectPlayer.isPlaying()) {
                // pausing the media player if media player
                // is playing we are calling below line to
                // stop our media player.
                walkEffectPlayer.stop();
                walkEffectPlayer.reset();
                walkEffectPlayer.release();
                walkEffectPlayer = null;

                // below line is to display a message
                // when media player is paused.
                //Toast.makeText(this, "Audio has been paused", Toast.LENGTH_SHORT).show();
            } else {
                // this method is called when media
                // player is not playing.
                //Toast.makeText(this, "Audio has not played", Toast.LENGTH_SHORT).show();
            }

        }
    }

    public void playJumpEffect(int rawSound, boolean shouldLoop){
        if(SettingsPage.isEffect) {
            if (jumpEffectPlayer == null) {
                // initializing media player
                jumpEffectPlayer = new MediaPlayer();

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

                jumpEffectPlayer = MediaPlayer.create(this, rawSound);
                jumpEffectPlayer.start();
                jumpEffectPlayer.setLooping(shouldLoop);

                // below line is use to display a toast message.
//            Toast.makeText(this, "Audio started playing..", Toast.LENGTH_SHORT).show();
//            Log.i("Sai", "Playing is: " + walkEffectPlayer.isPlaying());
            } else if (jumpEffectPlayer != null) {
                pauseJumpEffect();
                pauseWalkEffect();
                jumpEffectPlayer = new MediaPlayer();

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

                jumpEffectPlayer = MediaPlayer.create(this, rawSound);
                jumpEffectPlayer.start();
                jumpEffectPlayer.setLooping(shouldLoop);
            } else {
                //Toast.makeText(this, "Audio is already playing", Toast.LENGTH_SHORT).show();
            }
        }
//        Log.i("Sai", "Playing is: " + walkEffectPlayer.isPlaying());
    }

    public void pauseJumpEffect() {
        if (jumpEffectPlayer != null) {
            if (jumpEffectPlayer.isPlaying()) {
                // pausing the media player if media player
                // is playing we are calling below line to
                // stop our media player.
                jumpEffectPlayer.stop();
                jumpEffectPlayer.reset();
                jumpEffectPlayer.release();
                jumpEffectPlayer = null;

                // below line is to display a message
                // when media player is paused.
                //Toast.makeText(this, "Audio has been paused", Toast.LENGTH_SHORT).show();
            } else {
                // this method is called when media
                // player is not playing.
                //Toast.makeText(this, "Audio has not played", Toast.LENGTH_SHORT).show();
            }

        }
    }

    // CHANGE ACTIVITY METHODS:

    public void catalogPage(View v) {
        Intent intent = new Intent(this, CatalogPage.class);
        startActivity(intent);
    }

    public void settingPage(View view) {
        Intent intent = new Intent(this, SettingsPage.class);
        startActivity(intent);
    }

    // SAVE/LOAD DATA RELATED METHODS:

    private void saveData() {

        GameObject.displayHitBoxes = false;
        for(GameObject object : collisionGameLayout.getLayoutObjects()){
            object.getHitBox().stopShowingHitBox();
        }
        for(GameObject object: backgroundGameLayout.getLayoutObjects()){
            object.getHitBox().stopShowingHitBox();
        }
        for(GameObject object: foregroundGameLayout.getLayoutObjects()){
            object.getHitBox().stopShowingHitBox();
        }

        backgroundGameLayout.removeAllLayoutObjects();
        collisionGameLayout.removeAllLayoutObjects();
        foregroundGameLayout.removeAllLayoutObjects();
        itemHandler.removeCallbacksAndMessages(null);
        eHandler.removeCallbacksAndMessages(null);
        cHandler.removeCallbacksAndMessages(null);
        rHandler.removeCallbacksAndMessages(null);

        kirby.stopFall();
        kirby.stopAction();
        kirby.stopJump();
        kirby.setStopMoving(true);

        kirby.getUdHandler().removeCallbacksAndMessages(null);
        kirby.getLrHandler().removeCallbacksAndMessages(null);
        kirby.getAHandler().removeCallbacksAndMessages(null);


        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json;

        editor.putBoolean(IS_FLOATING, isFloating);

        editor.putBoolean(START_FLOAT_FINISHED, startFloatFinished);

        editor.putInt(JUMP_COUNT, jumpCount);

        editor.putBoolean(IS_GROUNDED, kirby.isGrounded());

        editor.putString(TIME_OF_DAY, timeOfDay);
        editor.putInt(LIGHTING_BA,bA);
        editor.putInt(LIGHTING_BR,bR);
        editor.putInt(LIGHTING_BG,bG);
        editor.putInt(LIGHTING_BB,bB);
        editor.putInt(LIGHTING_OA,oA);
        editor.putInt(LIGHTING_OR,oR);
        editor.putInt(LIGHTING_OG,oG);
        editor.putInt(LIGHTING_OB,oB);
        editor.putBoolean(NEGATE_DAY_NIGHT, negateDayNight);

        editor.putString(ENVIRONMENT, environment);
        editor.putBoolean(ITEMS_ARE_SET, itemsAreSet);

        editor.putInt(SOUP_NUM, soupNum);

        gameCameraXPosition = gameCamera.getXPosition();
        gameCameraYPosition = gameCamera.getYPosition();
        gameCameraFixed = gameCamera.isFixedPosition();

        editor.putFloat(GAME_CAMERA_XPOSITION,gameCameraXPosition);
        editor.putFloat(GAME_CAMERA_YPOSITION,gameCameraYPosition);
        editor.putBoolean(GAME_CAMERA_FIXED, gameCameraFixed);

        kirbyXPosition = kirby.getXPosition();
        kirbyYPosition = kirby.getYPosition();

        editor.putFloat(KIRBY_XPOSITION, kirbyXPosition);
        editor.putFloat(KIRBY_YPOSITION, kirbyYPosition);

        specialDeeXPosition = allNPCs.get("Waddle Dee 1").getXPosition();
        specialDeeYPosition = allNPCs.get("Waddle Dee 1").getYPosition();

        editor.putFloat(SPECIALDEE_XPOSITION, specialDeeXPosition);
        editor.putFloat(SPECIALDEE_YPOSITION, specialDeeYPosition);
        editor.putBoolean(IS_SPECIALDEE_PRESENT, isSpecialDeePresent);
        editor.putString(SPECIALDEE_DIRECTION, specialDeeDirection);
        editor.putInt(FOREST_HINT_NUMBER,forestHintNumber);



        gson = new Gson();
        json = gson.toJson(invDrawables);
        editor.putString(INV_DRAWABLES,json);

        gson = new Gson();
        json = gson.toJson(itemNames);
        editor.putString(ITEM_NAMES,json);

        forestCloudCoordinates = new float[20][3];
        for(int i = 0; i < 20; i++){
            forestCloudCoordinates[i][0] = forestClouds.get(i).getXPosition();
            forestCloudCoordinates[i][1] = forestClouds.get(i).getYPosition();
            forestCloudCoordinates[i][2] = forestClouds.get(i).getObjectWidth()/180F;
        }

        gson = new Gson();
        json = gson.toJson(forestCloudCoordinates);
        editor.putString(FOREST_CLOUD_COORDINATES, json);

        int j = 0;
        allForestCurrentItemLocations = new float[200];
        allForestCurrentItemNames = new ArrayList<String>();
        for(Ingredient item : allForestCurrentItems){
            allForestCurrentItemNames.add(item.getName());
            allForestCurrentItemLocations[2 * j] = item.getXPosition();
            allForestCurrentItemLocations[2 * j + 1] = item.getYPosition();
            j++;
        }

        gson = new Gson();
        json = gson.toJson(allForestCurrentItemNames);
        editor.putString(ALL_FOREST_CURRENT_ITEM_NAMES, json);

        gson = new Gson();
        json = gson.toJson(allForestCurrentItemLocations);
        editor.putString(ALL_FOREST_CURRENT_ITEM_LOCATIONS, json);

        int k = 0;
        allSwampCurrentItemLocations = new float[200];
        allSwampCurrentItemNames = new ArrayList<String>();
        for(Ingredient item : allSwampCurrentItems){
            allSwampCurrentItemNames.add(item.getName());
            allSwampCurrentItemLocations[2 * k] = item.getXPosition();
            allSwampCurrentItemLocations[2 * k + 1] = item.getYPosition();
            k++;
        }

        gson = new Gson();
        json = gson.toJson(allSwampCurrentItemNames);
        editor.putString(ALL_SWAMP_CURRENT_ITEM_NAMES, json);

        gson = new Gson();
        json = gson.toJson(allSwampCurrentItemLocations);
        editor.putString(ALL_SWAMP_CURRENT_ITEM_LOCATIONS, json);

        soupIngredients = new ArrayList<String>();
        soupRanks = new int[500];
        int i = 0;
        for(Soup soup : userSoups){
            String temp = "";
            for(Ingredient ingredient : soup.getIngredients()){
                temp += ingredient.getName() + "/";
            }
            String ingredientList = temp.substring(0,temp.length());
            soupIngredients.add(ingredientList);
            soupRanks[i] = soup.getStarRank();
            i++;
        }

        gson = new Gson();
        json = gson.toJson(soupIngredients);
        editor.putString(SOUP_INGREDIENTS, json);

        gson = new Gson();
        json = gson.toJson(soupRanks);
        editor.putString(SOUP_RANKS, json);


        editor.commit();

        editor.apply();

        //Toast.makeText(this, "Data Saved", Toast.LENGTH_SHORT).show();
        Log.i("Sai", "Saved data");
    }

    public void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        timeOfDay = sharedPreferences.getString(TIME_OF_DAY, "Morning");
        bA = sharedPreferences.getInt(LIGHTING_BA, 0);
        bR = sharedPreferences.getInt(LIGHTING_BR, 255);
        bG = sharedPreferences.getInt(LIGHTING_BG, 255);
        bB = sharedPreferences.getInt(LIGHTING_BB, 190);
        oA = sharedPreferences.getInt(LIGHTING_OA, 0);
        oR = sharedPreferences.getInt(LIGHTING_OR, 255);
        oG = sharedPreferences.getInt(LIGHTING_OG, 255);
        oB = sharedPreferences.getInt(LIGHTING_OB, 190);
        negateDayNight = sharedPreferences.getBoolean(NEGATE_DAY_NIGHT,false);


        isFloating = sharedPreferences.getBoolean(IS_FLOATING, false);
        startFloatFinished = sharedPreferences.getBoolean(START_FLOAT_FINISHED, false);
        jumpCount = sharedPreferences.getInt(JUMP_COUNT, 0);
        gameCameraXPosition = sharedPreferences.getFloat(GAME_CAMERA_XPOSITION, -1);
        gameCameraYPosition = sharedPreferences.getFloat(GAME_CAMERA_YPOSITION, -1);
        gameCameraFixed = sharedPreferences.getBoolean(GAME_CAMERA_FIXED, true);
        isGrounded = sharedPreferences.getBoolean(IS_GROUNDED, false);
        SettingsPage.isRight = sharedPreferences.getBoolean(SettingsPage.SWITCH1, false);

        environment = sharedPreferences.getString(ENVIRONMENT,"Forest");

        itemsAreSet = sharedPreferences.getBoolean(ITEMS_ARE_SET, false);

        kirbyXPosition = sharedPreferences.getFloat(KIRBY_XPOSITION, 0);

        kirbyYPosition = sharedPreferences.getFloat(KIRBY_YPOSITION, TitleActivity.HEIGHT/(2 * TitleActivity.DENSITY));

        specialDeeXPosition = sharedPreferences.getFloat(SPECIALDEE_XPOSITION, tWidth/4);

        specialDeeYPosition = sharedPreferences.getFloat(SPECIALDEE_YPOSITION, -1);

        isSpecialDeePresent = sharedPreferences.getBoolean(IS_SPECIALDEE_PRESENT, true);

        specialDeeDirection = sharedPreferences.getString(SPECIALDEE_DIRECTION, "Right");

        forestHintNumber = sharedPreferences.getInt(FOREST_HINT_NUMBER, 0);

        soupNum = sharedPreferences.getInt(SOUP_NUM,0);


        Gson gson = new Gson();
        String json = sharedPreferences.getString(INV_DRAWABLES, "");
        if(!json.equals("")){
            invDrawables = gson.fromJson(json, int[].class);
        }
        else{
            invDrawables = new int[15];
        }

        gson = new Gson();
        json = sharedPreferences.getString(ITEM_NAMES, "");
        if(!json.equals("")){
            itemNames = gson.fromJson(json, String[].class);
        }
        else{
            itemNames = new String[15];
        }

        gson = new Gson();
        json = sharedPreferences.getString(FOREST_CLOUD_COORDINATES, "");
        if(!json.equals("")){
            forestCloudCoordinates = gson.fromJson(json, float[][].class);
        }
        else{
            forestCloudCoordinates = null;
        }

        gson = new Gson();
        json = sharedPreferences.getString(ALL_FOREST_CURRENT_ITEM_NAMES, "");
        if(!json.equals("")){
            allForestCurrentItemNames = gson.fromJson(json, ArrayList.class);
        }
        else{
            allForestCurrentItemNames = new ArrayList<String>();
        }

        gson = new Gson();
        json = sharedPreferences.getString(ALL_FOREST_CURRENT_ITEM_LOCATIONS, "");
        if(!json.equals("")){
            allForestCurrentItemLocations = gson.fromJson(json, float[].class);
        }
        else{
            allForestCurrentItemLocations = new float[200];
        }

        gson = new Gson();
        json = sharedPreferences.getString(ALL_SWAMP_CURRENT_ITEM_NAMES, "");
        if(!json.equals("")){
            allSwampCurrentItemNames = gson.fromJson(json, ArrayList.class);
        }
        else{
            allSwampCurrentItemNames = new ArrayList<String>();
        }

        gson = new Gson();
        json = sharedPreferences.getString(ALL_SWAMP_CURRENT_ITEM_LOCATIONS, "");
        if(!json.equals("")){
            allSwampCurrentItemLocations = gson.fromJson(json, float[].class);
        }
        else{
            allSwampCurrentItemLocations = new float[200];
        }

        gson = new Gson();
        json = sharedPreferences.getString(SOUP_INGREDIENTS, "");
        if(!json.equals("")){
            soupIngredients = gson.fromJson(json, ArrayList.class);
        }
        else{
            soupIngredients = new ArrayList<String>();
        }

        gson = new Gson();
        json = sharedPreferences.getString(SOUP_RANKS, "");
        if(!json.equals("")){
            soupRanks = gson.fromJson(json, int[].class);
        }
        else{
            soupRanks = new int[500];
        }

    }

    public void updateViews(){
        if(isOn){
            playAudio();
        }
        else{
            pauseAudio();
        }

        if(SettingsPage.isRight){
            LayoutParams lp = new LayoutParams((int)(75*(TitleActivity.DENSITY)),(int)(75*(TitleActivity.DENSITY)));
            lp.endToEnd =ConstraintSet.PARENT_ID;
            lp.topToTop =ConstraintSet.PARENT_ID;
            lp.leftToLeft =ConstraintSet.PARENT_ID;
            lp.rightToRight =ConstraintSet.PARENT_ID;
            lp.setMargins((int)(400*TitleActivity.DENSITY),(int)(285*TitleActivity.DENSITY),(int)(50*TitleActivity.DENSITY),0);
            rightButton.setLayoutParams(lp);

            lp = new LayoutParams((int)(75*(TitleActivity.DENSITY)),(int)(75*(TitleActivity.DENSITY)));
            lp.endToEnd =ConstraintSet.PARENT_ID;
            lp.topToTop =ConstraintSet.PARENT_ID;
            lp.leftToLeft =ConstraintSet.PARENT_ID;
            lp.rightToRight =ConstraintSet.PARENT_ID;
            lp.setMargins((int)(400*TitleActivity.DENSITY),(int)(285*TitleActivity.DENSITY),(int)(150*TitleActivity.DENSITY),0);
            leftButton.setLayoutParams(lp);

            lp = new LayoutParams((int)(75*(TitleActivity.DENSITY)),(int)(75*(TitleActivity.DENSITY)));
            lp.endToEnd =ConstraintSet.PARENT_ID;
            lp.topToTop =ConstraintSet.PARENT_ID;
            lp.leftToLeft =ConstraintSet.PARENT_ID;
            lp.rightToRight =ConstraintSet.PARENT_ID;
            lp.setMargins((int)(25*TitleActivity.DENSITY),(int)(285*TitleActivity.DENSITY),(int)(600*TitleActivity.DENSITY),0);
            jumpButton.setLayoutParams(lp);

            lp = new LayoutParams((int)(60*(TitleActivity.DENSITY)),(int)(60*(TitleActivity.DENSITY)));
            lp.endToEnd =ConstraintSet.PARENT_ID;
            lp.topToTop =ConstraintSet.PARENT_ID;
            lp.leftToLeft =ConstraintSet.PARENT_ID;
            lp.rightToRight =ConstraintSet.PARENT_ID;
            lp.setMargins((int)(100*TitleActivity.DENSITY),(int)(290*TitleActivity.DENSITY),(int)(525*TitleActivity.DENSITY),0);
            actionButton.setLayoutParams(lp);
        }
        else{
            LayoutParams lp = new LayoutParams((int)(75*(TitleActivity.DENSITY)),(int)(75*(TitleActivity.DENSITY)));
            lp.endToEnd =ConstraintSet.PARENT_ID;
            lp.topToTop =ConstraintSet.PARENT_ID;
            lp.leftToLeft =ConstraintSet.PARENT_ID;
            lp.rightToRight =ConstraintSet.PARENT_ID;
            lp.setMargins((int)(400*TitleActivity.DENSITY),(int)(285*TitleActivity.DENSITY),(int)(50*TitleActivity.DENSITY),0);
            jumpButton.setLayoutParams(lp);

            lp = new LayoutParams((int)(60*(TitleActivity.DENSITY)),(int)(60*(TitleActivity.DENSITY)));
            lp.endToEnd =ConstraintSet.PARENT_ID;
            lp.topToTop =ConstraintSet.PARENT_ID;
            lp.leftToLeft =ConstraintSet.PARENT_ID;
            lp.rightToRight =ConstraintSet.PARENT_ID;
            lp.setMargins((int)(400*TitleActivity.DENSITY),(int)(290*TitleActivity.DENSITY),(int)(150*TitleActivity.DENSITY),0);
            actionButton.setLayoutParams(lp);

            lp = new LayoutParams((int)(75*(TitleActivity.DENSITY)),(int)(75*(TitleActivity.DENSITY)));
            lp.endToEnd =ConstraintSet.PARENT_ID;
            lp.topToTop =ConstraintSet.PARENT_ID;
            lp.leftToLeft =ConstraintSet.PARENT_ID;
            lp.rightToRight =ConstraintSet.PARENT_ID;
            lp.setMargins((int)(25*TitleActivity.DENSITY),(int)(285*TitleActivity.DENSITY),(int)(600*TitleActivity.DENSITY),0);
            leftButton.setLayoutParams(lp);

            lp = new LayoutParams((int)(75*(TitleActivity.DENSITY)),(int)(75*(TitleActivity.DENSITY)));
            lp.endToEnd =ConstraintSet.PARENT_ID;
            lp.topToTop =ConstraintSet.PARENT_ID;
            lp.leftToLeft =ConstraintSet.PARENT_ID;
            lp.rightToRight =ConstraintSet.PARENT_ID;
            lp.setMargins((int)(125*TitleActivity.DENSITY),(int)(285*TitleActivity.DENSITY),(int)(500*TitleActivity.DENSITY),0);
            rightButton.setLayoutParams(lp);
        }

        Log.i("SetUp","Initial Set Up");

        initialCameraSetUp();
        initialCharacterSetUp();
        initialInventorySetUp();
        initialSoupSetUp();
        initialEnvironmentSetUp();
        dayNightCycle();
    }

    protected void onRestart() {
        super.onRestart();
//         loadData();
//         updateViews();

        Log.i("Sai", "Restart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        loadData();
        updateViews();

        Log.i("Sai", "Resume");
    }

    @Override
    protected void onStop() {
        super.onStop();

//        saveData();

        Log.i("Sai", "Stop");

    }

    @Override
    protected void onPause() {
        super.onPause();

        pauseWalkEffect();

        saveData();

        Log.i("Sai", "Pause");
    }

    // FULL SCREEN METHODS:

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

    // OTHER METHODS:

    // Moves SpecialDee
    private void moveSpecialDee(){
        switch(forestHintNumber){
            case 1:
                allNPCs.get("Waddle Dee 1").fadeOut(backgroundGameLayout, new GameObject.FadeCompletionListener() {
                    @Override
                    public void fadeOnComplete() {
                        allNPCs.get("Waddle Dee 1").setXPosition(tWidth/2 + tWidth/7);
                        specialDeeDirection = "Left";
                        allNPCs.get("Waddle Dee 1").faceDirection(specialDeeDirection);
                        isSpecialDeePresent = false;
                        isByForestSoupHintWaddleDee = false;
                    }
                });
                break;

            case 2:
                allNPCs.get("Waddle Dee 1").fadeOut(backgroundGameLayout, new GameObject.FadeCompletionListener() {
                    @Override
                    public void fadeOnComplete() {
                        allNPCs.get("Waddle Dee 1").setXPosition(tWidth/18);
                        specialDeeDirection = "Right";
                        allNPCs.get("Waddle Dee 1").faceDirection(specialDeeDirection);
                        isSpecialDeePresent = false;
                        isByForestSoupHintWaddleDee = false;
                    }
                });
                break;

            case 3:
                allNPCs.get("Waddle Dee 1").fadeOut(backgroundGameLayout, new GameObject.FadeCompletionListener() {
                    @Override
                    public void fadeOnComplete() {
                        allNPCs.get("Waddle Dee 1").setCenterXPosition(tWidth - tWidth/3 - tWidth/25 + (float)(16*8/7F));
                        allNPCs.get("Waddle Dee 1").setYPosition(gameCamera.getBottomYPosition() + 6 + (int)(39*8/7F));
                        specialDeeDirection = "Right";
                        isSpecialDeePresent = false;
                        isByForestSoupHintWaddleDee = false;
                    }
                });
                break;

            case 4:
                allNPCs.get("Waddle Dee 1").fadeOut(backgroundGameLayout, new GameObject.FadeCompletionListener() {
                    @Override
                    public void fadeOnComplete() {
                        allNPCs.get("Waddle Dee 1").setXPosition(tWidth/4);
                        allNPCs.get("Waddle Dee 1").setYPosition(gameCamera.getBottomYPosition() + 6);
                        specialDeeDirection = "Right";
                        allNPCs.get("Waddle Dee 1").faceDirection(specialDeeDirection);
                        isSpecialDeePresent = false;
                        isByForestSoupHintWaddleDee = false;
                    }
                });
                break;

            default:
                break;
        }
    }

    // Deals with collisions with GameObjects that don't stop movement
    private boolean specialCollisionHandler(GameObject object1, GameObject object2, String collisionType){
        if(object2.isIngredient() && object1.isCharacter()){
            if(!((Ingredient) object2).isCollected()) {
                if(object1.getObjectName().toLowerCase().equals("kirby")) {
                    collectIngredient((Ingredient) object2);
                }
            }
            Log.i("Collision","Special Collision between " + object1.getObjectName() + " and " + object2.getObjectName());
            return true;
        }
        else if(object1.isIngredient() && object2.isCharacter()){
            if(!((Ingredient) object1).isCollected()) {
                if(object2.getObjectName().toLowerCase().equals("kirby")) {
                    collectIngredient((Ingredient) object1);
                }
            }
            Log.i("Collision","Special Collision between " + object1.getObjectName() + " and " + object2.getObjectName());
            return true;
        }
        else if(object1.isCharacter() && object1.getObjectName().toLowerCase().equals("kirby") && object2.getObjectName().toLowerCase().equals("mushroom house")){
            if(((Character) object1).isGrounded()) {
                isCloseToHouse = true;
                actionButton.setBackgroundResource(R.drawable.door_icon);
            }
            Log.i("Collision","Special Collision between " + object1.getObjectName() + " and " + object2.getObjectName());
            return true;
        }
        // Jumping on character is a bit buggy.
//        else if(object1.isCharacter() && object2.isCharacter() && collisionType.contains("top")) {
//            ((Character) object1).getUdHandler().removeCallbacksAndMessages(null);
//            ((Character) object1).stopFall();
//            ((Character) object1).setYPosition(object2.getHitBox().topLeft().y - ((Character) object1).getHitBox().getYBottom());
//            ((Character) object1).setObjectResource(((Character) object1).getIdleResource());
//            ((Character) object1).setHitBox(((Character) object1).getIdleHitBox());
//            ((Character) object1).showHitBox();
//
//            ((Character) object1).stopJump();
//            if(object1.getObjectName().toLowerCase().equals("kirby")) {
//                isFloating = false;
//                startFloatFinished = false;
//                jumpCount = 0;
//                ((Character) object1).getUdHandler().postDelayed(((Character) object1).getAllActions().get("High Jump"), 0);
//            }
//            else{
//                ((Character) object1).getUdHandler().postDelayed(((Character) object1).getAllActions().get("Jump"), 0);
//            }
//            Log.i("Collision","Special Collision between " + object1.getObjectName() + " and " + object2.getObjectName());
//            return true;
//        }
        else if(object1.isCharacter() && object1.getObjectName().toLowerCase().equals("kirby")
                && object2.equals(allNPCs.get("Waddle Dee 0")) && collisionType.equals("left")){
            if(((Character) object1).isGrounded()) {
                isByTutorialWaddleDee = true;
                actionButton.setBackgroundResource(R.drawable.testtalkicon);
            }
            Log.i("Collision","Special Collision between " + object1.getObjectName() + " and " + object2.getObjectName());
        }
        else if(object1.isCharacter() && object1.getObjectName().toLowerCase().equals("kirby")
                && object2.equals(allNPCs.get("Waddle Dee 1"))){
            if(((Character) object1).isGrounded()) {
                isByForestSoupHintWaddleDee = true;
                actionButton.setBackgroundResource(R.drawable.testtalkicon);
            }
            Log.i("Collision","Special Collision between " + object1.getObjectName() + " and " + object2.getObjectName());
        }
        else if(object1.isCharacter() && object1.getObjectName().toLowerCase().equals("kirby")
                && object2.getObjectName().toLowerCase().equals("cauldron")){
            if(((Character) object1).isGrounded()) {
                isCloseToCauldron = true;
                actionButton.setBackgroundResource(R.drawable.testmakesoupicon);
            }
            Log.i("Collision","Special Collision between " + object1.getObjectName() + " and " + object2.getObjectName());
            return true;
        }
        else if(object1.isCharacter() && object1.getObjectName().toLowerCase().equals("kirby")
                && object2.getObjectName().toLowerCase().equals("forest door")){
            if(((Character) object1).isGrounded()) {
                isCloseToForestDoor = true;
                actionButton.setBackgroundResource(R.drawable.door_icon);
            }
            Log.i("Collision","Special Collision between " + object1.getObjectName() + " and " + object2.getObjectName());
            return true;
        }
        else if(object1.isCharacter() && object1.getObjectName().toLowerCase().equals("kirby")
                && object2.getObjectName().toLowerCase().equals("swamp door")){
            if(((Character) object1).isGrounded()) {
                isCloseToSwampDoor = true;
                actionButton.setBackgroundResource(R.drawable.door_icon);
            }
            Log.i("Collision","Special Collision between " + object1.getObjectName() + " and " + object2.getObjectName());
            return true;
        }
        else if(object1.isCharacter() && object1.getObjectName().toLowerCase().equals("kirby")
                && object2.getObjectName().toLowerCase().equals("mushroom house (back)")){
            if(((Character) object1).isGrounded()) {
                isCloseToHouseBack = true;
                actionButton.setBackgroundResource(R.drawable.door_icon);
            }
            Log.i("Collision","Special Collision between " + object1.getObjectName() + " and " + object2.getObjectName());
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
        for(GameObject object: backgroundGameLayout.getLayoutObjects()){
            object.showHitBox();
        }
        for(GameObject object: foregroundGameLayout.getLayoutObjects()){
            object.showHitBox();
        }


        try {
            Log.i("CharacterDebug", "Character: Position: xPosition = " + String.valueOf(kirby.getXPosition()) +
                    " yPosition = " + String.valueOf(kirby.getYPosition()) + " | Width = " + String.valueOf(kirby.getObjectWidth()) +
                    " Height = " + String.valueOf(kirby.getObjectHeight()) + " | Attributes: isGrounded = " + String.valueOf(kirby.isGrounded()) +
                    " isFacingRight = " + String.valueOf(kirby.isFacingRight()) + " | HitBox: xPosition = " + String.valueOf(kirby.getHitBox().getXPosition()) +
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

}