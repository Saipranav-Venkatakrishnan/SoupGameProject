package com.example.soupgameproject;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;

public class Character extends GameObject{

    // Character Animation/visual variables
    private AnimationDrawable animation;

    // These decide whether or not an action is animated
    private boolean isIdleAnimated;

    // The following help to control jump, fall, and action logic
    private boolean jumpStarted, fallStarted, stopJump, stopFall;
    private boolean actionStarted, stopAction;

    // Resource for the character (animation or static)
    private int idleResource;

    // Character attributes
    // This boolean indicates whether or not the character is colliding with the top of another GameObject
    private boolean isGrounded;
    // The idle hit box to be used for the character
    private HitBox idleHitBox;

    // Creates a visible Character and assigns all instance variables appropriately
    public Character(Context context, String objectName, int objectWidth, int objectHeight,
                     float xPosition, float yPosition, HitBox idleHitBox, boolean isIdleAnimated, int idleResource) {
        super(context, objectName, objectWidth, objectHeight, idleResource, xPosition, yPosition,
                true, idleHitBox);
        // Set resource
        this.idleResource = idleResource;

        // Assign booleans to distinguish animation from static images
        this.isIdleAnimated = isIdleAnimated;

        // Set character attributes
        this.isGrounded = true;
        this.idleHitBox = idleHitBox;
        this.jumpStarted = false;
        this.fallStarted = false;
        this.stopJump = true;
        this.stopFall = true;

        this.actionStarted = false;
        this.stopAction = true;
    }

    // The following 3 movement methods follow a general pattern: Movement, Animation, HitBoxes, and Collision detection
    // Each of them include a CollisionListener and a PositionListener. When using these methods, create a new CollisionListener and a new PositionListener
    // inside the parameters of the method. Fill in the methods with what you want to happen when the Character collides with another GameObject and what
    // you want to happen when the character reaches a certain position.

    // The walk method contains an additional listener called the NotGroundedListener. This is to listen for when the Character is
    // no longer grounded but is moving horizontally to apply appropriate animations/image resources when this occurs.
    public Runnable walk(Handler handler, int walkResource, String direction, float speed, ArrayList<HitBox> hitBoxes, CollisionListener collisionListener,
                         NotGroundedListener notGroundedListener, PositionListener positionListener){

        Runnable action = new Runnable() {

            int i = 0;
            long initialAnimationTime = 0;

            @Override public void run() {

                // Movement
                if(direction.toLowerCase().equals("left")) {
                    setFacingRight(false);
                    setRotationY(180);
                    setXPosition(getXPosition() - speed);
                }
                else if(direction.toLowerCase().equals("right")){
                    setFacingRight(true);
                    setRotationY(0);
                    setXPosition(getXPosition() + speed);
                }

                positionListener.atPosition(getXPosition(),getYPosition());


                // Animation & hitBoxes
                if(isGrounded) {
                    // Animation
                    if (getObjectResource() != walkResource) {
                        setObjectResource(walkResource);
                        animation = (AnimationDrawable) getBackground();
                        animation.setOneShot(false);
                        animation.start();
                        i = 0;
                        initialAnimationTime = System.currentTimeMillis();
                    }

                    if(i < animation.getNumberOfFrames()-1){
                        long currentAnimationTime = System.currentTimeMillis();
                        if((int)(currentAnimationTime-initialAnimationTime) >= animation.getDuration(i)) {
                            i++;
                            initialAnimationTime = currentAnimationTime;
                        }
                    }
                    else{
                        i = 0;
                    }

                    // HitBoxes
                    hitBoxes.get(i).setXPosition(getXPosition());
                    hitBoxes.get(i).setYPosition(getYPosition());
                    setHitBox(hitBoxes.get(i));
                    showHitBox();
                }

                // Collision Handling

                boolean groundedSomewhere = false;

                for(GameObject object : detectCollisions()) {
                    collisionListener.onCollision(Character.this, object);
                    if (GameObject.getCollisionType(Character.this, object).equals("top") && isGrounded) {
                        groundedSomewhere = true;
                    }
                }

                if(!groundedSomewhere && isGrounded){
                    isGrounded = false;
                    notGroundedListener.notGrounded();
                }


                handler.postDelayed(this, 1);

            }
        };

        return action;
    }

    // The jump method takes in a specified jump height in DP. The necessary speed of the jump will be determined using GameObject.GRAVITY.
    // This method has an additional listener called CharacterListener. This is to listen for when actions are complete, such as
    // when the jumpHeight has been reached. Once reached, you can fill in the appropriate method after creating the CharacterListener
    // to deal with such an event.
    public Runnable jump(Handler handler, int jumpResource, boolean isJumpAnimated, float jumpHeight, ArrayList<HitBox> hitBoxes, CollisionListener collisionListener,
                         CharacterListener characterListener, PositionListener positionListener){


        Runnable action = new Runnable() {

            long initialTime = 0;
            long initialAnimationTime = 0;

            float initialJumpSpeed = (float) (Math.sqrt(2 * GameObject.GRAVITY * (jumpHeight+1)));
            float initialYPosition = 0;
            int i = 0;

            @Override public void run() {
                if(!jumpStarted){
                    // time the jump was initiated
                    initialTime = System.currentTimeMillis();
                    initialAnimationTime = System.currentTimeMillis();
                    initialYPosition = getYPosition();
                    isGrounded = false;
                    jumpStarted = true;
                    stopJump = false;
                    stopFall();
                }

                if(jumpStarted) {
                    long currentTime = System.currentTimeMillis();
                    float changeInTime = (currentTime - initialTime) / 1000F;

                    // Movement
                    if (getYPosition() - initialYPosition >= jumpHeight) {
                        stopJump();
                        characterListener.onActionComplete();
                    } else {
                        setYPosition((float) (initialYPosition + ((initialJumpSpeed * changeInTime) - .5 * GameObject.GRAVITY * Math.pow(changeInTime, 2))));
                    }

                    positionListener.atPosition(getXPosition(),getYPosition());

                    // Animation
                    if (isJumpAnimated) {
                        if (getObjectResource() != jumpResource) {
                            setObjectResource(jumpResource);
                            animation = (AnimationDrawable) getBackground();
                            animation.setOneShot(false);
                            animation.start();
                            i = 0;
                        }

                        if (i < animation.getNumberOfFrames() - 1) {
                            long currentAnimationTime = System.currentTimeMillis();
                            if((int)(currentAnimationTime-initialAnimationTime) >= animation.getDuration(i)) {
                                i++;
                                initialAnimationTime = currentAnimationTime;
                            }
                        } else {
                            i = 0;
                        }

                        // HitBoxes
                        hitBoxes.get(i).setXPosition(getXPosition());
                        hitBoxes.get(i).setYPosition(getYPosition());
                        setHitBox(hitBoxes.get(i));
                        showHitBox();
                    }
                    // No Animation
                    else {
                        if (getObjectResource() != jumpResource) {
                            setObjectResource(jumpResource);
                        }

                        // HitBox
                        hitBoxes.get(0).setXPosition(getXPosition());
                        hitBoxes.get(0).setYPosition(getYPosition());
                        setHitBox(hitBoxes.get(0));
                        showHitBox();

                    }

                    // Collision Handling
                    for (GameObject object : detectCollisions()) {
                        collisionListener.onCollision(Character.this, object);
                    }

                    if(!stopJump) {
                        handler.postDelayed(this, 1);
                    }
                }
            }
        };

        return action;
    }

    // The fall method moves the Character downwards at a pace related to GameObject.GRAVITY. Falling occurs forever. You can
    // use the CollisionListener to detect when the Character collides with the top of a GameObject to stop the fall.
    // To stop a fall, remove callbacks to this runnable and call the stopFall() method. If the Character stopped falling because it collided with the top
    // of another GameObject, set isGrounded to true.
    public Runnable fall(Handler handler, int fallResource, boolean isFallAnimated, ArrayList<HitBox> hitBoxes, CollisionListener collisionListener, PositionListener positionListener){

        Runnable action = new Runnable() {

            long initialTime = 0;
            long initialAnimationTime = 0;
            float initialYPosition = 0;
            int i = 0;

            @Override public void run() {
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
                    setYPosition((float) (initialYPosition - (.5 * GameObject.GRAVITY * Math.pow(changeInTime, 2))));
                    positionListener.atPosition(getXPosition(),getYPosition());

                    // Animation
                    if (isFallAnimated) {
                        if (getObjectResource() != fallResource) {
                            setObjectResource(fallResource);
                            animation = (AnimationDrawable) getBackground();
                            animation.setOneShot(false);
                            animation.start();
                            isGrounded = false;
                            i = 0;
                        }

                        if (i < animation.getNumberOfFrames() - 1) {
                            long currentAnimationTime = System.currentTimeMillis();
                            if((int)(currentAnimationTime-initialAnimationTime) >= animation.getDuration(i)) {
                                i++;
                                initialAnimationTime = currentAnimationTime;
                            }
                        } else {
                            i = 0;
                        }

                        // HitBoxes
                        hitBoxes.get(i).setXPosition(getXPosition());
                        hitBoxes.get(i).setYPosition(getYPosition());
                        setHitBox(hitBoxes.get(i));
                        showHitBox();
                    }
                    // No Animation
                    else {
                        if (getObjectResource() != fallResource) {
                            setObjectResource(fallResource);
                            isGrounded = false;
                        }

                        // HitBox
                        hitBoxes.get(0).setXPosition(getXPosition());
                        hitBoxes.get(0).setYPosition(getYPosition());
                        setHitBox(hitBoxes.get(0));
                        showHitBox();

                    }

                    // Collision Handling
                    for (GameObject object : detectCollisions()) {
                        collisionListener.onCollision(Character.this, object);
                    }

                    if(!stopFall) {
                        handler.postDelayed(this, 1);
                    }
                }
            }
        };

        return action;
    }

    // This method is for any other animated action the Character can take that doesn't involve any movement.
    // The method requires a CharacterListener which will signify when the action has been completed.
    public Runnable animatedAction(Handler handler, int actionResource, ArrayList<HitBox> hitBoxes, CollisionListener collisionListener,
                                   CharacterListener characterListener){
        Runnable action = new Runnable() {

            long initialAnimationTime = 0;
            int i = 0;

            @Override public void run() {
                if (!actionStarted) {
                    initialAnimationTime = System.currentTimeMillis();
                    actionStarted = true;
                    stopAction = false;
                }

                if (actionStarted) {
                    // Animation
                    if (getObjectResource() != actionResource) {
                        setObjectResource(actionResource);
                        animation = (AnimationDrawable) getBackground();
                        animation.setOneShot(true);
                        animation.start();
                        i = 0;
                    }

                    if (i < animation.getNumberOfFrames() - 1) {
                        long currentAnimationTime = System.currentTimeMillis();
                        if((int)(currentAnimationTime-initialAnimationTime) >= animation.getDuration(i)) {
                            i++;
                            initialAnimationTime = currentAnimationTime;
                        }
                    } else {
                        stopAction();
                        characterListener.onActionComplete();
                    }

                    // HitBoxes & Collision Handling
                    if(hitBoxes != null) {
                        hitBoxes.get(i).setXPosition(getXPosition());
                        hitBoxes.get(i).setYPosition(getYPosition());
                        setHitBox(hitBoxes.get(i));
                        showHitBox();

                        for (GameObject object : detectCollisions()) {
                            collisionListener.onCollision(Character.this, object);
                        }
                    }

                    if(!stopAction) {
                        handler.postDelayed(this, 1);
                    }

                }
            }
        };

        return action;
    }

    // The following three methods stop their associated Character actions. Be sure to removeCallbacks from the Handler when stopping one of these as well.
    public void stopJump(){
        jumpStarted = false;
        stopJump = true;
    }

    public void stopFall(){
        stopFall = true;
        fallStarted = false;
    }

    public void stopAction(){
        actionStarted = false;
        stopAction = true;
    }

    // Getters and Setters:
    public boolean isGrounded() {
        return isGrounded;
    }

    public void setGrounded(boolean grounded) {
        isGrounded = grounded;
    }

    public int getIdleResource() {
        return idleResource;
    }

    public void setIdleResource(int idleResource) {
        this.idleResource = idleResource;
    }

    // This method sets the object resource. If the object resource is the idleResource, and idleIsAnimated, then the animation will be played.
    @Override
    public void setObjectResource(int objectResource){
        if(isIdleAnimated && objectResource == this.getIdleResource()){
            super.setObjectResource(objectResource);
            animation = (AnimationDrawable) getBackground();
            animation.setOneShot(false);
            animation.start();
        }
        else{
            super.setObjectResource(objectResource);
        }
    }

    public boolean isJumpStarted() {
        return jumpStarted;
    }

    public void setJumpStarted(boolean jumpStarted) {
        this.jumpStarted = jumpStarted;
    }

    public boolean isFallStarted() {
        return fallStarted;
    }

    public void setFallStarted(boolean fallStarted) {
        this.fallStarted = fallStarted;
    }

    public boolean isStopJump() {
        return stopJump;
    }

    public void setStopJump(boolean stopJump) {
        this.stopJump = stopJump;
    }

    public boolean isStopFall() {
        return stopFall;
    }

    public void setStopFall(boolean stopFall) {
        this.stopFall = stopFall;
    }

    public HitBox getIdleHitBox(){
        this.idleHitBox.setXPosition(getXPosition());
        this.idleHitBox.setYPosition(getYPosition());
        return this.idleHitBox;
    }

    public AnimationDrawable getCharacterAnimation() {
        return animation;
    }

    // The three listeners of the Character Object
    public interface CharacterListener{
        void onActionComplete();
    }

    public interface NotGroundedListener{
        void notGrounded();
    }

    public interface PositionListener{
        void atPosition(float xPosition, float yPosition);
    }
}