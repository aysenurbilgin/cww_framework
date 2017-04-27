/*
 * Copyright (c) 2015 Aysenur Bilgin
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package activity;

import cbr.*;
import fatSecret.Recipe;
import fuzzylogic.type2.zSlices.AgreementMF_zMFs;
import gui.Utility;

import java.io.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author abilgin
 */
public class Food extends Solution implements Serializable {
    
    private String nameOfDish;
    private Recipe recipe;          //the recipe can be stored in an object and serialized
    
    private Value difficulty;
    private static String concept = "difficulty";
    
    private final double prepWeight = 0.8;
    private final double cookWeight = 0.3;


    public Food(double prepTime, double cookTime, String itemINDEX) {

        this.nameOfDish = itemINDEX;
        super.initialize();

        if (Utility.usingIntervalT2 || Utility.usingLGT2_EIA) {
            super.addFVtoSolution(new FVPair(new Feature("preparation time", prepWeight), new Value(prepTime)));
            super.addFVtoSolution(new FVPair(new Feature("cooking time", cookWeight), new Value(cookTime)));

        }
        else {
            //IMPORTANT: note that the order changes the way that the rulebase works
            super.addFVtoSolution(new FVPair(new Feature("cooking time", cookWeight), new Value(cookTime)));
            super.addFVtoSolution(new FVPair(new Feature("preparation time", prepWeight), new Value(prepTime)));
        }

        inread(concept);
        //updateVocabulary
        int i = 0;
        for (String s: Utility.fcconceptDifficulty.getConceptNames()) {
            Vocabulary.addValue(s, Utility.fcconceptDifficulty.getInputConceptSets().get(i));
            i++;
        }

        saveNewConcept();

    }

    public Food(String nameofdish, double prepTime, double cookTime) {

        this.nameOfDish = nameofdish;

        super.initialize();
        
        //IMPORTANT: note that the order changes the way that the rulebase works
        super.addFVtoSolution(new FVPair(new Feature("cooking time", cookWeight), new Value(cookTime)));
        super.addFVtoSolution(new FVPair(new Feature("preparation time", prepWeight), new Value(prepTime)));
        
        //the difficulty is based on the preparation time and cooking time
        //the FCC helps define the difficulty
        //FCC is outputting an interval
        //the aim is to find the corresponding of this interval from the mf definition of difficulty which is already in Vocabulary

        inread(concept);
        
        Utility.fcconceptDifficulty.reDesignInputConcept("cooking time", cookTime);
        Utility.fcconceptDifficulty.reDesignInputConcept("preparation time", prepTime);
        
        //updateVocabulary
        int i = 0;
        for (String s: Utility.fcconceptDifficulty.getConceptNames()) {
            Vocabulary.addValue(s, Utility.fcconceptDifficulty.getInputConceptSets().get(i));
            i++;
        }

        saveNewConcept();
 
    }
    
    public Food(Recipe rec) {
        
        this.recipe = rec;
        
        this.nameOfDish = rec.getName();
        
        super.initialize();

        super.addFVtoSolution(new FVPair(new Feature("cooking time", cookWeight), new Value(rec.getCookingTime())));
        super.addFVtoSolution(new FVPair(new Feature("preparation time", prepWeight), new Value(rec.getPreparationTime())));
        
        inread(concept);

        if (!Utility.usingIntervalT2 && !Utility.usingLGT2_EIA) {
            Utility.fcconceptDifficulty.reDesignInputConcept("cooking time", rec.getCookingTime());
            Utility.fcconceptDifficulty.reDesignInputConcept("preparation time", rec.getPreparationTime());
            //september 2013 adding overall time
            Utility.fcconceptDifficulty.reDesignInputConcept("zoverall time", rec.getOverallTime());
        }

        //updateVocabulary
        int i = 0;
        for (String s: Utility.fcconceptDifficulty.getConceptNames()) {
            Vocabulary.addValue(s, Utility.fcconceptDifficulty.getInputConceptSets().get(i));
            i++;
        }

        saveNewConcept();

    }
    
    public Food(Food f) {
        
        this.nameOfDish = f.getNameofDish();
        
        super.initialize();

        super.addAllFVtoSolution(f.getSolutionFVPairs());
        super.setFavourIndicator(f.getFavourIndicator());

        this.difficulty = new Value(f.getDifficulty());
        
        inread(concept);
    }

    public String getNameofDish() {
        return this.nameOfDish;
    }
    
    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }
    
    public double getPreparationTime() {
        return (Double)super.getSolutionValueforFeature("preparation time").getDoubleValue();
    }
    
    public String getPreparationLabel() {
        return super.getSolutionValueforFeature("preparation time").getSlicesValue("preparation time").get(0).getName();
    }

    public double getOverallTime() {
        return this.getCookingTime()+this.getPreparationTime();
    }
    
    public double getCookingTime() {
        return (Double)super.getSolutionValueforFeature("cooking time").getDoubleValue();
    }
    
    public String getCookingLabel() {
        return super.getSolutionValueforFeature("cooking time").getSlicesValue("cooking time").get(0).getName();
    }
    
    public Value getDifficulty() {
        return this.difficulty;
    }
    
    public String getUserFriendlyDifficulty() {
       
        AgreementMF_zMFs[] compsets = Utility.fcconceptDifficulty.getCompositeSets();
        //new AgreementMF_zMFs[this.fcconceptDifficulty.establishCompositeSets().length];
        //compsets = this.fcconceptDifficulty.getCompositeSets();
        
        //this difficulty has a tuple value
        //we want to retrieve the linguistic label for the tuple
        return this.difficulty.getConsensusSliceName(compsets);
        
    }

    //september 2013 adding overall time
    public String getUserFriendlyDifficulty2(double overallTime) {

        AgreementMF_zMFs[] compsets = Utility.fcconceptDifficulty.getCompositeSets2(overallTime);
        //new AgreementMF_zMFs[this.fcconceptDifficulty.establishCompositeSets().length];
        //compsets = this.fcconceptDifficulty.getCompositeSets();

        //this difficulty has a tuple value
        //we want to retrieve the linguistic label for the tuple
        return this.difficulty.getConsensusSliceName(compsets);

    }
    
    public void defineDifficulty() {

        ArrayList<Double> inputs = new ArrayList<Double>();
        for (FVPair fvp: this.getSolutionFVPairs()) {
            
            inputs.add(fvp.getValue().getDoubleValue());
        }

        Vocabulary.addValue(concept, Utility.fcconceptDifficulty.getCompositeSets());

        this.difficulty = new Value(Utility.fcconceptDifficulty.evaluateConceptfor(inputs));
        
    }

    //september 2013 addition of overallTime
    public void defineDifficulty2(double overallTime) {

        ArrayList<Double> inputs = new ArrayList<Double>();
        for (FVPair fvp: this.getSolutionFVPairs()) {

            inputs.add(fvp.getValue().getDoubleValue());
        }

        //september2013
        inputs.add(overallTime);

        Vocabulary.addValue(concept, Utility.fcconceptDifficulty.getCompositeSets2(overallTime));

        this.difficulty = new Value(Utility.fcconceptDifficulty.evaluateConceptfor(inputs));

    }
       
    private void inread(String linguisticvariable) {

        File files = new File(Utility.foldername);
        if (!files.exists()) {
            if (files.mkdirs()) {
//                System.out.println("FOOD INREAD: Multiple directories are created!");
            } else {
//                System.out.println("FOOD INREAD: Failed to create multiple directories!");
            }
        }
//        String inputfilename = "." + File.separator + "Difficulties" + File.separator + Utility.USER_NAME+ concept;
        String inputfilename = Utility.foldername+linguisticvariable;
        File f;
        f = new File(inputfilename);
    
        //if the case base and the vocabulary exists on a file, read it from the file
        if(f.exists()) {

            FileInputStream fis = null;
            ObjectInputStream in = null;
            try {
                fis = new FileInputStream(f);
                in = new ObjectInputStream(fis);
                try
                {
                    Utility.fcconceptDifficulty = (FuzzyCompositeConceptDifficulty) in.readObject();

                }
                catch (ClassNotFoundException ex)
                {
                    ex.printStackTrace();
                }
                finally {
                    in.close();
                    fis.close();
                }
//                System.out.println("Food: FCC is loaded from file : "+f.getName());
            }
            catch(IOException ex) {
                ex.printStackTrace();
            }

        }
        //else create an expert case base
        else {
            
            FileOutputStream fos;
            ObjectOutputStream out;
            
            try {
                int i = 0;
                //add the input sets to vocabulary now
                for (AgreementMF_zMFs[] inputsets: Utility.fcconceptDifficulty.getInputConceptSets()) {
                    Vocabulary.addValue(Utility.fcconceptDifficulty.getConceptNames().get(i), inputsets);
                    i++;
                }
//                System.out.println("Food: Concept is created from expert knowledge!");
                f.createNewFile();
                fos = new FileOutputStream(f);
                out = new ObjectOutputStream(fos);
                out.writeObject(Utility.fcconceptDifficulty);
                out.close();
                fos.close();
//                System.out.println("FOOD: writing to filename " + inputfilename);
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
            }
                       
        } //end else
            
    }
    
    public static void saveNewConcept() {

        String inputfilename = Utility.foldername+concept;

        File files = new File(Utility.foldername);
        if (!files.exists()) {
            if (files.mkdirs()) {
//                System.out.println("FOOD: Multiple directories are created!");
            } else {
//                System.out.println("FOOD: Failed to create multiple directories!");
            }
        }
        
        File f;
        f = new File(inputfilename);
        
        FileOutputStream fos;
        ObjectOutputStream out;
        
        if (f.exists()) {
            synchronized (f) {
                f.delete();
            }
        }
        
        try {
            synchronized (f) {
                boolean createNewFile = f.createNewFile();
            }
            synchronized (f) {
                fos = new FileOutputStream(f);
            }
            out = new ObjectOutputStream(fos);
            out.writeObject(Utility.fcconceptDifficulty);
//            System.out.println("FOOD: writing to file " + inputfilename);
            out.close();
            fos.close();
        } 
        catch (IOException ex) {
            Logger.getLogger(Food.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    @Override
    public void modifySolution() {
        //write new solution to casebase
    }
        
    @Override
    public String toString() {
        String s = "";
        if (difficulty == null) {
            defineDifficulty();
        }
        //september 2013 adding overall time
        s = (new StringBuilder()).append(s).append("Food: ").append(this.nameOfDish).append(" which is ").append(this.difficulty.getIntervalValue().toString()).append(" - ").append(getUserFriendlyDifficulty2(this.getOverallTime())).append(" having { ").toString();
        for (FVPair fvpair: super.getSolutionFVPairs()) {
            s = (new StringBuilder()).append(s).append("[").append(fvpair.getFeature().getName()).append(" - ").append(fvpair.getSliceValue().get(0).getName()).append("]\n").toString();
        }
        s = (new StringBuilder()).append(s).append("}").toString();
        return s;
    }

    @Override
    public int compareTo(Object f) {
        //ascending order
        //return ((Double)super.getFavourIndicator()).compareTo((Double)((Solution)f).getFavourIndicator());
        //descending order
        return ((Double)((Solution)f).getFavourIndicator()).compareTo(((Double)super.getFavourIndicator()));
    }
    
}
