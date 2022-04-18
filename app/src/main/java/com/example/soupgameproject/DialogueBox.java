package com.example.soupgameproject;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

public class DialogueBox extends ConstraintLayout {

    private String dialogue;
    private int dialogueSpeed;
    private int characterImage;
    private ConstraintLayout userInterfaceLayout;
    private TextView textView;
    private ImageView imageView;


    public DialogueBox(Context context, ConstraintLayout userInterfaceLayout, String dialogue, int dialogueSpeed, int characterImage){
        super(context);
        this.userInterfaceLayout = userInterfaceLayout;
        this.dialogue = dialogue;
        this.dialogueSpeed = dialogueSpeed;
        this.characterImage = characterImage;
        this.textView = new TextView(context);
        this.imageView = new ImageView(context);

        
        int width = 400;
        int height = 100;
        
        LayoutParams lp = new LayoutParams((int)(width * TitleActivity.DENSITY),(int)(height * TitleActivity.DENSITY));
        lp.topToTop = ConstraintSet.PARENT_ID;
        lp.startToStart = ConstraintSet.PARENT_ID;
        
        lp.setMargins((int)((TitleActivity.WIDTH-width * TitleActivity.DENSITY)/2F),0,0,0);
        
        setLayoutParams(lp);

        LayoutParams textLP = new LayoutParams((int)(height *2/3F * TitleActivity.DENSITY),(int)(height * 2/3F * TitleActivity.DENSITY));
        textLP.topToTop = ConstraintSet.PARENT_ID;
        textLP.startToStart = ConstraintSet.PARENT_ID;

        textLP.setMargins((int)((TitleActivity.WIDTH-width * TitleActivity.DENSITY)/2F),0,0,0);

        textView.setLayoutParams(textLP);

        LayoutParams imageLP = new LayoutParams((int)(width * TitleActivity.DENSITY),(int)(width * TitleActivity.DENSITY));
        imageLP.topToTop = ConstraintSet.PARENT_ID;
        imageLP.startToStart = ConstraintSet.PARENT_ID;

        imageLP.setMargins((int)((TitleActivity.WIDTH-width * TitleActivity.DENSITY)/2F),0,0,0);

        imageView.setLayoutParams(imageLP);
        
        
    }
}
