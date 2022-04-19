package com.example.soupgameproject;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

public class DialogueBox extends ConstraintLayout {

    private String dialogue;
    private int dialogueSpeed;
    private int characterImage;
    private ConstraintLayout dialogueBoxLayout;
    private TextView dialogueTextView;
    private ImageView portraitImageView;


    public DialogueBox(Context context, ConstraintLayout dialogueBoxLayout, TextView dialogueTextView, String dialogue, int dialogueSpeed, ImageView portraitImageView, int characterImage){
        super(context);
        this.dialogueBoxLayout = dialogueBoxLayout;
        this.dialogue = dialogue;
        this.dialogueSpeed = dialogueSpeed;
        this.characterImage = characterImage;
        this.dialogueTextView = dialogueTextView;
        this.portraitImageView = portraitImageView;

        portraitImageView.setBackgroundResource(characterImage);
    }
}
