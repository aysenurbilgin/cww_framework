/*
 * Copyright (c) 2015 Aysenur Bilgin
 */

package gui;

import activity.Food;
import cbr.Case;
import cbr.FVPair;
import cbr.Feature;
import cbr.Value;
import com.centerkey.utils.BareBonesBrowserLaunch;
import de.looksgood.ani.Ani;
import edu.stanford.ejalbert.BrowserLauncher;
import edu.stanford.ejalbert.exception.BrowserLaunchingInitializingException;
import edu.stanford.ejalbert.exception.UnsupportedOperatingSystemException;
import fatSecret.Ingredient;
import fatSecret.Recipe;
import gui.Utility.FeedbackProperties;
import gui.Utility.Mood;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;

import javax.swing.*;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FatSecretSketch extends PApplet {

    DecimalFormat df = new DecimalFormat("#.##");
    PFont font;
    PFont fontBold, fontItalic;
    int screenwidth, screenheight, slidingx;
    float y = 0;
    float urlYbegin = 0, urlYend = 0;
    Ani aniY;
    String welcomeMessage = "Please use the menu on the left to get started...";

    String infoMessage1 = "This system is designed to adapt to your cooking experience.";
    String infoMessage2 = "Throughout your interaction, you will be shown recipes where the difficulty level of the recipe";
    String infoMessage3 = "is adaptive according to your feedback and assessed on the preparation time and cooking time criteria.";
    String infoMessage4 = "You may browse the recipes you have tried before or you may explore new ones.";
    public static String feedbacktext = "Please leave your feedback using the menu on the left...";
    //input questions
    String tiredQuestion = "To which degree are you feeling tired?";
    String hungryQuestion = "To which degree are you feeling hungry?";
    String freeQuestion = "To which degree are you free?";
    String creativityQuestion = "Are you feeling creative to try new recipes?";
    //feedback questions
    String preptimeFeedbackQuestion = "How long did the recipe take to prepare?";
    String cooktimeFeedbackQuestion = "How quick did the recipe take to cook?";
    String diffFeedbackQuestion = "How did you find the difficulty of the recipe?";
    String customizeQuestion = "Would you cook this recipe again?";
    //grammar files
    String yesnogrammar = "DS-question"; // for YES and NO
    String foodOptionsGrammar = "fs-food"; // for food options
    String hungrinessGrammar = "fs-hungry";
    String tirednessGrammar = "fs-tired";
    String freenessGrammar = "fs-free";
    String buttonGrammar = "fs-button";
    String prepTimeGrammar = "fs-preptime";
    String cookTimeGrammar = "fs-cooktime";
    String diffGrammar = "fs-diff";

    public static int currentRecipe = 0, markNoBaseRecipe = -1;
    public static String difficultyOfRecipe = "";
    public static Recipe chosenRecipe;
    //ControlP5 controlP5;
    static boolean welcomeMess = true;
    static boolean firstImageLoaded = false;
    static boolean difficultyPrintedOnce = false;
    static boolean guiTextPrintedOnce = false;
    static boolean backrepeat = false;
    static boolean recallfromprevpage = false;
    static boolean spoken = false;
    static boolean panelChanged = false;
    static boolean welcomeOnce = false;
    static boolean feedbackOnce = false;
    static boolean caseminusone = false;
    static boolean buttonOnce = false;
    public static boolean startOverFlag = false;
    PImage recipeImage;
    String imgURL;
    BrowserLauncher browserLauncher;
    private static boolean onURL = false;
    public static boolean isMac = false;
    private static boolean mousePressedNow = false;

    ImageButton imageButtonBack;
    ImageButton imageButtonGo;
    ImageButton imageButtonNext;
    ImageButton imageButtonView;
    RecipeViewer recipeViewer;
    //    PopupWindow recipeViewerWindow;
    RecipeViewerWindow recipeViewerWindow;

    @Override
    public void setup() {

//        size(displayWidth, displayHeight);
//             
//        screenwidth = this.getParent().getWidth();
//        screenheight = this.getParent().getHeight();
//        size(screenwidth, screenheight);
//        System.out.println("Width : " + width + " and height : " + height);
        screenheight = height;
        screenwidth = width - FatSecretGUI.controlPanel.getWidth();
        slidingx = screenwidth;
//        size(screenwidth, screenheight);
//        System.out.println("DisplayWidth : " + displayWidth + " and height : " + displayHeight);
//        System.out.println("buneWidth : " + width + " and height : " + height);
        // Width : 1377 and height : 912 in windows
        //Width : 1080 and height : 700 in mac
        //Width : 924 and height : 728 in mac alternate

        //get system type
        if (System.getProperty("os.name").toLowerCase().contains("mac")) {
            isMac = true;
        }

        if (!FatSecretSketch.isMac) {
            try {
                browserLauncher = new BrowserLauncher();
            } catch (BrowserLaunchingInitializingException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (UnsupportedOperatingSystemException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

        int fontSize;
        fontSize = 24;

        font = createFont("Arial", fontSize);
        fontBold = createFont("Arial Bold", fontSize);
        fontItalic = createFont("Arial Italic", fontSize);
        textFont(font);
//        textMode(CENTER);
//        textAlign(CENTER);
        fill(0);
        noStroke();
//        strokeWeight(5);
//        controlP5 = new ControlP5(this);

//        controlP5.setFont(font,fontSize-1);
//        controlP5.setColorCaptionLabel(color(255,255,255));
        smooth();

        addButtons();

        // you have to call always Ani.init() first!
        Ani.init(this);
        Ani.setDefaultEasing(Ani.QUART_IN);
        // save the references to the anis
        aniY = Ani.to(this, 2, "y", screenheight / 2 - fontSize);

        recipeViewerWindow = new RecipeViewerWindow("Detailed Recipe Viewer");

        recipeViewer = new RecipeViewer();

        recipeViewerWindow.addRecipeViewerSketch(recipeViewer);

    }


    @Override
    public void draw() {
        background(230);

        if (Utility.NO_BROWSING_HISTORY && !Utility.WANTED_RECIPE_FOUND && (Utility.FETCHING_DATA_PROGRESS == 1 || Utility.FETCHING_DATA_PROGRESS_BASE == 1)) {

            textSize(map(24, 0, 1480, 0, screenwidth));
            String messagehere = "";

            if (Utility.BASE_EMPTY && FatSecretGUI.noButton.isSelected()) {
                //            System.out.println("Case -1");

                messagehere = "No browsing history! Please use the menu on the left to make a new search...";

                FatSecretGUI.foodOptions.setVisible(true);
                FatSecretGUI.radioPanel.setVisible(true);
                FatSecretGUI.noButton.setEnabled(false);
                FatSecretGUI.yesButton.setSelected(true);
                FatSecretGUI.getRecipes.setEnabled(true);
                validate();

            } else {
                //TO DO: implement popup window g4p
                messagehere = "No browsing history!";
                imageButtonNext.update("next");
                imageButtonNext.display();
                currentRecipe = 0;

            }

            text(messagehere, screenwidth / 2, screenheight / 2);

            welcomeMess = false;
            startOverFlag = false;

        } else if (backrepeat && !Utility.WANTED_RECIPE_FOUND) {

            boolean experienced = false;
            if ((!Utility.IS_BASE_CONSUMED || currentRecipe < markNoBaseRecipe) && FatSecretGUI.noButton.isSelected()) {
                experienced = true;
            }
            if (currentRecipe < Utility.ALLRECIPES.size()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    Logger.getLogger(FatSecretSketch.class.getName()).log(Level.SEVERE, null, ex);
                }

                synchronized (Utility.ALLRECIPES) {
                    Recipe recipe = Utility.ALLRECIPES.get(currentRecipe);

                    String textToPrint = recipe.getName();

                    if (!difficultyPrintedOnce) {
                        difficultyPrintedOnce = true;
                        difficultyOfRecipe = recipe.getUserFriendlyDifficulty();
                    }

                    displayRecipeSearchResult(experienced, textToPrint, recipe);

                    welcomeMess = false;
                    startOverFlag = false;
                }
            }

        } else if (Utility.FETCHING_DATA_PROGRESS_BASE == 1 && !Utility.FETCHING_DATA_BASE && !Utility.WANTED_RECIPE_FOUND && !Utility.IS_BASE_CONSUMED && FatSecretGUI.noButton.isSelected()) {
//            textAlign(CENTER);
            synchronized (Utility.ALLRECIPES) {
                if (!Utility.ALLRECIPES.isEmpty() && currentRecipe < Utility.ALLRECIPES.size()) {

                    Recipe recipe = Utility.ALLRECIPES.get(currentRecipe);
                    String textToPrint = recipe.getName() + "? \n";

                    if (!difficultyPrintedOnce) {
                        difficultyPrintedOnce = true;
                        difficultyOfRecipe = recipe.getUserFriendlyDifficulty();
                        FatSecretGUI.getRecipes.setEnabled(false);
                        FatSecretGUI.controlPanel.remove(FatSecretGUI.progressBar);
                        FatSecretGUI.controlPanel.validate();
                        FatSecretGUI.controlPanel.repaint();
                    }

                    displayRecipeSearchResult(true, textToPrint, recipe);
                } else {
//                controlP5.get("yes").hide();
//                controlP5.get("no").hide();
                    Utility.IS_BASE_CONSUMED = true;
                    markNoBaseRecipe = currentRecipe;
                    //text("Let's try a new recipe...",width/2,y);

                }
                welcomeMess = false;
                startOverFlag = false;
            }
        } else if (Utility.FETCHING_DATA_PROGRESS == 1 && !Utility.FETCHING_DATA && !Utility.WANTED_RECIPE_FOUND) {
            synchronized (Utility.ALLRECIPES) {
                if (!Utility.ALLRECIPES.isEmpty() && currentRecipe < Utility.ALLRECIPES.size()) {

                    boolean experienced = false;
                    if ((!Utility.IS_BASE_CONSUMED || currentRecipe < markNoBaseRecipe) && FatSecretGUI.noButton.isSelected()) {
                        experienced = true;
                    }
                    Recipe recipe = Utility.ALLRECIPES.get(currentRecipe);
                    String textToPrint = recipe.getName();

                    if (!difficultyPrintedOnce) {

                        if (Utility.usingIntervalT2 || Utility.usingLGT2_EIA) {
                            String inputs = JOptionPane.showInputDialog(frame, "Prep Time, Cook Time?");
                            String inp[] = inputs.split(",");

                            //according to new EIA the mfs are in domain [0,10]
                            //hence need to normalize the inputs by dividing with 10
                            Utility.ALLRECIPES.get(currentRecipe).setPreparationTime(Double.parseDouble(inp[0]) / 10.0);
                            Utility.ALLRECIPES.get(currentRecipe).setCookingTime(Double.parseDouble(inp[1]) / 10.0);

                            System.out.println("Recipe is : " + Utility.ALLRECIPES.get(currentRecipe).toString());
                        } else {
                        }

                        difficultyPrintedOnce = true;
                        difficultyOfRecipe = recipe.getUserFriendlyDifficulty();
                        System.out.println("Case 3 and current chosenRecipe " + currentRecipe);
                        FatSecretGUI.getRecipes.setEnabled(false);
                        FatSecretGUI.controlPanel.remove(FatSecretGUI.progressBar);
                        FatSecretGUI.controlPanel.validate();
                    }

                    displayRecipeSearchResult(experienced, textToPrint, recipe);

                } else {
//                controlP5.get("yes").hide();
//                controlP5.get("no").hide();
                    text("No recipes to show, please choose other ingredients...", screenwidth / 2, screenheight / 2);
                    FatSecretGUI.foodOptions.setVisible(true);
                    FatSecretGUI.getRecipes.setEnabled(true);
                    validate();
                }
                welcomeMess = false;
                startOverFlag = false;
            }
        } else if (Utility.BASE_EMPTY && !Utility.WANTED_RECIPE_FOUND && !Utility.FETCHING_DATA && Utility.FETCHING_DATA_PROGRESS == 0) {

            textAlign(CENTER);
            textSize(map(24, 0, 1480, 0, screenwidth));
            text("No tried recipes yet! Please choose ingredients...", screenwidth / 2, screenheight / 2);
            FatSecretGUI.foodOptions.setVisible(true);
            FatSecretGUI.radioPanel.setVisible(true);
            FatSecretGUI.noButton.setEnabled(false);
            FatSecretGUI.yesButton.setSelected(true);
            welcomeMess = false;
            startOverFlag = false;

        } else if (startOverFlag || welcomeMess) {
            textAlign(CENTER);
            textSize(map(24, 0, 1480, 0, screenwidth));
            int y = screenheight / 3;
            int y_increment = (int) map(30, 0, 900, 0, screenheight);
            if (startOverFlag) {
                text("Welcome back " + Utility.USER_NAME + "!", screenwidth / 2, y);
                y += y_increment * 2;
                text(infoMessage1, screenwidth / 2, y);
                y += y_increment;
                text(infoMessage2, screenwidth / 2, y);
                y += y_increment;
                text(infoMessage3, screenwidth / 2, y);
                y += y_increment;
                text(infoMessage4, screenwidth / 2, y);
                y += y_increment * 2;
                text(welcomeMessage, screenwidth / 2, y);

                firstImageLoaded = false;
                currentRecipe = 0;
            } else {
                text("Welcome " + Utility.USER_NAME + "!", screenwidth / 2, y);
                y += y_increment * 2;
                text(infoMessage1, screenwidth / 2, y);
                y += y_increment;
                text(infoMessage2, screenwidth / 2, y);
                y += y_increment;
                text(infoMessage3, screenwidth / 2, y);
                y += y_increment;
                text(infoMessage4, screenwidth / 2, y);
                y += y_increment * 2;
                text(welcomeMessage, screenwidth / 2, y);
            }

        } else if (Utility.FETCHING_DATA_PROGRESS > 0.5 && Utility.FETCHING_DATA && Utility.IS_BASE_CONSUMED) {

            textAlign(CENTER);
            textSize(map(24, 0, 1480, 0, screenwidth));
            text("Let's try a new recipe...", screenwidth / 2, screenheight / 2);
//            controlP5.get("yes").hide();
//            controlP5.get("no").hide();

        } else if (Utility.FETCHING_DATA_PROGRESS < 0.5 && Utility.FETCHING_DATA && Utility.IS_BASE_CONSUMED) {
            textAlign(CENTER);
//            text("Please wait for the next recipes to be loaded",width/2,y);
            text("Please wait for the next recipes to be loaded...", screenwidth / 2, screenheight / 2);
//            controlP5.get("yes").hide();
//            controlP5.get("no").hide();
        } else if (Utility.FETCHING_DATA_PROGRESS < 1 && Utility.FETCHING_DATA) {
            textAlign(CENTER);
//            text("Please wait for the next recipes to be loaded",width/2,y);
            text("Please wait for the next recipes to be loaded...", screenwidth / 2, screenheight / 2);
//            controlP5.get("yes").hide();
//            controlP5.get("no").hide();
        } else if (Utility.WANTED_RECIPE_FOUND) {
            textFont(font);

            y = screenheight / 14;

            fill(180);
            textSize(map(30, 0, 1000, 0, screenheight));
            text(feedbacktext, slidingx, y);
            slidingx += -1.5;
            if (slidingx < -100) {
                slidingx = screenwidth;
            }

//            FatSecretGUI.foodOptions.setVisible(false);
//            FatSecretGUI.getRecipes.setEnabled(false);
            if (!panelChanged) {
                FatSecretGUI.changePanels();
                panelChanged = true;
            }
            textAlign(CORNER);
//            textSize(map(24,0,1480,0,screenwidth));
            textSize(map(16, 0, 1000, 0, screenheight));
            fill(0);
//            rect((width / 2) + 95, (height / 5) - 5, recipeImage.width + 10, recipeImage.height + 10);
//            image(recipeImage, (width / 2) + 100, height / 5);

            rect((screenwidth / 3) * 2 - 5, (screenheight / 4) - 5, recipeImage.width + 10, recipeImage.height + 10);
            image(recipeImage, (screenwidth / 3) * 2, screenheight / 4);

            int y_increment = 0;
            if (isMac) {
                y_increment = (int) map(25, 0, 700, 0, screenheight);
            } else {
                y_increment = (int) map(30, 0, 900, 0, screenheight);
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
            y += y_increment;
            text("For the full recipe, please go to (opens in new window) :", 40, y);
            urlYbegin = y;
            y += y_increment;

            if (onURL) {
                fill(255, 200, 200);
            } else {
                fill(0, 0, 255);
            }
            text(chosenRecipe.getRecipeUrl(), 40, y);
            urlYend = y + map(16, 0, 1000, 0, screenheight);
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
            String regex = "(?<=\\G.{150})";
            if (isMac) {
                margin = 180;
                regex = "(?<=\\G.{140})";
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

            if (chosenRecipe.getPersonalNotes().isEmpty()) {
                //then display no notes message
                textFont(fontItalic);
                textSize(map(10, 0, 1000, 0, screenheight));
                text("No personal notes. ", 40, y);
            }
            //notes addition
            else {
                text("Personal Notes: ", 40, y);
                stroke(153);
                line(35, y + 3, 40 + textWidth("Personal Notes:") + 5, y + 3);
                y += 3;

                steps = 1;
                for (String desc : chosenRecipe.getPersonalNotes()) {

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
            }

        }//end if block
        else {
//            System.out.println("No matching condition!");
            //System.exit(1);
        }
        mousePressedNow = false;

    }

    private void addButtons() {

        //image buttons
        // Define and create image button

        String path;

        path = "." + File.separator + "icons" + File.separator;

        PImage leftNormal = loadImage(path + "arrow-left-normal.png");  //base
        PImage leftHover = loadImage(path + "arrow-left-hover.png"); //roll
        PImage leftPressed = loadImage(path + "arrow-left-pressed.png"); //down
        int w = leftNormal.width;
        int h = leftNormal.height;
        int x = ((screenwidth - w) / 6) * 1; // Two fifth the horizontal space
        int y = ((screenheight - h) / 8) * 7; // Four fifth the vertical space
        imageButtonBack = new ImageButton(x, y, w, h, leftNormal, leftHover, leftPressed);

        PImage midNormal = loadImage(path + "check-normal.png");  //base
        PImage midHover = loadImage(path + "check-hover.png"); //roll
        PImage midPressed = loadImage(path + "check-pressed.png"); //down
        x = ((screenwidth - w) / 6) * 2; // Three fifth the horizontal space
        imageButtonGo = new ImageButton(x, y, w, h, midNormal, midHover, midPressed);

        PImage rightNormal = loadImage(path + "arrow-right-normal.png");  //base
        PImage rightHover = loadImage(path + "arrow-right-hover.png"); //roll
        PImage rightPressed = loadImage(path + "arrow-right-pressed.png"); //down
        x = ((screenwidth - w) / 6) * 3; // Four fifth the horizontal space
        imageButtonNext = new ImageButton(x, y, w, h, rightNormal, rightHover, rightPressed);

        PImage viewNormal = loadImage(path + "view-normals.png");  //base
        PImage viewHover = loadImage(path + "view-hovers.png"); //roll
        PImage viewPressed = loadImage(path + "view-presseds.png"); //down
        x = ((screenwidth - w) / 6) * 2; // Four fifth the horizontal space
        y = ((screenheight - h) / 9) * 3; // Four fifth the vertical space
        imageButtonView = new ImageButton(x, y, w, h, viewNormal, viewHover, viewPressed);

    }

    public void yes(int theValue) {
        recipeViewerWindow.setVisible(false);
        Utility.WANTED_RECIPE_FOUND = true;
//        controlP5.get("yes").hide();
//        controlP5.get("no").hide();

        synchronized (Utility.ALLRECIPES) {
            Recipe r = Utility.ALLRECIPES.get(currentRecipe);


            imgURL = r.getImageFilename().replace(".jpg", "_medium.jpg");

            recipeImage = loadImage("img//" + imgURL);
            chosenRecipe = r;

        }

        difficultyPrintedOnce = false;
        buttonOnce = false;

    }

    private ArrayList<String> convertIngredientsToStrings(Set<Ingredient> ingredients) {
        ArrayList<String> ings = new ArrayList<String>();

        for (Ingredient i : ingredients) {
            ings.add(i.getFoodName().toLowerCase());
        }

        return ings;
    }

    public void no(int theValue) {

        if (recipeViewer != null) {
//            recipeViewer.stop();
            recipeViewerWindow.setVisible(false);
        }

        difficultyPrintedOnce = false;
        buttonOnce = false;
        spoken = false;
        backrepeat = false;
        caseminusone = false;
        Utility.NO_BROWSING_HISTORY = false;

        recallfromprevpage = false;

//        FatSecretGUI.controlPanel.remove(FatSecretGUI.progressBar);

        synchronized (Utility.ALLRECIPES) {


            if (currentRecipe < Utility.ALLRECIPES.size() - 1) {

                currentRecipe++;

                imgURL = Utility.ALLRECIPES.get(currentRecipe).getImageFilename().replace(".jpg", "_medium.jpg");

                recipeImage = loadImage("img//" + imgURL);

            } else {

                Utility.FETCHING_DATA_PROGRESS = 0;
                Utility.PAGE_NUMBER++;
                Utility.FETCHING_DATA = true;
                firstImageLoaded = false;
                currentRecipe = Utility.ALLRECIPES.size();
                Utility.runSearch2();
            }
        }

    }

    public void back() {

        if (recipeViewer != null) {
            recipeViewer.stop();
            recipeViewerWindow.setVisible(false);
        }
        difficultyPrintedOnce = false;
        buttonOnce = false;
        spoken = false;
        firstImageLoaded = false;

        if (currentRecipe > 0) {
//            int removeR = Utility.ALLRECIPES.size() - 1;
//            Utility.ALLRECIPES.remove(removeR);
            currentRecipe--;
            backrepeat = true;
            recallfromprevpage = false;
        }
        else {
            Utility.NO_BROWSING_HISTORY = true;
        }

    }

    public void view() {

//        RecipeViewer recipeViewer = new RecipeViewer();
//        PopupWindow recipeViewerWindow  = new PopupWindow(this, recipeViewer, "Detailed Recipe View", 1000, 800);
//        recipeViewerWindow.setBounds(0,0,640, 480);
//        recipeViewerWindow.setResizable(false);
//        recipeViewerWindow.revalidate();

//        println("Current recipe: "+currentRecipe + " "+Utility.ALLRECIPES.get(currentRecipe).getName());
//        recipeViewerWindow.setVisible(true);

//        recipeViewer = new RecipeViewer();
//        recipeViewerWindow  = new PopupWindow(this, recipeViewer, "Detailed recipe viewer", 500, 100);
//        recipeViewerWindow.addWindowListener(new WindowAdapter() {
//            @Override
//            public void windowClosing(WindowEvent e) {
//                recipeViewer.stop();
//                recipeViewerWindow.setVisible(false);
//            }
//        });

//        synchronized(Utility.ALLRECIPES) {
        if (!recipeViewer.isDisplayable()) {
//            recipeViewer.start();
            try {
                recipeViewer.init();
            } catch (NullPointerException e) {
                println("Exception from setup");
            }
//            recipeViewer.redraw();
        }

        recipeViewer.setChosenRecipe(Utility.ALLRECIPES.get(currentRecipe));
//        }
        recipeViewer.setDifficultyOfRecipe(difficultyOfRecipe);


        try {
            recipeViewer.setRecipeImage((PImage) recipeImage.clone());
//        recipeViewer.setRecipeImage(recipeImage);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


        recipeViewerWindow.setVisible(true);

//        println("Chosen recipe: "+((RecipeViewer)recipeViewerWindow.getContentPane().getComponent(0)).chosenRecipe);
//        ((RecipeViewer)recipeViewerWindow.getContentPane().getComponent(0)).repaint();

    }

    @Override
    public void mouseMoved() {
        if (mouseX > 41 && mouseX < 661 && mouseY > urlYbegin && mouseY < urlYend) {
            onURL = true;
        } else {
            onURL = false;
        }
    }

    @Override
    public void mousePressed() {
        mousePressedNow = true;

        if ((chosenRecipe != null) && (mouseX > 41 && mouseX < 661 && mouseY > urlYbegin && mouseY < urlYend)) {
//            browserLauncher.openURLinBrowser("Chrome",chosenRecipe.getRecipeUrl());

            if (isMac) {
                BareBonesBrowserLaunch.openURL(chosenRecipe.getRecipeUrl());
            } else {
                browserLauncher.openURLinBrowser(chosenRecipe.getRecipeUrl());
            }
        }

    }

    public static void addRecipeToCaseBase(Recipe chosenR) {

        Case q = new Case();
        q.addFVtoCase(new FVPair(new Feature("tiredness"), new Value(Utility.LEVEL_OF_TIREDNESS)));
        q.addFVtoCase(new FVPair(new Feature("hungriness"), new Value(Utility.LEVEL_OF_HUNGRINESS)));
        q.addFVtoCase(new FVPair(new Feature("leisuretime"), new Value(Utility.LEVEL_OF_FREENESS)));

        q.addSolution(new Food(chosenR));

        //find the difficulty
        //difficultyOfRecipe = chosenRecipe.getUserFriendlyDifficulty();
        //q.addSolution(new Food(chosenR.getName(), chosenR.getPreparationTime(), chosenR.getCookingTime()));

        //if feedback is provided, adapt the case and save!

        Utility.PUBLIC_BASE.addCaseToBase(Utility.activity, q);

//        System.out.println("Case to be added : " + q.toString());

        Utility.PUBLIC_BASE.save();

        Utility.PUBLIC_BASE.printBase();

//        Utility.fcconceptDifficulty.establishCompositeSets();

        //september 2013 adding overall time
        Utility.fcconceptDifficulty.establishCompositeSets2(chosenR.getOverallTime());

    }

    void star(int x, int y, float radius) {
        beginShape();
        for (int i = 0; i <= 20; i++) {
            fill(200, 100, 100);
            stroke(200, 100, 100);
            float yStar = sin(radians(i * 135)) * radius + y;
            float xStar = cos(radians(i * 135)) * radius + x;
            vertex(xStar, yStar);

        }
        endShape();

    }

    private void displayRecipeSearchResult(boolean drawStar, String textToPrint, Recipe recipe) {

        if (!firstImageLoaded) {

            imgURL = recipe.getImageFilename().replace(".jpg", "_medium.jpg");

            recipeImage = loadImage("img//" + imgURL);
            firstImageLoaded = true;
        }

        imageButtonBack.update("back");
        imageButtonBack.display();

        imageButtonGo.update("go");
        imageButtonGo.display();

        imageButtonNext.update("next");
        imageButtonNext.display();

        imageButtonView.update("view");
        imageButtonView.display();

        if (drawStar) {
            if (isMac) {
                star((screenwidth / 6) * 4, screenheight / 2, map(100, 0, 1080, 0, screenwidth));
                fill(0);
                text(difficultyOfRecipe, (screenwidth / 6) * 4, screenheight / 2 + 5);
            } else {
//                star(width - 300, height / 4, 140);
//                fill(255);
//                text(difficultyOfRecipe, width - 300, height / 4);
                star((screenwidth / 6) * 4, screenheight / 2, map(120, 0, 1480, 0, screenwidth));
                fill(0);
                text(difficultyOfRecipe, (screenwidth / 6) * 4, screenheight / 2 + 5);
            }
        } else {
//            textToPrint += "? " + " It seems " + difficultyOfRecipe + "... \n";
            textToPrint += "? \n";
            showSummary(recipe);
        }

        textAlign(CENTER);
        textSize(map(24, 0, 1480, 0, screenwidth));
        text(textToPrint, screenwidth / 3, screenheight / 4);


        if (recipeImage == null) {

            String path;
//            if (isMac) {
//                path = "/Users/ays/Dropbox/PhD/CWWFramework/icons/";
//            } else {
////                path = "C:\\Users\\abilgin\\Dropbox\\PhD\\CWWFramework\\icons\\";
////                path = "C:\\Users\\Ays\\Documents\\NetBeansProjects\\CWWFramework\\icons\\";  //ispace computer
//
////                  path = "C:\\Users\\abilgin\\Documents\\NetBeansProjects\\CWWFramework\\icons\\";
//                path = ".\\icons\\";
//            }

            path = "." + File.separator + "icons" + File.separator;
            recipeImage = loadImage(path + "noimage.jpg");
        }

        fill(0);
        stroke(0);
        rect((screenwidth / 3 - recipeImage.width / 2) - 5, (screenheight / 2 - recipeImage.height / 2) - 5, recipeImage.width + 10, recipeImage.height + 10);
        image(recipeImage, (screenwidth / 3) - recipeImage.width / 2, screenheight / 2 - recipeImage.height / 2);
    }

    private void showSummary(Recipe r) {

        textAlign(CORNER, CENTER);
        textSize(14);
        fill(0);

        int x = (screenwidth / 6) * 4;
        int y_increment = 0;
        if (isMac) {
            y_increment = (int) map(30, 0, 700, 0, screenheight);
        } else {
            y_increment = (int) map(30, 0, 900, 0, screenheight);
        }

        y = (float) (screenheight / 3.5);
        int initialy = (int) y;


        text("Preparation time: " + r.getPreparationTime() + " mins \n", x, y);
        stroke(153);
        line(x - 5, y + 3, x + textWidth("Preparation time") + 5, y + 3);
        y += y_increment;
        text("Cooking time: " + r.getCookingTime() + " mins \n", x, y);
        stroke(153);
        line(x - 5, y + 3, x + textWidth("Cooking time") + 5, y + 3);
        y += y_increment;
        textFont(fontBold);
        textSize(14);
        fill(200, 100, 100);
        text("Difficulty: " + difficultyOfRecipe + "\n", x, y);
//        stroke(153);
        line(x - 5, y + 3, x + textWidth("Difficulty") + 5, y + 3);
        textFont(font);
        textSize(14);
        validate();
        fill(0);

        y += y_increment;
        text("Calories: " + r.getCalories() + " kcal\n", x, y);
        stroke(153);
        line(x - 5, y + 3, x + textWidth("Calories") + 5, y + 3);
        y += y_increment;

        text("Ingredients: \n", x, y);
        stroke(153);
        line(x - 5, y + 3, x + textWidth("Ingredients:") + 5, y + 3);

        for (Ingredient ingredient : r.getIngredients()) {

            String ing = ingredient.getFoodName();
            float ingWidth = textWidth(ing);

            if (ingWidth > screenwidth / 4) {
                String[] split = ing.split("(?<=\\G.{25})");
                int count = 1;
                for (String partialStr : split) {

                    y += (int) (y_increment / 1.5);
                    if (count == split.length) {
                        text(partialStr, x, y);
                    } else {
                        text(partialStr + "-", x, y);
                    }
                    count++;
                }
            } else {
                y += (int) (y_increment / 1.5);
                text(ing, x, y);
            }

        }


        stroke(153);
        line(x - 10, initialy - 25, x - 10, y + 25);

    }

    private boolean checkTopic(String userString, Object obj) {

//        System.out.println("Check topic : " + userString);

        if (obj instanceof Mood) {
            Mood mood = (Mood) obj;
            if (mood.equals(Mood.TIREDNESS)) {
                if (userString.contains("tired") || userString.contains("energetic")) {
                    return true;
                }
            } else if (mood.equals(Mood.HUNGRINESS)) {
                if (userString.contains("hungry") || userString.contains("full")) {
                    return true;
                }
            } else if (mood.equals(Mood.FREETIME)) {
                if (userString.contains("free") || userString.contains("busy")) {
                    return true;
                }
            }
        } else if (obj instanceof FeedbackProperties) {
            FeedbackProperties fp = (FeedbackProperties) obj;
            if (fp.equals(FeedbackProperties.PREP_TIME)) {
                if (userString.contains("long") || userString.contains("short")) {
                    return true;
                }
            } else if (fp.equals(FeedbackProperties.COOK_TIME)) {
                if (userString.contains("quick") || userString.contains("slow")) {
                    return true;
                }
            } else if (fp.equals(FeedbackProperties.DIFF)) {
                if (userString.contains("easy") || userString.contains("challenging")) {
                    return true;
                }
            } else if (fp.equals(FeedbackProperties.CUSTOMIZE)) {
                if (userString.equalsIgnoreCase("yes") || userString.equalsIgnoreCase("no")) {
                    return true;
                }
            }
        }

        return false;

    }

    private void checkSelectionGUI(JPanel panel, String selectedText) {

        for (int i = 0; i < panel.getComponents().length; i++) {
            JCheckBox cb = (JCheckBox) panel.getComponent(i);

            if (cb.getText().equalsIgnoreCase(selectedText)) {
                cb.setSelected(true);
//                System.out.println("Setting to true : " + cb.getText());
            }
        }

    }

    public static void refreshSketchParameters() {

        currentRecipe = 0;
        markNoBaseRecipe = -1;

        firstImageLoaded = false;
        difficultyPrintedOnce = false;
        guiTextPrintedOnce = false;
        backrepeat = false;
        recallfromprevpage = false;
        spoken = false;
        panelChanged = false;
        welcomeOnce = false;
        feedbackOnce = false;
        caseminusone = false;
        buttonOnce = false;

        onURL = false;
        isMac = false;
        mousePressedNow = false;
    }

    public class Button {

        int x, y; // Top-left corner
        int w, h; // Width, height
        //        int basecolor = unhex("00007F");
//        int rollcolor = unhex("7F00FF");
//        int downcolor = unhex("FFFF00");
        int currentcolor;
        boolean over = false; // Mouse over button
        boolean pressed = false; // Mouse pressed over button

        public Button(int ix, int iy, int iw, int ih) {
            x = ix;
            y = iy;
            w = iw;
            h = ih;
//            currentcolor = basecolor;
        }

        void update(String whichButton) {
            // Is the mouse over the button now?
            over = mouseX >= x && mouseX < x + w
                    && mouseY >= y && mouseY < y + h;

            // Check for button activation (mouse released over button
            // when button in the pressed state)
            if (pressed && over && !mousePressed) {

                if (whichButton.contentEquals("go")) {
                    println("Button go activated!");
                    yes(0);
                } else if (whichButton.contentEquals("next")) {
                    println("Button next activated!");
                    no(0);
                } else if (whichButton.contentEquals("back")) {
                    println("Button back activated!");
                    back();
                } else {
                    println("Button view activated!");
                    view();
                }


            }

            // If we registered a press before and the mouse is
            // pressed now, maintain pressed;
            // if mouse is over now and mouse was pressed just now,
            // register the press
            pressed = (pressed && mousePressed)
                    || (over && mousePressedNow);

            // If the mouse is over the button and pressed, show the
            // button as down; if over but not presed, show the rollover;
            // otherwise show as normal
//            currentcolor = over ? (pressed ? downcolor : rollcolor)
//                    : basecolor;
        }

        void display() {
            pushStyle();
            fill(currentcolor);
            noStroke();
            rect(x, y, w, h);
            popStyle();
        }
    }

    public class ImageButton extends Button {

        PImage base;
        PImage roll;
        PImage down;
        PImage currentimage; // Reference to one of base, roll, down

        public ImageButton(int ix, int iy, int iw, int ih, PImage ibase, PImage iroll, PImage idown) {
            super(ix, iy, iw, ih);
            base = ibase;
            roll = iroll;
            down = idown;
            currentimage = base;
        }

        @Override
        void update(String whichButton) {
            // Sort out the button state and check for activation
            super.update(whichButton);

            // If the mouse is over the button and the button is registered
            // as pressed, show the button as down; if over but not pressed,
            // show the rollover; otherwise show as normal
            currentimage = over ? (pressed ? down : roll)
                    : base;
        }

        @Override
        void display() {
            image(currentimage, x, y);
        }
    }

}


