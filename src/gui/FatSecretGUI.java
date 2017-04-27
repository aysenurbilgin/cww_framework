/*
 * Copyright (c) 2015 Aysenur Bilgin
 */

package gui;

import activity.Food;
import activity.FuzzyCompositeConceptDifficulty;
import cbr.*;
import edu.stanford.ejalbert.BrowserLauncher;
import fatSecret.Recipe;
import processing.core.PApplet;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;


public class FatSecretGUI extends JFrame implements ItemListener, ActionListener {
    private static JPanel fsPanel;
    public static JPanel controlPanel;
    public static JPanel radioPanel;
    public static JPanel feedbackPanel;
    public static JPanel loginPanel;
    public static JTextField username;
    public static JTextField notes;
    private static Container panelsContainer;
    public static JPanel feedbackPrepTimePanel;
    public static JPanel feedbackCookTimePanel;
    public static JPanel feedbackDifficultyPanel;
    public static JPanel feedbackRepeatRecipePanel;
    JPanel thankPanel;
    JPanel buttonPanel;
    JLabel taLabel;
    public static JPanel foodOptions;
    //tiredness radio buttons
    public static JRadioButton exttiredButton;
    public static JRadioButton verytiredButton;
    public static JRadioButton tiredButton;
    public static JRadioButton enerButton;
    public static JRadioButton veryenerButton;
    public static JRadioButton extenerButton;
    public static ButtonGroup tiredbgroup;
    
    //hungriness radio buttons
    public static JRadioButton exthungryButton;
    public static JRadioButton veryhungryButton;
    public static JRadioButton hungryButton;
    public static JRadioButton fullButton;
    public static JRadioButton veryfullButton;
    public static JRadioButton extfullButton;
    public static ButtonGroup hungrybgroup;
    
    //freetime radio buttons
    public static JRadioButton extfreeButton;
    public static JRadioButton veryfreeButton;
    public static JRadioButton freeButton;
    public static JRadioButton busyButton;
    public static JRadioButton verybusyButton;
    public static JRadioButton extbusyButton;
    public static ButtonGroup freebgroup;
    
    public static JRadioButton noButton;
    public static JRadioButton yesButton;
    //difficulty radio buttons for feedback
    public static JRadioButton fexteasyButton;
    public static JRadioButton fveryeasyButton;
    public static JRadioButton feasyButton;
    public static JRadioButton fchalButton;
    public static JRadioButton fverychalButton;
    public static JRadioButton fextchalButton;
    //preparation time radio buttons for feedback
    public static JRadioButton ftpextshortButton;
    public static JRadioButton ftpveryshortButton;
    public static JRadioButton ftpshortButton;
    public static JRadioButton ftplongButton;
    public static JRadioButton ftpverylongButton;
    public static JRadioButton ftpextlongButton;
    //cooking time radio buttons for feedback
    public static JRadioButton ftcextquickButton;
    public static JRadioButton ftcveryquickButton;
    public static JRadioButton ftcquickButton;
    public static JRadioButton ftcslowButton;
    public static JRadioButton ftcveryslowButton;
    public static JRadioButton ftcextslowButton;
    //ingredients radio buttons for feedback
    public static JRadioButton ftfewButton;
    public static JRadioButton ftalotButton;
    //feedback repeating chosenRecipe
    public static JRadioButton fyesButton;
    public static JRadioButton fnoButton;
    //action buttons
    public static JButton getRecipes;
    public static JButton submitFeedback;
    public static JButton startOverButton;
    public static JButton exitButton;
    public PApplet fsSketch;
    private JCheckBox chickenCheckBox;
    private JCheckBox meatCheckBox;
    private JCheckBox fishCheckBox;
    private JCheckBox riceCheckBox;
    private JCheckBox pastaCheckBox;
    private JCheckBox pizzaCheckBox;
    private JCheckBox breadCheckBox;
    private JCheckBox cheeseCheckBox;
    private JCheckBox vegetablesCheckBox;
    public static JProgressBar progressBar;
    
    JButton enterName;
    
    BrowserLauncher browserLauncher;

    public FatSecretGUI(String name)
    {
        super(name);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
//        System.out.println("GUI JAVA:  width : " + dim.width + " height : " + dim.height);
//        setBounds(0,0,dim.width,dim.height-100);
//        setResizable(false);
        setBounds(0,0,dim.width,dim.height - 50);
//        setBounds(0,0,1600, 900);

        initPanels();

        fsPanel.add(fsSketch);
        panelsContainer = new JPanel(new BorderLayout());
        
//        panelsContainer.add(loginPanel, BorderLayout.CENTER);
        panelsContainer.add(controlPanel,BorderLayout.WEST);
        panelsContainer.add(fsPanel,BorderLayout.CENTER);


//        setContentPane(panelsContainer);
        setContentPane(loginPanel);
//        fsSketch.init();
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

    }

    private void initPanels()
    {
        //login panel
        loginPanel = new JPanel();
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.PAGE_AXIS));
        loginPanel.setSize(getWidth(), getHeight());
        JLabel loginLabel = new JLabel("Please enter your name:");
        username = new JTextField(30);
        
        enterName = new JButton("Login");
        enterName.addActionListener(this);
        enterName.setActionCommand("Login");
//        loginPanel.add(loginLabel);
//        loginPanel.add(username);
        
        JPanel login = new JPanel(new GridBagLayout());
        GridBagConstraints fcl = new GridBagConstraints();
        fcl.anchor = GridBagConstraints.CENTER;
        login.add(loginLabel, fcl);
        login.add(username, fcl);
        login.add(enterName,fcl);
        
        loginPanel.add(login);

        
        feedbackPanel = new JPanel();
        feedbackPanel.setLayout(new GridLayout(7,1));
        feedbackPanel.setSize(90, getHeight());
        feedbackPanel.setBorder(BorderFactory.createTitledBorder("Feedback Panel"));
        feedbackPanel.setBorder(new LineBorder(Color.GRAY, 3));
        
        feedbackPrepTimePanel = new JPanel();
        feedbackPrepTimePanel.setLayout(new GridLayout(7,1));
        feedbackPrepTimePanel.setBorder(BorderFactory.createTitledBorder("Preparation time in your opinion"));
        JLabel timeLabel = new JLabel("How long did the recipe take to prepare?");
        feedbackPrepTimePanel.add( timeLabel );
        ftpextshortButton = new JRadioButton("Extremely short", false);
        ftpveryshortButton = new JRadioButton("Very short", false);
        ftpshortButton = new JRadioButton("Short", false);
        ftplongButton = new JRadioButton("Long", false);
        ftpverylongButton = new JRadioButton("Very long", false);
        ftpextlongButton = new JRadioButton("Extremely long", false);
        ButtonGroup ftbgroup = new ButtonGroup();
        ftbgroup.add(ftpextshortButton);
        ftbgroup.add(ftpveryshortButton);
        ftbgroup.add(ftpshortButton);
        ftbgroup.add(ftplongButton);
        ftbgroup.add(ftpverylongButton);
        ftbgroup.add(ftpextlongButton);
        feedbackPrepTimePanel.add(ftpextshortButton);
        feedbackPrepTimePanel.add(ftpveryshortButton);
        feedbackPrepTimePanel.add(ftpshortButton);
        feedbackPrepTimePanel.add(ftplongButton);
        feedbackPrepTimePanel.add(ftpverylongButton);
        feedbackPrepTimePanel.add(ftpextlongButton);
        
        ftpextshortButton.addItemListener(this);
        ftpveryshortButton.addItemListener(this);
        ftpshortButton.addItemListener(this);
        ftplongButton.addItemListener(this);
        ftpverylongButton.addItemListener(this);
        ftpextlongButton.addItemListener(this);
        
        feedbackPanel.add(feedbackPrepTimePanel);
        
        feedbackCookTimePanel = new JPanel();
        feedbackCookTimePanel.setLayout(new GridLayout(7,1));
        feedbackCookTimePanel.setBorder(BorderFactory.createTitledBorder("Cooking time in your opinion"));
//        feedbackCookTimePanel.setBorder(BorderFactory.createTitledBorder("Overall time in your opinion"));
        JLabel ctimeLabel = new JLabel("How quick did the recipe take to cook?");
//        JLabel ctimeLabel = new JLabel("How quick did the chosenRecipe take to be ready to eat?");
        feedbackCookTimePanel.add( ctimeLabel );
        ftcextquickButton = new JRadioButton("Extremely quick", false);
        ftcveryquickButton = new JRadioButton("Very quick", false);
        ftcquickButton = new JRadioButton("Quick", false);
        ftcslowButton = new JRadioButton("Slow", false);
        ftcveryslowButton = new JRadioButton("Very slow", false);
        ftcextslowButton = new JRadioButton("Extremely slow", false);
        ButtonGroup ftcbgroup = new ButtonGroup();
        ftcbgroup.add(ftcextquickButton);
        ftcbgroup.add(ftcveryquickButton);
        ftcbgroup.add(ftcquickButton);
        ftcbgroup.add(ftcslowButton);
        ftcbgroup.add(ftcveryslowButton);
        ftcbgroup.add(ftcextslowButton);
        feedbackCookTimePanel.add(ftcextquickButton);
        feedbackCookTimePanel.add(ftcveryquickButton);
        feedbackCookTimePanel.add(ftcquickButton);
        feedbackCookTimePanel.add(ftcslowButton);
        feedbackCookTimePanel.add(ftcveryslowButton);
        feedbackCookTimePanel.add(ftcextslowButton);
        
        ftcextquickButton.addItemListener(this);
        ftcveryquickButton.addItemListener(this);
        ftcquickButton.addItemListener(this);
        ftcslowButton.addItemListener(this);
        ftcveryslowButton.addItemListener(this);
        ftcextslowButton.addItemListener(this);
        
        feedbackPanel.add(feedbackCookTimePanel);
        
        feedbackDifficultyPanel = new JPanel();
        feedbackDifficultyPanel.setLayout(new GridLayout(7,1));
        feedbackDifficultyPanel.setBorder(BorderFactory.createTitledBorder("Difficulty level in your opinion"));
        JLabel diffLabel = new JLabel("How did you find the difficulty of the recipe?");
        feedbackDifficultyPanel.add( diffLabel );
        fexteasyButton = new JRadioButton("Extremely easy", false);
        fveryeasyButton = new JRadioButton("Very easy", false);
        feasyButton = new JRadioButton("Easy", false);
        fchalButton = new JRadioButton("Challenging", false);
        fverychalButton = new JRadioButton("Very challenging", false);
        fextchalButton = new JRadioButton("Extremely challenging", false);
        ButtonGroup fbgroup = new ButtonGroup();
        fbgroup.add(fexteasyButton);
        fbgroup.add(fveryeasyButton);
        fbgroup.add(feasyButton);
        fbgroup.add(fchalButton);
        fbgroup.add(fverychalButton);
        fbgroup.add(fextchalButton);
        feedbackDifficultyPanel.add(fexteasyButton);
        feedbackDifficultyPanel.add(fveryeasyButton);
        feedbackDifficultyPanel.add(feasyButton);
        feedbackDifficultyPanel.add(fchalButton);
        feedbackDifficultyPanel.add(fverychalButton);
        feedbackDifficultyPanel.add(fextchalButton);
        
        fexteasyButton.addItemListener(this);
        fveryeasyButton.addItemListener(this);
        feasyButton.addItemListener(this);
        fchalButton.addItemListener(this);
        fverychalButton.addItemListener(this);
        fextchalButton.addItemListener(this);
        
        feedbackPanel.add(feedbackDifficultyPanel);
        
        feedbackRepeatRecipePanel = new JPanel();
        feedbackRepeatRecipePanel.setName("customize");
        feedbackRepeatRecipePanel.setLayout(new GridLayout(7,1));
        feedbackRepeatRecipePanel.setBorder(BorderFactory.createTitledBorder("Customize"));
        JLabel repeatLabel = new JLabel("Would you cook this recipe again?");
        feedbackRepeatRecipePanel.add( repeatLabel );
        fyesButton = new JRadioButton("Yes", false);
        fyesButton.setName("customize");
        fnoButton = new JRadioButton("No", false);
        fnoButton.setName("customize");

        ButtonGroup fbrgroup = new ButtonGroup();
        fbrgroup.add(fyesButton);
        fbrgroup.add(fnoButton);

        feedbackRepeatRecipePanel.add(fyesButton);
        feedbackRepeatRecipePanel.add(fnoButton);
        
        fyesButton.addItemListener(this);
        fnoButton.addItemListener(this);
        
//        notes = new JTextArea(3, 20);
        notes = new JTextField("");
//        notes.setLineWrap(true);
        notes.setActionCommand("PersonalNotes");
        notes.addActionListener(this);

//        notes.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(notes);
        taLabel = new JLabel("Enter your notes below:");

        
        feedbackRepeatRecipePanel.add(taLabel);
        feedbackRepeatRecipePanel.add(notes);
        taLabel.setVisible(false);
        notes.setVisible(false);
        
        feedbackPanel.add(feedbackRepeatRecipePanel);
        
        submitFeedback = new JButton("<html><center>Submit Feedback<br/>&<br/>Open Survey</center></html>");
        submitFeedback.addActionListener(this);
        submitFeedback.setActionCommand("Submit Feedback");
        submitFeedback.setFont(new Font("Dialog", Font.PLAIN, 24));
//        feedbackPanel.add(new JPanel());
//        feedbackPanel.add(submitFeedback);
        
        JPanel fbutt = new JPanel(new GridBagLayout());
        GridBagConstraints fc = new GridBagConstraints();
        fc.anchor = GridBagConstraints.CENTER;
        fbutt.add(submitFeedback, fc);
        feedbackPanel.add(fbutt);
        
        thankPanel = new JPanel();
        JLabel thankLabel = new JLabel("Thank you!");
        thankLabel.setAlignmentX(JLabel.CENTER);
        thankLabel.setFont(new Font("Dialog", Font.ITALIC, 18));
        thankPanel.add(thankLabel);
        
        //button controls
        
        startOverButton = new JButton("Start Over");
        startOverButton.addActionListener(this);
        startOverButton.setActionCommand("Start Over");
        startOverButton.setFont(new Font("Dialog", Font.PLAIN, 24));
        
        exitButton = new JButton("Exit");
        exitButton.addActionListener(this);
        exitButton.setActionCommand("Exit");
        exitButton.setFont(new Font("Dialog", Font.PLAIN, 24));
        
        buttonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints fcb = new GridBagConstraints();
        fcb.anchor = GridBagConstraints.CENTER;
        buttonPanel.add(startOverButton, fcb);
        buttonPanel.add(exitButton, fcb);
        
        //control panel
        
        controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(7,1));
//        controlPanel.setLayout(new GridLayout(7,1));
        controlPanel.setSize(90, getHeight());
        controlPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Questions Panel"));

        exttiredButton = new JRadioButton("Extremely tired", true);
        verytiredButton = new JRadioButton("Very tired", false);
        tiredButton = new JRadioButton("Tired", false);
        enerButton = new JRadioButton("Energetic", false);
        veryenerButton = new JRadioButton("Very energetic", false);
        extenerButton = new JRadioButton("Extremely energetic", false);
        Utility.LEVEL_OF_TIREDNESS = "Extremely tired";
        
        tiredbgroup = new ButtonGroup();
        tiredbgroup.add(exttiredButton);
        tiredbgroup.add(verytiredButton);
        tiredbgroup.add(tiredButton);
        tiredbgroup.add(enerButton);
        tiredbgroup.add(veryenerButton);
        tiredbgroup.add(extenerButton);

        exthungryButton = new JRadioButton("Extremely hungry", true);
        veryhungryButton = new JRadioButton("Very hungry", false);
        hungryButton = new JRadioButton("Hungry", false);
        fullButton = new JRadioButton("Full", false);
        veryfullButton = new JRadioButton("Very full", false);
        extfullButton = new JRadioButton("Extremely full", false);
        Utility.LEVEL_OF_HUNGRINESS = "Extremely hungry";
        
        hungrybgroup = new ButtonGroup();
        hungrybgroup.add(exthungryButton);
        hungrybgroup.add(veryhungryButton);
        hungrybgroup.add(hungryButton);
        hungrybgroup.add(fullButton);
        hungrybgroup.add(veryfullButton);
        hungrybgroup.add(extfullButton);

        extfreeButton = new JRadioButton("Extremely free", true);
        veryfreeButton = new JRadioButton("Very free", false);
        freeButton = new JRadioButton("Free", false);
        busyButton = new JRadioButton("Busy", false);
        verybusyButton = new JRadioButton("Very busy", false);
        extbusyButton = new JRadioButton("Extremely busy", false);
        Utility.LEVEL_OF_FREENESS = "Extremely free";
        
        freebgroup = new ButtonGroup();
        freebgroup.add(extfreeButton);
        freebgroup.add(veryfreeButton);
        freebgroup.add(freeButton);
        freebgroup.add(busyButton);
        freebgroup.add(verybusyButton);
        freebgroup.add(extbusyButton);

        noButton = new JRadioButton("No, show me the tried recipes!", true);
        yesButton = new JRadioButton("Yes, show me the new recipes!", false);
        noButton.addItemListener(this);
        yesButton.addItemListener(this);

        ButtonGroup bgroup = new ButtonGroup();
        bgroup.add(noButton);
        bgroup.add(yesButton);

        radioPanel = new JPanel();
        radioPanel.setLayout(new GridLayout(2, 1));
        radioPanel.add(noButton);
        radioPanel.add(yesButton);  

        radioPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Are you feeling creative?"));
        controlPanel.add(radioPanel);
        radioPanel.setVisible(true);
        
        chickenCheckBox = new JCheckBox();
        chickenCheckBox.setText("Chicken");
        chickenCheckBox.addItemListener(this);

        meatCheckBox = new JCheckBox();
        meatCheckBox.setText("Meat");
        meatCheckBox.addItemListener(this);

        fishCheckBox = new JCheckBox();
        fishCheckBox.setText("Fish");
        fishCheckBox.addItemListener(this);

        riceCheckBox = new JCheckBox();
        riceCheckBox.setText("Rice");
        riceCheckBox.addItemListener(this);

        pastaCheckBox = new JCheckBox();
        pastaCheckBox.setText("Pasta");
        pastaCheckBox.addItemListener(this);

        pizzaCheckBox = new JCheckBox();
        pizzaCheckBox.setText("Pizza");
        pizzaCheckBox.addItemListener(this);

        breadCheckBox = new JCheckBox();
        breadCheckBox.setText("Bread");
        breadCheckBox.addItemListener(this);

        cheeseCheckBox = new JCheckBox();
        cheeseCheckBox.setText("Cheese");
        cheeseCheckBox.addItemListener(this);

        vegetablesCheckBox = new JCheckBox();
        vegetablesCheckBox.setText("Vegetables");
        vegetablesCheckBox.addItemListener(this);

        foodOptions = new JPanel();
        foodOptions.setLayout(new GridLayout(3,3));
        foodOptions.setBorder(BorderFactory.createTitledBorder("Choose Ingredients"));
        foodOptions.add(chickenCheckBox);
        foodOptions.add(meatCheckBox);
        foodOptions.add(fishCheckBox);
        foodOptions.add(riceCheckBox);
        foodOptions.add(pastaCheckBox);
        foodOptions.add(pizzaCheckBox);
        foodOptions.add(breadCheckBox);
        foodOptions.add(cheeseCheckBox);
//        foodOptions.add(vegetablesCheckBox);
        controlPanel.add(foodOptions);
        foodOptions.setVisible(false);

        getRecipes = new JButton("Get Recipes");
        getRecipes.addActionListener(this);
        getRecipes.setActionCommand("Get Recipes");
        getRecipes.setFont(new Font("Dialog", Font.PLAIN, 24));
        
        JPanel butt = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.CENTER;
        butt.add(getRecipes, c);
        controlPanel.add(butt);


        progressBar = new JProgressBar();
        progressBar.setBackground(this.getBackground());
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        Border progressBarBorder = BorderFactory.createTitledBorder("Fetching Data...");
        progressBar.setBorder(progressBarBorder);

        controlPanel.setBorder(new LineBorder(Color.GRAY, 3));
        
        fsPanel = new JPanel(null);
        Border grayBorder = new LineBorder(Color.LIGHT_GRAY, 5);
        fsPanel.setBorder(grayBorder);

        fsSketch = new FatSecretSketch();
        fsPanel.setSize(getWidth()-controlPanel.getWidth(),getHeight());
        fsPanel.setBackground(Color.LIGHT_GRAY);
        fsSketch.setBounds(0,0, fsPanel.getWidth(), fsPanel.getHeight());
        Utility.sketchWidth = (float)fsSketch.getWidth();
        Utility.sketchHeight = (float)fsSketch.getHeight();

    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        Object source = e.getItemSelectable();

        if (source == fyesButton) {
            if (fyesButton.isSelected()) {
                Utility.recipeRepeat = true;
            }
            notes.setVisible(true);
            taLabel.setVisible(true);
            Utility.CUSTOMIZE_FEEDBACK = "Yes";
        }
        else if (source == fnoButton) {
            if (fnoButton.isSelected()) {
                Utility.recipeRepeat = false;
            }
            notes.setVisible(false);
            taLabel.setVisible(false);
            Utility.CUSTOMIZE_FEEDBACK = "No";
        }
        else if (source == exttiredButton) {
            if (exttiredButton.isSelected()) {
                Utility.LEVEL_OF_TIREDNESS = "Extremely tired";
                System.out.println("Chosen for tiredness : "+Utility.LEVEL_OF_TIREDNESS);
            }
        }
        else if (source == verytiredButton) {
            if (verytiredButton.isSelected()) {
                Utility.LEVEL_OF_TIREDNESS = "Very tired";
                System.out.println("Chosen for tiredness : "+Utility.LEVEL_OF_TIREDNESS);
            }
        }
        else if (source == tiredButton) {
            if (tiredButton.isSelected()) {
                Utility.LEVEL_OF_TIREDNESS = "Tired";
                System.out.println("Chosen for tiredness : "+Utility.LEVEL_OF_TIREDNESS);
            }
        }
        else if (source == enerButton) {
            if (enerButton.isSelected()) {
                Utility.LEVEL_OF_TIREDNESS = "Energetic";
                System.out.println("Chosen for tiredness : "+Utility.LEVEL_OF_TIREDNESS);
            }
        }
        else if (source == veryenerButton) {
            if (veryenerButton.isSelected()) {
                Utility.LEVEL_OF_TIREDNESS = "Very energetic";
                System.out.println("Chosen for tiredness : "+Utility.LEVEL_OF_TIREDNESS);
            }    
        }
        else if (source == extenerButton) {
            if (extenerButton.isSelected()) {
                Utility.LEVEL_OF_TIREDNESS = "Extremely energetic";
                System.out.println("Chosen for tiredness : "+Utility.LEVEL_OF_TIREDNESS);
            }    
        }
                //hungriness
        else if (source == exthungryButton) {
            if (exthungryButton.isSelected()) {
                Utility.LEVEL_OF_HUNGRINESS = "Extremely hungry";
                System.out.println("Chosen for hungriness : "+Utility.LEVEL_OF_HUNGRINESS);
            }
        }
        else if (source == veryhungryButton) {
            if (veryhungryButton.isSelected()) {
                Utility.LEVEL_OF_HUNGRINESS = "Very hungry";
                System.out.println("Chosen for hungriness : "+Utility.LEVEL_OF_HUNGRINESS);
            }
        }
        else if (source == hungryButton) {
            if (hungryButton.isSelected()) {
                Utility.LEVEL_OF_HUNGRINESS = "Hungry";
                System.out.println("Chosen for hungriness : "+Utility.LEVEL_OF_HUNGRINESS);
            }
        }
        else if (source == fullButton) {
            if (fullButton.isSelected()) {
                Utility.LEVEL_OF_HUNGRINESS = "Full";
                System.out.println("Chosen for hungriness : "+Utility.LEVEL_OF_HUNGRINESS);
            }
        }
        else if (source == veryfullButton) {
            if (veryfullButton.isSelected()) {
                Utility.LEVEL_OF_HUNGRINESS = "Very full";
                System.out.println("Chosen for hungriness : "+Utility.LEVEL_OF_HUNGRINESS);
            }    
        }
        else if (source == extfullButton) {
            if (extfullButton.isSelected()) {
                Utility.LEVEL_OF_HUNGRINESS = "Extremely full";
                System.out.println("Chosen for hungriness : "+Utility.LEVEL_OF_HUNGRINESS);
            }    
        }
        else if (source == extbusyButton) {
            if (extbusyButton.isSelected()) {
                Utility.LEVEL_OF_FREENESS = "Extremely busy";
                System.out.println("Chosen for freeness : "+Utility.LEVEL_OF_FREENESS);
            }    
        }
        else if (source == verybusyButton) {
            if (verybusyButton.isSelected()) {
                Utility.LEVEL_OF_FREENESS = "Very busy";
                System.out.println("Chosen for freeness : "+Utility.LEVEL_OF_FREENESS);
            }    
        }
        else if (source == busyButton) {
            if (busyButton.isSelected()) {
                Utility.LEVEL_OF_FREENESS = "Busy";
                System.out.println("Chosen for freeness : "+Utility.LEVEL_OF_FREENESS);
            }    
        }
        else if (source == freeButton) {
            if (freeButton.isSelected()) {
                Utility.LEVEL_OF_FREENESS = "Free";
                System.out.println("Chosen for freeness : "+Utility.LEVEL_OF_FREENESS);
            }    
        }
        else if (source == veryfreeButton) {
            if (veryfreeButton.isSelected()) {
                Utility.LEVEL_OF_FREENESS = "Very free";
                System.out.println("Chosen for freeness : "+Utility.LEVEL_OF_FREENESS);
            }    
        }
        else if (source == extfreeButton) {
            if (extfreeButton.isSelected()) {
                Utility.LEVEL_OF_FREENESS = "Extremely free";
                System.out.println("Chosen for freeness : "+Utility.LEVEL_OF_FREENESS);
            }    
        }
        else if (source == yesButton) {
            foodOptions.setVisible(true);
        }
        else if (source == noButton) {
            foodOptions.setVisible(false);
        }
        else if (source == chickenCheckBox) {
            if(chickenCheckBox.isSelected())
            {
                 if(Utility.RECIPE_SEARCH.equals(""))
                 {
                    Utility.RECIPE_SEARCH+="Chicken";
                 }
                 else
                 {
                     Utility.RECIPE_SEARCH+="%20Chicken";
                 }
            }
            else if(!chickenCheckBox.isSelected())
            {
                  Utility.RECIPE_SEARCH = Utility.RECIPE_SEARCH.replace("%20Chicken","");
                  Utility.RECIPE_SEARCH = Utility.RECIPE_SEARCH.replace("Chicken%20","");
                  Utility.RECIPE_SEARCH = Utility.RECIPE_SEARCH.replace("Chicken","");
            }
        }
        else if (source == meatCheckBox) {
            if(meatCheckBox.isSelected())
            {
                if(Utility.RECIPE_SEARCH.equals(""))
                {
                    Utility.RECIPE_SEARCH+="Meat";
                }
                else
                {
                    Utility.RECIPE_SEARCH+="%20Meat";
                }
            }
            else if(!meatCheckBox.isSelected())
            {
                Utility.RECIPE_SEARCH = Utility.RECIPE_SEARCH.replace("%20Meat","");
                Utility.RECIPE_SEARCH = Utility.RECIPE_SEARCH.replace("Meat%20","");
                Utility.RECIPE_SEARCH = Utility.RECIPE_SEARCH.replace("Meat","");
            }
        }
        else if (source == fishCheckBox) {
            if(fishCheckBox.isSelected())
            {
                if(Utility.RECIPE_SEARCH.equals(""))
                {
                    Utility.RECIPE_SEARCH+="Fish";
                }
                else
                {
                    Utility.RECIPE_SEARCH+="%20Fish";
                }
            }
            else if(!fishCheckBox.isSelected())
            {
                Utility.RECIPE_SEARCH = Utility.RECIPE_SEARCH.replace("%20Fish","");
                Utility.RECIPE_SEARCH = Utility.RECIPE_SEARCH.replace("Fish%20","");
                Utility.RECIPE_SEARCH = Utility.RECIPE_SEARCH.replace("Fish","");
            }
        }
        else if (source == riceCheckBox) {
            if(riceCheckBox.isSelected())
            {
                if(Utility.RECIPE_SEARCH.equals(""))
                {
                    Utility.RECIPE_SEARCH+="Rice";
                }
                else
                {
                    Utility.RECIPE_SEARCH+="%20Rice";
                }
            }
            else if(!riceCheckBox.isSelected())
            {
                Utility.RECIPE_SEARCH = Utility.RECIPE_SEARCH.replace("%20Rice","");
                Utility.RECIPE_SEARCH = Utility.RECIPE_SEARCH.replace("Rice%20","");
                Utility.RECIPE_SEARCH = Utility.RECIPE_SEARCH.replace("Rice","");
            }
        }
        else if (source == pastaCheckBox) {
            if(pastaCheckBox.isSelected())
            {
                if(Utility.RECIPE_SEARCH.equals(""))
                {
                    Utility.RECIPE_SEARCH+="Pasta";
                }
                else
                {
                    Utility.RECIPE_SEARCH+="%20Pasta";
                }
            }
            else if(!pastaCheckBox.isSelected())
            {
                Utility.RECIPE_SEARCH = Utility.RECIPE_SEARCH.replace("%20Pasta","");
                Utility.RECIPE_SEARCH = Utility.RECIPE_SEARCH.replace("Pasta%20","");
                Utility.RECIPE_SEARCH = Utility.RECIPE_SEARCH.replace("Pasta","");
            }
        }
        else if (source == pizzaCheckBox) {
            if(pizzaCheckBox.isSelected())
            {
                if(Utility.RECIPE_SEARCH.equals(""))
                {
                    Utility.RECIPE_SEARCH+="Pizza";
                }
                else
                {
                    Utility.RECIPE_SEARCH+="%20Pizza";
                }
            }
            else if(!pizzaCheckBox.isSelected())
            {
                Utility.RECIPE_SEARCH = Utility.RECIPE_SEARCH.replace("%20Pizza","");
                Utility.RECIPE_SEARCH = Utility.RECIPE_SEARCH.replace("Pizza%20","");
                Utility.RECIPE_SEARCH = Utility.RECIPE_SEARCH.replace("Pizza","");
            }
        }
        else if (source == breadCheckBox) {
            if(breadCheckBox.isSelected())
            {
                if(Utility.RECIPE_SEARCH.equals(""))
                {
                    Utility.RECIPE_SEARCH+="Bread";
                }
                else
                {
                    Utility.RECIPE_SEARCH+="%20Bread";
                }
            }
            else if(!breadCheckBox.isSelected())
            {
                Utility.RECIPE_SEARCH = Utility.RECIPE_SEARCH.replace("%20Bread","");
                Utility.RECIPE_SEARCH = Utility.RECIPE_SEARCH.replace("Bread%20","");
                Utility.RECIPE_SEARCH = Utility.RECIPE_SEARCH.replace("Bread","");
            }
        }
        else if (source == cheeseCheckBox) {
            if(cheeseCheckBox.isSelected())
            {
                if(Utility.RECIPE_SEARCH.equals(""))
                {
                    Utility.RECIPE_SEARCH+="Cheese";
                }
                else
                {
                    Utility.RECIPE_SEARCH+="%20Cheese";
                }
            }
            else if(!cheeseCheckBox.isSelected())
            {
                Utility.RECIPE_SEARCH = Utility.RECIPE_SEARCH.replace("%20Cheese","");
                Utility.RECIPE_SEARCH = Utility.RECIPE_SEARCH.replace("Cheese%20","");
                Utility.RECIPE_SEARCH = Utility.RECIPE_SEARCH.replace("Cheese","");
            }
        }
        else if (source == vegetablesCheckBox) {
            if(vegetablesCheckBox.isSelected())
            {
                if(Utility.RECIPE_SEARCH.equals(""))
                {
                    Utility.RECIPE_SEARCH+="Vegetables";
                }
                else
                {
                    Utility.RECIPE_SEARCH+="%20Vegetables";
                }
            }
            else if(!vegetablesCheckBox.isSelected())
            {
                Utility.RECIPE_SEARCH = Utility.RECIPE_SEARCH.replace("%20Vegetables","");
                Utility.RECIPE_SEARCH = Utility.RECIPE_SEARCH.replace("Vegetables%20","");
                Utility.RECIPE_SEARCH = Utility.RECIPE_SEARCH.replace("Vegetables","");
            }
        }
                //feedback on prep time
        else if (source == ftpextshortButton) {
            if (ftpextshortButton.isSelected()) {
                Utility.PREPTIME_FEEDBACK = "Extremely short";
//                System.out.println("Chosen for preptimefeedback : "+Utility.PREPTIME_FEEDBACK);
            }    
        }
        else if (source == ftpveryshortButton) {
            if (ftpveryshortButton.isSelected()) {
                Utility.PREPTIME_FEEDBACK = "Very short";
//                System.out.println("Chosen for preptimefeedback : "+Utility.PREPTIME_FEEDBACK);
            }    
        }
        else if (source == ftpshortButton) {
            if (ftpshortButton.isSelected()) {
                Utility.PREPTIME_FEEDBACK = "Short";
//                System.out.println("Chosen for preptimefeedback : "+Utility.PREPTIME_FEEDBACK);
            }    
        }
        else if (source == ftplongButton) {
            if (ftplongButton.isSelected()) {
                Utility.PREPTIME_FEEDBACK = "Long";
//                System.out.println("Chosen for preptimefeedback : "+Utility.PREPTIME_FEEDBACK);
            }    
        }
        else if (source == ftpverylongButton) {
            if (ftpverylongButton.isSelected()) {
                Utility.PREPTIME_FEEDBACK = "Very long";
//                System.out.println("Chosen for preptimefeedback : "+Utility.PREPTIME_FEEDBACK);
            }    
        }
        else if (source == ftpextlongButton) {
            if (ftpextlongButton.isSelected()) {
                Utility.PREPTIME_FEEDBACK = "Extremely long";
//                System.out.println("Chosen for preptimefeedback : "+Utility.PREPTIME_FEEDBACK);
            }    
        }
                //feedback on cook time
        else if (source == ftcextquickButton) {
            if (ftcextquickButton.isSelected()) {
                Utility.COOKTIME_FEEDBACK = "Extremely quick";
//                System.out.println("Chosen for cooktimefeedback : "+Utility.COOKTIME_FEEDBACK);
            }    
        }
        else if (source == ftcveryquickButton) {
            if (ftcveryquickButton.isSelected()) {
                Utility.COOKTIME_FEEDBACK = "Very quick";
//                System.out.println("Chosen for cooktimefeedback : "+Utility.COOKTIME_FEEDBACK);
            }    
        }
        else if (source == ftcquickButton) {
            if (ftcquickButton.isSelected()) {
                Utility.COOKTIME_FEEDBACK = "Quick";
//                System.out.println("Chosen for cooktimefeedback : "+Utility.COOKTIME_FEEDBACK);
            }    
        }
        else if (source == ftcslowButton) {
            if (ftcslowButton.isSelected()) {
                Utility.COOKTIME_FEEDBACK = "Slow";
//                System.out.println("Chosen for cooktimefeedback : "+Utility.COOKTIME_FEEDBACK);
            }    
        }
        else if (source == ftcveryslowButton) {
            if (ftcveryslowButton.isSelected()) {
                Utility.COOKTIME_FEEDBACK = "Very slow";
//                System.out.println("Chosen for cooktimefeedback : "+Utility.COOKTIME_FEEDBACK);
            }    
        }
        else if (source == ftcextslowButton) {
            if (ftcextslowButton.isSelected()) {
                Utility.COOKTIME_FEEDBACK = "Extremely slow";
//                System.out.println("Chosen for cooktimefeedback : "+Utility.COOKTIME_FEEDBACK);
            }    
        }

                //difficulty
        else if (source == fexteasyButton) {
            if (fexteasyButton.isSelected()) {
                Utility.DIFF_FEEDBACK = "Extremely easy";
//                System.out.println("Chosen for difffeedback : "+Utility.DIFF_FEEDBACK);
            }    
        }
        else if (source == fveryeasyButton) {
            if (fveryeasyButton.isSelected()) {
                Utility.DIFF_FEEDBACK = "Very easy";
//                System.out.println("Chosen for difffeedback : "+Utility.DIFF_FEEDBACK);
            }    
        }
        else if (source == feasyButton) {
            if (feasyButton.isSelected()) {
                Utility.DIFF_FEEDBACK = "Easy";
//                System.out.println("Chosen for difffeedback : "+Utility.DIFF_FEEDBACK);
            }    
        }
        else if (source == fchalButton) {
            if (fchalButton.isSelected()) {
                Utility.DIFF_FEEDBACK = "Challenging";
//                System.out.println("Chosen for difffeedback : "+Utility.DIFF_FEEDBACK);
            }    
        }
        else if (source == fverychalButton) {
            if (fverychalButton.isSelected()) {
                Utility.DIFF_FEEDBACK = "Very challenging";
//                System.out.println("Chosen for difffeedback : "+Utility.DIFF_FEEDBACK);
            }    
        }
        else if (source == fextchalButton) {
            if (fextchalButton.isSelected()) {
                Utility.DIFF_FEEDBACK = "Extremely challenging";
//                System.out.println("Chosen for difffeedback : "+Utility.DIFF_FEEDBACK);
            }    
        }
    }


    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        
        Utility.NO_BROWSING_HISTORY = false;

        if (actionEvent.getActionCommand().equals("Login")) {

            //files for pearson correlation
            Calendar cal = new GregorianCalendar();
            Format formatter = new SimpleDateFormat("yyyy.MM.dd-HH.mm.ss");

            Utility.USER_NAME = username.getText();

            if (!Utility.usingIntervalT2) {

                Utility.foldername = "." + File.separator + "LGT2_experiments_EIA" + File.separator + Utility.USER_NAME + File.separator;
            }
            else {
                Utility.foldername = "."+ File.separator+"IT2_experiments_EIA"+File.separator+ Utility.USER_NAME + File.separator;
            }

            Utility.PUBLIC_BASE.init(username.getText());


            try {
                Utility.fileWriter = new FileWriter(Utility.foldername.concat(Utility.USER_NAME+formatter.format(cal.getTime()).toString()+".csv"));
                Utility.printWriter = new PrintWriter(Utility.fileWriter);

                Utility.printWriter.print("User Preparation Time");
                Utility.printWriter.print(",");
                Utility.printWriter.print("System Preparation Time");
                Utility.printWriter.print(",");
                Utility.printWriter.print("Actual Preparation Time");
                Utility.printWriter.print(",");
                Utility.printWriter.print("User Cooking Time");
                Utility.printWriter.print(",");
                Utility.printWriter.print("System Cooking Time");
                Utility.printWriter.print(",");
                Utility.printWriter.print("Actual Cooking Time");
                Utility.printWriter.print(",");
                Utility.printWriter.print("User Difficulty");
                Utility.printWriter.print(",");
                Utility.printWriter.println("System Difficulty");
                Utility.printWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

            Utility.activity = Utility.PUBLIC_BASE.lookForActivity("cook");
            setContentPane(panelsContainer);
            validate();
            
            fsSketch.init();
            
            Utility.fcconceptDifficulty = new FuzzyCompositeConceptDifficulty("difficulty", "challenging", "easy");
            
        } else if (actionEvent.getActionCommand().equals("Get Recipes")) {
            
            getRecipesButtonFired();
            
        } else if (actionEvent.getActionCommand().equals("Submit Feedback")) {

            submitFeedbackButtonFired();

        } else if (actionEvent.getActionCommand().equals("PersonalNotes")) {

            submitFeedbackButtonFired();
        } else if (actionEvent.getActionCommand().equals("Exit")) {

            Utility.printWriter.flush();
            Utility.printWriter.close();

            dispose();
            System.exit(0);
        } else if (actionEvent.getActionCommand().equals("Start Over")) {

//            System.out.println("Personal notes : " + notes.getText());
            if (!notes.getText().isEmpty()) {
                FatSecretSketch.chosenRecipe.addPersonalNotes(notes.getText());
            }

            FatSecretSketch.startOverFlag = true;
            
            FatSecretSketch.feedbacktext = "Please leave your feedback using the menu on the left...";
            
            Utility.NEWRECIPES = new ArrayList<Recipe>();
            Utility.ALLRECIPES = new ArrayList<Recipe>();
            Utility.SOLUTIONS = new ArrayList<Case>();
            Utility.RECIPE_SEARCH = "";
            Utility.FETCHING_DATA_PROGRESS = 0;
            Utility.FETCHING_DATA_PROGRESS_BASE = 0;
            Utility.FETCHING_DATA = false;
            Utility.FETCHING_DATA_BASE = false;
            Utility.IS_BASE_CONSUMED = false;
            Utility.NO_BROWSING_HISTORY = false;
            Utility.PAGE_NUMBER = 0;
            Utility.WANTED_RECIPE_FOUND = false;
            Utility.BASE_EMPTY = false;
            Utility.PUBLIC_BASE = new Base();
            Utility.PUBLIC_BASE.init(Utility.USER_NAME);
            Utility.LEVEL_OF_TIREDNESS = "";
            Utility.LEVEL_OF_HUNGRINESS = "";
            Utility.LEVEL_OF_FREENESS = "";
            Utility.PREPTIME_FEEDBACK = "";
            Utility.COOKTIME_FEEDBACK = "";
            Utility.CUSTOMIZE_FEEDBACK = "";
            Utility.DIFF_FEEDBACK = "";

            Utility.queryCase = new Case();
            
            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

            setBounds(0, 0, dim.width, dim.height - 50);


            initPanels();
            fsPanel.add(fsSketch);
            panelsContainer = new JPanel(new BorderLayout());

            panelsContainer.add(controlPanel, BorderLayout.WEST);
            panelsContainer.add(fsPanel, BorderLayout.CENTER);

            setContentPane(panelsContainer);

            fsSketch.init();
            FatSecretSketch.refreshSketchParameters();
            setVisible(true);
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            
            Utility.fcconceptDifficulty = new FuzzyCompositeConceptDifficulty("difficulty", "challenging", "easy");

            Utility.printWriter.flush();

            try {
                Utility.fileWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

        }
    }

    
    public static void changePanels() {
        
        panelsContainer.removeAll();
//        panelsContainer.remove(controlPanel);
        panelsContainer.add(feedbackPanel, BorderLayout.WEST);
        panelsContainer.add(fsPanel,BorderLayout.CENTER);
        panelsContainer.setVisible(true);
        panelsContainer.validate();
        
    }

    private void getRecipesButtonFired() {

        Utility.PAGE_NUMBER = 0;
        controlPanel.add(progressBar);
        FatSecretGUI.getRecipes.setEnabled(false);
        FatSecretGUI.controlPanel.setEnabled(false);
        FatSecretGUI.radioPanel.setEnabled(false);
        noButton.setEnabled(false);
        yesButton.setEnabled(false);
        repaint();
        validate();

        //System.out.println("The size of case base is : " + Utility.PUBLIC_BASE.getBaseSize(Utility.activity));

        int numberOfRecipesInBase = Utility.PUBLIC_BASE.getNumberOfRecipesInBase(Utility.activity);

        //check whether the case base is full
        if (noButton.isSelected()&& numberOfRecipesInBase > 0 && !Utility.IS_BASE_CONSUMED) {

            //retrieve the recipes from thr casebase  
            Utility.FETCHING_DATA_BASE = true;
            Utility.FETCHING_DATA = false;

            Utility.queryCase.addFVtoCase(new FVPair(new Feature("tiredness"), new Value(Utility.LEVEL_OF_TIREDNESS)));
            Utility.queryCase.addFVtoCase(new FVPair(new Feature("hungriness"), new Value(Utility.LEVEL_OF_HUNGRINESS)));
            Utility.queryCase.addFVtoCase(new FVPair(new Feature("leisuretime"), new Value(Utility.LEVEL_OF_FREENESS)));

            //new Thread(new ProgressBarThreadforBase(this)).start();
            new Thread(new FetchingDataThreadforBase()).start();
        } 
        else {
            if (noButton.isSelected() && numberOfRecipesInBase == 0) {
                Utility.BASE_EMPTY = true;
                controlPanel.remove(progressBar);
                FatSecretGUI.getRecipes.setEnabled(true);
                repaint();
                validate();
            } 
            else {
                Utility.FETCHING_DATA = true;
                Utility.FETCHING_DATA_BASE = false;
                new Thread(new ProgressBarThread(this)).start();
                new Thread(new FetchingDataThread(this)).start();
            }
        }

    }

    
    private void submitFeedbackButtonFired() {

        System.out.println("Cook feedback : " + Utility.COOKTIME_FEEDBACK);
        System.out.println("Prep feedback : " + Utility.PREPTIME_FEEDBACK);
        System.out.println("Diff feedback : " + Utility.DIFF_FEEDBACK);
        System.out.println("Cust feedback : " + Utility.CUSTOMIZE_FEEDBACK);

        //check all input is ok
        if (Utility.COOKTIME_FEEDBACK.equals("") || Utility.PREPTIME_FEEDBACK.equals("") || Utility.DIFF_FEEDBACK.equals("")
                || Utility.CUSTOMIZE_FEEDBACK.equals("")) {
            //get the chosenRecipe defaults for feedback

            JOptionPane.showMessageDialog(this, "Please leave your feedback using the menu on the left.", "Feedback Incomplete!", JOptionPane.PLAIN_MESSAGE);

            
        }
        else {

            submitFeedback.setEnabled(false);
            feedbackPanel.setEnabled(false);

            feedbackPrepTimePanel.setEnabled(false);
            for (int i = 0; i < feedbackPrepTimePanel.getComponents().length; i++) {
                Component component = feedbackPrepTimePanel.getComponent(i);
                if (component instanceof JLabel) {
                    JLabel lbl = (JLabel) component;
                    lbl.setEnabled(false);
                }
                else {
                    JRadioButton rb = (JRadioButton) component;
                    rb.setEnabled(false);
                }
            }

            feedbackCookTimePanel.setEnabled(false);
            for (int i = 0; i < feedbackCookTimePanel.getComponents().length; i++) {
                Component component = feedbackCookTimePanel.getComponent(i);
                if (component instanceof JLabel) {
                    JLabel lbl = (JLabel) component;
                    lbl.setEnabled(false);
                }
                else {
                    JRadioButton rb = (JRadioButton) component;
                    rb.setEnabled(false);
                }
            }

            feedbackDifficultyPanel.setEnabled(false);
            for (int i = 0; i < feedbackDifficultyPanel.getComponents().length; i++) {
                Component component = feedbackDifficultyPanel.getComponent(i);
                if (component instanceof JLabel) {
                    JLabel lbl = (JLabel) component;
                    lbl.setEnabled(false);
                }
                else {
                    JRadioButton rb = (JRadioButton) component;
                    rb.setEnabled(false);
                }
            }



            feedbackRepeatRecipePanel.setEnabled(false);
            for (int i = 0; i < feedbackRepeatRecipePanel.getComponents().length; i++) {
                Component component = feedbackRepeatRecipePanel.getComponent(i);
                if (component instanceof JLabel) {
                    JLabel lbl = (JLabel) component;
                    lbl.setEnabled(false);
                }
                else if (component instanceof JTextField) {
                    JTextField lbl = (JTextField) component;
                    lbl.setEnabled(false);
                }
                else {
                    JRadioButton rb = (JRadioButton) component;
                    rb.setEnabled(false);
                }
            }

            feedbackPanel.add(thankPanel);
            feedbackPanel.add(buttonPanel);
            validate();
            repaint();

            //september 2013
            //changing the learning to be made by the labels only
            Utility.fcconceptDifficulty.performLearningUsingNumbers(FatSecretSketch.chosenRecipe.getOverallTime(), Utility.COOKTIME_FEEDBACK,
                    Utility.PREPTIME_FEEDBACK,
                    Utility.DIFF_FEEDBACK);

            Food.saveNewConcept();

            FatSecretSketch.feedbacktext = "THANK YOU...";

            //add the chosenRecipe to the casebase if the user specified to cook it again
            if (Utility.recipeRepeat) {
                if (!notes.getText().isEmpty()) {
                    FatSecretSketch.chosenRecipe.addPersonalNotes(notes.getText());
                }
                FatSecretSketch.addRecipeToCaseBase(FatSecretSketch.chosenRecipe);
                System.out.println(FatSecretSketch.chosenRecipe.getName()+" has been added to CB.");
            }

            //write to file what the user has specified

            //november 2013 - adding for pearson correlation
            Utility.printWriter.print(Utility.PREPTIME_FEEDBACK);
            Utility.printWriter.print(",");
            Utility.printWriter.print(Utility.fcconceptDifficulty.getSystemResponse(FatSecretSketch.chosenRecipe.getPreparationTime(),"prep"));
            Utility.printWriter.print(",");
            Utility.printWriter.print(FatSecretSketch.chosenRecipe.getPreparationTime());
            Utility.printWriter.print(",");
            Utility.printWriter.print(Utility.COOKTIME_FEEDBACK);
            Utility.printWriter.print(",");
            Utility.printWriter.print(Utility.fcconceptDifficulty.getSystemResponse(FatSecretSketch.chosenRecipe.getCookingTime(),"cook"));
            Utility.printWriter.print(",");
            Utility.printWriter.print(FatSecretSketch.chosenRecipe.getCookingTime());
            Utility.printWriter.print(",");
            Utility.printWriter.print(Utility.DIFF_FEEDBACK);
            Utility.printWriter.print(",");
            Utility.printWriter.println(FatSecretSketch.difficultyOfRecipe);

            Utility.printWriter.flush();

        }

    }
    
    public static class ProgressBarThread implements Runnable{

        FatSecretGUI fatSecretGUI;
        public ProgressBarThread(FatSecretGUI fatSecretGUI) {
            this.fatSecretGUI = fatSecretGUI;
        }

        public void run(){
            while(Utility.FETCHING_DATA){
                progressBar.setValue((int) (Utility.FETCHING_DATA_PROGRESS*100));
                progressBar.repaint();
                fatSecretGUI.repaint();
                fatSecretGUI.validate();
                try{Thread.sleep(50);}
                catch (InterruptedException err){}
            }
            
            fatSecretGUI.repaint();
            fatSecretGUI.validate();
        }
    }
    
    public static class ProgressBarThreadforBase implements Runnable{

        FatSecretGUI fatSecretGUI;
        public ProgressBarThreadforBase(FatSecretGUI fatSecretGUI) {
            this.fatSecretGUI = fatSecretGUI;
        }
        
        public void run(){
            while(Utility.FETCHING_DATA_BASE){
                progressBar.setValue((int) (Utility.FETCHING_DATA_PROGRESS_BASE*100));
                progressBar.repaint();
                fatSecretGUI.repaint();
                fatSecretGUI.validate();
                try{Thread.sleep(50);}
                catch (InterruptedException err){}
            }

            fatSecretGUI.repaint();
            fatSecretGUI.validate();
        }
    }

    public static class FetchingDataThread implements Runnable{
        
        FatSecretGUI fatSecretGUI;
        public FetchingDataThread(FatSecretGUI fatSecretGUI) {
            this.fatSecretGUI = fatSecretGUI;
        }
        @Override
        public void run()
        {
            Utility.runSearch();
            getRecipes.setEnabled(true);
            controlPanel.remove(progressBar);
            fatSecretGUI.repaint();
            fatSecretGUI.validate();

        }
    }
    
    public static class FetchingDataThreadforBase implements Runnable{
        @Override
        public void run()
        {
            ArrayList<RetrievedCase> retrievedCases = Utility.PUBLIC_BASE.retrieve(Utility.activity, Utility.queryCase, false);
            
//            System.out.println("FETCH BASE : retrieved case are : "+ retrievedCases);
            
            Utility.SOLUTIONS = Utility.PUBLIC_BASE.reuse(Utility.activity, retrievedCases);
            
            int numberOfRecipes = Utility.PUBLIC_BASE.getNumberOfRecipesInBase(Utility.activity);
            Utility.ALLRECIPES = new ArrayList<Recipe>();
            int i = 0;

            for (Case c : Utility.SOLUTIONS) {

                for (Solution s : c.getSolutions()) {
                    i++;

                    Utility.ALLRECIPES.add(((Food)s).getRecipe());
                    Utility.FETCHING_DATA_PROGRESS_BASE = (double)i/(double)numberOfRecipes;
                }
            }
            
            Utility.FETCHING_DATA_PROGRESS_BASE = 1;
            Utility.FETCHING_DATA_BASE = false;

            getRecipes.setEnabled(true);
            controlPanel.remove(progressBar);
           

        }
    }
}

