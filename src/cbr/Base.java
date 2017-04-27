/*
 * Copyright (c) 2015 Aysenur Bilgin
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cbr;

import activity.Activity;
import activity.ActivityRecommenderGUI;
import activity.CaseBaseGenerator;
import activity.Food;
import fatSecret.Recipe;
import gui.Utility;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * Case base will be categorized according to activity preference of the user
 * @author abilgin
 */
public class Base implements Serializable {

    private Map<Index, ArrayList<Case>> casebase;

    public Base() {
        
        if (!ActivityRecommenderGUI.usingDB) {
            casebase = new TreeMap<Index, ArrayList<Case>>();
        }
    }
    
    public void addCases(Activity act, ArrayList<Case> createCasesperActivity) {

        if (!ActivityRecommenderGUI.usingDB) {
            casebase.put(act, createCasesperActivity);
        }
        else {
            //add to the database
        }
    }
    
    public void addCaseToBase(Activity act, Case newcase) {
        
        //check whether the case exists
        ArrayList<Case> clist = casebase.get(act);
        
        boolean foundflag = false;
        boolean foundsolflag = false;
             
        for (Case c: clist) {
            if (c.checkEqualityForIdentification(newcase)) {
                //case found
                //get the solutions of the list
                ArrayList<Solution> existingsolutions = new ArrayList<Solution>();
                existingsolutions = c.getSolutions();
                //check for each solution whether it exists
                //if yes increase the favour index
                foundflag = true;

                for (Solution s: existingsolutions) {
                    //newcase always comes with one solution
                    for (Solution casesol: newcase.getSolutions()) {
                        if (s instanceof Food && casesol instanceof Food) {
                            if (((Food)s).getNameofDish().equalsIgnoreCase(((Food)casesol).getNameofDish())) {
                                //if the similar solution is found
                                //increase the favour index
                                s.setFavourIndicator(s.getFavourIndicator()+1.0D);
                                foundsolflag = true;
                                break;
                            }
                        }
                    }
                }

                if (!foundsolflag) {
                    c.addSolution(newcase.getSolution(0));
                }
                break;
            }
        }
        
        //should not exist
        if (!foundflag) {
            casebase.remove(act);
            clist.add(newcase);
            casebase.put(act, clist);
        }
    }
    
    public synchronized Integer getBaseSize() {
        return casebase.size();
    }
    
    public synchronized Integer getBaseSize(Activity act) {
        return casebase.get(act).size();
    }
    
    public synchronized Integer getNumberOfRecipesInBase(Activity act) {
        
        int numberOfRecipes = 0;
        ArrayList<Case> cases = casebase.get(act);
        for (Case c : cases) {
            for (Solution s: c.getSolutions()) {
                numberOfRecipes++;
            }
        }
        return numberOfRecipes;
    }
    
    public synchronized boolean doesSolutionExist(Activity act, Object solution) {
        
        ArrayList<Case> cases = casebase.get(act);
        for (Case c : cases) {
            for (Solution s: c.getSolutions()) {
                if (solution instanceof Recipe) {
                    if (((Food)s).getRecipe().getName().equals(((Recipe)solution).getName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    /**
     * Identify the current problem situation and find a past case similar to the new one
     * @param index is the index of the casebase to retrieve the cases e.g. activity that the user wishes to do
     * @param query is the current problem
     * @param usethreshold is the flag to whether use a certain threshold to retrieve cases or not
     * @return retrieved cases with their similarity measure values to the current problem
     */
    public ArrayList<RetrievedCase> retrieve(Index index, Case query, boolean usethreshold) {

        //when a query comes, check similarity and retrieve the n best cases
        //or the ones having similarity above a threshold
        ArrayList<RetrievedCase> listRetrieved = new ArrayList<RetrievedCase>();

        //if usethreshold then retrieve the cases that have the global similarity above the defined threshold
        //index is retrieved from the user or the higher level context
        if (usethreshold) {
            for (Case c: casebase.get(index)) {
                double comparisonResult = c.getGlobalSimilarity(query);
                if (comparisonResult >= Similarity.similarityThreshold) {
                    listRetrieved.add(new RetrievedCase(c, comparisonResult));
                } 
            }

            //in case most of the cases resemble the query case
            //also sort the retrieved cases according to the similarity values
            Collections.sort(listRetrieved, new SimilarityComparator());
            
            return listRetrieved;
        }
        else {
            //calculate all the case base
            for (Case c: casebase.get(index)) {
                listRetrieved.add(new RetrievedCase(c, c.getGlobalSimilarity(query)));        
            }
            
            int count = 0;
            
            //take the first n having the most similarity
            //the list should be in descending order so that the first n cases count for the most similar ones
            ArrayList<RetrievedCase> listRetrievedBestN = new ArrayList<RetrievedCase>();
            
            int iterCount = Math.min(Similarity.nBestCases, listRetrieved.size());
            
            for (int i = 0; i < iterCount; i++) {
                listRetrievedBestN.add(listRetrieved.get(i));
            }
            
            //sort the cases from which we will take the first n
            Collections.sort(listRetrievedBestN, new SimilarityComparator());
            
            return listRetrievedBestN;
            
        }

    }
    
    /**
     * Use the cases returned from retrieve to suggest a solution to the current problem
     * @param ind is the index indicating that a certain activity has been chosen
     * @param selectedCases are the cases that are retrieved according to the similarity measure from method retrieve
     * @return 
     */
    public ArrayList<Case> reuse(Activity ind, ArrayList<RetrievedCase> selectedCases) {
        
        //after the selected cases are retrieved
        //we need to offer a solution, either adapted or merged
        //if there are no selected cases then no solution can be offered
        if (selectedCases.isEmpty()) {
            System.out.println("No solution can be suggested!");
            return null;
        }

        ArrayList<Case> solutions = new ArrayList<Case>();
//        
        //calculate the favourIndicator and sort
        for (RetrievedCase rc: selectedCases) {
            solutions.add(rc.getCase());
        }

        return solutions;
    }
    
        
    public Activity lookForActivity(String name) {
        
        for (Index i: casebase.keySet()) {
            if ("cook".equalsIgnoreCase((String) i.getIndex())) {
                return (Activity)i;
            }
        }
        
        return null;
    }

    public void init(String username) {

        String inputfilename = Utility.foldername + "casebase.ser";
        
        File files = new File(Utility.foldername);
        if (!files.exists()) {
            if (files.mkdirs()) {
//                System.out.println("BASE: Multiple directories are created!");
            } else {
//                System.out.println("BASE: Failed to create multiple directories!");
            }
        }

        File f;
        f = new File(inputfilename);
    
        //if the case base and the vocabulary exists on a file, read it from the file
        if(f.exists()) {

            FileInputStream fis;
            ObjectInputStream in;
            try {
                fis = new FileInputStream(f);
                in = new ObjectInputStream(fis);
                try
                {
                    casebase = (Map) in.readObject();
                    Vocabulary.copyFromFile((Map)in.readObject());
                }
                catch (ClassNotFoundException ex)
                {
                    ex.printStackTrace();
                }
                in.close();
            }
            catch(IOException ex) {
                ex.printStackTrace();
            }
        }
        //else create an expert case base
        else {
            
            FileOutputStream fos = null;
            ObjectOutputStream out = null;
            
            try {
                //set membership functions for linguistic labels
                CaseBaseGenerator.initialize();
                f.createNewFile();
                fos = new FileOutputStream(f);
                out = new ObjectOutputStream(fos);
                out.writeObject(casebase);
                out.writeObject(Vocabulary.getInstance());
                System.out.println("BASE: writing to folder " + Utility.foldername);
                out.close();
                fos.close();
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
            }
            
        } //end else

    }// end init()
    
    public void save(){
        
        File files = new File(Utility.foldername);
        if (!files.exists()) {
            if (files.mkdirs()) {
//                System.out.println("Multiple directories are created!");
            } else {
//                System.out.println("Failed to create multiple directories!");
            }
        }
        String inputfilename = Utility.foldername + "casebase.ser";
        File f;
        f = new File(inputfilename);

        //save the casebase and vocabulary on the file
        FileOutputStream fos;
        ObjectOutputStream out;

        try {
            //set membership functions for linguistic labels

            if (f.exists()) {
                f.delete();
            }
            f.createNewFile();
            fos = new FileOutputStream(f);
            out = new ObjectOutputStream(fos);
            out.writeObject(casebase);
            out.writeObject(Vocabulary.getInstance());
            out.flush();
            out.close();
            fos.close();
            System.out.println("BASE: writing to folder " + Utility.foldername);
            System.out.println("BASE : " + inputfilename+ " written successfully");
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        finally {
//            System.out.println("File: "+ inputfilename + " saved successfully!");
        }
        
    }

    public Map<Index, ArrayList<Case>> getCasebase() {
        return casebase;
    }


    public void printBase()
    {
        for (Entry e : casebase.entrySet()) {
            
            for (Case c : (ArrayList<Case>) e.getValue()) {
                System.out.println(c.toString() + " and " + c.getSolutions().toString());
            }
        }
    }
    

}
