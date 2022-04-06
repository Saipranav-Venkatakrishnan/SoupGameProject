package com.example.soupgameproject;

import android.content.Context;
import android.widget.FrameLayout;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;

public class SaveTestObject {

    private int test;
    private ArrayList<Integer> testList;
    private GameObject object;
    private Camera camera;
    private ConstraintLayout layout;

    public SaveTestObject(Context context, int test, ArrayList<Integer> testList){
        this.test = test;
        this.testList = testList;
        //object = new GameObject(context, "hi",1,1,R.drawable.kirby1,1,1,false);
        //camera = new Camera(new FrameLayout(context), new ConstraintLayout(context));

    }
}
