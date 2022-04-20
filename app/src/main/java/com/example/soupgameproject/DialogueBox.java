package com.example.soupgameproject;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

public class DialogueBox extends ConstraintLayout {

    private String dialogue;
    private int dialogueSpeed;
    private int characterImage;
    private String name;
    private static ConstraintLayout dialogueBoxLayout;
    private TextView dialogueTextView;
    private ImageView portraitImageView;
    private TextView nameTextView;
    private Handler textHandler;
    private Handler delayClose;
    private boolean isPlaying;
    private int start;
    private int end;
    private Runnable playDialogue;


    public DialogueBox(Context context, ConstraintLayout dialogueBoxLayout, TextView nameTextView, String name,
                       TextView dialogueTextView, String dialogue, int dialogueSpeed, ImageView portraitImageView, int characterImage){
        super(context);
        this.dialogueBoxLayout = dialogueBoxLayout;
        this.dialogue = dialogue;
        this.name = name;
        this.dialogueSpeed = dialogueSpeed;
        this.characterImage = characterImage;
        this.dialogueTextView = dialogueTextView;
        this.portraitImageView = portraitImageView;
        this.nameTextView = nameTextView;
        textHandler = new Handler();
        delayClose = new Handler();
        isPlaying = false;
        start = 0;
        end = 0;

        playDialogue = new Runnable() {

            @Override
            public void run() {
                setEnd(getEnd()+1);
                if(getEnd()>dialogue.length()){
                    setStart(0);
                    setEnd(0);
                    isPlaying = false;
                    delayStopDialog(3000);
                    return;
                }
                else if(dialogue.substring(getStart(),getEnd() + findNextSpace()).length() + 3 > 130){
                    String substring = dialogue.substring(getStart(), getEnd()) + "...";
                    dialogueTextView.setText(substring);
                    setStart(end);
                    setEnd(end);
                    isPlaying = false;
                    delayStopDialog(3000);
                }
                else {
                    isPlaying = true;
                    delayClose.removeCallbacksAndMessages(null);
                    dialogueTextView.setText(dialogue.substring(getStart(), getEnd()));
                }

                if(isPlaying) {
                    textHandler.postDelayed(this, dialogueSpeed);
                }


            }
        };;
    }

    public void showDialogBox(){
        dialogueBoxLayout.setVisibility(VISIBLE);
        nameTextView.setText(name);
        portraitImageView.setBackgroundResource(characterImage);
    }

    public static void hideDialogBox(){
        dialogueBoxLayout.setVisibility(INVISIBLE);
    }

    public void resetDialogue(){
        start = 0;
        end = 0;
        isPlaying = false;
    }
    private void delayStopDialog(int time){
        delayClose.removeCallbacksAndMessages(null);

        delayClose.postDelayed(new Runnable() {
            @Override
            public void run() {
                hideDialogBox();
                resetDialogue();
            }
        }, time);
    }
    
    private int findNextSpace(){
        if(end < dialogue.length()) {
            String substring = dialogue.substring(end, end + 1);
            int i = 0;
            while (!substring.equals(" ") && end + i < dialogue.length() - 1) {
                i++;
                substring = dialogue.substring(end + i, end + 1 + i);
            }

            return i;
        }
        else{
            return -1;
        }
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public Handler getTextHandler() {
        return textHandler;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public Runnable getPlayDialogue() {
        return playDialogue;
    }

    public int getDialogueSpeed() {
        return dialogueSpeed;
    }
}
