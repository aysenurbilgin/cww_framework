/*
 * Copyright (c) 2015 Aysenur Bilgin
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cbr;

import activity.Food;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Class Case is an important part of CBR implementation
 * Case holds the representation of a case as feature and value pairs
 * @author abilgin
 */
public class Case implements Serializable, Comparable {
    
    private ArrayList<FVPair> fvpairs;
    private ArrayList<Solution> solutions;

    public Case() {
        this.fvpairs = new ArrayList<FVPair>();
        this.solutions = new ArrayList<Solution>();
    }
    
    public Case(Case c) {
        this.fvpairs = new ArrayList<FVPair>();
        this.solutions = new ArrayList<Solution>();
        
        for (int i = 0; i < c.getAllFVPairs().size(); i++) {
            this.fvpairs.add(c.getFVPair(i));
        }
        
        for (int i = 0; i < c.getSolutions().size(); i++) {
            this.solutions.add(c.getSolution(i));
        }
    }

    public void addFVtoCase(FVPair newfv) {
        this.fvpairs.add(newfv);
    }
    
    public void addAllFVtoCase(ArrayList<FVPair> newfv) {
        this.fvpairs.addAll(newfv);
    }

    public ArrayList<FVPair> getAllFVPairs() {
        return this.fvpairs;
    }

    public FVPair getFVPair(int index) {
        return this.fvpairs.get(index);
    }
    
    public void addSolution(Solution s) {
        
        for (Solution sol: this.getSolutions()) {
            if (sol instanceof Food && s instanceof Food) {
                if (((Food)sol).getRecipe().getName().equals(((Food)s).getRecipe().getName()))
                {
                    return;
                }
            }
        }
        this.solutions.add(s); 
        
        Collections.sort(this.solutions);

    }
    
    public void sortSolutions() {
        Collections.sort(this.solutions);
    }
    
    public Solution getSolution(int index) {
        return this.solutions.get(index);
    }
    
    public ArrayList<Solution> getSolutions() {
        return this.solutions;
    }
    
    public void removeSolutions() {
        this.solutions = new ArrayList<Solution>();
    }
    
    public void addAllSolutions(ArrayList<Solution> sols) {
        this.solutions.addAll(sols);
    }
    
    public void addToSolutions(Solution sols) {
        this.solutions.add(sols);
    }
    
    public boolean checkEqualityForIdentification(Case c) {
        
        boolean flag = false;
        
        //if the number of fvpair are equal then there is the chance of cases being equal
        if (this.getAllFVPairs().size() == c.getAllFVPairs().size()) {
            boolean secondflag = true;
            for (int i = 0; i < this.getAllFVPairs().size(); i++) {
                secondflag &= this.getAllFVPairs().get(i).equals(c.getAllFVPairs().get(i));
            }
            
            flag = secondflag;
        }
        
        return flag;
    }
        
    public double getGlobalSimilarity(Case query) {

        double globalSimilarity = 0.0, weightTotal = 0.0;
        
        for (FVPair qfeature : query.getAllFVPairs()) {
            for (FVPair thisfeature : this.getAllFVPairs()) {   
                if (qfeature.getFeature().getName().equalsIgnoreCase(thisfeature.getFeature().getName())) {
                    globalSimilarity += thisfeature.getFeature().getWeight() * Similarity.findJaccardSimilarity(qfeature.getSliceValue(), thisfeature.getSliceValue(), qfeature.getCombinedSliceIntervalValue());
                    weightTotal += thisfeature.getFeature().getWeight();
                }
            }
        }

        return globalSimilarity/weightTotal;

    }
    
    @Override
    public String toString() {
        String s = "";
        s = (new StringBuilder()).append(s).append("Case: ").append("{ ").toString();
        for (FVPair fvpair: this.getAllFVPairs()) {
            s = (new StringBuilder()).append(s).append("[").append(fvpair.getFeature().getName()).append(" - ").append(fvpair.getSliceValue().toString()).append("]\n").toString();
        }
        s = (new StringBuilder()).append(s).append("}").toString();
        return s;
    }


    @Override
    public int compareTo(Object t) {
        return this.solutions.get(0).compareTo(((Case)t).getSolutions().get(0));
    }

    
}
