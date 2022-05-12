package com.example.soupgameproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

public class CatalogPage extends AppCompatActivity {

    private LinearLayoutCompat cards;
    private TextView numCollectedText;
    private TextView messageText;
    private int threeStarCount;

    // https://stackoverflow.com/questions/63661601/add-many-cardviews-with-onclick-method-programmatically
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog_page);

        threeStarCount = 0;
        cards = findViewById(R.id.cards);
        messageText = findViewById(R.id.messageTextView);

        for(Soup soup: InGameActivity.userSoups){
            createCard(soup);
            if(soup.getStarRank() == 3){
                threeStarCount++;
            }
        }
        numCollectedText = findViewById(R.id.collectedAmntText);
        if(InGameActivity.soupNum == 26) {
            messageText.setText("Congratulations! You've made all the different kinds of soups! Now aim for all 3 stars!");
            if(threeStarCount == 26){
                messageText.setText("PERFECTION! You've made all possible soups with 3 stars on each!");
            }
        }
        else{
            messageText.setText("Keep Making Soups!");
        }
        numCollectedText.setText("Soups Collected: " + InGameActivity.soupNum + "/26");
    }

    public void createCard(Soup soup) {
        CardView newCard = new CardView(CatalogPage.this);
        getLayoutInflater().inflate(R.layout.catalog_base, newCard);

        TextView name = newCard.findViewById(R.id.nameTxt);
        ImageView img = newCard.findViewById(R.id.soupImg);
        TextView ingredients = newCard.findViewById(R.id.ingredientsList);
        RatingBar stars = newCard.findViewById(R.id.ratingBar);

        soup.showSoup(img);
        name.setText(soup.getSoupName());
        stars.setRating(soup.getStarRank());
        ingredients.setText(soup.getDesc());

        cards.addView(newCard);
    }

    public void closeScreen(View v) {
        Intent intent = new Intent(this, InGameActivity.class);
        startActivity(intent);
    }

    // The following code was from https://developer.android.com/training/system-ui/immersive to create a fullscreen (has changed)
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        //| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the status bar
                        // | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }
}