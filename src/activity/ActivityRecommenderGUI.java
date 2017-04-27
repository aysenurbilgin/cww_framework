/*
 * Copyright (c) 2015 Aysenur Bilgin
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package activity;

import fatSecret.FatSecret;
import gui.FatSecretGUI;
import gui.Utility;

import java.applet.Applet;

/**
 * This is the application that runs CBR and recommends activity
 * @author abilgin
 */
public class ActivityRecommenderGUI extends Applet {
    
    public static final boolean usingDB = false;
    
    public ActivityRecommenderGUI() {
        
    }
    
    public static void main(String[] args) throws Exception
    {
        FatSecret fs = new FatSecret();
        Utility.fatSecret = fs;

        FatSecretGUI fatSecretGUI = new FatSecretGUI("Experience Adaptation Application");

    }//end main

}

