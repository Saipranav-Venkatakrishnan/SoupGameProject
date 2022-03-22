package com.example.soupgameproject;

import android.content.Context;
import android.graphics.PointF;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams;
import androidx.constraintlayout.widget.ConstraintSet;

public class HitBox {

    // Units are in DP

    private boolean isActive;
    private int hitWidth, hitHeight;

    // The associated GameObject to this hit box
    private GameObject object;

    // The BOTTOM LEFT corner of the GameObject that this hit box is associated with
    private float xPosition, yPosition;

    // The distance away from the position of the GameObject this hit box is associated with
    // xLeft = distance from the left of the GameObject
    // yBottom = distance from the bottom of the GameObject
    private float xLeft, yBottom;

    // Variables for visualizing the hit box.
    private Context context;
    private ImageView box;
    private boolean isDisplayed;

    // Creates a hit box with the position of the GameObject, (xPosition,yPosition), and the distances
    // the hit box is away from the bottom left hand corner of the GameObject
    public HitBox(Context context, boolean isActive, int hitWidth, int hitHeight, float xPosition, float yPosition,
                  float xLeft, float yBottom){
        this.context = context;
        this.isActive = isActive;
        this.hitWidth = hitWidth;
        this.hitHeight = hitHeight;
        this.xPosition = xPosition;
        this.yPosition = yPosition;

        this.xLeft = xLeft;
        this.yBottom = yBottom;

        this.isDisplayed = false;

        this.box = new ImageView(context);

        LayoutParams layoutParams = new LayoutParams((int)(hitWidth * TitleActivity.DENSITY), (int) (hitHeight * TitleActivity.DENSITY));

        layoutParams.startToStart = ConstraintSet.PARENT_ID;
        layoutParams.bottomToBottom = ConstraintSet.PARENT_ID;

        layoutParams.setMargins(0,0,0,0);

        box.setLayoutParams(layoutParams);
    }

    // Uncomment once the collisionGameLayout has been created and change TitleActivity to whatever the main game activity is
//    These methods are for visualizing hit boxes for debugging purposes
    public void visualizeHitBox(){
        if(!isDisplayed) {
            isDisplayed = true;

            if(object.isFacingRight()) {
                box.setTranslationX((xPosition + xLeft) * TitleActivity.DENSITY);
            }
            else{
                box.setTranslationX((xPosition + object.getObjectWidth() - xLeft - hitWidth) * TitleActivity.DENSITY);
            }
            box.setTranslationY(-(yPosition + yBottom) * TitleActivity.DENSITY);

            if (isActive) {
                box.setBackgroundColor(context.getResources().getColor(R.color.activeBox));
            } else {
                box.setBackgroundColor(context.getResources().getColor(R.color.nonActiveBox));
            }

            InGameActivity.collisionGameLayout.getLayout().addView(box);
        }
    }

    public void removeHitBox(){
        if(isDisplayed){
            isDisplayed = false;
            InGameActivity.collisionGameLayout.getLayout().removeView(box);
        }
    }

    // These methods aid in detecting collisions by getting the top left and bottom right points of the hit box
    public PointF topLeft(){
        PointF point = new PointF();
        if(object.isFacingRight()) {
            point.set(xPosition + xLeft, yPosition + yBottom + hitHeight);
        }
        else{
            point.set(xPosition + object.getObjectWidth() - xLeft - hitWidth, yPosition + yBottom + hitHeight);
        }
        return point;
    }

    public PointF bottomRight(){
        PointF point = new PointF();
        if(object.isFacingRight()) {
            point.set(xPosition + xLeft + hitWidth, yPosition + yBottom);
        }
        else{
            point.set(xPosition + object.getObjectWidth() - xLeft, yPosition + yBottom);
        }
        return point;
    }

    // Getters and Setters:
    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public int getHitWidth() {
        return hitWidth;
    }

    public void setHitWidth(int hitWidth) {
        this.hitWidth = hitWidth;
    }

    public int getHitHeight() {
        return hitHeight;
    }

    public void setHitHeight(int hitHeight) {
        this.hitHeight = hitHeight;
    }

    public float getXPosition() {
        return xPosition;
    }

    public void setXPosition(float xPosition) {
        this.xPosition = xPosition;
    }

    public float getYPosition() {
        return yPosition;
    }

    public void setYPosition(float yPosition) {
        this.yPosition = yPosition;
    }

    public float getXLeft() {
        return xLeft;
    }

    public float getYBottom() {
        return yBottom;
    }

    // Sets the GameObject associated with the hit box. Is called whenever a GameObject sets its hit box
    public void setObject(GameObject object){
        this.object = object;
    }
}