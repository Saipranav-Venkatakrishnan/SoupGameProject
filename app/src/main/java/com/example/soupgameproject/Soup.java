package com.example.soupgameproject;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.Log;
import android.widget.ImageView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class Soup{

    private int starRank;
    private String soupName;
    private static final int bowlResource = R.drawable.soupbase;
    private static final int soupResource = R.drawable.souptop;
    private int soupColor;
    private int bowlColor;
    private String desc;
    
    private ArrayList<Ingredient> ingredients;
    // private ArrayList<Integer> amountOfIngredient;

    private int totalAmountOfIngredients;
    
    // Subject to change
    public static final int minIngredients = 3;
    // do we need max ingredients?
    public static final int maxIngredients = 10;

//    // Basic Soup Constructor
//    // 1 ingredient
//    public Soup(Ingredient ingredient, int amount){
//        ingredients = new ArrayList<Ingredient>();
//        amountOfIngredient = new ArrayList<Integer>();
//        this.starRank = starRankHandler(amount);
//        this.soupName = ingredient.getName() + " Soup";
//        this.totalAmountOfIngredients = amount;
//
//        ingredients.add(ingredient);
//        amountOfIngredient.add(amount);
//
//        soupColorHandler();
//        bowlColorHandler();
//    }
//
//    // Paired Soup Constructor
//    // 2 different ingredients
//    public Soup(Ingredient ingredient1, int amount1, Ingredient ingredient2, int amount2){
//        ingredients = new ArrayList<Ingredient>();
//        amountOfIngredient = new ArrayList<Integer>();
//
//        this.totalAmountOfIngredients = amount1 + amount2;
//
//        this.starRank = starRankHandler(totalAmountOfIngredients);
//        if(amount1 > amount2){
//            this.soupName = ingredient1.getName() + "-" + ingredient2.getName() + " Soup";
//        }
//        else if(amount1 < amount2){
//            this.soupName = ingredient2.getName() + "-" + ingredient1.getName() + " Soup";
//        }
//        else{
//            if(ingredient1.getName().compareToIgnoreCase(ingredient2.getName())<0){
//                this.soupName = ingredient1.getName() + "-" + ingredient2.getName() + " Soup";
//            }
//            else{
//                this.soupName = ingredient2.getName() + "-" + ingredient1.getName() + " Soup";
//            }
//        }
//
//        ingredients.add(ingredient1);
//        amountOfIngredient.add(amount1);
//
//        ingredients.add(ingredient2);
//        amountOfIngredient.add(amount2);
//
//        soupColorHandler();
//        bowlColorHandler();
//    }

    public Soup(ArrayList<Ingredient> ingredients){
        this.ingredients = ingredients;
        this.starRank = starRankHandler(ingredients.size());
        this.totalAmountOfIngredients = ingredients.size();
        soupNameHandler();


        soupColorHandler();
        bowlColorHandler();
        soupDescHandler();
    }

    // recreate soup with specified rank
    public Soup(ArrayList<Ingredient> ingredients, int starRank){
        this.ingredients = ingredients;
        this.totalAmountOfIngredients = ingredients.size();
        soupNameHandler();
        this.starRank = starRank;

        bowlColorHandler();
        soupColorHandler();
        soupDescHandler();
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
        else if(totalAmount >= 3 * minIngredients){
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
            double proportion = (double) 1/totalAmountOfIngredients;
            a += (int)(ingredients.get(i).getA() * proportion);
            r += (int)(ingredients.get(i).getR() * proportion);
            g += (int)(ingredients.get(i).getG() * proportion);
            b += (int)(ingredients.get(i).getB() * proportion);
        }
        
        soupColor = Color.argb(a, r, g, b);
    }

    // Subject to change
    private void soupNameHandler(){
        String[] names = new String[]{"Carrot", "Mushroom", "Radish", "Tomato", "Plant"};
        int[] counts = countIngredients();

        // sort
        for (int i = 1; i < counts.length; i++) {
            int current = counts[i];
            String temp = names[i];
            int j = i - 1;
            while(j >= 0 && current < counts[j]) {
                counts[j+1] = counts[j];
                names[j+1] = names[j];
                j--;
            }
            counts[j+1] = current;
            names[j+1] = temp;
        }

//        Log.i("SoupMaking",names[0] + ", " + names[1] + ", " + names[2]+ ", " + names[3]+ ", " + names[4]);
//        Log.i("SoupMaking",counts[0] + ", " + counts[1] + ", " + counts[2]+ ", " + counts[3]+ ", " + counts[4]);
        String tempName = "";

        if(counts[2] == 0) {
            for (int i = 4; i >2; i--) {
                if (counts[i] > 0) {
                    tempName += names[i] + "-";
                }
            }
            String substring = tempName.substring(0, tempName.length()-1);

            tempName = substring + " Soup";
        }
        else{
            tempName = "Generic Soup";
        }

        this.soupName = tempName;
    }

    private void soupDescHandler(){
        desc = "";
        String[] names = new String[]{"Carrot", "Mushroom", "Radish", "Tomato", "Plant"};
        int[] counts = countIngredients();

        // sort
        for (int i = 1; i < counts.length; i++) {
            int current = counts[i];
            String temp = names[i];
            int j = i - 1;
            while(j >= 0 && current < counts[j]) {
                counts[j+1] = counts[j];
                names[j+1] = names[j];
                j--;
            }
            counts[j+1] = current;
            names[j+1] = temp;
        }

        for(int i = 0; i < counts.length; i++){
            if(counts[i] > 1){
                desc += String.valueOf(counts[i]) + " " + names[i] + "s, ";
            }
            else if(counts[i] > 0){
                desc += String.valueOf(counts[i]) + " " + names[i] + ", ";
            }
        }

        Log.i("SoupDesc", desc);
    }

    private int[] countIngredients(){
       int[] counts = new int[5];

        int carrotCount = 0;
        int mushroomCount = 0;
        int radishCount = 0;
        int tomatoCount = 0;
        int plantCount = 0;

        for(Ingredient ingredient : ingredients){
            String lcName = ingredient.getName().toLowerCase();
            if(lcName.equals("carrot")){
                carrotCount++;
            }
            else if(lcName.equals("mushroom")){
                mushroomCount++;
            }
            else if(lcName.equals("radish")){
                radishCount++;
            }
            else if(lcName.equals("tomato")){
                tomatoCount++;
            }
            else if(lcName.equals("plant")){
                plantCount++;
            }
        }

        counts[0] = carrotCount;
        counts[1] = mushroomCount;
        counts[2] = radishCount;
        counts[3] = tomatoCount;
        counts[4] = plantCount;

        return counts;
    }

    // Maybe have different colored bowls depending on rank?
    private void bowlColorHandler(){
        if(starRank == 3){
            bowlColor = Color.argb(120,255, 215, 0);
        }
        else if(starRank == 2){
            bowlColor = Color.argb(100,250, 250, 250);
        }
        else{
            bowlColor = Color.argb(100,205, 127, 50);
        }
    }
    
    public void showSoup(ImageView imageView){
        imageView.setBackgroundResource(bowlResource);
        imageView.setImageResource(soupResource);
        imageView.setScaleType(ImageView.ScaleType.FIT_START);
        imageView.setImageTintMode(PorterDuff.Mode.SRC_ATOP);
        imageView.setImageTintList(ColorStateList.valueOf(soupColor));

        imageView.setBackgroundTintMode(PorterDuff.Mode.SRC_ATOP);
        imageView.setBackgroundTintList(ColorStateList.valueOf(bowlColor));
    }

    public void stopShowingSoup(ImageView imageView){
        imageView.setImageResource(android.R.color.transparent);
        imageView.setScaleType(ImageView.ScaleType.FIT_START);
        imageView.setImageTintMode(PorterDuff.Mode.SRC_ATOP);
        imageView.setImageTintList(ColorStateList.valueOf(Color.argb(0,0,0,0)));

        imageView.setBackgroundTintMode(PorterDuff.Mode.SRC_ATOP);
        imageView.setBackgroundTintList(ColorStateList.valueOf(Color.argb(0,0,0,0)));
    }

    public int getStarRank() {
        return starRank;
    }

    public void setStarRank(int starRank) {
        this.starRank = starRank;
    }

    public String getSoupName() {
        return soupName;
    }

    public void setSoupName(String soupName) {
        this.soupName = soupName;
    }

    public ArrayList<Ingredient> getIngredients() {
        return ingredients;
    }

    public String getDesc(){
        return desc;
    }
}
