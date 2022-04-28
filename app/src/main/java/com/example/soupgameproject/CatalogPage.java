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

    // https://stackoverflow.com/questions/63661601/add-many-cardviews-with-onclick-method-programmatically
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog_page);

        cards = findViewById(R.id.cards);

        for(Soup soup: InGameActivity.userSoups){
            createCard(soup);
        }
    }

    public void createCard(Soup soup) {
        CardView newCard = new CardView(CatalogPage.this);
        getLayoutInflater().inflate(R.layout.catalog_base, newCard);

        TextView name = newCard.findViewById(R.id.nameTxt);
        ImageView img = newCard.findViewById(R.id.soupImg);
        TextView desc = newCard.findViewById(R.id.descTxt);
        RatingBar stars = newCard.findViewById(R.id.ratingBar);

        soup.showSoup(img);
        name.setText(soup.getSoupName());
        stars.setRating(soup.getStarRank());

        cards.addView(newCard);
    }

    public void closeScreen(View v) {
        Intent intent = new Intent(this, InGameActivity.class);
        startActivity(intent);
    }
}