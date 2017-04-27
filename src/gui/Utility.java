/*
 * Copyright (c) 2015 Aysenur Bilgin
 */

package gui;

import activity.Activity;
import activity.FuzzyCompositeConceptDifficulty;
import cbr.Base;
import cbr.Case;
import fatSecret.FatSecret;
import fatSecret.Recipe;
import fatSecret.recipeSearch.RecipeSearchResult;
import fatSecret.recipeSearch.RecipeSearchResults;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 *
 * @author abilgin
 */
public class Utility {
    public static float sketchWidth=0;
    public static float sketchHeight=0;
    public static ArrayList<Recipe> NEWRECIPES = new ArrayList<Recipe>();
    public static ArrayList<Recipe> ALLRECIPES = new ArrayList<Recipe>();
    public static ArrayList<Case> SOLUTIONS = new ArrayList<Case>();
    public static String RECIPE_SEARCH = "";
    public static double FETCHING_DATA_PROGRESS=0;
    public static double FETCHING_DATA_PROGRESS_BASE=0;
    public static boolean FETCHING_DATA=false;
    public static boolean FETCHING_DATA_BASE=false;
    public static boolean IS_BASE_CONSUMED=false;
    public static boolean NO_BROWSING_HISTORY = false;
    public static FatSecret fatSecret;
    public static int PAGE_NUMBER=0;
    public static boolean WANTED_RECIPE_FOUND=false;
    public static boolean BASE_EMPTY = false;
    public static boolean recipeRepeat = true;
    public static Base PUBLIC_BASE = new Base();
    public static String LEVEL_OF_TIREDNESS="";
    public static String LEVEL_OF_HUNGRINESS="";
    public static String LEVEL_OF_FREENESS="";
    public static String PREPTIME_FEEDBACK="";
    public static String COOKTIME_FEEDBACK="";
    public static String CUSTOMIZE_FEEDBACK="";
    public static String DIFF_FEEDBACK="";
    public static FuzzyCompositeConceptDifficulty fcconceptDifficulty;
    public static String foldername = "";
    public static Activity activity;
    public static Case queryCase = new Case();
    public static final String maxResult = "10";

    public static  boolean usingIntervalT2 = false;
    public static  boolean usingLGT2_EIA = false;
    public static  boolean usingLGT2_optimised = true;
    public static boolean loginSuccessful = false;

    //september 2013 new changes for the sets not to be deconstructed
    public static ArrayList<String> positiveEffectHistory = new ArrayList<String>();
    public static ArrayList<String> negativeEffectHistory = new ArrayList<String>();
    
    public static String USER_NAME ="";
    public static PrintWriter printWriter;
    public static FileWriter fileWriter;
    
    public enum Mood {HUNGRINESS, TIREDNESS, FREETIME}
    public enum FeedbackProperties {PREP_TIME, COOK_TIME, DIFF, CUSTOMIZE}


    public static void runSearch()
    {
        try{
            NEWRECIPES.clear();
            Utility.WANTED_RECIPE_FOUND = false;
            RecipeSearchResults recipeSearchResults = fatSecret.searchRecipe(RECIPE_SEARCH,fatSecret,
                    Integer.toString(PAGE_NUMBER), maxResult);

            for (int i=0;i<recipeSearchResults.getRecipeSearchResults().size();i++)
            {
                RecipeSearchResult recipeSearchResult = recipeSearchResults.getRecipeSearchResults().get(i);
//                System.out.println("\n\n"+recipeSearchResult);
                fatSecret.crawlOneRecipe(recipeSearchResult.getRecipeID(), fatSecret);
                Utility.FETCHING_DATA_PROGRESS = (double)(i+1)/(double)recipeSearchResults.getRecipeSearchResults().size();
                try{Thread.sleep(50);}
                catch (InterruptedException err){}
            }
            Collections.sort(Utility.NEWRECIPES, new Comparator<Recipe>() {
                @Override
                public int compare(Recipe recipe, Recipe recipe1) {
                    return new Double(recipe.getPreparationTime()).compareTo(recipe1.getPreparationTime());
                }
            });

            Utility.FETCHING_DATA_PROGRESS = 1;
            Utility.FETCHING_DATA = false;
        }
        catch (Exception e)
        {
            System.err.println(e);
        }
        
        if (Utility.NEWRECIPES.isEmpty()) {
            //try changing the key
            FatSecret.changeKey = true;
        }
        
        Utility.ALLRECIPES.addAll(Utility.NEWRECIPES);
        
    }

    public static void runSearch2()
    {

        Thread search = new Thread("Search")
        {
            public void run()
            {
                try{
                    NEWRECIPES.clear();
                    RecipeSearchResults recipeSearchResults = fatSecret.searchRecipe(RECIPE_SEARCH,fatSecret,
                            Integer.toString(PAGE_NUMBER), maxResult);

                    for (int i=0;i<recipeSearchResults.getRecipeSearchResults().size();i++)
                    {
                        RecipeSearchResult recipeSearchResult = recipeSearchResults.getRecipeSearchResults().get(i);
//                        System.out.println("\n\n"+recipeSearchResult);
                        fatSecret.crawlOneRecipe(recipeSearchResult.getRecipeID(), fatSecret);
                        Utility.FETCHING_DATA_PROGRESS = (double)(i+1)/(double)recipeSearchResults.getRecipeSearchResults().size();
                        try{Thread.sleep(50);}
                        catch (InterruptedException err){}
                    }
                    Collections.sort(Utility.NEWRECIPES, new Comparator<Recipe>() {
                        @Override
                        public int compare(Recipe recipe, Recipe recipe1) {
                            return new Double(recipe.getPreparationTime()).compareTo(recipe1.getPreparationTime());
                        }
                    });

                    if (Utility.NEWRECIPES.isEmpty()) {
                        //try changing the key
                        FatSecret.changeKey = true;
                    }
                    
                    Utility.ALLRECIPES.addAll(Utility.NEWRECIPES);

                    Utility.FETCHING_DATA_PROGRESS = 1;
                    Utility.FETCHING_DATA = false;
                }
                catch (Exception e)
                {
                    System.err.println(e);
                }
            }
        };

        
        search.start();



    }
}
