/*
 * Copyright (c) 2015 Aysenur Bilgin
 */

package gui;

import fatSecret.Ingredient;
import fatSecret.Recipe;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;


public class RecipeViewer extends PApplet {


    Recipe chosenRecipe;
    private PImage recipeImage;
    PFont font;
    int y;
    int screenheight;
    int screenwidth;
    private boolean isMac;
    String difficultyOfRecipe;
    private int urlYbegin;
    private boolean onURL;
    private float urlYend;


    public void setup() {

//        System.out.println("In setup");
        size(1000, 800);
        font = createFont("Arial", 32);
        smooth();
        screenheight = height;
        screenwidth = width;

        if (System.getProperty("os.name").toLowerCase().contains("mac")) {
            isMac = true;
        }

    }

    public void draw() {
        if (chosenRecipe != null) {
            background(200);

            textFont(font);

            y = screenheight / 14;

            fill(180);
//            textSize(map(30, 0, 1000, 0, screenheight));

//            System.out.println("Case 7");
            textAlign(CORNER);
//            textSize(map(24,0,1480,0,screenwidth));
            textSize(map(20, 0, 1000, 0, screenheight));
            fill(0);
//            rect((width / 2) + 95, (height / 5) - 5, recipeImage.width + 10, recipeImage.height + 10);
//            image(recipeImage, (width / 2) + 100, height / 5);


            rect((screenwidth / 3) * 2 - 5, (screenheight / 5) - 5, recipeImage.width + 10, recipeImage.height + 10);
            image(recipeImage, (screenwidth / 3) * 2, screenheight / 5);

            int y_increment = 0;
            if (isMac) {
                y_increment = (int) map(25, 0, 700, 0, screenheight);
            } else {
                y_increment = (int) map(40, 0, 1000, 0, screenheight);
            }

            y = screenheight / 10;
            text("Dish Name: " + chosenRecipe.getName(), 40, y);
            y += y_increment;
            text("Preparation time: " + chosenRecipe.getPreparationTime() + " minutes", 40, y);
            y += y_increment;
            text("Cooking time: " + chosenRecipe.getCookingTime() + " minutes", 40, y);
            y += y_increment;
            text("Difficulty: " + difficultyOfRecipe, 40, y);
            y += y_increment;
            text("Calories: " + chosenRecipe.getCalories() + " kcal", 40, y);

            y += (int) (y_increment * 1.5);


            fill(0);

            text("Ingredients: ", 40, y);
            stroke(153);
            line(35, y + 3, 40 + textWidth("Ingredients:") + 5, y + 3);
            y += 3;
            for (Ingredient ingredient : chosenRecipe.getIngredients()) {
                y += y_increment / 2;
                text(ingredient.getFoodName(), 40, y);
            }
            y += (int) (y_increment * 1.5);
            text("Directions: ", 40, y);
            stroke(153);
            line(35, y + 3, 40 + textWidth("Directions:") + 5, y + 3);
            y += 3;

            int margin = 150;
            String regex = "(?<=\\G.{130})";
            if (isMac) {
                margin = 180;
                regex = "(?<=\\G.{120})";
            }

            int steps = 1;
            for (String desc : chosenRecipe.getDirections()) {

                float ingWidth = textWidth(desc);
                desc = steps + ". " + desc;
                if (ingWidth > screenwidth - margin) {
                    String[] split = desc.split(regex);
                    int count = 1;
                    for (String partialStr : split) {
                        y += (int) (y_increment / 2);
                        if (count == split.length) {
                            text(partialStr, 40, y);
                        } else {
                            text(partialStr + "-", 40, y);
                        }
                        count++;
                    }
                } else {
                    y += (int) (y_increment / 2);
                    text(desc, 40, y);
                }
                steps++;

            }

            y += (int) (y_increment * 1.5);

        }
    }


    public void stop()
    {

    }
    public void setChosenRecipe(Recipe r) {
//        String imgURL = r.getImageFilename().replace(".jpg", "_medium.jpg");
//        println(imgURL);
//        recipeImage = loadImage("img//" + imgURL);
        chosenRecipe = new Recipe(r);
    }

    public void setRecipeImage(PImage recipeImage)
    {
        this.recipeImage = recipeImage;
    }
    public void setDifficultyOfRecipe(String difficultyOfRecipe) {
        this.difficultyOfRecipe = difficultyOfRecipe;
    }
}
