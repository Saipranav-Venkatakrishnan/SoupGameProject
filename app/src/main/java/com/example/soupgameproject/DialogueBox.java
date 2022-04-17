package com.example.soupgameproject;

import android.content.Context;

import androidx.constraintlayout.widget.ConstraintLayout;

public class DialogueBox extends ConstraintLayout {

    private String dialogue;
    private int dialogueSpeed;
    private int characterImage;

    public DialogueBox(Context context, ConstraintLayout userInterfaceLayout, String dialogue, int dialogueSpeed, int characterImage){
        super(context);
        
    }
}
