/*
 * Copyright (c) 2015 Aysenur Bilgin
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cbr;

import fuzzylogic.generic.Tuple;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author abilgin
 */
public abstract class Solution implements Comparable, Serializable {
    
    private ArrayList<FVPair> fvpairs;
    private double favourIndicator;
    
    public void initialize() {
        this.fvpairs = new ArrayList<FVPair>();
        this.favourIndicator = 1.0;
    }
    
    public void addFVtoSolution(FVPair newfv) {
        this.fvpairs.add(newfv);
    }
    
    public void addAllFVtoSolution(ArrayList<FVPair> newfv) {
        this.fvpairs.addAll(newfv);
    }
    
    public ArrayList<FVPair> getSolutionFVPairs() {
        return this.fvpairs;
    }

    public FVPair getSolutionFVPair(int index) {
        return this.fvpairs.get(index);
    }
    
    public Value getSolutionValueforFeature(String fname) {
        
        for (FVPair fvp: this.fvpairs) {
            if (fvp.getFeature().getName().equalsIgnoreCase(fname)) {
                return fvp.getValue();
            }
        }
        
        return null;
        
    }
    
    public ArrayList<Double> getSolutionFValues() {
        
        ArrayList<Double> retvalues = new ArrayList<Double>();
        
        for (int i = 0; i < this.fvpairs.size(); i++) {
            retvalues.add(((Tuple)fvpairs.get(i).getValue().getIntervalValue()).getAverage());
        }

        return retvalues;
    }
    
    public boolean checkEqualityForIdentification(Solution s) {
        
        boolean flag = false;
        
        //if the number of fvpair are equal then there is the chance of cases being equal
        if (this.getSolutionFVPairs().size() == s.getSolutionFVPairs().size()) {
            boolean secondflag = true;
            for (int i = 0; i < this.getSolutionFVPairs().size(); i++) {
                secondflag &= this.getSolutionFVPairs().get(i).equals(s.getSolutionFVPairs().get(i));
            }
            
            flag = secondflag;
        }
        
        return flag;
    }
    
    public double getFavourIndicator() {
        return this.favourIndicator;
    }

    public void setFavourIndicator(double favourIndicator) {
        this.favourIndicator = favourIndicator;
    }
   
    
    public abstract void modifySolution();
    
}
