package com.example.soupgameproject;

import android.content.Context;
import android.graphics.PointF;
import android.os.Handler;

import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams;
import androidx.constraintlayout.widget.ConstraintSet;

import java.util.ArrayList;

public class GameObject extends androidx.appcompat.widget.AppCompatImageView {
    
    // General notes: 
    // px = dp * Title.DENSITY
    // Most pre-existing methods in Android Studio move views by PIXEL

    // Determines whether or not hit boxes show
    public static boolean displayHitBoxes;

    // Proximity defines how close another GameObject must be in order for this GameObject to consider it for collisions 
    // The distance away from this GameObjects xPosition and yPosition in all four directions.
    // (Value is subject to change for game efficiency)
    private static final int PROXIMITY = 50;

    // For the following units are DP
    // (xPosition, yPosition) defines the BOTTOM LEFT hand corner of the object
    private float xPosition, yPosition;
    private int objectWidth, objectHeight;
    
    // GameObject Attributes
    private String objectName;
    // Changes the visibility of the GameObject
    private boolean objectVisibility;
    
    // Determines if the GameObject will collide with other GameObjects
    private boolean canCollide;

    // The current hit box for the GameObject
    private HitBox hitBox;
    
    // The previous hit box is stored for use when visualizing hit boxes
    private HitBox previousHitBox;

    // The direction the GameObject is facing
    private boolean isFacingRight;

    // Whether or not the GameObject is an Ingredient
    private boolean isIngredient;

    // Whether or not the GameObject is a Character
    private boolean isCharacter;

    // GameObject static image resource (R.drawable...)
    // Is set to the backgroundResource of the ImageView
    private int objectResource;

    // ArrayList of all GameObjects that have been added to any GameLayout
    public static ArrayList<GameObject> allActiveGameObjects = new ArrayList<GameObject>();

    // Falling variables
    private boolean fallStarted, stopFall;

    // Game gravity (Subject to change)
    public static final float GRAVITY = 100;

    // ALL GameObject constructors require context, objectName, objectWidth, objectHeight, objectResource, xPosition, yPosition, and canCollide
    
    // Creates a GameObject with a specified visibility and defined hit box
    public GameObject(Context context, String objectName, int objectWidth, int objectHeight,
                      int objectResource, float xPosition, float yPosition, boolean canCollide, boolean objectVisibility, HitBox hitBox){
        super(context);

        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.objectName = objectName;
        this.objectWidth = objectWidth;
        this.objectHeight =  objectHeight;
        this.objectResource = objectResource;
        this.objectVisibility = objectVisibility;
        this.canCollide = canCollide;
        this.hitBox = hitBox;
        this.isFacingRight = true;

        this.fallStarted = false;
        this.stopFall = true;

        setBackgroundResource(objectResource);
        setScaleType(ScaleType.CENTER);

        LayoutParams layoutParams = new LayoutParams((int)(objectWidth * TitleActivity.DENSITY),
                (int)(objectHeight * TitleActivity.DENSITY));

        layoutParams.startToStart = ConstraintSet.PARENT_ID;
        layoutParams.bottomToBottom = ConstraintSet.PARENT_ID;

        layoutParams.setMargins(0,0,0,0);

        setLayoutParams(layoutParams);

        setTranslationX(xPosition * TitleActivity.DENSITY);
        setTranslationY(-yPosition * TitleActivity.DENSITY);

        if(!objectVisibility){
            setVisibility(GONE);
        }

        this.hitBox.setObject(this);
    }

    // Creates a GameObject that is visible and has a defined hit box
    public GameObject(Context context, String objectName, int objectWidth, int objectHeight,
                      int objectResource, float xPosition, float yPosition, boolean canCollide, HitBox hitBox){
        super(context);

        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.objectName = objectName;
        this.objectWidth = objectWidth;
        this.objectHeight =  objectHeight;
        this.objectResource = objectResource;
        this.canCollide = canCollide;
        this.objectVisibility = true;
        this.hitBox = hitBox;
        this.isFacingRight = true;

        this.fallStarted = false;
        this.stopFall = true;


        setBackgroundResource(objectResource);
        setScaleType(ScaleType.CENTER);

        LayoutParams layoutParams = new LayoutParams((int)(objectWidth * TitleActivity.DENSITY),
                (int)(objectHeight * TitleActivity.DENSITY));

        layoutParams.startToStart = ConstraintSet.PARENT_ID;
        layoutParams.bottomToBottom = ConstraintSet.PARENT_ID;

        layoutParams.setMargins(0,0,0,0);

        setLayoutParams(layoutParams);

        setTranslationX(xPosition * TitleActivity.DENSITY);
        setTranslationY(-yPosition * TitleActivity.DENSITY);

        this.hitBox.setObject(this);
    }

    // Creates a GameObject with a specified visibility and predefined hit box equal to the GameObject's dimensions
    public GameObject(Context context, String objectName, int objectWidth, int objectHeight,
                      int objectResource, float xPosition, float yPosition, boolean canCollide, boolean objectVisibility){
        super(context);

        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.objectName = objectName;
        this.objectWidth = objectWidth;
        this.objectHeight =  objectHeight;
        this.objectResource = objectResource;
        this.objectVisibility = objectVisibility;
        this.canCollide = canCollide;
        this.hitBox = new HitBox(context, canCollide, objectWidth, objectHeight, xPosition, yPosition, 0, 0);
        this.isFacingRight = true;

        this.fallStarted = false;
        this.stopFall = true;

        setBackgroundResource(objectResource);
        setScaleType(ScaleType.CENTER);

        LayoutParams layoutParams = new LayoutParams((int)(objectWidth * TitleActivity.DENSITY),
                (int)(objectHeight * TitleActivity.DENSITY));

        layoutParams.startToStart = ConstraintSet.PARENT_ID;
        layoutParams.bottomToBottom = ConstraintSet.PARENT_ID;

        layoutParams.setMargins(0,0,0,0);

        setLayoutParams(layoutParams);

        setTranslationX(xPosition * TitleActivity.DENSITY);
        setTranslationY(-yPosition * TitleActivity.DENSITY);

        if(!objectVisibility){
            setVisibility(GONE);
        }

        this.hitBox.setObject(this);
    }

    // Creates a GameObject that is visible and has a predefined hit box equal to the GameObject's dimensions
    public GameObject(Context context, String objectName, int objectWidth, int objectHeight,
                      int objectResource, float xPosition, float yPosition, boolean canCollide){
        super(context);

        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.objectName = objectName;
        this.objectWidth = objectWidth;
        this.objectHeight =  objectHeight;
        this.objectResource = objectResource;
        this.canCollide = canCollide;
        this.objectVisibility = true;
        this.hitBox = new HitBox(context, canCollide, objectWidth, objectHeight, xPosition, yPosition, 0, 0);
        this.isFacingRight = true;

        this.fallStarted = false;
        this.stopFall = true;


        setBackgroundResource(objectResource);
        setScaleType(ScaleType.CENTER);

        LayoutParams layoutParams = new LayoutParams((int)(objectWidth * TitleActivity.DENSITY),
                (int)(objectHeight * TitleActivity.DENSITY));

        layoutParams.startToStart = ConstraintSet.PARENT_ID;
        layoutParams.bottomToBottom = ConstraintSet.PARENT_ID;

        layoutParams.setMargins(0,0,0,0);

        setLayoutParams(layoutParams);

        setTranslationX(xPosition * TitleActivity.DENSITY);
        setTranslationY(-yPosition * TitleActivity.DENSITY);

        this.hitBox.setObject(this);
    }

    // Methods to populate the allActiveGameObjects ArrayList
    public static void objectAddedToView(GameObject object){
        allActiveGameObjects.add(object);
    }

    public static void objectRemovedFromView(GameObject object){
        allActiveGameObjects.remove(object);
    }

    // COLLISION METHODS

    // Listener to deal with collisions
    public interface CollisionListener{
        void onCollision(GameObject object1, GameObject object2);
    }

    // Finding overlap between two rectangles to determine collisions: https://www.geeksforgeeks.org/find-two-rectangles-overlap/
    private boolean doOverlap(PointF l1, PointF r1, PointF l2, PointF r2) {

        // To check if either rectangle is actually a line
        // For example :  l1 ={-1,0}  r1={1,1}  l2={0,-1}  r2={0,1}

        if (l1.x == r1.x || l1.y == r1.y || l2.x == r2.x || l2.y == r2.y) {
            // the line cannot have positive overlap
            return false;
        }

        // If one rectangle is on left side of other
        if (l1.x > r2.x || l2.x > r1.x) {
            return false;
        }

        // If one rectangle is above other
        if (r1.y > l2.y || r2.y > l1.y) {
            return false;
        }

        return true;
    }

    // Gets all of the GameObjects within the defined PROXIMITY that are within any GameLayout (May change to only the CollisionLayout)
    private ArrayList<GameObject> nearByGameObjects(){
        ArrayList<GameObject> nearBy = new ArrayList<GameObject>();

        for(GameObject object : allActiveGameObjects){
            if((Math.abs(object.getXPosition() - this.getXPosition()) <= PROXIMITY || Math.abs(object.getYPosition()-this.getYPosition()) <= PROXIMITY)
                    && !this.equals(object) && object.getCanCollide()){
                nearBy.add(object);
            }
        }

        return nearBy;
    }

    // Detect All collisions between this GameObject and any other GameObject it collides with
    // and returns a list of all GameObjects that this GameObject has collided with
    public ArrayList<GameObject> detectCollisions(){
        ArrayList<GameObject> collisions = new ArrayList<GameObject>();

        if(canCollide){
            for(GameObject object: nearByGameObjects()){
                if(doOverlap(this.getHitBox().topLeft(),
                        this.getHitBox().bottomRight(),
                        object.getHitBox().topLeft(),
                        object.getHitBox().bottomRight())){
                    collisions.add(object);
                }
            }

            return collisions;
        }

        return collisions;

    }

    // This method returns the type of collision
    // "top" means that object 1 has collided with the top of object 2
    // "bottom" means that object 1 has collided with the bottom of object 2
    // "left" means that object 1 has collided with the left side of object 2
    // "right" means that object 1 has collided with the right side of object 2
    // The returned string may contain multiple collision types such as "topleft"
    public static String getCollisionType(GameObject object1, GameObject object2){
        String collisionType = "";

        PointF l1 = object1.getHitBox().topLeft();
        PointF r1 = object1.getHitBox().bottomRight();

        PointF l2 = object2.getHitBox().topLeft();
        PointF r2 = object2.getHitBox().bottomRight();

        if((l2.y >= r1.y && l1.y - object1.getHitBox().getHitHeight()/2F >= l2.y) && (l1.x < r2.x && r1.x > l2.x)){
            collisionType += "top";
        }
        else if((l1.y >= r2.y && r1.y + object1.getHitBox().getHitHeight()/2F <= r2.y) && (l1.x < r2.x && r1.x > l2.x)){
            collisionType += "bottom";
        }

        if((l1.x <= r2.x && r1.x >= r2.x) && (l1.y > r2.y && r1.y < l2.y)){
            collisionType += "right";
        }
        else if((l2.x <= r1.x && l1.x <= l2.x)  && (l1.y > r2.y && r1.y < l2.y)){
            collisionType += "left";
        }

        return collisionType;
    }

    // Displays a this GameObject's hit box when called. Use this method whenever a hit box is changing/the GameObject is moving
    // To stop displaying hit boxes, as showing hit boxes slows down the game tremendously, set displayHitBoxes to false.
    // When no longer needed, comment the body of the method out but leave the header.
    public void showHitBox(){
        if(displayHitBoxes) {
            if (previousHitBox != null) {
                previousHitBox.stopShowingHitBox();
            }
            hitBox.visualizeHitBox();
        }
    }

    // Basic falling method
    public Runnable fall(Handler handler, float fallRate, CollisionListener collisionListener){
        Runnable action = new Runnable() {
            long initialTime = 0;
            long initialAnimationTime = 0;
            float initialYPosition = 0;

            @Override
            public void run() {
                if (!fallStarted) {
                    // time the fall was initiated
                    initialTime = System.currentTimeMillis();
                    initialAnimationTime = System.currentTimeMillis();
                    initialYPosition = getYPosition();
                    fallStarted = true;
                    stopFall = false;
                }

                if (fallStarted) {
                    long currentTime = System.currentTimeMillis();
                    float changeInTime = (currentTime - initialTime) / 1000F;

                    // Movement
                    setYPosition((float) (initialYPosition - (.5 * fallRate * Math.pow(changeInTime, 2))));

                    // HitBox
                    getHitBox().setXPosition(getXPosition());
                    getHitBox().setYPosition(getYPosition());
                    setHitBox(getHitBox());
                    showHitBox();
                }

                // Collision Handling
                for (GameObject object : detectCollisions()) {
                    collisionListener.onCollision(GameObject.this, object);
                }
                if(!stopFall) {
                    handler.postDelayed(this, 1);
                }
            }
        };

        return action;
    }


    public void fadeOut(GameLayout layout, FadeCompletionListener fadeCompletionListener){
        Handler handler = new Handler();
        Runnable fade = new Runnable() {

            private float i = 1;

            @Override
            public void run() {
                if(i >= 0) {
                    setAlpha(i);
                    i-=1/10F;
                    handler.postDelayed(this,1);
                }
                else{
                    layout.removeLayoutObject(GameObject.this);
                    setAlpha(1F);
                    fadeCompletionListener.fadeOnComplete();
                    handler.removeCallbacks(this);
                }
            }
        };

        handler.postDelayed(fade,0);
    }

    public void fadeIn(GameLayout layout, FadeCompletionListener fadeCompletionListener){
        setAlpha(0F);
        layout.addLayoutObject(GameObject.this);

        Handler handler = new Handler();
        Runnable fade = new Runnable() {

            private float i = 0;

            @Override
            public void run() {
                if(i <= 1) {
                    setAlpha(i);
                    i+=1/10F;
                    handler.postDelayed(this,1);
                }
                else{
                    setAlpha(1F);
                    fadeCompletionListener.fadeOnComplete();
                    handler.removeCallbacks(this);
                }
            }
        };

        handler.postDelayed(fade,0);
    }

    // Stops the falling
    public void stopFall(){
        stopFall = true;
        fallStarted = false;
    }

    // GENERAL GETTERS AND SETTERS

    public boolean getCanCollide() {
        return canCollide;
    }

    public void setCanCollide(boolean canCollide) {
        this.canCollide = canCollide;
        hitBox.setActive(canCollide);
    }

    public HitBox getHitBox() {
        return this.hitBox;
    }

    // May comment out the first line of the method if hit boxes aren't being visualized.
    public void setHitBox(HitBox hitBox) {
        this.previousHitBox = this.hitBox;
        this.hitBox = hitBox;
        this.hitBox.setObject(this);
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public float getCenterXPosition(){
        return xPosition + objectWidth/2F;
    }

    public void setCenterXPosition(float centerXPosition){
        this.xPosition = centerXPosition-objectWidth/2F;
        this.hitBox.setXPosition(this.xPosition);
        setTranslationX(this.xPosition * TitleActivity.DENSITY);
    }

    public float getCenterYPosition(){
        return yPosition + objectHeight/2F;
    }

    public void setCenterYPosition(float centerYPosition){
        this.yPosition = centerYPosition-objectHeight/2F;
        this.hitBox.setYPosition(this.yPosition);
        setTranslationY(-this.yPosition * TitleActivity.DENSITY);
    }


    public float getXPosition() {
        return xPosition;
    }

    public void setXPosition(float xPosition) {
        this.xPosition = xPosition;
        this.hitBox.setXPosition(xPosition);
        setTranslationX(xPosition * TitleActivity.DENSITY);
    }

    public float getYPosition() {
        return yPosition;
    }

    public void setYPosition(float yPosition) {
        this.yPosition = yPosition;
        this.hitBox.setYPosition(yPosition);
        setTranslationY(-yPosition * TitleActivity.DENSITY);
    }

    public int getObjectWidth() {
        return objectWidth;
    }

    public void setObjectWidth(int objectWidth) {
        this.objectWidth = objectWidth;

        LayoutParams layoutParams = new LayoutParams((int)(objectWidth * TitleActivity.DENSITY),
                (int)(objectHeight * TitleActivity.DENSITY));

        layoutParams.startToStart = ConstraintSet.PARENT_ID;
        layoutParams.bottomToBottom = ConstraintSet.PARENT_ID;

        layoutParams.setMargins(0,0,0,0);

        setLayoutParams(layoutParams);
    }

    public int getObjectHeight() {
        return objectHeight;
    }

    public void setObjectHeight(int objectHeight) {
        this.objectHeight = objectHeight;

        LayoutParams layoutParams = new LayoutParams((int)(objectWidth * TitleActivity.DENSITY),
                (int)(objectHeight * TitleActivity.DENSITY));

        layoutParams.startToStart = ConstraintSet.PARENT_ID;
        layoutParams.bottomToBottom = ConstraintSet.PARENT_ID;

        layoutParams.setMargins(0,0,0,0);

        setLayoutParams(layoutParams);
    }

    public int getObjectResource() {
        return objectResource;
    }

    public void setObjectResource(int objectResource) {
        this.objectResource = objectResource;
        setBackgroundResource(objectResource);
    }

    public boolean isObjectVisible() {
        return objectVisibility;
    }

    public void setObjectVisibility(boolean objectVisibility) {
        this.objectVisibility = objectVisibility;
        if(objectVisibility){
            setVisibility(VISIBLE);
        }
        else{
            setVisibility(GONE);
        }
    }

    public boolean getObjectVisibility() {
        return objectVisibility;
    }

    public boolean isFacingRight() {
        return isFacingRight;
    }

    public void setFacingRight(boolean facingRight) {
        isFacingRight = facingRight;
    }

    public boolean isIngredient() {
        return isIngredient;
    }

    public void setIsIngredient(boolean ingredient) {
        isIngredient = ingredient;
    }

    public boolean isCharacter() {
        return isCharacter;
    }

    public void setIsCharacter(boolean character) {
        isCharacter = character;
    }

    public boolean isFallStarted() {
        return fallStarted;
    }

    public void setFallStarted(boolean fallStarted) {
        this.fallStarted = fallStarted;
    }

    public boolean isStopFall() {
        return stopFall;
    }

    public void setStopFall(boolean stopFall) {
        this.stopFall = stopFall;
    }

    public interface FadeCompletionListener{
        void fadeOnComplete();
    }
}