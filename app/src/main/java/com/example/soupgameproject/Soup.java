package com.example.soupgameproject;

public class Soup {

    private int starRank;
    private String soupName;

    // Subject to change
    private final int minIngredients = 3;
    private final int maxIngredients = 10;

    // Basic Soup Constructor
    // 1 ingredient
    public Soup(Ingredient ingredient, int amount){
        this.starRank = starRankHandler(amount);
        this.soupName = ingredient.getName() + " Soup";
    }

    // Paired Soup Constructor
    // 2 different ingredients
    public Soup(Ingredient ingredient1, int amount1, Ingredient ingredient2, int amount2){
        this.starRank = starRankHandler(amount1 + amount2);
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
}
