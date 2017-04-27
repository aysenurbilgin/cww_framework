/*
 * Copyright (c) 2015 Aysenur Bilgin
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package activity;

import cbr.Base;
import cbr.Case;
import cbr.FVPair;
import cbr.Feature;
import cbr.Index;
import cbr.Solution;
import cbr.Value;
import cbr.Vocabulary;
import gui.Utility;
import java.util.ArrayList;

/**
 * Depending on expert knowledge, the generator creates cases 
 * @author abilgin
 */
public class CaseBaseGenerator extends Base {

    private static ArrayList<Activity> activityList = new ArrayList<Activity>();                       //application specific: the indexes are activities
    
    private CaseBaseGenerator() {
        
    }

    //create everything related to and will be used in cbr
    //vocabulary, features, membership functions
    public synchronized static void initialize() {
        
        //create vocabulary that is application dependent
        Vocabulary.populateVocabulary();
        
        //create the indexes which are activities
        createActivityBase();
        
        //create the individual cases
        createCases();

    }

    private static void createActivityBase() {
        
        //create the activities depending on expert knowledge
        activityList.add(new Activity());
        
    }
    
    public static Activity getActivity(String name) {
        
        for (Index i: activityList) {
            if (name.equalsIgnoreCase((String)i.getIndex())) {
                return (Activity)i;
            }
        }
        
        return null;
       
    }

    private static void createCases() {
        
        //create cases depending on expert knowledge per activity
        for (Activity act: activityList) {

            Utility.PUBLIC_BASE.addCases(act, createCasesperActivity(act));
            
            //finalize populating the vocabulary from the cases
            for (Case c: (ArrayList<Case>)Utility.PUBLIC_BASE.getCasebase().get(act)) {
                for (Solution s: c.getSolutions()) {
                    if (s instanceof Food) {
                        //define difficulty for each food
                        //september2013 add overall time
                        ((Food)s).defineDifficulty2(((Food) s).getOverallTime());
                    }
                }
            }
        }
    }

    private static ArrayList<Case> createCasesperActivity(Activity act) {
        
        ArrayList<Case> caseListperActivity = new ArrayList<Case>();
        
        if (act.getIndex().equalsIgnoreCase("rest")) {
            
            //when the activity chosen is to rest, then refer to music
//            Case c = new Case();
//            c.addAllFVtoCase(populateCase(act, "very tired", "extremely hungry", "free"));
//            caseListperActivity.add(c);
//            
//            c = new Case();
//            c.addAllFVtoCase(populateCase(act, "tired", "extremely hungry", "free"));
//            caseListperActivity.add(c);
//            
//            c = new Case();
//            c.addAllFVtoCase(populateCase(act, "tired", "hungry", "tight"));
//            caseListperActivity.add(c);
//            
//            c = new Case();
//            c.addAllFVtoCase(populateCase(act, "very energetic", "full", "free"));
//            caseListperActivity.add(c);
//            
//            c = new Case();
//            c.addAllFVtoCase(populateCase(act, "tired", "very hungry", " extremely free"));
//            caseListperActivity.add(c);
        }
        else if (act.getIndex().equalsIgnoreCase("cook")) {
            
            //when the activity chosen is to cook, then refer to food 
//            Case c = new Case();
//            c.addAllFVtoCase(populateCase(act, "tired", "extremely hungry", "very busy"));
//            c.addSolution(new Food("salmon&spinach", 5.0, 10.0));
//            caseListperActivity.add(c);
//            
//            c = new Case();
//            c.addAllFVtoCase(populateCase(act, "tired", "extremely full", "free"));
//            c.addSolution(new Food("muffins", 35.0, 25.0));
//            caseListperActivity.add(c);
//            
//            c = new Case();
//            c.addAllFVtoCase(populateCase(act, "tired", "hungry", "extremely busy"));
//            c.addSolution(new Food("sandwich", 5.0, 0.0));
//            caseListperActivity.add(c);
//            
//            c = new Case();
//            c.addAllFVtoCase(populateCase(act, "very energetic", "full", "free"));
//            c.addSolution(new Food("cake", 20.0, 45.0));
//            caseListperActivity.add(c);
//            
//            c = new Case();
//            c.addAllFVtoCase(populateCase(act, "tired", "extremely hungry", "free"));
//            c.addSolution(new Food("steak with mushroom", 20.0, 35.0));
//            caseListperActivity.add(c);
//            
//            c = new Case();
//            c.addAllFVtoCase(populateCase(act, "energetic", "hungry", "free"));
//            c.addSolution(new Food("lamb with ratatouelle", 40.0, 40.0));
//            caseListperActivity.add(c);
//            
//            c = new Case();
//            c.addAllFVtoCase(populateCase(act, "energetic", "extremely hungry", "busy"));
//            c.addSolution(new Food("baked pasta", 30.0, 10.0));
//            caseListperActivity.add(c);
        }
        
        return caseListperActivity;
    }

    private static ArrayList<FVPair> populateCase(Activity act, String stringt, String stringh, String stringl) {
        
        ArrayList<FVPair> fvlist = new ArrayList<FVPair>();
        
        for (Feature f: act.getFeatures()) {
     
            FVPair pair = new FVPair(f);
            if (f.getName().equalsIgnoreCase("tiredness")) {
                pair.setValue(new Value(stringt));
            }
            else if (f.getName().equalsIgnoreCase("hungriness")) {
                pair.setValue(new Value(stringh));
            }
            else if (f.getName().equalsIgnoreCase("leisuretime")) {
                pair.setValue(new Value(stringl));
            }
            
            fvlist.add(pair);

        }
        
        return fvlist;
  
    }
    
}
