/*
 * Copyright (c) 2015 Aysenur Bilgin
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cbr;

import fuzzylogic.type2.core.LVDesignerExpert;
import fuzzylogic.type2.interval.IntervalT2MF_Interface;
import fuzzylogic.type2.zSlices.AgreementMF_zMFs;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * Vocabulary provides the list that consists of the linguistic variables(string) and the corresponding linguistic labels
 * @author abilgin
 */
public final class Vocabulary implements Serializable {

    /**
     * Single instance created upon class loading.
     */
    private static Map CODEBOOK = new TreeMap<String, AgreementMF_zMFs[]>();

    public static void copyFromFile(Map readObject) {
        CODEBOOK.putAll(readObject);
    }

    /**
     * Private constructor prevents construction outside this class.
     */
    private Vocabulary() {

    }

    public static synchronized Map getInstance() {
        return CODEBOOK;
    }
    
    /**
     * Check whether the string exists, in order to prevent multiple calls for creating membership functions
     */
    public static synchronized boolean doesFeatureExist(String linguisticVariable) {

        return CODEBOOK.containsKey(linguisticVariable);

    }
    
    public static synchronized void addValue(String linguisticVariable, AgreementMF_zMFs[] linguisticLabels) {

        CODEBOOK.put(linguisticVariable, linguisticLabels);
    }
    
    /*
     * Check whether the modifier exists with the feature
     */
    public static synchronized boolean checkModifierExistence(String linguisticVariable, String modifier) {
        
        if (!CODEBOOK.isEmpty()) {
            for (AgreementMF_zMFs fset: (AgreementMF_zMFs[])CODEBOOK.get(linguisticVariable)) {
                for (int i = 0; i < fset.getNumberOfSlices(); i++) {
                    IntervalT2MF_Interface slice = fset.getZSlice(i);
                    if (slice.getName().contains(modifier)) {
                        //System.out.println("Slice name : " + slice.getName() + " and " + modifier);
                        return true;
                    }
                }
            }
        }
        
        return false;
    }
    
    /*
     * return the slices for the modifier exists with the feature and perception
     */
    public static synchronized ArrayList<RetrievedSlice> retrieveSlicesforModifier(String linguisticVariable, String modifier, String perception) {
        
        ArrayList<RetrievedSlice> modifierslices = new ArrayList<RetrievedSlice>();
        RetrievedSlice defaultslice = null;
        
        if (!CODEBOOK.isEmpty()) {
            for (AgreementMF_zMFs fset: (AgreementMF_zMFs[])CODEBOOK.get(linguisticVariable)) {
                for (int i = 0; i < fset.getNumberOfSlices(); i++) {

                    IntervalT2MF_Interface slice = fset.getZSlice(i);
                    //get the string and convert it into intervals of mf as the string might have the modifier as well
                    String[] wordList = ((String)slice.getName()).split("\\s+");  
                    String slmodifier = "", slperception = "";

                    if (wordList.length > 1) {
                        for (int w = 0; w < wordList.length-1; w++) {
                            slmodifier = slmodifier.concat(wordList[w]);  
                            if (i+1 != wordList.length-1) {
                                slmodifier = slmodifier.concat(" ");
                            }
                        }
                        slperception = wordList[wordList.length-1];   
                    }
                    else {
                        slperception = wordList[0];
                        slmodifier = "";
                    }
                    
                    slperception = slperception.trim();
                    slmodifier = slmodifier.trim();
                    
                    if (!slperception.equalsIgnoreCase(perception)) {
                        break;
                    } 
                    else if (slmodifier.equalsIgnoreCase(modifier) && slperception.equalsIgnoreCase(perception)) {
                        //System.out.println("Slice name : " + slice.getName() + " and modifier: " + slmodifier + " and perception: " + slperception);
                        RetrievedSlice rslice = new RetrievedSlice(slice, (double)(i+1)/fset.getNumberOfSlices(), i);
                        modifierslices.add(rslice);
                    }
                    else if ((i == 0) && slperception.equalsIgnoreCase(perception)) {
                        defaultslice = new RetrievedSlice(slice, (double)(i+1)/fset.getNumberOfSlices(), i);
                    }
                }
            }
        }
        
        //if no slice match is found return the default one
        if (modifierslices.isEmpty()) {
            modifierslices.add(defaultslice);
        }
        
        return modifierslices;
    }
    
    /*
     * Return the AgreementMF for the perception
     */
    public static synchronized AgreementMF_zMFs retrieveMFforPerception(String linguisticVariable, String perception) {
        
        if (!CODEBOOK.isEmpty()) {
            for (AgreementMF_zMFs fset: (AgreementMF_zMFs[])CODEBOOK.get(linguisticVariable)) {
                if (fset.getName().contains(perception)) {
                    //System.out.println("Set name : " + fset.getName() + " and " + perception);
                    return fset;
                }
            }
        }
        
        return null;
    }
    

    public static synchronized void populateVocabulary() {
        
        //vocabulary consists of features and corresponding membership functions 
        //for all the features, need to create corresponding linguistic variables
        //check if the features already exist, so no duplicate mf creations
        if (Vocabulary.doesFeatureExist("tiredness")) {
            return;
        }
        
        LVDesignerExpert tiredness = new LVDesignerExpert("tired", "energetic", false, 0.4,0.6);
        tiredness.formZsliceSets();
        Vocabulary.addValue("tiredness", tiredness.getFuzzySets());
        
        LVDesignerExpert hungriness = new LVDesignerExpert("hungry", "full", false, 0.5,0.7);
        hungriness.formZsliceSets();
        Vocabulary.addValue("hungriness", hungriness.getFuzzySets());

        LVDesignerExpert leisuretime = new LVDesignerExpert("free", "busy", false, 0.3,0.7);
        leisuretime.formZsliceSets();
        Vocabulary.addValue("leisuretime", leisuretime.getFuzzySets());
        
    }
 
    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("Clone is not allowed.");
    }
    
}
