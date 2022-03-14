package com.example.soupgameproject;

import android.content.Context;

public class Ingredient extends GameObject{

    private String name;

    public Ingredient(Context context, String name, int objectWidth, int objectHeight,
                      int objectResource, float xPosition, float yPosition){
        super(context, name, objectWidth, objectHeight, objectResource, xPosition, yPosition,
                true);
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
