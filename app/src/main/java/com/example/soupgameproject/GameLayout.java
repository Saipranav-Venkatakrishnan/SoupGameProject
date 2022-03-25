package com.example.soupgameproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.util.Log;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;

public class GameLayout {

    // All GameLayouts created
    public static ArrayList<GameLayout> allLayouts = new ArrayList<GameLayout>();

    // GameLayouts refer to:
    //...BackgroundLayout (ConstraintLayout) -> CollisionLayout (ConstraintLayout) -> ForegroundLayout (ConstraintLayout)...
    // portion of the template described in the Camera Class

    // The layout (Background, Collision, or Foreground)
    private ConstraintLayout layout;

    // All GameObjects within this GameLayout (Visible or not)
    private ArrayList<GameObject> layoutObjects;

    // A predefined ImageView within the layout that will contain the background image for this layout with scaleType: fitCenter.
    // This is necessary as we cannot apply a scaleType to the background image of a layout.
    private ImageView backgroundImageView;

    // The image resource for the background image of the layout
    private int backgroundImage;

    private String name;

    // The context where the GameLayout is being used.
    private Context context;

    // Multiple constructors for various uses

    // Create a GameLayout with a background image and predefined GameObjects
    public GameLayout(Context context, String name, ConstraintLayout layout, int backgroundImage, ImageView backgroundImageView, ArrayList<GameObject> layoutObjects){
        this.context = context;
        this.name = name;
        this.layout = layout;
        this.backgroundImage = backgroundImage;
        this.backgroundImageView = backgroundImageView;
        this.layoutObjects = layoutObjects;

        this.backgroundImageView.setImageResource(backgroundImage);

        for(GameObject gameObject: layoutObjects){
            layout.addView(gameObject);
            GameObject.objectAddedToView(gameObject);
        }

        allLayouts.add(this);
    }

    // Create a GameLayout with a background but without any GameObjects
    public GameLayout(Context context, String name, ConstraintLayout layout, int backgroundImage, ImageView backgroundImageView){
        this.context = context;
        this.name = name;
        this.layout = layout;
        this.backgroundImage = backgroundImage;
        this.backgroundImageView = backgroundImageView;
        this.layoutObjects = new ArrayList<GameObject>();

        this.backgroundImageView.setImageResource(backgroundImage);

        allLayouts.add(this);
    }

    // Create a GameLayout without a background but with predefined GameObjects
    public GameLayout(Context context, String name, ConstraintLayout layout, ArrayList<GameObject> layoutObjects){
        this.context = context;
        this.name = name;
        this.layout = layout;
        this.layoutObjects = layoutObjects;
        this.backgroundImage = -1;

        for(GameObject gameObject: layoutObjects){
            layout.addView(gameObject);
            GameObject.objectAddedToView(gameObject);
        }

        allLayouts.add(this);
    }

    // Create a GameLayout without a background and without any GameObjects
    public GameLayout(Context context, String name, ConstraintLayout layout){
        this.context = context;
        this.name = name;
        this.layout = layout;
        this.layoutObjects = new ArrayList<GameObject>();
        this.backgroundImage = -1;

        allLayouts.add(this);
    }

    // Get/set layoutObjects
    public ArrayList<GameObject> getLayoutObjects() {
        return this.layoutObjects;
    }

    // Setting layoutObjects removes all GameObjects within the layout before setting the new layoutObjects
    public void setLayoutObjects(ArrayList<GameObject> layoutObjects) {
        removeAllLayoutObjects();
        this.layoutObjects = layoutObjects;

        for(GameObject gameObject: layoutObjects){
            try {
                layout.addView(gameObject);
                GameObject.objectAddedToView(gameObject);
            }
            catch(Exception e){
                Log.i("GameLayout", "Error adding game objects");
            }
        }
    }

    // Removes all GameObjects within the layout
    public void removeAllLayoutObjects(){
        for(GameObject gameObject: this.layoutObjects) {
            try {
                layout.removeView(gameObject);
                GameObject.objectRemovedFromView(gameObject);
                gameObject.getHitBox().stopShowingHitBox();
                Log.i("GameLayout", "All layout objects removed.");
            }
            catch(Exception e){
                Log.i("GameLayout", "Error removing game objects");
            }
        }
        this.layoutObjects = new ArrayList<GameObject>();
    }

    // Remove/add individual GameObjects to layout
    public void removeLayoutObject(GameObject gameObject){
        try {
            this.layoutObjects.remove(gameObject);
            layout.removeView(gameObject);
            gameObject.getHitBox().stopShowingHitBox();
            GameObject.objectRemovedFromView(gameObject);
        }
        catch (Exception e){
            Log.i("GameLayout", "Error removing game object");
        }
    }

    public void addLayoutObject(GameObject gameObject){
        try {
            this.layoutObjects.add(gameObject);
            GameObject.objectAddedToView(gameObject);
            layout.addView(gameObject);
        }
        catch (Exception e){
            Log.i("GameLayout", "Error adding game object");
        }
    }

    public void addLayoutObjects(ArrayList<GameObject> layoutObjects){
        for(GameObject gameObject: layoutObjects){
            try {
                layout.addView(gameObject);
                this.layoutObjects.add(gameObject);
                GameObject.objectAddedToView(gameObject);
            }
            catch(Exception e){
                Log.i("GameLayout", "Error adding game objects");
            }
        }
    }

    // Get/set the background image resource
    public int getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(int backgroundImage) {
        this.backgroundImage = backgroundImage;
        backgroundImageView.setImageResource(backgroundImage);
    }

    // Get/set the background ImageView
    public ImageView getBackgroundImageView() {
        return backgroundImageView;
    }

    public void setBackgroundImageView(ImageView backgroundImageView) {
        this.backgroundImageView = backgroundImageView;
    }

    // Get the associated layout for this GameLayout
    public ConstraintLayout getLayout(){
        return this.layout;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Lighting testing
    public static void darkenBackgroundLighting(int color){
        for(GameLayout gameLayout : allLayouts){
            if(gameLayout.getBackgroundImageView() != null){
                // Apply tint
                gameLayout.getBackgroundImageView().setImageTintMode(PorterDuff.Mode.MULTIPLY);
                gameLayout.getBackgroundImageView().setImageTintList(ColorStateList.valueOf(color));
            }
        }
    }

    public static void brightenBackgroundLighting(int color){
        for(GameLayout gameLayout : allLayouts){
            if(gameLayout.getBackgroundImageView() != null){
                // Apply tint
                gameLayout.getBackgroundImageView().setImageTintMode(PorterDuff.Mode.SRC_ATOP);
                gameLayout.getBackgroundImageView().setImageTintList(ColorStateList.valueOf(color));
            }
        }
    }

    public static void darkenObjectLighting(int color){
        for(GameLayout gameLayout : allLayouts){
            for(GameObject gameObject : gameLayout.getLayoutObjects()){
                // Apply  tint
                if(!gameObject.getObjectName().toLowerCase().equals("sun")) {
                    gameObject.setBackgroundTintMode(PorterDuff.Mode.MULTIPLY);
                    gameObject.setBackgroundTintList(ColorStateList.valueOf(color));
                }
            }
        }
    }

    public static void brightenObjectLighting(int color){
        for(GameLayout gameLayout : allLayouts){
            for(GameObject gameObject : gameLayout.getLayoutObjects()){
                // Apply  tint
                if(!gameObject.getObjectName().toLowerCase().equals("sun")) {
                    gameObject.setBackgroundTintMode(PorterDuff.Mode.SRC_ATOP);
                    gameObject.setBackgroundTintList(ColorStateList.valueOf(color));
                }
            }
        }
    }
}

