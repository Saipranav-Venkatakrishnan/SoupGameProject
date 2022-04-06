package com.example.soupgameproject;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashMap;

public class Soup{

    private int starRank;
    private String soupName;
    private static final int bowlResource = R.drawable.soupbase;
    private static final int soupResource = R.drawable.souptop;
    private int soupColor;
    
    private ArrayList<Ingredient> ingredients;
    private ArrayList<Integer> amountOfIngredient;

    private int totalAmountOfIngredients;
    
    // Subject to change
    private static final int minIngredients = 3;
    private static final int maxIngredients = 10;

    // Basic Soup Constructor
    // 1 ingredient
    public Soup(Ingredient ingredient, int amount){
        ingredients = new ArrayList<Ingredient>();
        amountOfIngredient = new ArrayList<Integer>();
        this.starRank = starRankHandler(amount);
        this.soupName = ingredient.getName() + " Soup";
        this.totalAmountOfIngredients = amount;
        
        ingredients.add(ingredient);
        amountOfIngredient.add(amount);

        soupColorHandler();
    }

    // Paired Soup Constructor
    // 2 different ingredients
    public Soup(Ingredient ingredient1, int amount1, Ingredient ingredient2, int amount2){
        ingredients = new ArrayList<Ingredient>();
        amountOfIngredient = new ArrayList<Integer>();

        this.totalAmountOfIngredients = amount1 + amount2;

        this.starRank = starRankHandler(totalAmountOfIngredients);
        if(amount1 > amount2){
            this.soupName = ingredient1.getName() + "-" + ingredient2.getName() + " Soup";
        }
        else if(amount1 < amount2){
            this.soupName = ingredient2.getName() + "-" + ingredient1.getName() + " Soup";
        }
        else{
            if(ingredient1.getName().compareToIgnoreCase(ingredient2.getName())<0){
                this.soupName = ingredient1.getName() + "-" + ingredient2.getName() + " Soup";
            }
            else{
                this.soupName = ingredient2.getName() + "-" + ingredient1.getName() + " Soup";
            }
        }

        ingredients.add(ingredient1);
        amountOfIngredient.add(amount1);

        ingredients.add(ingredient2);
        amountOfIngredient.add(amount2);

        soupColorHandler();
    }

    // Special Soup Constructor
    // TBD

    private int starRankHandler(int totalAmount){
        int twoStarChance = (int)(Math.random() * 100);
        int threeStarChance = (int)(Math.random() * 100);

        if(totalAmount >= minIngredients && totalAmount < 2 * minIngredients){
            if(threeStarChance < 10){
                return 3;
            }
            else if(twoStarChance < 30){
                return 2;
            }
            else{
                return 1;
            }
        }
        else if(totalAmount >= 2 * minIngredients && totalAmount < 3 * minIngredients){
            if(threeStarChance < 30){
                return 3;
            }
            else{
                return 2;
            }
        }
        else if(totalAmount >= 3 * minIngredients && totalAmount <= maxIngredients){
            return 3;
        }

        return -1;
    }

    private void soupColorHandler(){
        int a = 0;
        int r = 0;
        int g = 0;
        int b = 0;

        for(int i = 0; i < ingredients.size(); i++){
            double proportion = (double) amountOfIngredient.get(i)/totalAmountOfIngredients;
            a += (int)(ingredients.get(i).getA() * proportion);
            r += (int)(ingredients.get(i).getR() * proportion);
            g += (int)(ingredients.get(i).getG() * proportion);
            b += (int)(ingredients.get(i).getB() * proportion);
        }
        
        soupColor = Color.argb(a, r, g, b);
    }
    
    private void showSoup(ImageView imageView){
        imageView.setBackgroundResource(bowlResource);
        imageView.setImageResource(soupResource);
        imageView.setScaleType(ImageView.ScaleType.FIT_START);
        imageView.setImageTintMode(PorterDuff.Mode.SRC_ATOP);
        imageView.setImageTintList(ColorStateList.valueOf(soupColor));
    }
}
