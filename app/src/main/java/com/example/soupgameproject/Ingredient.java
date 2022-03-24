package com.example.soupgameproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;

import androidx.core.graphics.drawable.DrawableCompat;

public class Ingredient extends GameObject{


    private String name;
    private boolean isCollected;

    public Ingredient(Context context, String name, int objectWidth, int objectHeight,
                      int objectResource, float xPosition, float yPosition){
        super(context, name, objectWidth, objectHeight, objectResource, xPosition, yPosition,
                true);
        this.name = name;
        this.isCollected = false;
        setIsIngredient(true);
    }

    public Runnable collected(Handler handler){
        Runnable collect = new Runnable() {

            private float i = 1;

            @Override
            public void run() {
                if(i >= 0) {
                    setAlpha(i);
                    i-=1/10F;
                }
                else{
                    InGameActivity.collisionGameLayout.removeLayoutObject(Ingredient.this);
                    setAlpha(1F);
                    handler.removeCallbacks(this);
                }

                handler.postDelayed(this,1);
            }
        };

        return collect;
    }

    public String getName() {
        return name;
    }

    public boolean isCollected() {
        return isCollected;
    }

    public void setCollected(boolean collected) {
        isCollected = collected;
    }
}
